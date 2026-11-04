#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes build/test-classes
javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d build/classes src/lab3/*.java
jar -J-Djava.io.tmpdir=build --create --file lab3.jar --main-class lab3.Main -C build/classes .
javac --release 17 -encoding UTF-8 -Xlint:all -Werror -cp build/classes -d build/test-classes test/lab3/*.java
java -cp build/classes:build/test-classes lab3.ModelTest
printf 'Build and tests completed. Run: java -jar lab3.jar\n'
