# Verificacion Rapida del Estado de DrakkarPress

Write-Host "`n==========================================================" -ForegroundColor Cyan
Write-Host "  DrakkarPress - Estado del Sistema" -ForegroundColor Cyan  
Write-Host "==========================================================`n" -ForegroundColor Cyan

# PostgreSQL
Write-Host "[1/4] PostgreSQL:" -ForegroundColor Yellow
$postgres = docker ps --filter "name=drakkarpress-postgres" --format "{{.Status}}"
if ($postgres) {
    Write-Host "      ESTADO: CORRIENDO" -ForegroundColor Green
    Write-Host "      $postgres`n" -ForegroundColor Gray
} else {
    Write-Host "      ESTADO: DETENIDO (Ejecuta: docker-compose up -d)`n" -ForegroundColor Red
}

# Java
Write-Host "[2/4] Java:" -ForegroundColor Yellow
try {
    $java = java -version 2>&1 | Select-Object -First 1
    Write-Host "      $java" -ForegroundColor Gray
    $match = [regex]::Match($java, '"(?<ver>[^\"]+)"')
    if ($match.Success) {
        $numeric = ($match.Groups['ver'].Value -replace '[^0-9\.]').Trim()
        try {
            $version = [version]$numeric
            if ($version.Major -lt 21) {
                Write-Host "      NECESITA ACTUALIZACION a Java 21`n" -ForegroundColor Red
            } else {
                Write-Host "      OK (Java $numeric)`n" -ForegroundColor Green
            }
        } catch {
            Write-Host "      No se pudo interpretar la version de Java. Asegura Temurin 21.`n" -ForegroundColor Yellow
        }
    } else {
        Write-Host "      No se pudo identificar la version. Verifica Java 21.`n" -ForegroundColor Yellow
    }
} catch {
    Write-Host "      NO INSTALADO`n" -ForegroundColor Red
}

# Maven
Write-Host "[3/4] Maven:" -ForegroundColor Yellow
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
try {
    $maven = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "      $maven" -ForegroundColor Gray
    Write-Host "      OK`n" -ForegroundColor Green
    $mavenInstalled = $true
} catch {
    Write-Host "      NO INSTALADO (Instalando...)`n" -ForegroundColor Yellow
    $mavenInstalled = $false
}

# Backend Spring Boot
Write-Host "[4/4] Backend Spring Boot:" -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/health" -UseBasicParsing -TimeoutSec 2 -ErrorAction Stop
    Write-Host "      ESTADO: CORRIENDO" -ForegroundColor Green
    Write-Host "      URL: http://localhost:8080`n" -ForegroundColor Gray
} catch {
    Write-Host "      ESTADO: NO DISPONIBLE`n" -ForegroundColor Yellow
}

Write-Host "==========================================================" -ForegroundColor Cyan

if ($mavenInstalled -and $postgres) {
    Write-Host "`n[INFO] Todo listo! Puedes iniciar con:" -ForegroundColor Green
    Write-Host "       mvn spring-boot:run`n" -ForegroundColor White
} elseif ($postgres) {
    Write-Host "`n[INFO] Esperando instalacion de Maven..." -ForegroundColor Yellow
    Write-Host "       La aplicacion se iniciara automaticamente`n" -ForegroundColor White
} else {
    Write-Host "`n[ACCION] Necesitas:" -ForegroundColor Yellow
    Write-Host "       1. Iniciar PostgreSQL: docker-compose up -d" -ForegroundColor White
    Write-Host "       2. Esperar Maven (instalando...)`n" -ForegroundColor White
}

Write-Host "Presiona cualquier tecla para continuar..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
