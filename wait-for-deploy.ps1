# Wait for Railway Deploy - Auto-check every 30s until ready
$baseUrl = "https://overflowing-consideration-production.up.railway.app"
$maxAttempts = 20
$attempt = 1

Write-Host ""
Write-Host "=== WAITING FOR RAILWAY DEPLOY ===" -ForegroundColor Cyan
Write-Host "Checking every 30 seconds (max $maxAttempts attempts = 10 min)" -ForegroundColor Gray
Write-Host ""

while ($attempt -le $maxAttempts) {
    Write-Host "[$attempt/$maxAttempts] Checking at $(Get-Date -Format 'HH:mm:ss')..." -ForegroundColor Yellow
    
    # Test /api/ping for new version marker
    $pingOk = $false
    try {
        $r = Invoke-WebRequest -Uri "$baseUrl/api/ping" -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        $json = $r.Content | ConvertFrom-Json
        if ($json.marker -eq "ping-controller-v1") {
            Write-Host "  PING: OK - New version detected!" -ForegroundColor Green
            $pingOk = $true
        }
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        if ($status -eq 403) {
            Write-Host "  PING: 403 (old version still active)" -ForegroundColor Yellow
        } else {
            Write-Host "  PING: $status" -ForegroundColor Yellow
        }
    }
    
    # Test /api/auth/social
    $socialOk = $false
    try {
        $body = @{ provider = "google"; externalToken = "demo12345" } | ConvertTo-Json
        $r = Invoke-WebRequest -Uri "$baseUrl/api/auth/social" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -TimeoutSec 10 -ErrorAction Stop
        Write-Host "  SOCIAL: OK (200)" -ForegroundColor Green
        $socialOk = $true
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        if ($status -eq 400) {
            Write-Host "  SOCIAL: OK (400 validation)" -ForegroundColor Green
            $socialOk = $true
        } elseif ($status -eq 500) {
            try {
                $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $errorBody = $reader.ReadToEnd()
                $errorJson = $errorBody | ConvertFrom-Json
                if ($errorJson.error -match "No static resource") {
                    Write-Host "  SOCIAL: 500 (old code still running)" -ForegroundColor Yellow
                } else {
                    Write-Host "  SOCIAL: 500 - $($errorJson.error)" -ForegroundColor Yellow
                }
            } catch {
                Write-Host "  SOCIAL: 500" -ForegroundColor Yellow
            }
        } else {
            Write-Host "  SOCIAL: $status" -ForegroundColor Yellow
        }
    }
    
    # Check if deploy is complete
    if ($pingOk -and $socialOk) {
        Write-Host ""
        Write-Host "=== DEPLOY COMPLETE ===" -ForegroundColor Green
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor Cyan
        Write-Host "1. Run full verification:" -ForegroundColor White
        Write-Host "   powershell -ExecutionPolicy Bypass -File verify-deploy.ps1" -ForegroundColor Gray
        Write-Host ""
        Write-Host "2. Open test suite in browser:" -ForegroundColor White
        Write-Host "   Start-Process test-suite.html" -ForegroundColor Gray
        Write-Host ""
        exit 0
    }
    
    if ($attempt -lt $maxAttempts) {
        Write-Host "  Waiting 30 seconds before next check..." -ForegroundColor Gray
        Write-Host ""
        Start-Sleep -Seconds 30
    }
    
    $attempt++
}

Write-Host ""
Write-Host "=== TIMEOUT ===" -ForegroundColor Red
Write-Host "Deploy did not complete in 10 minutes." -ForegroundColor Yellow
Write-Host "Check Railway dashboard for build logs:" -ForegroundColor Yellow
Write-Host "https://railway.app" -ForegroundColor Gray
Write-Host ""
exit 1
