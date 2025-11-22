<#
.SYNOPSIS
    Script de inicio rapido para DrakkarPress

.DESCRIPTION
    Arranca el backend y abre el navegador con la aplicacion
#>

param(
    [switch]$SkipBrowser
)

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   DrakkarPress - Inicio Rapido       " -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 1. Verificar PostgreSQL
Write-Host "[1/4] Verificando PostgreSQL..." -ForegroundColor Yellow
$dbRunning = docker ps --filter "name=drakkarpress-db" --format "{{.Names}}" 2>$null
if (-not $dbRunning) {
    Write-Host "  -> Iniciando PostgreSQL..." -ForegroundColor Gray
    docker start drakkarpress-db 2>$null | Out-Null
    Start-Sleep -Seconds 3
    Write-Host "  [OK] PostgreSQL iniciado" -ForegroundColor Green
} else {
    Write-Host "  [OK] PostgreSQL ya esta corriendo" -ForegroundColor Green
}

# 2. Verificar Backend
Write-Host "`n[2/4] Verificando Backend Java..." -ForegroundColor Yellow
$backendRunning = Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object {
    (netstat -ano | Select-String "12000.*LISTENING.*$($_.Id)")
}

if (-not $backendRunning) {
    Write-Host "  -> Iniciando Backend en puerto 12000..." -ForegroundColor Gray
    Set-Location "$PSScriptRoot"
    
    $javaPath = ".\backend\.java\jdk21\jdk-21.0.9+10\bin\java.exe"
    $jarPath = ".\backend\target\drakkarpress-platform-1.0.0.jar"
    
    if (-not (Test-Path $jarPath)) {
        Write-Host "  [!] JAR no encontrado. Compilando..." -ForegroundColor Yellow
        Set-Location backend
        $env:JAVA_HOME = "$PSScriptRoot\backend\.java\jdk21\jdk-21.0.9+10"
        .\mvnw.cmd clean package -DskipTests | Out-Null
        Set-Location ..
    }
    
    Start-Process -NoNewWindow -FilePath $javaPath `
        -ArgumentList "-jar", $jarPath, "--server.port=12000" `
        -RedirectStandardOutput ".\backend\app.log" `
        -RedirectStandardError ".\backend\app-error.log" `
        -WorkingDirectory ".\backend"
    
    Write-Host "  -> Esperando que el backend inicie..." -ForegroundColor Gray
    Start-Sleep -Seconds 10
    Write-Host "  [OK] Backend iniciado" -ForegroundColor Green
} else {
    Write-Host "  [OK] Backend ya esta corriendo (PID: $($backendRunning.Id))" -ForegroundColor Green
}

# 3. Verificar archivos frontend
Write-Host "`n[3/4] Verificando Frontend..." -ForegroundColor Yellow
$frontendFiles = @("index.html", "login.html", "js\api-client.js")
$allPresent = $true

foreach ($file in $frontendFiles) {
    if (Test-Path $file) {
        Write-Host "  [OK] $file" -ForegroundColor Green
    } else {
        Write-Host "  [X] $file NO ENCONTRADO" -ForegroundColor Red
        $allPresent = $false
    }
}

# 4. Abrir navegador
if (-not $SkipBrowser -and $allPresent) {
    Write-Host "`n[4/4] Abriendo navegador..." -ForegroundColor Yellow
    
    # Intentar detectar Live Server
    $liveServerUrl = "http://localhost:5500/index.html"
    $fileUrl = "file:///$((Get-Location).Path.Replace('\','/').Replace(' ','%20'))/index.html"
    
    Start-Sleep -Seconds 1
    Start-Process $liveServerUrl
    
    Write-Host "  [OK] Navegador abierto" -ForegroundColor Green
    Write-Host "`n  [i] Si no carga, usa Live Server en VS Code" -ForegroundColor Yellow
    Write-Host "     o abre manualmente: $fileUrl" -ForegroundColor Gray
}

# Resumen
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "        TODO LISTO                      " -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "URLs:" -ForegroundColor White
Write-Host "   Backend API:  http://localhost:12000" -ForegroundColor Cyan
Write-Host "   Frontend:     http://localhost:5500" -ForegroundColor Cyan
Write-Host "`nPara detener todo:" -ForegroundColor White
Write-Host "   .\backend\manage-backend.ps1 -Action stop" -ForegroundColor Gray
Write-Host "   docker stop drakkarpress-db`n" -ForegroundColor Gray

# Mantener abierto si hay error
if (-not $allPresent) {
    Write-Host "`n[!] Hay archivos faltantes. Presiona Enter para salir..." -ForegroundColor Yellow
    Read-Host
}
