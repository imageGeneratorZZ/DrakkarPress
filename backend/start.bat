@echo off
REM DrakkarPress Backend - Quick Start Script
REM Windows Batch file to setup and run the application

echo ===========================================================
echo    DrakkarPress Backend - Quick Start
echo ===========================================================
echo.

REM Check if Java is installed
echo Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17 or higher from: https://adoptium.net/
    pause
    exit /b 1
)
echo Java is installed
echo.

REM Check if Maven is installed (prefer mvnw wrapper)
echo Checking Maven installation...
if exist "mvnw.cmd" (
    echo Using Maven Wrapper (mvnw.cmd)
    set MVN_CMD=mvnw.cmd
) else (
    mvn -version >nul 2>&1
    if %errorlevel% neq 0 (
        echo ERROR: Maven is not installed and mvnw.cmd not found
        echo Please install Maven from: https://maven.apache.org/download.cgi
        pause
        exit /b 1
    )
    echo Using system Maven
    set MVN_CMD=mvn
)
echo.

REM Check if database is setup
if not exist ".env" (
    echo .env file not found. Running database setup...
    echo.
    powershell -ExecutionPolicy Bypass -File setup-database.ps1
    echo.
    if %errorlevel% neq 0 (
        echo Database setup failed!
        pause
        exit /b 1
    )
)

REM Build the project
echo Building the project...
echo.
call %MVN_CMD% clean install -DskipTests
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b 1
)
echo.
echo Build successful!
echo.

REM Run the application
echo Starting DrakkarPress Platform...
echo.
echo ===========================================================
echo   Application will start on http://localhost:8080
echo   Health Check: http://localhost:8080/api/health
echo   Press Ctrl+C to stop
echo ===========================================================
echo.

call %MVN_CMD% spring-boot:run

pause
