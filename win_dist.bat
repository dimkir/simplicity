@echo off
REM *************************************************************************.
REM *************************************************************************
REM ******** HERE YOU MUST SPECIFY path to your [libraries] folder  *********
REM ********  inside your processing sketchbook directory      **************
REM *************************************************************************
REM *************************************************************************
SET SKETCHBOOK_LIBRARIES_FOLDER="C:\Users\Local User\Documents\Processing\libraries"
REM **** Important: always put " (doublequotes) around your full pathname. ****


xcopy dist %SKETCHBOOK_LIBRARIES_FOLDER% /E

pause