Param(
  [string]$BaseUrl = "https://overflowing-consideration-production.up.railway.app",
  [switch]$IncludeSocial
)

function Invoke-Json($Method, $Url, $BodyObj, $Token) {
  $Headers = @{}
  if ($Token) { $Headers['Authorization'] = "Bearer $Token" }
  $Body = if ($BodyObj) { ($BodyObj | ConvertTo-Json -Depth 5) } else { $null }
  try {
    $resp = Invoke-WebRequest -Uri $Url -Method $Method -Headers $Headers -ContentType 'application/json' -Body $Body -UseBasicParsing -TimeoutSec 15
    return [pscustomobject]@{ Status=$resp.StatusCode; Json=(try { $resp.Content | ConvertFrom-Json } catch { $null }); Raw=$resp.Content }
  }
  catch {
    $code = $null; if ($_.Exception.Response) { $code = $_.Exception.Response.StatusCode.value__ }
    $raw = $null; if ($_.Exception.Response) { try { $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream()); $raw = $sr.ReadToEnd() } catch {} }
    $json = $null; if ($raw) { try { $json = $raw | ConvertFrom-Json } catch {} }
    return [pscustomobject]@{ Status=$code; Json=$json; Raw=$raw }
  }
}

Write-Host "=== Verificación Producción DrakkarPress ===" -ForegroundColor Cyan
Write-Host "Base URL: $BaseUrl"

# 1. Health
$health = Invoke-Json GET "$BaseUrl/api/health" $null $null
Write-Host "[HEALTH] Status: $($health.Status) Body: $($health.Raw)"

# 2. Ping
$ping = Invoke-Json GET "$BaseUrl/api/ping" $null $null
Write-Host "[PING] Status: $($ping.Status) Marker: $($ping.Json.marker) Epoch: $($ping.Json.epochMs)"

# 3. Registro usuario nuevo
$suffix = Get-Random -Maximum 1000000
$email = "e2e+$suffix@drakkar.test"
$username = "user$suffix"
$password = "Passw0rd!$suffix"
$registerBody = @{ email=$email; username=$username; password=$password }
$reg = Invoke-Json POST "$BaseUrl/api/auth/register" $registerBody $null
Write-Host "[REGISTER] Status: $($reg.Status) User: $email TokenPresent: $([bool]$reg.Json.data.token)"
$token = $reg.Json.data.token
if (-not $token) { Write-Host "Registro falló, abortando login/profile" -ForegroundColor Red; exit 1 }

# 4. Login (verificar credenciales)
$loginBody = @{ email=$email; password=$password }
$login = Invoke-Json POST "$BaseUrl/api/auth/login" $loginBody $null
Write-Host "[LOGIN] Status: $($login.Status) TokenMatch: $([bool]($login.Json.data.token -eq $token))"
$loginToken = $login.Json.data.token

# 5. Profile GET (requiere endpoint; adapt if different)
# Asumimos /api/profile retorna datos del usuario autenticado
$profile = Invoke-Json GET "$BaseUrl/api/profile" $null $loginToken
Write-Host "[PROFILE GET] Status: $($profile.Status) Username: $($profile.Json.data.username)"

# 6. Profile UPDATE demo (si existe endpoint PUT /api/profile)
$updateBody = @{ bio = "E2E test run $(Get-Date -Format o)" }
$profileUpdate = Invoke-Json PUT "$BaseUrl/api/profile" $updateBody $loginToken
Write-Host "[PROFILE PUT] Status: $($profileUpdate.Status) UpdatedBio: $($profileUpdate.Json.data.bio)"

# 7. Social login mock (opcional)
if ($IncludeSocial) {
  $socialBody = @{ provider = 'google'; externalToken = 'demo12345'; email = ''; username = '' }
  $social = Invoke-Json POST "$BaseUrl/api/auth/social" $socialBody $null
  Write-Host "[SOCIAL] Status: $($social.Status) Provider: $($social.Json.data.provider) TokenPresent: $([bool]$social.Json.data.token)"
}

Write-Host "=== Resumen ===" -ForegroundColor Cyan
Write-Host "Health: $($health.Status) | Ping: $($ping.Status) | Register: $($reg.Status) | Login: $($login.Status) | Profile GET: $($profile.Status) | Profile PUT: $($profileUpdate.Status)"
if ($IncludeSocial) { Write-Host "Social: $($social.Status)" }

if ($health.Status -eq 200 -and $ping.Status -eq 200 -and $reg.Status -eq 200 -and $login.Status -eq 200) {
  Write-Host "E2E básico OK" -ForegroundColor Green
} else {
  Write-Host "Fallas en flujo E2E" -ForegroundColor Red
  exit 2
}
