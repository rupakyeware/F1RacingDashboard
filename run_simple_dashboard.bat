@echo off
ECHO Compiling Simple F1 Dashboard...
javac -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar TrackMapPanel.java F1SimpleDashboard.java
IF %ERRORLEVEL% == 0 (
  ECHO Running Simple F1 Dashboard...
  java -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar F1SimpleDashboard
) ELSE (
  ECHO Compilation Failed!
)
PAUSE