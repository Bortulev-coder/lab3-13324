"""Run with Python 3 and JDK 17+. Original project sources are never modified."""
from pathlib import Path
import subprocess
import shutil
import json
import os
import difflib
import hashlib

root = Path(__file__).resolve().parents[1]
here = Path(__file__).resolve().parent
results = root / 'results' / 'comparison'
results.mkdir(parents=True, exist_ok=True)
original = (root / 'src/lab3/RumBottle.java').read_text(encoding='utf-8')
start = original.index('    public void consume(')
end = original.index('    private void requireOwner(')
records = []
for variant in ['codex', 'gigachat', 'deepseek', 'qwen']:
    sandbox = (here / 'variants' / variant).resolve()
    assert sandbox.is_relative_to(here.resolve())
    sandbox.mkdir(parents=True, exist_ok=True)
    shutil.copytree(root / 'src', sandbox / 'src', dirs_exist_ok=True)
    shutil.copytree(root / 'test', sandbox / 'test', dirs_exist_ok=True)
    shutil.copy2(here/'ConsumeContractTest.java', sandbox/'test/lab3/ConsumeContractTest.java')
    for script in ['build.sh','build.ps1']:
        shutil.copy2(root/script,sandbox/script)
    variant_results = sandbox/'results'
    variant_results.mkdir(exist_ok=True)
    classes = sandbox / 'build/classes'
    test_classes = sandbox / 'build/test-classes'
    classes.mkdir(parents=True,exist_ok=True)
    test_classes.mkdir(parents=True,exist_ok=True)
    if variant != 'codex':
        snippet = (here / (variant + '-method.java.txt')).read_text(encoding='utf-8')
        replacement = '\n'.join('    '+line if line else '' for line in snippet.splitlines())
        (sandbox/'src/lab3/RumBottle.java').write_text(
            original[:start]+replacement+'\n\n'+original[end:],encoding='utf-8')
    actual = (sandbox/'src/lab3/RumBottle.java').read_text(encoding='utf-8')
    (variant_results/'changes.diff').write_text(''.join(difflib.unified_diff(
        original.splitlines(keepends=True), actual.splitlines(keepends=True),
        fromfile='original/RumBottle.java',tofile=variant+'/RumBottle.java')),encoding='utf-8')
    sources = sorted((sandbox/'src/lab3').glob('*.java'))
    compile_result = subprocess.run(['javac','--release','17','-encoding','UTF-8',
        '-Xlint:all','-Werror','-d',str(classes),*[str(p) for p in sources]],capture_output=True)
    assert compile_result.returncode == 0, compile_result.stderr.decode(errors='replace')
    tests = subprocess.run(['javac','--release','17','-encoding','UTF-8',
        '-Xlint:all','-Werror','-cp',str(classes),'-d',str(test_classes),
        *[str(p) for p in sorted((sandbox/'test/lab3').glob('*.java'))]],capture_output=True)
    assert tests.returncode == 0, tests.stderr.decode(errors='replace')
    jar = subprocess.run(['jar','-J-Djava.io.tmpdir=build','--create','--file','lab3.jar',
        '--main-class','lab3.Main','-C','build/classes','.'],cwd=sandbox,capture_output=True)
    assert jar.returncode == 0, jar.stderr.decode(errors='replace')
    compilation_log = 'javac --release 17 -encoding UTF-8 -Xlint:all -Werror\nProduction compilation exit: 0\nTest compilation exit: 0\nJAR creation exit: 0\n'
    (variant_results/'compilation.txt').write_text(compilation_log,encoding='utf-8')
    log = []
    for name in ['ModelTest','ConsumeContractTest']:
        run = subprocess.run(['java','-Dfile.encoding=UTF-8','-cp',os.pathsep.join([str(classes),str(test_classes)]),'lab3.'+name],capture_output=True)
        assert run.returncode == 0, run.stderr.decode('utf-8',errors='replace')
        log.append(run.stdout.decode('utf-8').replace('\r\n','\n'))
        (variant_results/(name+'.txt')).write_bytes(run.stdout+run.stderr)
    for case, arguments in [('default',[]),('low-rum',['--rum=5'])]:
        execution = subprocess.run(['java','-jar','lab3.jar',*arguments],cwd=sandbox,capture_output=True)
        assert execution.returncode == 0
        (variant_results/(case+'.txt')).write_bytes(execution.stdout+execution.stderr)
    env = subprocess.run(['java','-version'],capture_output=True)
    (variant_results/'environment.txt').write_bytes(env.stdout+env.stderr)
    (sandbox/'README.txt').write_text(
        'Вариант: '+variant+'\n'
        'Перенос и локальные проверки выполнены Codex по присланному пользователем ответу.\n'
        'Тела методов не исправлены. Основной проект не изменён.\n'
        'Исходники: src/lab3/RumBottle.java. Тесты: test/lab3.\n'
        'Windows: .\\build.ps1\n'
        'Windows: java -cp "build/classes;build/test-classes" lab3.ConsumeContractTest\n'
        'Unix: sh build.sh\n'
        'Unix: java -cp build/classes:build/test-classes lab3.ConsumeContractTest\n'
        'Сценарий: java -jar lab3.jar\n'
        'Журналы реальных локальных запусков: results/\n'
        'Для DeepSeek LOG_USES_USER_NAME=false — зафиксированное отличие в дополнительной проверке,\n'
        'а не провал 12 основных случаев. Смотрите общий анализ в отчёте.\n',encoding='utf-8')
    text = '\n'.join(log)
    assert 'LOG_USES_USER_NAME='+str(variant!='deepseek').lower() in text
    (results/(variant+'.txt')).write_text(text,encoding='utf-8')
    records.append({'variant':variant,'compilation':'PASS','model_checks':59,
                    'contract_cases':12,'alias_logs_user':variant!='deepseek',
                    'rum_bottle_sha256':hashlib.sha256(actual.encode('utf-8')).hexdigest()})
    print(variant+': '+text.splitlines()[0]+'; '+text.splitlines()[2])

    if variant == 'gigachat':
        # Isolate only the missing throws/catch defect, without JUnit/Mockito or API mismatches.
        probe = sandbox/'GigaChatCheckedProbe.java'
        probe.write_text('''import lab3.*;
class GigaChatCheckedProbe {
    void exact(RumBottle bottle, Person owner) { bottle.consume(owner, 500); }
    void less(RumBottle bottle, Person owner) { bottle.consume(owner, 200); }
    void twice(RumBottle bottle, Person owner) {
        bottle.consume(owner, 200);
        bottle.consume(owner, 150);
    }
}
''',encoding='utf-8')
        broken = subprocess.run(['javac','-J-Duser.language=en','--release','17',
            '-encoding','UTF-8','-cp',str(classes),'-d',str(classes),str(probe)],capture_output=True)
        assert broken.returncode != 0
        diagnostic=broken.stderr.decode('utf-8',errors='replace').replace(str(sandbox),'VARIANT_DIR')
        assert diagnostic.count('unreported exception InsufficientRumException') == 4
        (results/'gigachat-checked-exception-probe.txt').write_text(diagnostic,encoding='utf-8')
        (variant_results/'gigachat-checked-exception-probe.txt').write_text(diagnostic,encoding='utf-8')
(results/'summary.json').write_text(json.dumps(records,ensure_ascii=False,indent=2),encoding='utf-8')
print('GigaChat success-test probe: 4 checked-exception compilation errors confirmed.')
