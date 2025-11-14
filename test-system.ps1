# Test del sistema completo - DrakkarPress Beta

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "       DRAKKARPRESS - TESTS AUTOMATICOS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

$testsPassed = 0
$testsFailed = 0

# Test 1: Backend Health Check
Write-Host "[TEST 1] Backend API Health Check..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/health" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "  PASS: Backend respondiendo correctamente" -ForegroundColor Green
        Write-Host "  Response: $($response.Content)" -ForegroundColor Gray
        $testsPassed++
    } else {
        Write-Host "  FAIL: Status code incorrecto: $($response.StatusCode)" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  FAIL: Backend no responde" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 2: Backend - Crear usuario (mock)
Write-Host "[TEST 2] Backend API - Register endpoint..." -ForegroundColor Yellow
try {
    $body = @{
        username = "testuser"
        email = "test@drakkarpress.com"
        password = "test123"
    } | ConvertTo-Json

    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/auth/register" `
        -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -TimeoutSec 5
    
    if ($response.StatusCode -eq 200) {
        Write-Host "  PASS: Usuario creado correctamente" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  FAIL: Error creando usuario" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  FAIL: Error en register endpoint" -ForegroundColor Red
    Write-Host "  Error: $_" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 3: Backend - Listar creaciones
Write-Host "[TEST 3] Backend API - Get creations..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/creations" -UseBasicParsing -TimeoutSec 5
    if ($response.StatusCode -eq 200) {
        Write-Host "  PASS: Creations endpoint OK" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  FAIL: Error obteniendo creations" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  FAIL: Error en creations endpoint" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 4: Backend - Crear creación
Write-Host "[TEST 4] Backend API - Create creation..." -ForegroundColor Yellow
try {
    $body = @{
        title = "Test Book"
        type = "book"
        content = "This is a test book content"
        genre = "fiction"
    } | ConvertTo-Json

    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/creations" `
        -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -TimeoutSec 5
    
    if ($response.StatusCode -eq 200) {
        Write-Host "  PASS: Creacion creada correctamente" -ForegroundColor Green
        $creationData = $response.Content | ConvertFrom-Json
        Write-Host "  Creation ID: $($creationData.id)" -ForegroundColor Gray
        $testsPassed++
        
        # Guardar ID para test siguiente
        $script:testCreationId = $creationData.id
    } else {
        Write-Host "  FAIL: Error creando creacion" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  FAIL: Error en create creation" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 5: Backend - Generator
Write-Host "[TEST 5] Backend API - Generator endpoint..." -ForegroundColor Yellow
try {
    $body = @{
        type = "book"
        genre = "fantasy"
        length = "short"
    } | ConvertTo-Json

    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/generators/generate" `
        -Method POST -Body $body -ContentType "application/json" -UseBasicParsing -TimeoutSec 5
    
    if ($response.StatusCode -eq 200) {
        Write-Host "  PASS: Generator funcionando" -ForegroundColor Green
        $testsPassed++
    } else {
        Write-Host "  FAIL: Error en generator" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  FAIL: Error en generator endpoint" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 6: Verificar archivos Desktop App
Write-Host "[TEST 6] Desktop App - Archivos compilados..." -ForegroundColor Yellow
$desktopAppFiles = @(
    "desktop-app\dist\main\index.js",
    "desktop-app\dist\main\ipc\handlers.js",
    "desktop-app\dist\main\services\creations.service.js",
    "desktop-app\dist\main\database\connection.js"
)

$allFilesExist = $true
foreach ($file in $desktopAppFiles) {
    $fullPath = "c:\Users\SuperUsuario\DrakkarPress.com\$file"
    if (Test-Path $fullPath) {
        Write-Host "  OK: $file" -ForegroundColor Gray
    } else {
        Write-Host "  MISSING: $file" -ForegroundColor Red
        $allFilesExist = $false
    }
}

if ($allFilesExist) {
    Write-Host "  PASS: Todos los archivos compilados existen" -ForegroundColor Green
    $testsPassed++
} else {
    Write-Host "  FAIL: Faltan archivos compilados" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""

# Test 7: Verificar Java 21
Write-Host "[TEST 7] Java 21 LTS instalacion..." -ForegroundColor Yellow
$env:JAVA_HOME = "C:\Users\SuperUsuario\.jdk\jdk-21.0.8"
$env:PATH = "C:\Users\SuperUsuario\.jdk\jdk-21.0.8\bin;$env:PATH"
try {
    $javaVersion = java -version 2>&1 | Select-String "21.0"
    if ($javaVersion) {
        Write-Host "  PASS: Java 21 instalado correctamente" -ForegroundColor Green
        Write-Host "  Version: $javaVersion" -ForegroundColor Gray
        $testsPassed++
    } else {
        Write-Host "  FAIL: Java 21 no encontrado" -ForegroundColor Red
        $testsFailed++
    }
} catch {
    Write-Host "  FAIL: Error verificando Java" -ForegroundColor Red
    $testsFailed++
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "           RESUMEN DE TESTS" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Tests Pasados: $testsPassed" -ForegroundColor Green
Write-Host "Tests Fallidos: $testsFailed" -ForegroundColor Red
Write-Host ""

$totalTests = $testsPassed + $testsFailed
$successRate = [math]::Round(($testsPassed / $totalTests) * 100, 2)

Write-Host "Tasa de exito: $successRate%" -ForegroundColor $(if ($successRate -ge 80) { "Green" } else { "Yellow" })
Write-Host ""

if ($testsFailed -eq 0) {
    Write-Host "================================================" -ForegroundColor Green
    Write-Host "   TODOS LOS TESTS PASARON - SISTEMA OK!" -ForegroundColor Green
    Write-Host "================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Listo para:" -ForegroundColor Yellow
    Write-Host "  1. Testing manual de funcionalidades" -ForegroundColor Gray
    Write-Host "  2. Deployment a produccion (.\deploy-maestro.ps1)" -ForegroundColor Gray
    Write-Host "  3. Generar instalador Windows" -ForegroundColor Gray
} else {
    Write-Host "================================================" -ForegroundColor Yellow
    Write-Host "   ALGUNOS TESTS FALLARON - REVISAR" -ForegroundColor Yellow
    Write-Host "================================================" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Posibles causas:" -ForegroundColor Yellow
    Write-Host "  - Backend no esta corriendo (ejecuta .\START-ALL.ps1)" -ForegroundColor Gray
    Write-Host "  - Desktop App no compilo correctamente" -ForegroundColor Gray
    Write-Host "  - Java no esta en el PATH" -ForegroundColor Gray
}

Write-Host ""
