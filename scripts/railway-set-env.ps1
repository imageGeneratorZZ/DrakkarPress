param(
  [string]$ServiceId,
  [switch]$Deploy,
  [string]$Environment,
  [switch]$ShowStatus
)

# Verifica Railway CLI y sesión
if (-not (Get-Command railway -ErrorAction SilentlyContinue)) {
  Write-Error "Railway CLI no encontrado. Instala con: npm i -g @railway/cli"
  exit 1
}
& railway whoami | Out-Null
if ($LASTEXITCODE -ne 0) {
  Write-Error "No has iniciado sesión en Railway. Ejecuta: railway login"
  exit 1
}

function SetVar($k, $v) {
  if ($ServiceId) {
    railway variables set "$k=$v" --service $ServiceId | Out-Null
  } else {
    railway variables set "$k=$v" | Out-Null
  }
  Write-Host "Set $k" -ForegroundColor Green
}

SetVar "NIXPACKS_JAVA_VERSION" "21"
SetVar "MAVEN_OPTS" "-DskipTests -T1C -Dmaven.wagon.http.retryHandler.count=3 -Dhttps.protocols=TLSv1.2,TLSv1.3"
SetVar "JAVA_TOOL_OPTIONS" "-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
SetVar "JDK_JAVA_OPTIONS"  "-XX:+UseG1GC -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"
SetVar "MALLOC_ARENA_MAX" "2"
SetVar "SPRING_PROFILES_ACTIVE" "prod"
SetVar "TZ" "UTC"
SetVar "NIXPACKS_BUILD_CMD" "mvn -q -B -DskipTests -T1C -f backend/pom.xml package"
SetVar "NIXPACKS_START_CMD" "java -Dserver.port=`$PORT -jar backend/target/*.jar"

Write-Host "Variables configuradas. Ejecuta: .\scripts\railway-set-env.ps1 [-ServiceId <id>] [-Deploy] [-Environment <env>] [-ShowStatus]" -ForegroundColor Cyan

# Opcional: desplegar y/o mostrar estado
if ($Deploy) {
  $args = @()
  if ($ServiceId)    { $args += @("--service", $ServiceId) }
  if ($Environment)  { $args += @("--environment", $Environment) }
  Write-Host "Desplegando con Railway..." -ForegroundColor Cyan
  & railway up @args
  if ($LASTEXITCODE -ne 0) { Write-Error "Falló el deploy en Railway"; exit 1 }
}

if ($ShowStatus -or $Deploy) {
  $sargs = @()
  if ($ServiceId)    { $sargs += @("--service", $ServiceId) }
  if ($Environment)  { $sargs += @("--environment", $Environment) }
  & railway status @sargs
}
