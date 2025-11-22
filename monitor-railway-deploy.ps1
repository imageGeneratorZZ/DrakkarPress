# Monitor Railway Deploy Progress
# Este script verifica cada 30 segundos si Railway ha actualizado el backend

$RAILWAY_URL = "https://overflowing-consideration-production.up.railway.app"
$MAX_ATTEMPTS = 20  # 10 minutos (20 * 30 seg)
$attempt = 1

Write-Host "🚀 Monitoreando Railway Deploy..." -ForegroundColor Cyan
Write-Host "URL: $RAILWAY_URL" -ForegroundColor Gray
Write-Host "Commit: 3123c81 (main branch)" -ForegroundColor Gray
Write-Host ""

while ($attempt -le $MAX_ATTEMPTS) {
    Write-Host "[$attempt/$MAX_ATTEMPTS] Probando... " -NoNewline
    
    try {
        # Test 1: Health Check
        $health = Invoke-WebRequest -Uri "$RAILWAY_URL/api/health" -UseBasicParsing -TimeoutSec 10
        Write-Host "Health: ✅ " -NoNewline -ForegroundColor Green
        
        # Test 2: Social Login Endpoint
        try {
            $social = Invoke-WebRequest `
                -Uri "$RAILWAY_URL/api/auth/social" `
                -Method POST `
                -Body '{"provider":"google","externalToken":"demo12345"}' `
                -ContentType 'application/json' `
                -UseBasicParsing `
                -TimeoutSec 10
            
            Write-Host "Social: ✅ " -NoNewline -ForegroundColor Green
            
            # Test 3: Profile Endpoint (debe dar 401 sin token)
            try {
                $profile = Invoke-WebRequest -Uri "$RAILWAY_URL/api/profile/me" -UseBasicParsing -TimeoutSec 10
            } catch {
                if ($_.Exception.Response.StatusCode.value__ -eq 401) {
                    Write-Host "Profile: ✅" -ForegroundColor Green
                    Write-Host ""
                    Write-Host "🎉 ¡DEPLOY EXITOSO!" -ForegroundColor Green
                    Write-Host ""
                    Write-Host "Endpoints activos:" -ForegroundColor Yellow
                    Write-Host "  - GET  /api/health" -ForegroundColor White
                    Write-Host "  - POST /api/auth/register" -ForegroundColor White
                    Write-Host "  - POST /api/auth/login" -ForegroundColor White
                    Write-Host "  - POST /api/auth/social (NUEVO ✨)" -ForegroundColor White
                    Write-Host "  - GET  /api/profile/me (NUEVO ✨)" -ForegroundColor White
                    Write-Host "  - PUT  /api/profile/me (NUEVO ✨)" -ForegroundColor White
                    Write-Host ""
                    Write-Host "Siguiente paso: Probar en www.drakkarpress.com" -ForegroundColor Cyan
                    exit 0
                }
            }
            
        } catch {
            $status = $_.Exception.Response.StatusCode.value__
            if ($status -eq 400) {
                Write-Host "Social: ✅ (validación) " -NoNewline -ForegroundColor Green
                
                # Test Profile
                try {
                    $profile = Invoke-WebRequest -Uri "$RAILWAY_URL/api/profile/me" -UseBasicParsing -TimeoutSec 10
                } catch {
                    if ($_.Exception.Response.StatusCode.value__ -eq 401) {
                        Write-Host "Profile: ✅" -ForegroundColor Green
                        Write-Host ""
                        Write-Host "🎉 ¡DEPLOY EXITOSO!" -ForegroundColor Green
                        exit 0
                    }
                }
            } elseif ($status -eq 500) {
                Write-Host "Social: ⏳ (aún compilando)" -ForegroundColor Yellow
            } else {
                Write-Host "Social: ❌ (error $status)" -ForegroundColor Red
            }
        }
        
    } catch {
        Write-Host "❌ Backend no responde" -ForegroundColor Red
    }
    
    if ($attempt -lt $MAX_ATTEMPTS) {
        Write-Host "Siguiente intento en 30 segundos..." -ForegroundColor Gray
        Start-Sleep -Seconds 30
    }
    
    $attempt++
}

Write-Host ""
Write-Host "⏱️ Tiempo agotado esperando Railway deploy" -ForegroundColor Yellow
Write-Host ""
Write-Host "Opciones:" -ForegroundColor Cyan
Write-Host "1. Verificar Railway Dashboard: https://railway.app" -ForegroundColor White
Write-Host "2. Revisar logs de build en Railway" -ForegroundColor White
Write-Host "3. Ejecutar este script nuevamente: .\monitor-railway-deploy.ps1" -ForegroundColor White
Write-Host "4. Redeploy manual desde Railway panel" -ForegroundColor White
