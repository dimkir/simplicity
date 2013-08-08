@echo off
javac -d bin -target 1.6 -source 1.6 -sourcepath src -cp lib/core.jar src/simplicity/*.java  -bootclasspath lib/rt.jar
echo %ERRORLEVEL%
if ERRORLEVEL 1 GOTO L_FAILURE

echo ############ creating jar ###############
cd bin
jar cfv ../dist/simplicity/library/simplicity.jar  * 
cd ..
GOTO L_END

:L_FAILURE

echo "Error compiling skipping creating JAR"


:L_END

pause