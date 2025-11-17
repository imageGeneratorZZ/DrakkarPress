@echo off
setlocal
REM Script de inicio de DrakkarPress con Java 21

echo ============================================================
echo   DrakkarPress Backend - Iniciando con Java 21
echo ============================================================
echo.

REM Resolver JAVA_HOME (portable primero, luego instalacion global)
set "LOCAL_JAVA=%~dp0.java\jdk-21.0.8"
set "USER_JAVA=C:\Users\SuperUsuario\.jdk\jdk-21.0.8"

if exist "%LOCAL_JAVA%\bin\java.exe" (
    set "JAVA_HOME=%LOCAL_JAVA%"
) else if exist "%USER_JAVA%\bin\java.exe" (
    set "JAVA_HOME=%USER_JAVA%"
) else (
    echo [ERROR] No se encontro un JDK 21. Instala Temurin 21 o copia el portable a .java\jdk-21.0.8
    exit /b 1
)

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

REM Usar Maven Wrapper con Java 21
call mvnw.cmd clean spring-boot:run

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Fallo al iniciar la aplicacion
    pause
    exit /b 1
)

pause
endlocal
