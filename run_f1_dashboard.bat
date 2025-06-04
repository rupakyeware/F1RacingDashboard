@echo off
SETLOCAL EnableDelayedExpansion

ECHO ================================================
ECHO F1 Dashboard - Comprehensive Windows Setup
ECHO ================================================
ECHO.

REM Create lib directory if it doesn't exist
IF NOT EXIST lib (
    ECHO Creating lib directory...
    mkdir lib
    ECHO Created lib directory.
) ELSE (
    ECHO Lib directory already exists.
)

REM Check for MySQL JDBC driver
IF NOT EXIST lib\mysql-connector-j.jar (
    ECHO Downloading MySQL JDBC driver...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.33/mysql-connector-j-8.0.33.jar' -OutFile 'lib\mysql-connector-j.jar'"
    IF !ERRORLEVEL! NEQ 0 (
        ECHO Failed to download MySQL JDBC driver.
        GOTO error
    ) ELSE (
        ECHO MySQL JDBC driver downloaded successfully.
    )
) ELSE (
    ECHO MySQL JDBC driver already exists.
)

REM Check for JSON library
IF NOT EXIST lib\json-20231013.jar (
    ECHO Downloading JSON library...
    powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar' -OutFile 'lib\json-20231013.jar'"
    IF !ERRORLEVEL! NEQ 0 (
        ECHO Failed to download JSON library.
        GOTO error
    ) ELSE (
        ECHO JSON library downloaded successfully.
    )
) ELSE (
    ECHO JSON library already exists.
)

ECHO.
ECHO ------------------------------------------------
ECHO Step 1: Clean up any existing class files
ECHO ------------------------------------------------
del /Q *.class 2>nul
ECHO Class files removed.

ECHO.
ECHO ------------------------------------------------
ECHO Step 2: Compiling database components
ECHO ------------------------------------------------
ECHO Compiling F1DatabaseManager.java...
javac -verbose -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar F1DatabaseManager.java
IF !ERRORLEVEL! NEQ 0 GOTO compilation_error

ECHO Compiling F1DatabaseService.java...
javac -verbose -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar F1DatabaseService.java
IF !ERRORLEVEL! NEQ 0 GOTO compilation_error

ECHO.
ECHO ------------------------------------------------
ECHO Step 3: Compiling UI components
ECHO ------------------------------------------------
ECHO Compiling TrackMapPanel.java...
javac -verbose -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar TrackMapPanel.java
IF !ERRORLEVEL! NEQ 0 GOTO compilation_error

ECHO Compiling HistoricalComparisonPanel.java...
javac -verbose -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar HistoricalComparisonPanel.java
IF !ERRORLEVEL! NEQ 0 GOTO compilation_error

ECHO.
ECHO ------------------------------------------------
ECHO Step 4: Compiling main application
ECHO ------------------------------------------------
ECHO Compiling F1SwingDashboardApp.java...
javac -verbose -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar F1SwingDashboardApp.java
IF !ERRORLEVEL! NEQ 0 GOTO compilation_error

ECHO.
ECHO ------------------------------------------------
ECHO Step 5: Running F1 Dashboard
ECHO ------------------------------------------------
ECHO Starting F1 Dashboard application...
java -cp .;lib\mysql-connector-j.jar;lib\json-20231013.jar F1SwingDashboardApp
GOTO end

:compilation_error
ECHO.
ECHO ------------------------------------------------
ECHO COMPILATION ERROR
ECHO ------------------------------------------------
ECHO Failed to compile. See errors above.
GOTO end

:error
ECHO.
ECHO ------------------------------------------------
ECHO ERROR
ECHO ------------------------------------------------
ECHO An error occurred. See details above.

:end
ECHO.
ECHO ------------------------------------------------
ECHO Press any key to close...
PAUSE > NUL
ENDLOCAL