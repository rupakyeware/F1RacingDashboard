@echo off
SETLOCAL

ECHO ===========================================
ECHO F1 Dashboard Application - Windows Version
ECHO ===========================================

REM Check if lib directory exists and create it if it doesn't
IF NOT EXIST lib mkdir lib

REM Check if MySQL JDBC driver exists and download if needed
IF NOT EXIST lib\mysql-connector-j.jar (
    ECHO Downloading MySQL JDBC driver...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar' -OutFile 'lib\mysql-connector-j.jar'"
    ECHO MySQL JDBC driver downloaded successfully.
)

REM Check if JSON library exists and download if needed
IF NOT EXIST lib\json-20231013.jar (
    ECHO Downloading JSON library...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar' -OutFile 'lib\json-20231013.jar'"
    ECHO JSON library downloaded successfully.
)

REM Compile all Java files
ECHO.
ECHO Compiling Java files...
javac -cp ".;lib\mysql-connector-j.jar;lib\json-20231013.jar" *.java
IF NOT %ERRORLEVEL% == 0 (
    ECHO.
    ECHO Compilation failed! See errors above.
    GOTO end
)

REM Run the application
ECHO.
ECHO Starting F1 Dashboard application...
java -cp ".;lib\mysql-connector-j.jar;lib\json-20231013.jar" F1SwingDashboardApp

:end
ECHO.
ECHO Press any key to exit...
PAUSE > NUL
ENDLOCAL