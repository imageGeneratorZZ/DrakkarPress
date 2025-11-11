# GENERADOR DE SECRETOS DE PRODUCCION - DRAKKARPRESS
# PowerShell Script

Write-Host ""
Write-Host "GENERADOR DE SECRETOS - DRAKKARPRESS" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Funcion para generar strings aleatorios seguros
function New-SecureString {
    param (
        [int]$Length = 64,
        [switch]$AlphaNumericOnly
    )
    
    if ($AlphaNumericOnly) {
        $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
    } else {
        $chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?'
    }
    
    $random = 1..$Length | ForEach-Object { Get-Random -Maximum $chars.Length }
    $private:ofs = ""
    return [String]$chars[$random]
}

# Funcion para generar JWT Secret (base64)
function New-JWTSecret {
    $bytes = New-Object byte[] 64
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($bytes)
    return [Convert]::ToBase64String($bytes)
}

# Funcion para generar password de base de datos
function New-DatabasePassword {
    $upper = 'ABCDEFGHJKLMNPQRSTUVWXYZ'
    $lower = 'abcdefghijkmnopqrstuvwxyz'
    $numbers = '23456789'
    $special = '!@#$%^&*()-_=+'
    
    $password = ""
    $password += $upper[(Get-Random -Maximum $upper.Length)]
    $password += $lower[(Get-Random -Maximum $lower.Length)]
    $password += $numbers[(Get-Random -Maximum $numbers.Length)]
    $password += $special[(Get-Random -Maximum $special.Length)]
    
    $allChars = $upper + $lower + $numbers + $special
    for ($i = 0; $i -lt 28; $i++) {
        $password += $allChars[(Get-Random -Maximum $allChars.Length)]
    }
    
    # Mezclar caracteres
    $passwordArray = $password.ToCharArray()
    $random = [System.Random]::new()
    for ($i = $passwordArray.Length - 1; $i -gt 0; $i--) {
        $j = $random.Next(0, $i + 1)
        $temp = $passwordArray[$i]
        $passwordArray[$i] = $passwordArray[$j]
        $passwordArray[$j] = $temp
    }
    
    return -join $passwordArray
}

# Generar todos los secretos
Write-Host "Generando secretos..." -ForegroundColor Yellow
Write-Host ""

$jwtSecret = New-JWTSecret
$jwtRefreshSecret = New-JWTSecret
$dbPassword = New-DatabasePassword
$encryptionKey = New-SecureString -Length 64 -AlphaNumericOnly
$apiKey = "dk_live_" + (New-SecureString -Length 48 -AlphaNumericOnly)
$sessionSecret = New-SecureString -Length 64
$webhookSecret = New-SecureString -Length 32 -AlphaNumericOnly
$adminPassword = New-DatabasePassword

Write-Host "SECRETOS GENERADOS" -ForegroundColor Green
Write-Host "==================" -ForegroundColor Green
Write-Host ""
Write-Host "JWT Secret:" -ForegroundColor Yellow
Write-Host $jwtSecret
Write-Host ""
Write-Host "JWT Refresh Secret:" -ForegroundColor Yellow
Write-Host $jwtRefreshSecret
Write-Host ""
Write-Host "Database Password:" -ForegroundColor Yellow
Write-Host $dbPassword
Write-Host ""
Write-Host "Encryption Key:" -ForegroundColor Yellow
Write-Host $encryptionKey
Write-Host ""
Write-Host "API Key:" -ForegroundColor Yellow
Write-Host $apiKey
Write-Host ""
Write-Host "Webhook Secret:" -ForegroundColor Yellow
Write-Host $webhookSecret
Write-Host ""
Write-Host "Admin Password:" -ForegroundColor Yellow
Write-Host $adminPassword
Write-Host ""
Write-Host "Session Secret:" -ForegroundColor Yellow
Write-Host $sessionSecret
Write-Host ""

# Guardar en archivo
$timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
$content = @"
# DRAKKARPRESS PRODUCTION SECRETS
# Generado: $timestamp

JWT_SECRET=$jwtSecret
JWT_REFRESH_SECRET=$jwtRefreshSecret
DATABASE_PASSWORD=$dbPassword
ENCRYPTION_KEY=$encryptionKey
API_KEY=$apiKey
WEBHOOK_SECRET=$webhookSecret
ADMIN_PASSWORD=$adminPassword
SESSION_SECRET=$sessionSecret
"@

$secretsFile = Join-Path $PSScriptRoot "SECRETS_ONLY.txt"
$content | Out-File -FilePath $secretsFile -Encoding UTF8

Write-Host "Archivo guardado: SECRETS_ONLY.txt" -ForegroundColor Green
Write-Host ""
Write-Host "IMPORTANTE: Guardar este archivo en lugar seguro" -ForegroundColor Red
Write-Host "NO commitear a Git" -ForegroundColor Red
Write-Host ""
