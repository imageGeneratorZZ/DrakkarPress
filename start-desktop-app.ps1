# Iniciar DrakkarPress Desktop App con Backend Python
# PowerShell Script

Write-Host "🚀 Iniciando DrakkarPress Desktop..." -ForegroundColor Cyan
Write-Host ""

# Verificar Python
Write-Host "🐍 Verificando Python..." -ForegroundColor Yellow
$pythonVersion = python --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Python instalado: $pythonVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Python no encontrado. Instala Python 3.8+" -ForegroundColor Red
    exit 1
}

# Verificar Node.js
Write-Host "📦 Verificando Node.js..." -ForegroundColor Yellow
$nodeVersion = node --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Node.js instalado: $nodeVersion" -ForegroundColor Green
} else {
    Write-Host "❌ Node.js no encontrado. Instala Node.js 18+" -ForegroundColor Red
    exit 1
}

# Navegar al directorio raíz
$rootDir = Split-Path -Parent $PSScriptRoot
Set-Location $rootDir

# Instalar dependencias Python si no existen
Write-Host ""
Write-Host "📦 Verificando dependencias Python..." -ForegroundColor Yellow
if (-Not (Test-Path "backend_python\venv")) {
    Write-Host "Creando entorno virtual Python..." -ForegroundColor Yellow
    python -m venv backend_python\venv
}

# Activar entorno virtual e instalar dependencias
Write-Host "Instalando dependencias Python..." -ForegroundColor Yellow
& "backend_python\venv\Scripts\python.exe" -m pip install -q --upgrade pip
& "backend_python\venv\Scripts\python.exe" -m pip install -q -r backend_python\requirements.txt
Write-Host "✅ Dependencias Python instaladas" -ForegroundColor Green

# Verificar dependencias Electron
Write-Host ""
Write-Host "📦 Verificando dependencias Electron..." -ForegroundColor Yellow
Set-Location desktop-app
if (-Not (Test-Path "node_modules")) {
    Write-Host "Instalando dependencias Node.js..." -ForegroundColor Yellow
    npm install
}
Write-Host "✅ Dependencias Electron instaladas" -ForegroundColor Green

# Compilar TypeScript
Write-Host ""
Write-Host "🔨 Compilando TypeScript..." -ForegroundColor Yellow
npm run build:main
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    exit 1
}

# Volver al directorio raíz
Set-Location $rootDir

# Iniciar backend Python en segundo plano
Write-Host ""
Write-Host "🐍 Iniciando backend Python..." -ForegroundColor Cyan
$env:GROQ_API_KEY = $env:GROQ_API_KEY
$env:BACKEND_PORT = "5000"

$pythonProcess = Start-Process -FilePath "backend_python\venv\Scripts\python.exe" `
    -ArgumentList "backend_python\server.py" `
    -PassThru `
    -NoNewWindow `
    -RedirectStandardOutput "backend_python\output.log" `
    -RedirectStandardError "backend_python\error.log"

Write-Host "✅ Backend Python iniciado (PID: $($pythonProcess.Id))" -ForegroundColor Green
Write-Host "📋 Logs: backend_python\output.log" -ForegroundColor Gray

# Esperar a que el backend esté listo
Write-Host ""
Write-Host "⏳ Esperando a que el backend esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 3

# Iniciar Electron
Write-Host ""
Write-Host "⚡ Iniciando Electron..." -ForegroundColor Cyan
Set-Location desktop-app

# Ejecutar Electron (esto bloqueará hasta que se cierre la app)
npm run dev:main

# Limpieza al cerrar
Set-Location $rootDir
Write-Host ""
Write-Host "🛑 Cerrando aplicación..." -ForegroundColor Yellow

if ($pythonProcess -and -Not $pythonProcess.HasExited) {
    Write-Host "Deteniendo backend Python..." -ForegroundColor Yellow
    Stop-Process -Id $pythonProcess.Id -Force
    Write-Host "✅ Backend Python detenido" -ForegroundColor Green
}

Write-Host ""
Write-Host "👋 DrakkarPress cerrado correctamente" -ForegroundColor Cyan
