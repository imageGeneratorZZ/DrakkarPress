@echo off
REM Script de inicio de DrakkarPress con Java 17 local

echo ============================================================
echo   DrakkarPress Backend - Iniciando con Java 17 Local
echo ============================================================
echo.

REM Configurar JAVA_HOME local
set "JAVA_HOME=%~dp0.java\jdk-17.0.10+7"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo Verificando Java...
"%JAVA_HOME%\bin\java.exe" -version
echo.

REM Verificar PostgreSQL
echo Verificando PostgreSQL...
docker ps | findstr "drakkarpress-postgres" >nul 2>&1
if %errorlevel% neq 0 (
    echo PostgreSQL no esta corriendo. Iniciando...
    docker-compose up -d
    timeout /t 5 /nobreak >nul
) else (
    echo PostgreSQL corriendo OK
)

echo.
echo ============================================================
echo   Compilando e iniciando aplicacion...
echo   (Primera vez: descarga dependencias - 3-5 minutos)
echo ============================================================
echo.

REM Usar Maven Wrapper con Java 17 local
call mvnw.cmd clean spring-boot:run

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo al iniciar la aplicacion
    pause
    exit /b 1
)

pause
