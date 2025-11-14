<#
deploy-local.ps1
Script de despliegue local para DrakkarPress
- crea un `.env` con valores locales si no existe
- ejecuta `docker-compose up -d --build`
- espera al endpoint /actuator/health del backend
#>

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $scriptDir

Write-Host "Working directory: $scriptDir"

# Comprueba que Docker exista
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker no está disponible en el PATH. Instala Docker Desktop y vuelve a intentarlo."
    exit 1
}

# Crear .env con valores por defecto para entorno local si no existe
$envFile = Join-Path $scriptDir ".env"
if (-not (Test-Path $envFile)) {
    @"
DB_NAME=drakkar
DB_USER=drakkar
DB_PASSWORD=drakkar
DOMAIN=localhost
"@ | Out-File -Encoding UTF8 $envFile
    Write-Host ".env creado con valores por defecto (local)."
} else {
    Write-Host ".env existente detectado. Usando variables del archivo .env"
}

Write-Host "Arrancando servicios con docker-compose (puede tardar varios minutos la primera vez)..."
docker-compose -f (Join-Path $scriptDir 'docker-compose.yml') up -d --build

# Esperar health endpoint del backend
$healthUrl = 'http://localhost:8080/actuator/health'
$maxWaitSeconds = 180
$interval = 5
$elapsed = 0

Write-Host "Esperando a que el backend responda en $healthUrl (timeout ${maxWaitSeconds}s)"
while ($elapsed -lt $maxWaitSeconds) {
    try {
        $resp = Invoke-RestMethod -Uri $healthUrl -UseBasicParsing -TimeoutSec 5
        if ($resp.status -eq 'UP') {
            Write-Host "Backend operativo (status UP)."
            break
        }
    } catch {
        # Ignorar errores de conexión temporales
    }
    Start-Sleep -Seconds $interval
    $elapsed += $interval
    Write-Host -NoNewline "."
}

if ($elapsed -ge $maxWaitSeconds) {
    Write-Error "El backend no respondió a tiempo. Revisa los logs: docker logs drakkar_backend"
    Pop-Location
    exit 1
}

Write-Host "Despliegue local completado. Revisa http://localhost (nginx) o http://localhost:8080 (backend)."

Pop-Location

exit 0
