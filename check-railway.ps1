# Verificación Rápida Railway
Write-Host ""
Write-Host "Verificando Railway..." -ForegroundColor Cyan
Write-Host ""

# Test Health
try { 
    Invoke-WebRequest "https://overflowing-consideration-production.up.railway.app/api/health" -UseBasicParsing | Out-Null
    Write-Host "Health: OK" -ForegroundColor Green
} catch {
    Write-Host "Health: FAIL" -ForegroundColor Red
    exit 1
}

# Test Social Login
try {
    Invoke-WebRequest -Uri "https://overflowing-consideration-production.up.railway.app/api/auth/social" -Method POST -Body '{"provider":"google","externalToken":"test"}' -ContentType 'application/json' -UseBasicParsing -ErrorAction Stop | Out-Null
    Write-Host "Social: OK (200)" -ForegroundColor Green
    Write-Host ""
    Write-Host "RAILWAY DEPLOY COMPLETO!" -ForegroundColor Green
} catch {
    $code = $_.Exception.Response.StatusCode.value__
    if ($code -eq 400) {
        Write-Host "Social: OK (400)" -ForegroundColor Green
        Write-Host ""
        Write-Host "RAILWAY DEPLOY COMPLETO!" -ForegroundColor Green
    } elseif ($code -eq 500) {
        Write-Host "Social: Compilando (500)" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "Espera 5-10 minutos mas" -ForegroundColor Yellow
    } else {
        Write-Host "Social: Error $code" -ForegroundColor Yellow
    }
}
