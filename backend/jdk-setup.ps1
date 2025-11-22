<#
Script: jdk-setup.ps1
Objetivo: Descargar y configurar JDK 21 solo para esta sesión sin privilegios admin.
Ubicación destino: .java\jdk21 (dentro de backend) y se ajusta PATH temporalmente.
Uso: powershell -ExecutionPolicy Bypass -File .\jdk-setup.ps1
#>

$ErrorActionPreference = 'Stop'
function Info($m){ Write-Host "[INFO] $m" -ForegroundColor Cyan }
function Warn($m){ Write-Host "[WARN] $m" -ForegroundColor Yellow }
function Err($m){ Write-Host "[ERROR] $m" -ForegroundColor Red }

$destRoot = Join-Path $PSScriptRoot '.java'
if(!(Test-Path $destRoot)){ New-Item -ItemType Directory -Path $destRoot | Out-Null }
$jdkTarget = Join-Path $destRoot 'jdk21'
if(Test-Path $jdkTarget){ Warn "Eliminando instalación previa jdk21"; Remove-Item $jdkTarget -Recurse -Force }
New-Item -ItemType Directory -Path $jdkTarget | Out-Null

$zipPath = Join-Path $env:TEMP 'temurin21.zip'

# Intento dinámico vía GitHub API
$zipUrl = $null
try {
  Info "Consultando API GitHub Temurin 21..."
  $release = Invoke-RestMethod -Uri 'https://api.github.com/repos/adoptium/temurin21-binaries/releases/latest' -Headers @{ 'User-Agent' = 'PowerShell-jdk-setup' }
  $allAssets = $release.assets | Where-Object { $_.name -match 'OpenJDK21U-jdk.*windows_hotspot.*\.zip$' }
  if(-not $allAssets){ Warn "No se encontraron assets Windows en release" }
  else {
    $arch = $env:PROCESSOR_ARCHITECTURE
    if($arch -match 'AMD64'){
      $asset = $allAssets | Where-Object { $_.name -match 'jdk_x64_windows_hotspot' } | Select-Object -First 1
    } elseif($arch -match 'ARM64') {
      $asset = $allAssets | Where-Object { $_.name -match 'jdk_aarch64_windows_hotspot' } | Select-Object -First 1
    }
    if(-not $asset){
      # Fallback a x64 explícito primero
      $asset = $allAssets | Where-Object { $_.name -match 'jdk_x64_windows_hotspot' } | Select-Object -First 1
    }
    if($asset){
      $zipUrl = $asset.browser_download_url
      Info "Asset seleccionado ($arch): $($asset.name)"
    } else { Warn "No se pudo seleccionar asset por arquitectura" }
  }
} catch { Warn "Fallo consulta GitHub API: $($_.Exception.Message)" }

if(-not $zipUrl){
  Warn "Usando URLs fallback..."
  $fallbacks = @(
    'https://download.bell-sw.com/java/21+37/bellsoft-jdk21+37-windows-amd64.zip',
    'https://aka.ms/download-jdk/microsoft-jdk-21.0.2-windows-x64.zip'
  )
  foreach($u in $fallbacks){
    Info "Probando fallback: $u"
    try {
      Invoke-WebRequest -Uri $u -OutFile $zipPath -UseBasicParsing
      $zipUrl = $u
      break
    } catch { Warn "Fallback falló: $u -> $($_.Exception.Message)" }
  }
  if(-not $zipUrl){ Err "No se pudo descargar ningún ZIP de JDK 21"; exit 1 }
} else {
  Info "Descargando JDK 21 ZIP desde GitHub: $zipUrl"
  Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath -UseBasicParsing
}
Info "Extrayendo..."
Expand-Archive -Path $zipPath -DestinationPath $jdkTarget -Force

# Buscar carpeta interna jdk* dentro de jdkTarget
$inner = Get-ChildItem $jdkTarget | Where-Object { $_.PSIsContainer -and $_.Name -match '^jdk' } | Select-Object -First 1
if(!$inner){ Err "No se encontró carpeta interna jdk dentro de $jdkTarget"; exit 1 }
$jdkHome = $inner.FullName

# Ajustar variables para esta sesión y forzar uso del binario recién extraído
$env:JAVA_HOME = $jdkHome
$filteredPath = ($env:Path -split ';' | Where-Object { $_ -and ($_ -notmatch 'Program Files\\Java\\jre1.8') })
$env:Path = "$jdkHome\bin;" + ($filteredPath -join ';')
Info "JAVA_HOME establecido a $jdkHome"

Info "Verificando java -version (bin directo)..."
$javaExe = Join-Path $env:JAVA_HOME 'bin/java.exe'
if(!(Test-Path $javaExe)){ Err "No existe java.exe en $javaExe"; exit 1 }
$versionOutput = & $javaExe -version 2>&1
$versionOutput | ForEach-Object { Write-Host $_ }

# Parsear versión mayor
$majorMatch = [Regex]::Match(($versionOutput -join ' '), '"(\d+)(?:\.\d+)?')
if(!$majorMatch.Success){ Warn "No se pudo parsear versión; continuando" } else {
  $major = [int]$majorMatch.Groups[1].Value
  if($major -lt 21){
    Warn "Versión detectada ($major) < 21. Intentando BellSoft x64..."
    Remove-Item $jdkTarget -Recurse -Force -ErrorAction SilentlyContinue
    New-Item -ItemType Directory -Path $jdkTarget | Out-Null
    $bellUrl = 'https://download.bell-sw.com/java/21+37/bellsoft-jdk21+37-windows-amd64.zip'
    Invoke-WebRequest -Uri $bellUrl -OutFile $zipPath -UseBasicParsing
    Expand-Archive -Path $zipPath -DestinationPath $jdkTarget -Force
    $inner = Get-ChildItem $jdkTarget | Where-Object { $_.PSIsContainer -and $_.Name -match '^jdk' } | Select-Object -First 1
    if(!$inner){ Err "BellSoft ZIP sin carpeta jdk"; exit 1 }
    $jdkHome = $inner.FullName
    $env:JAVA_HOME = $jdkHome
    $filteredPath = ($env:Path -split ';' | Where-Object { $_ -and ($_ -notmatch 'Program Files\\Java\\jre1.8') })
    $env:Path = "$jdkHome\bin;" + ($filteredPath -join ';')
    Info "BellSoft instalado JAVA_HOME=$jdkHome"
    $versionOutput = & $javaExe -version 2>&1
    $versionOutput | ForEach-Object { Write-Host $_ }
    $majorMatch = [Regex]::Match(($versionOutput -join ' '), '"(\d+)(?:\.\d+)?')
    if($majorMatch.Success -and [int]$majorMatch.Groups[1].Value -lt 21){ Err "Tras BellSoft la versión sigue <21"; exit 1 }
  }
}

# Probar wrapper Maven si existe en este directorio
$mvnw = Join-Path $PSScriptRoot 'mvnw.cmd'
if(Test-Path $mvnw){
  Info "Probando mvnw.cmd -version..."
  & $mvnw -version
  if($LASTEXITCODE -ne 0){ Err "mvnw fallo"; exit 1 }
  Info "Wrapper Maven OK"
} else { Warn "mvnw.cmd no encontrado en $PSScriptRoot" }

Info "JDK listo para esta sesión. Ahora puedes ejecutar:"
Write-Host "   powershell -ExecutionPolicy Bypass -File ..\run-dedication-flow.ps1" -ForegroundColor Green
