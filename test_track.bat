@echo off
echo Compiling Test Track Map...
javac -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar TestTrackMap.java
if %ERRORLEVEL% == 0 (
  echo Running Test...
  java -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar TestTrackMap
) else (
  echo Compilation Failed!
)
pause