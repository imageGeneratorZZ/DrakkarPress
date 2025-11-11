# Monitor de instalacion y auto-inicio de DrakkarPress
# Este script verifica si Maven esta instalado y luego inicia la aplicacion

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  DrakkarPress - Monitor de Instalacion" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

cd "C:\Users\SuperUsuario\DrakkarPress.com\backend"

Write-Host "Esperando a que Maven se instale..." -ForegroundColor Yellow
Write-Host "Verificando cada 15 segundos..." -ForegroundColor Gray
Write-Host ""

$maxAttempts = 40  # 10 minutos maximo
$attempt = 0

while ($attempt -lt $maxAttempts) {
    $attempt++
    Write-Host "[$attempt/$maxAttempts] Verificando Maven..." -ForegroundColor Gray
    
    # Refrescar PATH
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
    
    # Verificar si Maven esta disponible
    $mavenFound = $false
    try {
        $mavenVersion = & mvn -version 2>&1
        if ($LASTEXITCODE -eq 0) {
            $mavenFound = $true
        }
    } catch {
        # Maven aun no esta disponible
    }
    
    if ($mavenFound) {
        Write-Host ""
        Write-Host "[OK] Maven encontrado!" -ForegroundColor Green
        Write-Host $mavenVersion -ForegroundColor White
        Write-Host ""
        break
    }
    
    Start-Sleep -Seconds 15
}

if (-not $mavenFound) {
    Write-Host ""
    Write-Host "[ERROR] Timeout esperando Maven" -ForegroundColor Red
    Write-Host "Por favor, verifica la instalacion manualmente" -ForegroundColor Yellow
    pause
    exit 1
}

# Verificar Java
Write-Host "Verificando Java..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1
Write-Host $javaVersion -ForegroundColor White
Write-Host ""

# Verificar PostgreSQL
Write-Host "Verificando PostgreSQL..." -ForegroundColor Yellow
$postgresRunning = docker ps | Select-String "drakkarpress-postgres"
if ($postgresRunning) {
    Write-Host "[OK] PostgreSQL corriendo" -ForegroundColor Green
} else {
    Write-Host "[WARNING] PostgreSQL no esta corriendo, iniciando..." -ForegroundColor Yellow
    docker-compose up -d
    Start-Sleep -Seconds 5
}
Write-Host ""

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host "  Iniciando Compilacion y Ejecucion" -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "IMPORTANTE: Este proceso tomara 3-5 minutos" -ForegroundColor Yellow
Write-Host "La primera compilacion descarga dependencias de Maven" -ForegroundColor Yellow
Write-Host ""

# Compilar y ejecutar
Write-Host "Ejecutando: mvn clean spring-boot:run" -ForegroundColor Cyan
Write-Host ""

mvn clean spring-boot:run

Write-Host ""
Write-Host "La aplicacion se detuvo. Presiona cualquier tecla para salir..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
