@echo off
REM Script de inicio rapido para DrakkarPress Backend

echo ============================================================
echo   DrakkarPress Backend - Iniciando...
echo ============================================================
echo.

REM Verificar PostgreSQL
echo Verificando PostgreSQL...
docker ps | findstr "drakkarpress-postgres" >nul 2>&1
if %errorlevel% neq 0 (
    echo PostgreSQL no esta corriendo. Iniciando...
    docker-compose up -d
    timeout /t 5 /nobreak >nul
) else (
    echo PostgreSQL ya esta corriendo
)

echo.
echo ============================================================
echo   Compilando y ejecutando aplicacion...
echo ============================================================
echo.

REM Compilar y ejecutar
call mvn clean spring-boot:run

pause
