# DrakkarPress Desktop - Setup Script
# Este script instala dependencias y prepara la aplicacion

Write-Host "Iniciando configuracion de DrakkarPress Desktop..." -ForegroundColor Cyan
Write-Host ""

# Verificar Node.js
Write-Host "Verificando Node.js..." -ForegroundColor Yellow
try {
    $nodeVersion = node --version
    Write-Host "Node.js instalado: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "Node.js no encontrado. Instalalo desde: https://nodejs.org/" -ForegroundColor Red
    exit 1
}

# Verificar npm
Write-Host "Verificando npm..." -ForegroundColor Yellow
try {
    $npmVersion = npm --version
    Write-Host "npm instalado: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "npm no encontrado" -ForegroundColor Red
    exit 1
}

# Instalar dependencias
Write-Host ""
Write-Host "Instalando dependencias (esto puede tardar varios minutos)..." -ForegroundColor Yellow
npm install
if ($LASTEXITCODE -eq 0) {
    Write-Host "Dependencias instaladas correctamente" -ForegroundColor Green
} else {
    Write-Host "Error instalando dependencias" -ForegroundColor Red
    exit 1
}

# Crear .env si existe .env.example
Write-Host ""
Write-Host "Configurando variables de entorno..." -ForegroundColor Yellow
if (Test-Path ".env.example") {
    if (!(Test-Path ".env")) {
        Copy-Item ".env.example" ".env"
        Write-Host "Archivo .env creado" -ForegroundColor Green
    } else {
        Write-Host "Archivo .env ya existe" -ForegroundColor Gray
    }
} else {
    Write-Host "No hay archivo .env.example, continuando..." -ForegroundColor Gray
}

# Verificar backend
Write-Host ""
Write-Host "Verificando backend..." -ForegroundColor Yellow
try {
    $null = Invoke-WebRequest -Uri "http://localhost:8080/api/health" -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop
    Write-Host "Backend conectado en localhost:8080" -ForegroundColor Green
    $backendRunning = $true
} catch {
    Write-Host "Backend no disponible (se puede usar en modo offline)" -ForegroundColor Yellow
    $backendRunning = $false
}

# Resumen
Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "CONFIGURACION COMPLETADA" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Node.js: $nodeVersion" -ForegroundColor Gray
Write-Host "npm: $npmVersion" -ForegroundColor Gray
if ($backendRunning) {
    Write-Host "Backend: Conectado" -ForegroundColor Green
} else {
    Write-Host "Backend: Offline (modo demo disponible)" -ForegroundColor Yellow
}
Write-Host ""

# Instrucciones finales
Write-Host "PROXIMOS PASOS:" -ForegroundColor White
Write-Host ""
Write-Host "1. Modo Desarrollo:" -ForegroundColor Cyan
Write-Host "   npm run dev" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Compilar para Produccion:" -ForegroundColor Cyan
Write-Host "   npm run build:win" -ForegroundColor Gray
Write-Host ""

if (!$backendRunning) {
    Write-Host "NOTA: Para activar IA real, ejecuta el backend:" -ForegroundColor Yellow
    Write-Host "   cd ..\backend" -ForegroundColor Gray
    Write-Host "   java -jar target\drakkarpress-platform-1.0.0.jar" -ForegroundColor Gray
    Write-Host ""
}

Write-Host "Documentacion completa: README.md" -ForegroundColor White
Write-Host "Guia de lanzamiento BETA: BETA_LAUNCH_GUIDE.md" -ForegroundColor White
Write-Host ""
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "Quieres iniciar la app ahora? (S/N)" -ForegroundColor Yellow
$answer = Read-Host

if ($answer -eq "S" -or $answer -eq "s") {
    Write-Host ""
    Write-Host "Iniciando DrakkarPress Desktop..." -ForegroundColor Cyan
    Write-Host ""
    npm run dev
} else {
    Write-Host ""
    Write-Host "Configuracion lista. Ejecuta 'npm run dev' cuando quieras iniciar." -ForegroundColor Green
    Write-Host ""
}
