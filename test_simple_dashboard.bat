@echo off
echo Compiling Simple F1 Dashboard...
javac -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar SimpleF1Dashboard.java
if %ERRORLEVEL% == 0 (
  echo Running Simple F1 Dashboard...
  java -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar SimpleF1Dashboard
) else (
  echo Compilation Failed!
)
pause