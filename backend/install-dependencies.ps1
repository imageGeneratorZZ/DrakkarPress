# Script para instalar Maven y Java 17 en DrakkarPress
# DEBE EJECUTARSE COMO ADMINISTRADOR

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Instalador de Dependencias para DrakkarPress Backend" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar si se está ejecutando como administrador
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    Write-Host "[ERROR] Este script debe ejecutarse como ADMINISTRADOR" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor:" -ForegroundColor Yellow
    Write-Host "1. Cierra esta ventana" -ForegroundColor White
    Write-Host "2. Click derecho en PowerShell" -ForegroundColor White
    Write-Host "3. Selecciona 'Ejecutar como administrador'" -ForegroundColor White
    Write-Host "4. Navega a: cd 'C:\Users\SuperUsuario\DrakkarPress.com\backend'" -ForegroundColor White
    Write-Host "5. Ejecuta: .\install-dependencies.ps1" -ForegroundColor White
    Write-Host ""
    pause
    exit 1
}

Write-Host "[OK] Ejecutando como Administrador" -ForegroundColor Green
Write-Host ""

# Instalar Chocolatey
Write-Host "Paso 1: Instalando Chocolatey..." -ForegroundColor Yellow
try {
    Set-ExecutionPolicy Bypass -Scope Process -Force
    [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
    iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    Write-Host "[OK] Chocolatey instalado correctamente" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Fallo al instalar Chocolatey: $_" -ForegroundColor Red
    pause
    exit 1
}

Write-Host ""

# Refrescar variables de entorno y agregar Chocolatey al PATH actual
$env:ChocolateyInstall = Convert-Path "$((Get-Command choco -ErrorAction SilentlyContinue).Path)\..\.."
Import-Module "$env:ChocolateyInstall\helpers\chocolateyProfile.psm1" -ErrorAction SilentlyContinue
refreshenv
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
$env:Path += ";C:\ProgramData\chocolatey\bin"

# Instalar Java 17
Write-Host "Paso 2: Instalando Java 17 (Temurin)..." -ForegroundColor Yellow
try {
    & C:\ProgramData\chocolatey\bin\choco.exe install temurin17 -y
    Write-Host "[OK] Java 17 instalado correctamente" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Fallo al instalar Java 17: $_" -ForegroundColor Red
}

Write-Host ""

# Instalar Maven
Write-Host "Paso 3: Instalando Apache Maven..." -ForegroundColor Yellow
try {
    & C:\ProgramData\chocolatey\bin\choco.exe install maven -y
    Write-Host "[OK] Maven instalado correctamente" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] Fallo al instalar Maven: $_" -ForegroundColor Red
}

Write-Host ""

# Refrescar variables de entorno nuevamente
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Instalacion Completada" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Verificando instalaciones..." -ForegroundColor Yellow
Write-Host ""

# Verificar Java
Write-Host "Java:" -ForegroundColor Cyan
try {
    $javaVersion = java -version 2>&1
    Write-Host $javaVersion -ForegroundColor White
} catch {
    Write-Host "[WARNING] Java no se encuentra en PATH todavia" -ForegroundColor Yellow
}

Write-Host ""

# Verificar Maven
Write-Host "Maven:" -ForegroundColor Cyan
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host $mavenVersion -ForegroundColor White
} catch {
    Write-Host "[WARNING] Maven no se encuentra en PATH todavia" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "IMPORTANTE: Reinicia PowerShell para aplicar los cambios" -ForegroundColor Yellow
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Proximos pasos:" -ForegroundColor Cyan
Write-Host "1. Cierra TODAS las ventanas de PowerShell" -ForegroundColor White
Write-Host "2. Abre PowerShell nuevamente (NO necesitas ser admin)" -ForegroundColor White
Write-Host "3. Navega a: cd 'C:\Users\SuperUsuario\DrakkarPress.com\backend'" -ForegroundColor White
Write-Host "4. Verifica: java -version (debe mostrar version 17)" -ForegroundColor White
Write-Host "5. Verifica: mvn -version (debe mostrar version 3.x)" -ForegroundColor White
Write-Host "6. Ejecuta: mvn spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "Presiona cualquier tecla para cerrar..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
