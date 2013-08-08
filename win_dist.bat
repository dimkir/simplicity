@echo off
REM *************************************************************************
REM *************************************************************************
REM ******** HERE YOU MUST SPECIFY YOUR Processing SketchBook folder ********
REM *************************************************************************
REM *************************************************************************
SET SKETCHBOOK_FOLDER="C:\Users\Local User\Documents\Processing\libraries"
REM **** Important: always put " (doublequotes) around your full pathname. ****


xcopy dist %SKETCHBOOK_FOLDER% /E

pause