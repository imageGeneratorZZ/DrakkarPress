# Railway Deploy Verification - Confirm new version is active
Write-Host ""
Write-Host "=== RAILWAY DEPLOY VERIFICATION ===" -ForegroundColor Cyan
Write-Host ""

$baseUrl = "https://overflowing-consideration-production.up.railway.app"

# 1. Test /api/ping (must exist in new version)
Write-Host "1. Testing /api/ping (marker: ping-controller-v1)..." -ForegroundColor Yellow
try {
    $r = Invoke-WebRequest -Uri "$baseUrl/api/ping" -UseBasicParsing -TimeoutSec 10
    $json = $r.Content | ConvertFrom-Json
    if ($json.marker -eq "ping-controller-v1") {
        Write-Host "   OK - New version detected!" -ForegroundColor Green
        Write-Host "   Marker: $($json.marker)" -ForegroundColor Gray
    } else {
        Write-Host "   WARNING - Ping responds but without expected marker" -ForegroundColor Yellow
    }
} catch {
    $status = $_.Exception.Response.StatusCode.value__
    Write-Host "   FAIL - Status: $status" -ForegroundColor Red
    if ($status -eq 403) {
        Write-Host "   Old version still active (no permitAll /api/ping)" -ForegroundColor Yellow
    }
}

Write-Host ""

# 2. Test /api/health
Write-Host "2. Testing /api/health..." -ForegroundColor Yellow
try {
    $r = Invoke-WebRequest -Uri "$baseUrl/api/health" -UseBasicParsing -TimeoutSec 10
    Write-Host "   OK - Health endpoint working (Status: $($r.StatusCode))" -ForegroundColor Green
} catch {
    Write-Host "   FAIL - Health check failed" -ForegroundColor Red
}

Write-Host ""

# 3. Test /api/auth/social
Write-Host "3. Testing /api/auth/social..." -ForegroundColor Yellow
try {
    $body = @{
        provider = "google"
        externalToken = "demo12345"
    } | ConvertTo-Json
    
    $r = Invoke-WebRequest -Uri "$baseUrl/api/auth/social" -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -TimeoutSec 10
    
    Write-Host "   OK - Social Login working (Status: $($r.StatusCode))" -ForegroundColor Green
    $json = $r.Content | ConvertFrom-Json
    Write-Host "   Token received: $($json.data.token.Substring(0,20))..." -ForegroundColor Gray
} catch {
    $status = $_.Exception.Response.StatusCode.value__
    Write-Host "   Status: $status" -ForegroundColor Yellow
    
    # Capture error body
    try {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $errorBody = $reader.ReadToEnd()
        $errorJson = $errorBody | ConvertFrom-Json
        
        if ($status -eq 400) {
            Write-Host "   Validation (400) - Endpoint exists but rejects input" -ForegroundColor Yellow
            Write-Host "   Message: $($errorJson.message)" -ForegroundColor Gray
        } elseif ($status -eq 500) {
            Write-Host "   ERROR 500 - Incomplete deploy or DB failure" -ForegroundColor Red
            Write-Host "   Error: $($errorJson.error)" -ForegroundColor Gray
            if ($errorJson.error -match "No static resource") {
                Write-Host "   Old version without updated AuthController" -ForegroundColor Yellow
            } elseif ($errorJson.error -match "relation.*does not exist") {
                Write-Host "   Missing DB schema (set JPA_DDL_AUTO=update in Railway)" -ForegroundColor Yellow
            }
        }
    } catch {
        Write-Host "   (Could not read error body)" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "=== END VERIFICATION ===" -ForegroundColor Cyan
Write-Host ""
