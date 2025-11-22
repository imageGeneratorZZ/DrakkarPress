# Arranque local sin Docker: backend (H2) + frontend estático
$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $MyInvocation.MyCommand.Definition
Push-Location $root
Write-Host "Working dir: $root"

# 0) Pre-chequeos Java y Maven
Write-Host 'Verificando Java y Maven...' -ForegroundColor Gray
$javaOk = $true
try { & java -version 2>$null } catch { $javaOk = $false }
if (-not $javaOk) {
    $candidateJava = 'C:\Users\SuperUsuario\Java\jdk-21.0.5+11\bin\java.exe'
    if (Test-Path $candidateJava) {
        $env:JAVA_HOME = Split-Path -Parent (Split-Path -Parent $candidateJava)
        $env:Path = "$($env:JAVA_HOME)\bin;$env:Path"
        Write-Host "JAVA_HOME establecido temporalmente en $env:JAVA_HOME" -ForegroundColor Yellow
    } else {
        Write-Host "No se encontró Java en PATH ni en $candidateJava" -ForegroundColor Red
        Write-Host "Instala JDK 21 o ejecuta 'setup-completo.ps1' y reintenta." -ForegroundColor Red
    }
}

$mvnPath = 'C:\Users\SuperUsuario\apache-maven-3.9.6\bin\mvn.cmd'
if (-not (Test-Path $mvnPath)) {
    Write-Warning "Maven no encontrado en $mvnPath. Intentaré con 'mvn' global."
    $mvnPath = 'mvn'
}

# 1) Backend - build
$backend = Join-Path $root 'backend'
if (-not (Test-Path (Join-Path $backend 'pom.xml'))) {
    Write-Error 'No se encontró backend/pom.xml. Abortando.'
    Pop-Location; exit 1
}

Write-Host 'Compilando backend (Maven -DskipTests)...' -ForegroundColor Yellow
Push-Location $backend
& $mvnPath -q -version
& $mvnPath clean package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Error "Falló la compilación del backend ($LASTEXITCODE)"
    Pop-Location; Pop-Location; exit $LASTEXITCODE
}

# 2) Localizar JAR
$jar = Get-ChildItem -Path 'target' -Filter '*.jar' -Exclude '*-sources.jar','*-javadoc.jar' | Select-Object -First 1
if (-not $jar) {
    Write-Error 'No se encontró el JAR en backend/target. Abortando.'
    Pop-Location; Pop-Location; exit 1
}
Write-Host ("JAR: {0}" -f $jar.Name)

# 3) Lanzar backend con perfil h2 en nueva ventana
Write-Host 'Iniciando backend (perfil h2)...' -ForegroundColor Yellow
$cmd = "cd `"$backend`"; java -jar `"$($jar.FullName)`" --spring.profiles.active=h2"
Start-Process powershell -ArgumentList '-NoExit','-Command', $cmd | Out-Null
Pop-Location

# 4) Frontend: abrir index.html del root
$index = Join-Path $root 'index.html'
if (Test-Path $index) {
    Write-Host 'Abriendo frontend (index.html)...' -ForegroundColor Yellow
    Start-Process $index | Out-Null
} else {
    Write-Warning 'No se encontró index.html en la raíz. Abre tus archivos HTML manualmente.'
}

# 5) Mostrar URLs útiles
Write-Host ''
Write-Host '==========================' -ForegroundColor Cyan
Write-Host 'Local sin Docker listo' -ForegroundColor Green
Write-Host 'Backend:  http://localhost:8080/actuator/health'
Write-Host 'API:      http://localhost:8080/api/health'
Write-Host 'H2:       http://localhost:8080/h2-console (JDBC: jdbc:h2:mem:drakkar)'
Write-Host 'Frontend: index.html abierto en navegador' 
Write-Host '==========================' -ForegroundColor Cyan

Pop-Location
exit 0
