@echo off
ECHO Compiling Historical Comparison Test...
javac -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar HistoricalComparisonPanel.java HistoricalComparisonTest.java
IF %ERRORLEVEL% == 0 (
  ECHO Running Historical Comparison Test...
  java -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar HistoricalComparisonTest
) ELSE (
  ECHO Compilation Failed!
)
PAUSE