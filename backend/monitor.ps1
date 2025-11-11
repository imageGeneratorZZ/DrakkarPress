# Monitor en tiempo real de DrakkarPress

$host.UI.RawUI.WindowTitle = "DrakkarPress - Monitor"

function Show-Status {
    Clear-Host
    Write-Host "╔══════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
    Write-Host "║         DRAKKARPRESS - MONITOR EN TIEMPO REAL               ║" -ForegroundColor Cyan
    Write-Host "╚══════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
    Write-Host ""
    
    $timestamp = Get-Date -Format "HH:mm:ss"
    Write-Host "  Ultima actualizacion: $timestamp" -ForegroundColor Gray
    Write-Host ""
    
    # PostgreSQL
    $postgres = docker ps --filter "name=drakkarpress-postgres" --format "{{.Status}}" 2>$null
    if ($postgres) {
        Write-Host "  ✓ PostgreSQL:     " -NoNewline -ForegroundColor Green
        Write-Host "CORRIENDO" -ForegroundColor White
    } else {
        Write-Host "  ✗ PostgreSQL:     " -NoNewline -ForegroundColor Red
        Write-Host "DETENIDO" -ForegroundColor White
    }
    
    # Java Local
    $javaPath = "C:\Users\SuperUsuario\DrakkarPress.com\backend\.java\jdk-17.0.10+7\bin\java.exe"
    if (Test-Path $javaPath) {
        Write-Host "  ✓ Java 17 Local:  " -NoNewline -ForegroundColor Green
        Write-Host "DISPONIBLE" -ForegroundColor White
    } else {
        Write-Host "  ✗ Java 17 Local:  " -NoNewline -ForegroundColor Red
        Write-Host "NO ENCONTRADO" -ForegroundColor White
    }
    
    # Maven Wrapper
    $mvnwPath = "C:\Users\SuperUsuario\DrakkarPress.com\backend\mvnw.cmd"
    if (Test-Path $mvnwPath) {
        Write-Host "  ✓ Maven Wrapper:  " -NoNewline -ForegroundColor Green
        Write-Host "DISPONIBLE" -ForegroundColor White
    } else {
        Write-Host "  ✗ Maven Wrapper:  " -NoNewline -ForegroundColor Red
        Write-Host "NO ENCONTRADO" -ForegroundColor White
    }
    
    # Backend
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/api/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
        Write-Host "  ✓ Backend API:    " -NoNewline -ForegroundColor Green
        Write-Host "CORRIENDO" -ForegroundColor White
        Write-Host ""
        Write-Host "  " -NoNewline
        Write-Host "→ http://localhost:8080/api/health" -ForegroundColor Cyan
        $backendUp = $true
    } catch {
        Write-Host "  ⏳ Backend API:    " -NoNewline -ForegroundColor Yellow
        Write-Host "COMPILANDO/INICIANDO..." -ForegroundColor White
        $backendUp = $false
    }
    
    Write-Host ""
    Write-Host "──────────────────────────────────────────────────────────────" -ForegroundColor Gray
    
    if ($backendUp) {
        Write-Host ""
        Write-Host "  🎉 ¡APLICACION LISTA!" -ForegroundColor Green
        Write-Host ""
        Write-Host "  Endpoints disponibles:" -ForegroundColor Cyan
        Write-Host "  • http://localhost:8080/api" -ForegroundColor White
        Write-Host "  • http://localhost:8080/api/health" -ForegroundColor White
        Write-Host "  • http://localhost:8080/api/health/db" -ForegroundColor White
        Write-Host "  • http://localhost:8080/actuator/health" -ForegroundColor White
        Write-Host ""
        Write-Host "  Presiona Ctrl+C para salir" -ForegroundColor Gray
        return $true
    } else {
        Write-Host ""
        Write-Host "  Por favor espera..." -ForegroundColor Yellow
        Write-Host "  La primera compilacion toma 2-5 minutos" -ForegroundColor Gray
        Write-Host "  Maven esta descargando dependencias..." -ForegroundColor Gray
        Write-Host ""
        Write-Host "  Presiona Ctrl+C para salir del monitor" -ForegroundColor Gray
        return $false
    }
}

Write-Host "Iniciando monitor..." -ForegroundColor Cyan
Write-Host "Verificando cada 10 segundos..." -ForegroundColor Gray
Write-Host ""
Start-Sleep -Seconds 2

while ($true) {
    $ready = Show-Status
    
    if ($ready) {
        # Backend esta corriendo, verificar cada 30 segundos
        Start-Sleep -Seconds 30
    } else {
        # Backend no esta listo, verificar cada 10 segundos
        Start-Sleep -Seconds 10
    }
}
