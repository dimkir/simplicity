@REM #!/bin/sh
"../java/bin/javac" -g -verbose -d bin -target 1.6 -source 1.6 -sourcepath src -cp ../core/library/core.jar src/simplicity/*.java  -bootclasspath ../java/lib/rt.jar
pause