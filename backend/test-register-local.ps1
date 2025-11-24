# Script para probar registro local con displayName y logging de email
# Uso: .\test-register-local.ps1

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

# Configuración
$env:JAVA_HOME = "$PSScriptRoot\.java\jdk21\jdk-21.0.9+10"
$PORT = 12000
$WAIT_SECONDS = 25

Write-Host "`n🔪 Paso 1: Limpiando procesos Java existentes..." -ForegroundColor Yellow
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
Start-Sleep -Seconds 2
Write-Host "✅ Procesos Java terminados`n" -ForegroundColor Green

Write-Host "📦 Paso 2: Compilando JAR..." -ForegroundColor Cyan
.\mvnw.cmd -q package -DskipTests
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Error compilando" -ForegroundColor Red
    exit 1
}
Write-Host "✅ JAR compilado`n" -ForegroundColor Green

Write-Host "🚀 Paso 3: Arrancando backend (perfil local)..." -ForegroundColor Cyan
Write-Host "   Puerto: $PORT" -ForegroundColor Gray
Write-Host "   Logs aparecerán abajo...`n" -ForegroundColor Gray

# Arrancar en background capturando salida
$job = Start-Job -ScriptBlock {
    param($javaHome, $jarPath, $port)
    & "$javaHome\bin\java.exe" "-Dspring.profiles.active=local" -jar $jarPath "--server.port=$port" 2>&1
} -ArgumentList $env:JAVA_HOME, "$PSScriptRoot\target\drakkarpress-platform-1.0.0.jar", $PORT

Write-Host "⏳ Paso 4: Esperando $WAIT_SECONDS segundos para que arranque..." -ForegroundColor Yellow
for ($i = 1; $i -le $WAIT_SECONDS; $i++) {
    Write-Progress -Activity "Esperando backend" -Status "$i/$WAIT_SECONDS segundos" -PercentComplete (($i / $WAIT_SECONDS) * 100)
    Start-Sleep -Seconds 1
}
Write-Progress -Activity "Esperando backend" -Completed

Write-Host "`n✅ Paso 5: Probando health endpoint..." -ForegroundColor Cyan
$health = curl.exe -s "http://localhost:$PORT/api/health"
if ($LASTEXITCODE -eq 0) {
    Write-Host "   Respuesta: $health" -ForegroundColor Green
} else {
    Write-Host "❌ Health endpoint no responde" -ForegroundColor Red
    Stop-Job $job
    Remove-Job $job
    exit 1
}

Write-Host "`n📝 Paso 6: Registrando usuario con displayName..." -ForegroundColor Cyan
$timestamp = Get-Date -Format "HHmmss"
$body = @{
    email = "testlocal${timestamp}@gmail.com"
    username = "localuser${timestamp}"
    password = "Test12345!"
    displayName = "Usuario Test $timestamp"
} | ConvertTo-Json

Write-Host "   Email: testlocal${timestamp}@gmail.com" -ForegroundColor Gray
Write-Host "   Username: localuser${timestamp}" -ForegroundColor Gray
Write-Host "   DisplayName: Usuario Test $timestamp`n" -ForegroundColor Gray

try {
    $response = Invoke-RestMethod -Uri "http://localhost:$PORT/api/auth/register" -Method POST -ContentType 'application/json' -Body $body -ErrorAction Stop
    
    Write-Host "✅ REGISTRO EXITOSO`n" -ForegroundColor Green
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Cyan
    Write-Host "Respuesta del servidor:" -ForegroundColor Cyan
    $response | ConvertTo-Json -Depth 3 | Write-Host
    Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`n" -ForegroundColor Cyan
    
    Write-Host "🔍 Verificando datos del token..." -ForegroundColor Yellow
    if ($response.data.token) {
        Write-Host "   ✓ Access Token presente" -ForegroundColor Green
    }
    if ($response.data.refreshToken) {
        Write-Host "   ✓ Refresh Token presente" -ForegroundColor Green
    }
    if ($response.data.username) {
        Write-Host "   ✓ Username: $($response.data.username)" -ForegroundColor Green
    }
    if ($response.data.userId) {
        Write-Host "   ✓ User ID: $($response.data.userId)" -ForegroundColor Green
    }
    
} catch {
    Write-Host "❌ ERROR EN REGISTRO" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    if ($_.ErrorDetails) {
        Write-Host $_.ErrorDetails.Message -ForegroundColor Red
    }
}

Write-Host "`n📊 Paso 7: Mostrando últimos logs del backend..." -ForegroundColor Cyan
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" -ForegroundColor Gray
Start-Sleep -Seconds 2
Receive-Job $job | Select-Object -Last 30 | Write-Host
Write-Host "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━`n" -ForegroundColor Gray

Write-Host "🔍 Buscando logs [AUTH] y [EMAIL]..." -ForegroundColor Yellow
$allLogs = Receive-Job $job
$authLogs = $allLogs | Select-String "\[AUTH\]" | ForEach-Object { $_.Line }
$emailLogs = $allLogs | Select-String "\[EMAIL\]" | ForEach-Object { $_.Line }

if ($authLogs) {
    Write-Host "`n📨 Logs [AUTH]:" -ForegroundColor Cyan
    $authLogs | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
}

if ($emailLogs) {
    Write-Host "`n📧 Logs [EMAIL]:" -ForegroundColor Cyan
    $emailLogs | ForEach-Object { Write-Host "   $_" -ForegroundColor White }
}

if (-not $authLogs -and -not $emailLogs) {
    Write-Host "   ⚠️  No se encontraron logs [AUTH] ni [EMAIL]" -ForegroundColor Yellow
    Write-Host "   Esto puede significar que estás usando el JAR viejo" -ForegroundColor Yellow
}

Write-Host "`n🛑 Paso 8: Deteniendo backend..." -ForegroundColor Yellow
Stop-Job $job
Remove-Job $job
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force

Write-Host "`n✅ PRUEBA COMPLETADA" -ForegroundColor Green
Write-Host "`nPara volver a probar: .\test-register-local.ps1" -ForegroundColor Cyan
