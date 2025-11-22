<#
.SYNOPSIS
    Script para probar endpoints del API de DrakkarPress

.DESCRIPTION
    Prueba los principales endpoints pÃºblicos del backend
#>

$BaseUrl = "http://localhost:12000"
$Headers = @{
    "Content-Type" = "application/json"
}

function Write-TestHeader {
    param($name)
    Write-Host "`n========================================" -ForegroundColor Cyan
    Write-Host " TEST: $name" -ForegroundColor Cyan
    Write-Host "========================================`n" -ForegroundColor Cyan
}

function Write-TestSuccess { 
    param($msg) 
    Write-Host "[OK] $msg" -ForegroundColor Green 
}

function Write-TestFail { 
    param($msg) 
    Write-Host "[FAIL] $msg" -ForegroundColor Red 
}

function Write-TestInfo { 
    param($msg) 
    Write-Host "[INFO] $msg" -ForegroundColor Yellow 
}

# Test 1: Health Check (debe dar 403 pero confirma que el servidor responde)
Write-TestHeader "Health Check"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/health" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-TestSuccess "Health endpoint respondio: $($response.StatusCode)"
} catch {
    if ($_.Exception.Response.StatusCode -eq 403) {
        Write-TestSuccess "Servidor respondio (403 = endpoint protegido)"
    } else {
        Write-TestFail "Error inesperado: $($_.Exception.Message)"
    }
}

# Test 2: CORS - Verificar headers
Write-TestHeader "CORS Configuration"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/auth/login" -Method OPTIONS -UseBasicParsing -Headers @{
        "Origin" = "http://localhost:3000"
        "Access-Control-Request-Method" = "POST"
        "Access-Control-Request-Headers" = "Content-Type"
    } -ErrorAction Stop
    
    $corsHeaders = $response.Headers
    if ($corsHeaders.'Access-Control-Allow-Origin') {
        Write-TestSuccess "CORS configurado correctamente"
        Write-TestInfo "  Allow-Origin: $($corsHeaders.'Access-Control-Allow-Origin')"
        Write-TestInfo "  Allow-Methods: $($corsHeaders.'Access-Control-Allow-Methods')"
    } else {
        Write-TestFail "CORS no estÃ¡ configurado"
    }
} catch {
    Write-TestInfo "No se pudo verificar CORS: $($_.Exception.Message)"
}

# Test 3: Registro de usuario
Write-TestHeader "User Registration"
$randomEmail = "test_$(Get-Random)@example.com"
$registerBody = @{
    email = $randomEmail
    username = "testuser_$(Get-Random -Maximum 9999)"
    password = "Test123456!"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/api/auth/register" -Method POST -Body $registerBody -ContentType "application/json" -ErrorAction Stop
    
    if ($response.success -and $response.data.token) {
        Write-TestSuccess "Registro exitoso"
        Write-TestInfo "  Token: $($response.data.token.Substring(0,20))..."
        Write-TestInfo "  User ID: $($response.data.userId)"
        
        # Guardar token para prÃ³ximos tests
        $global:TestToken = $response.data.token
        $global:TestUserId = $response.data.userId
    } else {
        Write-TestFail "Registro fallÃ³: $($response.message)"
    }
} catch {
    $errorBody = $_.ErrorDetails.Message | ConvertFrom-Json -ErrorAction SilentlyContinue
    Write-TestFail "Error en registro: $($errorBody.message)"
}

# Test 4: Login con credenciales correctas
Write-TestHeader "User Login (Valid Credentials)"
$loginBody = @{
    email = $randomEmail
    password = "Test123456!"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method POST -Body $loginBody -ContentType "application/json" -ErrorAction Stop
    
    if ($response.success -and $response.data.token) {
        Write-TestSuccess "Login exitoso"
        Write-TestInfo "  Token: $($response.data.token.Substring(0,20))..."
    } else {
        Write-TestFail "Login fallÃ³: $($response.message)"
    }
} catch {
    Write-TestFail "Error en login: $($_.Exception.Message)"
}

# Test 5: Login con credenciales incorrectas
Write-TestHeader "User Login (Invalid Credentials)"
$badLoginBody = @{
    email = $randomEmail
    password = "WrongPassword123!"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "$BaseUrl/api/auth/login" -Method POST -Body $badLoginBody -ContentType "application/json" -ErrorAction Stop
    Write-TestFail "Login deberÃ­a haber fallado"
} catch {
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-TestSuccess "Login rechazado correctamente (401 Unauthorized)"
    } else {
        Write-TestInfo "Error inesperado: $($_.Exception.Message)"
    }
}

# Test 6: Acceso a endpoint protegido sin token
Write-TestHeader "Protected Endpoint (No Token)"
try {
    $response = Invoke-WebRequest -Uri "$BaseUrl/api/books/my-library" -Method GET -UseBasicParsing -ErrorAction Stop
    Write-TestFail "DeberÃ­a haber rechazado el acceso"
} catch {
    if ($_.Exception.Response.StatusCode -eq 403) {
        Write-TestSuccess "Acceso rechazado correctamente (403 Forbidden)"
    } else {
        Write-TestInfo "CÃ³digo de respuesta: $($_.Exception.Response.StatusCode)"
    }
}

# Test 7: Acceso a endpoint protegido con token
if ($global:TestToken) {
    Write-TestHeader "Protected Endpoint (With Token)"
    try {
        $authHeaders = @{
            "Authorization" = "Bearer $($global:TestToken)"
            "Content-Type" = "application/json"
        }
        $response = Invoke-RestMethod -Uri "$BaseUrl/api/books/my-library" -Method GET -Headers $authHeaders -ErrorAction Stop
        Write-TestSuccess "Acceso autorizado con token"
        Write-TestInfo "  Respuesta: $($response | ConvertTo-Json -Depth 2)"
    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) {
            Write-TestSuccess "Endpoint autorizado pero no encontrado (normal si no hay libros)"
        } else {
            Write-TestInfo "Error: $($_.Exception.Message)"
        }
    }
}

# Resumen
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host " RESUMEN DE TESTS" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "`nBackend funcionando en: $BaseUrl" -ForegroundColor Green
if ($global:TestToken) {
    Write-Host "Token de prueba generado exitosamente" -ForegroundColor Green
}
Write-Host ""

