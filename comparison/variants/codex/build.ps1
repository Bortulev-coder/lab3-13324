$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    New-Item -ItemType Directory -Force build/classes,build/test-classes | Out-Null
    $sources = (Get-ChildItem src/lab3/*.java).FullName
    & javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d build/classes $sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed' }
    & jar '-J-Djava.io.tmpdir=build' --create --file lab3.jar --main-class lab3.Main -C build/classes .
    if ($LASTEXITCODE -ne 0) { throw 'JAR creation failed' }
    $tests = (Get-ChildItem test/lab3/*.java).FullName
    & javac --release 17 -encoding UTF-8 -Xlint:all -Werror -cp build/classes -d build/test-classes $tests
    if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed' }
    & java -cp 'build/classes;build/test-classes' lab3.ModelTest
    if ($LASTEXITCODE -ne 0) { throw 'Tests failed' }
    Write-Output 'Build and tests completed. Run: java -jar lab3.jar'
} finally { Pop-Location }
