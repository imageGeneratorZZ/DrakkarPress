<#
Script: run-backend-local.ps1
Objetivo: Normalizar JAVA_HOME y PATH a JDK 21 y arrancar el backend sin usar wrapper (usa Maven instalado o jar directo).
Uso rápido:
  powershell -ExecutionPolicy Bypass -File .\run-backend-local.ps1 -Mode build-run
Parámetros:
  -Mode build-run | run-jar | build-only
#>
param(
  [ValidateSet('build-run','run-jar','build-only')]
  [string]$Mode = 'build-run'
)

function Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Err($m){ Write-Host "[ERROR] $m" -ForegroundColor Red }

$ErrorActionPreference = 'Stop'

# Detect JDK folder inside .java/jdk21
$jdkFolder = Get-ChildItem -Directory .\.java\jdk21 | Where-Object { $_.Name -match '^jdk' } | Select-Object -First 1
if(-not $jdkFolder){ Err "No se encontró JDK en .java\jdk21 (ejecuta jdk-setup.ps1 primero)"; exit 1 }
$env:JAVA_HOME = $jdkFolder.FullName

# Construct minimal PATH (avoid jre, duplicados)
$basePaths = @(
  "$env:JAVA_HOME\bin",
  'C:\tools\apache-maven-3.9.9\bin',
  'C:\WINDOWS\system32',
  'C:\WINDOWS',
  'C:\WINDOWS\System32\Wbem',
  'C:\WINDOWS\System32\WindowsPowerShell\v1.0',
  'C:\WINDOWS\System32\OpenSSH',
  'C:\Program Files\Git\cmd'
)
$validPaths = $basePaths | Where-Object { (Test-Path $_) -or ($_ -match 'WINDOWS') }
$env:Path = ($validPaths -join ';')

Info "JAVA_HOME=$env:JAVA_HOME"
try {
  $versionOut = & "$env:JAVA_HOME\bin\java.exe" -version 2>&1
  $versionOut | ForEach-Object { Write-Host $_ }
} catch {
  Warn "java -version produjo excepción (probablemente stderr); continuando..."
}

# Verify Maven (prefer external install)
$hasMvn = (Get-Command mvn -ErrorAction SilentlyContinue) -ne $null
if($hasMvn){
  Info "Usando Maven instalado externo."
  mvn -version
} else {
  Warn "Maven no encontrado en PATH. Intentando wrapper mvnw.cmd."
  if(!(Test-Path .\mvnw.cmd)){ Err "No existe mvnw.cmd y tampoco mvn instalado"; exit 1 }
}

if($Mode -in @('build-run','build-only')){
  Info "Compilando proyecto (skip tests)..."
  if($hasMvn){
    mvn clean package -DskipTests
  } else {
    .\mvnw.cmd clean package -DskipTests
  }
  if($LASTEXITCODE -ne 0){ Err "Fallo compilación"; exit 1 }
  Info "Build OK"
}

$jar = Get-ChildItem -Path .\target -Filter '*platform*1.0.0*.jar' | Where-Object { $_.Name -notmatch 'original' } | Select-Object -First 1
if(-not $jar){
  if($Mode -eq 'run-jar') { Err "No se encontró JAR. Ejecuta con -Mode build-run primero."; exit 1 }
  Warn "JAR no encontrado aún (tal vez build falló)." 
} else {
  Info "Jar detectado: $($jar.Name)"
}

if($Mode -in @('build-run','run-jar') -and $jar){
  Info "Arrancando backend (Ctrl+C para detener)..."
  & "$env:JAVA_HOME\bin\java.exe" -jar $jar.FullName --server.port=12000
}
