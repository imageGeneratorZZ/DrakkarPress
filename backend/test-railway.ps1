# Reemplaza AQUI_TU_URL con el dominio real de Railway
$RAILWAY_URL = "https://overflowing-consideration-production.up.railway.app"

Write-Host "=== Probando Backend Railway ===" -ForegroundColor Cyan
Write-Host "URL: $RAILWAY_URL" -ForegroundColor Yellow

# Test 1: Health check
Write-Host "`n[1/4] Health check..." -ForegroundColor Green
try {
    $health = Invoke-WebRequest -Uri "$RAILWAY_URL/api/health" -UseBasicParsing -ErrorAction Stop
    Write-Host "  OK - Status: $($health.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "  FAIL - $_" -ForegroundColor Red
}

# Test 2: Login
Write-Host "`n[2/4] Login endpoint..." -ForegroundColor Green
try {
    $login = Invoke-WebRequest -Uri "$RAILWAY_URL/api/auth/login" -Method POST -Body '{"email":"test@test.com","password":"test123"}' -ContentType 'application/json' -UseBasicParsing -ErrorAction Stop
    Write-Host "  OK - Status: $($login.StatusCode)" -ForegroundColor Green
    Write-Host "  Response: $($login.Content.Substring(0, [Math]::Min(100, $login.Content.Length)))..." -ForegroundColor Gray
} catch {
    Write-Host "  FAIL - $_" -ForegroundColor Red
}

# Test 3: Social login
Write-Host "`n[3/4] Social login endpoint..." -ForegroundColor Green
try {
    $social = Invoke-WebRequest -Uri "$RAILWAY_URL/api/auth/social" -Method POST -Body '{"provider":"google","externalToken":"demo12345"}' -ContentType 'application/json' -UseBasicParsing -ErrorAction Stop
    Write-Host "  OK - Status: $($social.StatusCode)" -ForegroundColor Green
    Write-Host "  Response: $($social.Content.Substring(0, [Math]::Min(100, $social.Content.Length)))..." -ForegroundColor Gray
} catch {
    Write-Host "  FAIL - $_" -ForegroundColor Red
}

# Test 4: Profile (sin token, debe dar 401)
Write-Host "`n[4/4] Profile endpoint (sin auth, debe dar 401)..." -ForegroundColor Green
try {
    $profile = Invoke-WebRequest -Uri "$RAILWAY_URL/api/profile/me" -UseBasicParsing -ErrorAction Stop
    Write-Host "  INESPERADO - Status: $($profile.StatusCode)" -ForegroundColor Yellow
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "  OK - 401 Unauthorized (esperado)" -ForegroundColor Green
    } else {
        Write-Host "  FAIL - $_" -ForegroundColor Red
    }
}

Write-Host "`n=== Resumen ===" -ForegroundColor Cyan
Write-Host "Si todos los tests pasaron, Railway esta actualizado." -ForegroundColor Yellow
Write-Host "Ahora actualiza netlify.toml con esta URL y haz push." -ForegroundColor Yellow
