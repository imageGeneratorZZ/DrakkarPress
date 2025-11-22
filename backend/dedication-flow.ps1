<#
Script: dedication-flow.ps1
Objetivo: Automatizar flujo completo para probar generación de libro, compra dummy y dedicación.
Requisitos: PowerShell 5+, winget disponible (para instalar JDK si falta), puerto 12000 libre.
Uso rápido:
  powershell -ExecutionPolicy Bypass -File .\dedication-flow.ps1 -Email demo@drakkarpress.com -Password DemoPass123 -Prompt "Historia épica de dragones" -Chapters 3 -Dedication "Para Ana, con toda mi inspiración" -Price 5.99
#>
param(
  [string]$Email = "demo@drakkarpress.com",
  [string]$Password = "DemoPass123",
  [string]$Username = "demo",
  [string]$Prompt = "Historia épica de dragones",
  [int]$Chapters = 3,
  [string]$Dedication = "Para Ana, con toda mi inspiración",
  [double]$Price = 5.99,
  [int]$PollSeconds = 4,
  [int]$PollTimeoutSeconds = 240
)

# Asegura ejecución dentro del directorio backend aunque se llame desde fuera
try {
  $scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
  Set-Location -Path $scriptDir
} catch {
  Write-Host "[WARN] No se pudo cambiar al directorio del script." -ForegroundColor Yellow
}

function Write-Info($msg){Write-Host "[INFO] $msg" -ForegroundColor Cyan}
function Write-Warn($msg){Write-Host "[WARN] $msg" -ForegroundColor Yellow}
function Write-Err($msg){Write-Host "[ERROR] $msg" -ForegroundColor Red}

Write-Info "Verificando versión de Java..."
$javaVersionLine = (& java -version 2>&1 | Select-String 'version')
if(-not $javaVersionLine){ Write-Warn "Java no encontrado en PATH." } else { Write-Info $javaVersionLine.ToString() }

$needsJdk = $true
if($javaVersionLine){
  $match = [Regex]::Match($javaVersionLine.ToString(), '"(\d+)(?:\.\d+)?')
  if($match.Success){
    $major = [int]$match.Groups[1].Value
    if($major -ge 21){ $needsJdk = $false }
  }
}
if($needsJdk){
  Write-Warn "Se requiere JDK 21. Intentando instalación automática..."

  function Test-Command($name){ (Get-Command $name -ErrorAction SilentlyContinue) -ne $null }
  $installed = $false

  # 1. Winget
  if(Test-Command 'winget'){
    Write-Info "Intentando con winget (Temurin)..."
    try {
      winget install --id EclipseAdoptium.Temurin.21.JDK -e --source winget --accept-source-agreements --accept-package-agreements | Out-Null
      $installed = $true
    } catch { Write-Warn "Winget falló: $($_.Exception.Message)" }
  } else { Write-Warn "winget no disponible." }

  # 2. Chocolatey
  if(-not $installed -and Test-Command 'choco'){
    Write-Info "Intentando con Chocolatey (temurin21)..."
    try {
      choco install temurin21 -y --no-progress | Out-Null
      $installed = $true
    } catch { Write-Warn "Chocolatey falló: $($_.Exception.Message)" }
  }

  # 3. Scoop
  if(-not $installed -and Test-Command 'scoop'){
    Write-Info "Intentando con Scoop (temurin-jdk)..."
    try {
      scoop install temurin-jdk | Out-Null
      $installed = $true
    } catch { Write-Warn "Scoop falló: $($_.Exception.Message)" }
  }

  # 4. Descarga directa MSI (requiere msiexec admin)
  if(-not $installed){
    Write-Info "Descargando MSI directo Temurin 21..."
    $temp = New-Item -ItemType Directory -Path (Join-Path $env:TEMP "jdk21_install") -Force
    $msiPath = Join-Path $temp.FullName "temurin21.msi"
    $downloadUrl = "https://github.com/adoptium/temurin21-binaries/releases/latest/download/OpenJDK21U-jdk_x64_windows_hotspot.msi"
    try {
      Invoke-WebRequest -Uri $downloadUrl -OutFile $msiPath -UseBasicParsing
      Write-Info "MSI descargado. Ejecutando msiexec /qn..."
      $msiArgs = "/i `"$msiPath`" /qn /norestart"
      $proc = Start-Process msiexec.exe -ArgumentList $msiArgs -Wait -PassThru
      if($proc.ExitCode -eq 0){ $installed = $true } else { Write-Warn "msiexec código $($proc.ExitCode)" }
    } catch { Write-Warn "Descarga/instalación directa falló: $($_.Exception.Message)" }
  }

  # 5. Fallback ZIP extracción manual (sin privilegios admin)
  if(-not $installed){
    Write-Info "Intentando fallback ZIP Temurin 21..."
    $zipUrl = "https://github.com/adoptium/temurin21-binaries/releases/latest/download/OpenJDK21U-jdk_x64_windows_hotspot.zip"
    $zipPath = Join-Path $env:TEMP "temurin21.zip"
    $extractDir = Join-Path $env:USERPROFILE "jdk-21"
    try {
      Invoke-WebRequest -Uri $zipUrl -OutFile $zipPath -UseBasicParsing
      if(Test-Path $extractDir){ Remove-Item $extractDir -Recurse -Force }
      Expand-Archive -Path $zipPath -DestinationPath $extractDir -Force
      # La carpeta extraída suele tener nombre jdk-21.*; tomamos la primera coincidencia
      $folder = Get-ChildItem $extractDir | Where-Object { $_.Name -match 'jdk' } | Select-Object -First 1
      if($folder){
        $jdkHome = $folder.FullName
        [Environment]::SetEnvironmentVariable('JAVA_HOME',$jdkHome,'User')
        $env:JAVA_HOME = $jdkHome
        $env:Path = "$jdkHome\bin;" + $env:Path
        Write-Info "ZIP extraído. JAVA_HOME=$jdkHome"
        $installed = $true
      } else { Write-Warn "No se encontró carpeta jdk en extracción" }
    } catch { Write-Warn "Fallback ZIP falló: $($_.Exception.Message)" }
  }

  if(-not $installed){
    Write-Err "No se pudo instalar JDK automáticamente. Instala manualmente y reejecuta."
    exit 1
  }

  $javaVersionLine = (& java -version 2>&1 | Select-String 'version')
  Write-Info "Nueva versión detectada: $($javaVersionLine.ToString())"
}

if($env:JAVA_HOME -eq $null -or -not (Test-Path $env:JAVA_HOME)){
  $candidate = Get-ChildItem "C:\Program Files\Eclipse Adoptium" -ErrorAction SilentlyContinue | Where-Object { $_.Name -match 'jdk-21' } | Select-Object -First 1
  if($candidate){
    $jdkPath = $candidate.FullName
    [Environment]::SetEnvironmentVariable('JAVA_HOME',$jdkPath,'User')
    $env:JAVA_HOME = $jdkPath
    Write-Info "JAVA_HOME establecido: $jdkPath"
  } else { Write-Warn "No se pudo establecer JAVA_HOME automáticamente. Continúo." }
}

Write-Info "Verificando mvnw wrapper..."
if(-not (Test-Path .\mvnw.cmd)) { Write-Err "No existe mvnw.cmd en el directorio actual. Ejecuta el script desde backend."; exit 1 }

Write-Info "Compilando backend (skip tests)..."
$build = .\mvnw.cmd -q clean package -DskipTests 2>&1
if($LASTEXITCODE -ne 0){ Write-Err "Error en build Maven"; $build | Select-Object -Last 20; exit 1 }
Write-Info "Build OK"

Write-Info "Iniciando backend en segundo plano..."
$serverJob = Start-Job -ScriptBlock { & .\mvnw.cmd spring-boot:run } -Name drakkarpress_run
Start-Sleep -Seconds 10

function Wait-Port($port){
  for($i=0;$i -lt 40;$i++){
    try {
      $client = New-Object Net.Sockets.TcpClient
      $client.Connect('localhost',$port)
      if($client.Connected){$client.Close(); return $true}
    } catch {}
    Start-Sleep -Milliseconds 500
  }
  return $false
}

if(-not (Wait-Port 12000)){ Write-Err "Puerto 12000 no abrió. Revisa logs del job con Get-Job -Name drakkarpress_run | Receive-Job"; exit 1 }
Write-Info "Backend escuchando en 12000"

# Registro (ignorar error si ya existe)
Write-Info "Registrando usuario $Email (si no existe)..."
try {
  $registerBody = @{ email=$Email; password=$Password; username=$Username } | ConvertTo-Json
  Invoke-WebRequest -Method POST -Uri http://localhost:12000/api/auth/register -ContentType application/json -Body $registerBody | Out-Null
} catch { Write-Warn "Registro puede haber fallado (quizá ya existe): $($_.Exception.Message)" }

Write-Info "Login usuario..."
$loginBody = @{ email=$Email; password=$Password } | ConvertTo-Json
$loginResp = Invoke-WebRequest -Method POST -Uri http://localhost:12000/api/auth/login -ContentType application/json -Body $loginBody
$token = (ConvertFrom-Json $loginResp.Content).token
if(-not $token){ Write-Err "No se obtuvo token"; exit 1 }
$authHeader = @{ Authorization = "Bearer $token" }
Write-Info "Token obtenido." 

Write-Info "Solicitando generación de libro..."
$genBody = @{ prompt=$Prompt; chapters=$Chapters } | ConvertTo-Json
$genResp = Invoke-WebRequest -Method POST -Uri http://localhost:12000/api/ai/books/generate -Headers $authHeader -ContentType application/json -Body $genBody
$genJson = (ConvertFrom-Json $genResp.Content)
$jobId = $genJson.data.jobId
if(-not $jobId){ Write-Err "No se encontró jobId en respuesta"; $genJson | ConvertTo-Json -Depth 5; exit 1 }
Write-Info "JobId: $jobId"

Write-Info "Poll estado de generación hasta COMPLETED (timeout ${PollTimeoutSeconds}s)..."
$elapsed = 0
$bookId = $null
while($elapsed -lt $PollTimeoutSeconds){
  $statusResp = Invoke-WebRequest -Method GET -Uri http://localhost:12000/api/ai/books/jobs/$jobId -Headers $authHeader
  $statusJson = (ConvertFrom-Json $statusResp.Content)
  $state = $statusJson.data.status
  Write-Info "Estado: $state"
  if($state -eq 'COMPLETED'){
    $bookId = $statusJson.data.bookId
    break
  }
  Start-Sleep -Seconds $PollSeconds
  $elapsed += $PollSeconds
}
if(-not $bookId){ Write-Err "Timeout sin COMPLETED"; exit 1 }
Write-Info "BookId: $bookId"

Write-Info "Creando compra dummy..."
$purchaseBody = @{ price=$Price; format='EPUB'; dedication=$Dedication } | ConvertTo-Json
$purchaseResp = Invoke-WebRequest -Method POST -Uri "http://localhost:12000/api/test/purchases/$bookId" -Headers $authHeader -ContentType application/json -Body $purchaseBody
$purchaseJson = (ConvertFrom-Json $purchaseResp.Content)
$purchaseId = $purchaseJson.purchaseId
if(-not $purchaseId){ Write-Err "No se obtuvo purchaseId"; $purchaseJson | ConvertTo-Json -Depth 5; exit 1 }
Write-Info "PurchaseId: $purchaseId"

Write-Info "Inyectando dedicación..."
$dedBody = @{ message=$Dedication } | ConvertTo-Json
$dedResp = Invoke-WebRequest -Method POST -Uri "http://localhost:12000/api/dedications/$purchaseId" -Headers $authHeader -ContentType application/json -Body $dedBody
$dedJson = (ConvertFrom-Json $dedResp.Content)
$hash = $dedJson.hash
Write-Info "Hash dedicación: $hash"

Write-Info "Verificando hash..."
$verifyResp = Invoke-WebRequest -Method GET -Uri "http://localhost:12000/api/dedications/verify/$hash"
$verifyJson = (ConvertFrom-Json $verifyResp.Content)

Write-Host "================= RESULTADOS =================" -ForegroundColor Green
Write-Host "BookId          : $bookId"
Write-Host "PurchaseId      : $purchaseId"
Write-Host "Dedication Hash : $hash"
Write-Host "Mensaje         : $($dedJson.message)"
Write-Host "Verificación    : $($verifyJson.valid)"
Write-Host "Archivo EPUB    : $($purchaseJson.filePath)" 
Write-Host "===============================================" -ForegroundColor Green

Write-Info "Para detener el backend: Stop-Job -Name drakkarpress_run; Receive-Job -Name drakkarpress_run | Out-Null"
