<#
.SYNOPSIS
    Script para gestionar el backend de DrakkarPress (arrancar, detener, reiniciar, ver estado)

.DESCRIPTION
    Este script permite controlar el servidor backend de DrakkarPress de forma sencilla.
    Usa Java 21 para ejecutar el JAR compilado en el puerto 12000.

.PARAMETER Action
    Acción a realizar: start, stop, restart, status, logs

.EXAMPLE
    .\manage-backend.ps1 -Action start
    .\manage-backend.ps1 -Action stop
    .\manage-backend.ps1 -Action status
    .\manage-backend.ps1 -Action logs
#>

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet("start", "stop", "restart", "status", "logs", "tail")]
    [string]$Action = "status"
)

# Configuración
$BackendDir = $PSScriptRoot
$JarFile = Join-Path $BackendDir "target\drakkarpress-platform-1.0.0.jar"
$JavaExe = Join-Path $BackendDir ".java\jdk21\jdk-21.0.9+10\bin\java.exe"
$Port = 12000
$LogFile = Join-Path $BackendDir "app.log"
$ErrorLogFile = Join-Path $BackendDir "app-error.log"
$PidFile = Join-Path $BackendDir ".backend.pid"

# Colores
function Write-Success { param($msg) Write-Host $msg -ForegroundColor Green }
function Write-Info { param($msg) Write-Host $msg -ForegroundColor Cyan }
function Write-Warning { param($msg) Write-Host $msg -ForegroundColor Yellow }
function Write-Error { param($msg) Write-Host $msg -ForegroundColor Red }

# Función para obtener el proceso del backend
function Get-BackendProcess {
    $processes = Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object {
        $_.Path -like "*java.exe" -and 
        (netstat -ano | Select-String "$Port.*LISTENING.*$($_.Id)")
    }
    return $processes
}

# Función para verificar si el puerto está en uso
function Test-PortInUse {
    param([int]$Port)
    $connection = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue
    return $null -ne $connection
}

# Función para mostrar el estado
function Show-Status {
    Write-Info "`n=== Estado del Backend DrakkarPress ==="
    
    $process = Get-BackendProcess
    
    if ($process) {
        Write-Success "✓ Backend está CORRIENDO"
        Write-Info "  PID: $($process.Id)"
        Write-Info "  Puerto: $Port"
        Write-Info "  Memoria: $([math]::Round($process.WorkingSet64/1MB, 2)) MB"
        Write-Info "  Inicio: $($process.StartTime)"
        
        # Verificar conectividad
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port/actuator/health" -Method GET -TimeoutSec 2 -UseBasicParsing -ErrorAction SilentlyContinue 2>$null
            if ($response.StatusCode -eq 403) {
                Write-Success "  API responde (protegido por seguridad)"
            }
        } catch {
            Write-Info "  API: Verificando..."
        }
        
        return $true
    } else {
        Write-Warning "✗ Backend está DETENIDO"
        
        # Verificar si el puerto está ocupado por otro proceso
        if (Test-PortInUse -Port $Port) {
            $portProcess = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Select-Object -First 1
            Write-Warning "  El puerto $Port está siendo usado por PID: $($portProcess.OwningProcess)"
        }
        
        return $false
    }
}

# Función para iniciar el backend
function Start-Backend {
    Write-Info "`n=== Iniciando Backend DrakkarPress ==="
    
    # Verificar si ya está corriendo
    if (Get-BackendProcess) {
        Write-Warning "El backend ya está corriendo."
        Show-Status
        return
    }
    
    # Verificar que exista el JAR
    if (-not (Test-Path $JarFile)) {
        Write-Error "Error: No se encuentra el archivo JAR en: $JarFile"
        Write-Info "Ejecuta primero: .\mvnw.cmd clean package -DskipTests"
        return
    }
    
    # Verificar que exista Java 21
    if (-not (Test-Path $JavaExe)) {
        Write-Error "Error: No se encuentra Java 21 en: $JavaExe"
        return
    }
    
    # Verificar PostgreSQL
    $dbRunning = docker ps --filter "name=drakkarpress-db" --format "{{.Names}}" 2>$null
    if (-not $dbRunning) {
        Write-Warning "PostgreSQL no está corriendo. Iniciando..."
        docker start drakkarpress-db 2>$null
        if ($LASTEXITCODE -ne 0) {
            Write-Error "Error iniciando PostgreSQL. Ejecuta: docker-compose up -d"
            return
        }
        Start-Sleep -Seconds 3
    }
    
    Write-Info "Iniciando servidor en puerto $Port..."
    
    # Iniciar el proceso
    $process = Start-Process -NoNewWindow -PassThru `
        -FilePath $JavaExe `
        -ArgumentList "-jar", $JarFile, "--server.port=$Port" `
        -RedirectStandardOutput $LogFile `
        -RedirectStandardError $ErrorLogFile `
        -WorkingDirectory $BackendDir
    
    # Guardar PID
    $process.Id | Out-File $PidFile -Encoding ASCII
    
    Write-Info "Esperando que el servidor inicie..."
    Start-Sleep -Seconds 5
    
    # Verificar que arrancó
    $maxAttempts = 6
    $attempt = 0
    $started = $false
    
    while ($attempt -lt $maxAttempts -and -not $started) {
        $attempt++
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:$Port/" -Method GET -TimeoutSec 2 -UseBasicParsing -ErrorAction Stop 2>$null
            $started = $true
        } catch {
            if ($_.Exception.Message -like "*403*" -or $_.Exception.Message -like "*Prohibido*") {
                $started = $true
            } else {
                Start-Sleep -Seconds 2
            }
        }
    }
    
    Write-Info ""
    if (Get-BackendProcess) {
        Write-Success "✓ Backend iniciado correctamente!"
        Write-Info "  URL: http://localhost:$Port"
        Write-Info "  Logs: $LogFile"
        Write-Info "`nPara ver los logs en tiempo real: .\manage-backend.ps1 -Action tail"
    } else {
        Write-Error "✗ Error al iniciar el backend."
        Write-Info "Revisa los logs en: $ErrorLogFile"
    }
}

# Función para detener el backend
function Stop-Backend {
    Write-Info "`n=== Deteniendo Backend DrakkarPress ==="
    
    $process = Get-BackendProcess
    
    if (-not $process) {
        Write-Warning "El backend no está corriendo."
        return
    }
    
    Write-Info "Deteniendo proceso PID: $($process.Id)..."
    Stop-Process -Id $process.Id -Force
    
    # Esperar a que se detenga
    Start-Sleep -Seconds 2
    
    # Verificar
    if (-not (Get-BackendProcess)) {
        Write-Success "✓ Backend detenido correctamente."
        
        # Eliminar PID file
        if (Test-Path $PidFile) {
            Remove-Item $PidFile -Force
        }
    } else {
        Write-Error "✗ Error al detener el backend."
    }
}

# Función para reiniciar el backend
function Restart-Backend {
    Write-Info "`n=== Reiniciando Backend DrakkarPress ==="
    Stop-Backend
    Start-Sleep -Seconds 2
    Start-Backend
}

# Función para mostrar logs
function Show-Logs {
    if (-not (Test-Path $LogFile)) {
        Write-Warning "No hay archivo de logs todavía."
        return
    }
    
    Write-Info "`n=== Últimas 50 líneas del log ==="
    Get-Content $LogFile -Tail 50
}

# Función para seguir logs en tiempo real
function Tail-Logs {
    if (-not (Test-Path $LogFile)) {
        Write-Warning "No hay archivo de logs todavía."
        return
    }
    
    Write-Info "`n=== Siguiendo logs en tiempo real (Ctrl+C para salir) ==="
    Get-Content $LogFile -Wait -Tail 20
}

# Ejecutar acción
switch ($Action) {
    "start"   { Start-Backend }
    "stop"    { Stop-Backend }
    "restart" { Restart-Backend }
    "status"  { Show-Status }
    "logs"    { Show-Logs }
    "tail"    { Tail-Logs }
}

Write-Host ""
