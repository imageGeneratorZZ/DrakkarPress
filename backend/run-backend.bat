@echo off
cd /d "%~dp0"

echo ==========================================
echo  IMPORTANTE:
echo    - Este script se ejecuta desde PowerShell o CMD,
echo      donde el prompt se ve como:  PS C:\...>  o  C:\...>
echo    - Si ves el prompt:  >>>  estas dentro de PYTHON,
echo      ahi NO debes ejecutar comandos como cd, mvn, java,
echo      Invoke-RestMethod, docker-compose, etc.
echo    - Para salir de >>> escribe:  exit()  y pulsa Enter.
echo    - Si este script arranca sin errores, el backend Java
echo      esta listo para que el agente lo use en http://127.0.0.1:8080
echo ==========================================

echo ==========================================
echo  Iniciando backend JAVA (Spring Boot)
echo  Puerto esperado: 8080
echo  NOTA: Esto NO arranca la API Python en el puerto 5000.
echo  Para la API Python usa en PowerShell:
echo     cd "C:\Users\SuperUsuario\genrador de perfiles redes sociales"
echo     .\.venv\Scripts\Activate.ps1
echo     python -u .\app_gestor_contenido.py
echo ==========================================

REM Comprueba Java (21+ requerido)
for /f "tokens=2 delims=\"" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%i
IF NOT DEFINED JAVA_VERSION (
    echo [ERROR] Java no esta en el PATH o JAVA_HOME no esta bien configurado.
    echo        Instala Temurin 21 y vuelve a intentar.
    pause
    exit /b 1
)
set "JAVA_VERSION=%JAVA_VERSION:"=%"
for /f "tokens=1 delims=." %%j in ("%JAVA_VERSION%") do set JAVA_MAJOR=%%j
IF %JAVA_MAJOR% LSS 21 (
    echo [ERROR] Se detecto Java %JAVA_VERSION%. Configura JAVA_HOME a un JDK 21 o superior.
    pause
    exit /b 1
)

REM Opcional: compilar si no existe el jar
IF NOT EXIST "target\drakkarpress-platform-1.0.0.jar" (
    echo Compilando proyecto Maven...
    mvn clean package -DskipTests
    IF ERRORLEVEL 1 (
        echo [ERROR] Fallo al compilar con Maven.
        pause
        exit /b 1
    )
)

echo Iniciando Spring Boot...
echo Puedes comprobar el estado con (EN POWERSHELL, NO EN >>> DE PYTHON):
echo   Invoke-RestMethod -Uri http://127.0.0.1:8080/actuator/health
echo Si la respuesta contiene "status":"UP", ya puedes usar el agente contra http://127.0.0.1:8080
echo ==========================================
java -jar target\drakkarpress-platform-1.0.0.jar

echo.
echo Spring Boot se ha detenido (Ctrl+C o fin del proceso).
echo Si estaba en "status":"UP" cuando abriste el agente, el backend Java estaba funcionando correctamente.
echo.
echo Spring Boot ha terminado. Recuerda:
echo  - Los comandos con 'Invoke-RestMethod', 'docker-compose', 'mvn', etc.
echo    se ejecutan en PowerShell, nunca dentro del prompt '>>>'
echo  - Si ves '>>>', escribe 'exit()' para volver a PowerShell.
echo.
echo Checklist antes de ir al agente:
echo   [1] Backend Java levantado en 8080  -> health "status":"UP"
echo   [2] (Opcional) API Python levantada en 5000, sin errores de imports
echo   [3] El agente apunta a http://127.0.0.1:8080 (y a 5000 si usa la API Python)

exit /b 0