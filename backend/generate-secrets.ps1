# ==========================================
# GENERADOR DE SECRETOS DE PRODUCCIÓN
# DrakkarPress Security Credentials Generator
# ==========================================

Write-Host ""
Write-Host "🔐 GENERADOR DE SECRETOS - DRAKKARPRESS" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Función para generar strings aleatorios seguros
function Generate-SecureString {
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

# Función para generar JWT Secret (base64)
function Generate-JWTSecret {
    $bytes = New-Object byte[] 64
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    $rng.GetBytes($bytes)
    return [Convert]::ToBase64String($bytes)
}

# Función para generar password de base de datos
function Generate-DatabasePassword {
    $upper = 'ABCDEFGHJKLMNPQRSTUVWXYZ' # Sin I, O para evitar confusión
    $lower = 'abcdefghijkmnopqrstuvwxyz' # Sin l para evitar confusión
    $numbers = '23456789' # Sin 0, 1 para evitar confusión
    $special = '!@#$%^&*()-_=+'
    
    $password = ""
    $password += $upper[(Get-Random -Maximum $upper.Length)]
    $password += $lower[(Get-Random -Maximum $lower.Length)]
    $password += $numbers[(Get-Random -Maximum $numbers.Length)]
    $password += $special[(Get-Random -Maximum $special.Length)]
    
    # Completar hasta 32 caracteres
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

# Función para generar API Key
function Generate-APIKey {
    $prefix = "dk_live_"
    $key = Generate-SecureString -Length 48 -AlphaNumericOnly
    return "$prefix$key"
}

# Generar todos los secretos
Write-Host "⚙️  Generando secretos..." -ForegroundColor Yellow
Write-Host ""

$secrets = @{
    "JWT_SECRET" = Generate-JWTSecret
    "JWT_REFRESH_SECRET" = Generate-JWTSecret
    "DATABASE_PASSWORD" = Generate-DatabasePassword
    "ENCRYPTION_KEY" = Generate-SecureString -Length 64 -AlphaNumericOnly
    "API_KEY" = Generate-APIKey
    "SESSION_SECRET" = Generate-SecureString -Length 64
    "WEBHOOK_SECRET" = Generate-SecureString -Length 32 -AlphaNumericOnly
    "ADMIN_PANEL_PASSWORD" = Generate-DatabasePassword
}

# Generar archivo .env de producción
$envContent = @"
# ==========================================
# DRAKKARPRESS - PRODUCTION ENVIRONMENT
# Generado: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
# ==========================================

# ============ DATABASE ============
DATABASE_URL=jdbc:postgresql://tu-servidor-postgres.com:5432/drakkarpress_prod
DATABASE_USERNAME=drakkarpress_user
DATABASE_PASSWORD=$($secrets.DATABASE_PASSWORD)

# Pool de Conexiones
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=30000
SPRING_DATASOURCE_HIKARI_IDLE_TIMEOUT=600000
SPRING_DATASOURCE_HIKARI_MAX_LIFETIME=1800000

# ============ JWT TOKENS ============
JWT_SECRET=$($secrets.JWT_SECRET)
JWT_EXPIRATION_MS=3600000
JWT_REFRESH_SECRET=$($secrets.JWT_REFRESH_SECRET)
JWT_REFRESH_EXPIRATION_MS=604800000

# ============ ENCRYPTION ============
ENCRYPTION_KEY=$($secrets.ENCRYPTION_KEY)
ENCRYPTION_ALGORITHM=AES/GCM/NoPadding

# ============ API SECURITY ============
API_KEY=$($secrets.API_KEY)
WEBHOOK_SECRET=$($secrets.WEBHOOK_SECRET)
CORS_ALLOWED_ORIGINS=https://www.drakkarpress.com,https://drakkarpress.com
CSRF_ENABLED=true

# ============ LULU.COM ============
LULU_CLIENT_KEY=a10cc795-35a4-4239-ae41-f78e6abb0df0
LULU_CLIENT_SECRET=sIyhz2KiOoJfHAcRxkLETMoq6LquCc87
LULU_API_URL=https://api.lulu.com/v1
LULU_API_BASE64=Basic YTEwY2M3OTUtMzVhNC00MjM5LWFlNDEtZjc4ZTZhYmIwZGYwOnNJeWh6MktpT29KZkhBY1J4a0xFVE1vcTZMcXVDYzg3

# ============ SHOPIFY ============
SHOPIFY_APP_CLIENT_ID=ddc72267b2a7244f8f7858961ec7d325
SHOPIFY_APP_CLIENT_SECRET=TU_CLIENT_SECRET_AQUI
SHOPIFY_WEBHOOK_SECRET=$($secrets.WEBHOOK_SECRET)
SHOPIFY_API_VERSION=2024-10

# ============ STRIPE PAYMENTS ============
STRIPE_PUBLIC_KEY=pk_live_TU_PUBLIC_KEY_AQUI
STRIPE_SECRET_KEY=sk_live_TU_SECRET_KEY_AQUI
STRIPE_WEBHOOK_SECRET=whsec_TU_WEBHOOK_SECRET_AQUI
STRIPE_CURRENCY=USD

# Precios por fase (en centavos)
STRIPE_PRICE_PHASE_1=500
STRIPE_PRICE_PHASE_2=1000
STRIPE_PRICE_PHASE_3=1999

# ============ AWS S3 ============
AWS_ACCESS_KEY_ID=TU_ACCESS_KEY_AQUI
AWS_SECRET_ACCESS_KEY=TU_SECRET_ACCESS_KEY_AQUI
AWS_REGION=us-east-1
AWS_S3_BUCKET_NAME=drakkarpress-production
AWS_S3_BUCKET_BOOKS=drakkarpress-books
AWS_S3_BUCKET_COVERS=drakkarpress-covers
AWS_S3_BUCKET_AVATARS=drakkarpress-avatars

# ============ EMAIL (SMTP) ============
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=noreply@drakkarpress.com
SMTP_PASSWORD=TU_APP_PASSWORD_AQUI
SMTP_FROM_EMAIL=noreply@drakkarpress.com
SMTP_FROM_NAME=DrakkarPress
SMTP_TLS_ENABLED=true

# Plantillas de Email
EMAIL_VERIFY_URL=https://www.drakkarpress.com/verify-email
EMAIL_RESET_PASSWORD_URL=https://www.drakkarpress.com/reset-password

# ============ OPENAI / IA ============
OPENAI_API_KEY=sk-TU_OPENAI_KEY_AQUI
OPENAI_MODEL=gpt-4-turbo-preview
OPENAI_MAX_TOKENS=4000
OPENAI_TEMPERATURE=0.7

# Límites por plan
AI_LIMIT_PHASE_1=1000
AI_LIMIT_PHASE_2=500
AI_LIMIT_PHASE_3=200
AI_LIMIT_GRANDFATHERED=999999
AI_LIMIT_COURTESY=999999

# ============ REDIS CACHE ============
REDIS_HOST=tu-redis-server.com
REDIS_PORT=6379
REDIS_PASSWORD=TU_REDIS_PASSWORD_AQUI
REDIS_DATABASE=0
REDIS_TTL_SECONDS=3600

# ============ ADMIN PANEL ============
ADMIN_EMAIL=admin@drakkarpress.com
ADMIN_PASSWORD=$($secrets.ADMIN_PANEL_PASSWORD)
ADMIN_PANEL_URL=https://admin.drakkarpress.com

# ============ MONITORING ============
SENTRY_DSN=https://tu-sentry-dsn.ingest.sentry.io/1234567
NEW_RELIC_LICENSE_KEY=TU_NEW_RELIC_KEY_AQUI
NEW_RELIC_APP_NAME=DrakkarPress-Production

# ============ ENVIRONMENT ============
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_DRAKKARPRESS=INFO

# ============ SECURITY HEADERS ============
SECURITY_HEADERS_HSTS_ENABLED=true
SECURITY_HEADERS_XSS_PROTECTION=true
SECURITY_HEADERS_CONTENT_TYPE_OPTIONS=true
SECURITY_HEADERS_FRAME_OPTIONS=DENY

# ============ RATE LIMITING ============
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=60
RATE_LIMIT_BURST_CAPACITY=100

# ============ SESSION ============
SESSION_SECRET=$($secrets.SESSION_SECRET)
SESSION_TIMEOUT_MINUTES=60
SESSION_MAX_CONCURRENT=3

# ============ BACKUP ============
BACKUP_S3_BUCKET=drakkarpress-backups
BACKUP_SCHEDULE_CRON=0 2 * * *
BACKUP_RETENTION_DAYS=30
"@

# Guardar .env de producción
$envPath = Join-Path $PSScriptRoot ".env.production"
$envContent | Out-File -FilePath $envPath -Encoding UTF8 -NoNewline

Write-Host "✅ Archivo generado: .env.production" -ForegroundColor Green
Write-Host ""

# Mostrar secretos importantes
Write-Host "📋 SECRETOS GENERADOS (GUARDAR EN LUGAR SEGURO)" -ForegroundColor Cyan
Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host ""

Write-Host "🔑 JWT Secret:" -ForegroundColor Yellow
Write-Host $secrets.JWT_SECRET -ForegroundColor White
Write-Host ""

Write-Host "🔑 JWT Refresh Secret:" -ForegroundColor Yellow
Write-Host $secrets.JWT_REFRESH_SECRET -ForegroundColor White
Write-Host ""

Write-Host "🗄️  Database Password:" -ForegroundColor Yellow
Write-Host $secrets.DATABASE_PASSWORD -ForegroundColor White
Write-Host ""

Write-Host "🔐 Encryption Key:" -ForegroundColor Yellow
Write-Host $secrets.ENCRYPTION_KEY -ForegroundColor White
Write-Host ""

Write-Host "🌐 API Key:" -ForegroundColor Yellow
Write-Host $secrets.API_KEY -ForegroundColor White
Write-Host ""

Write-Host "🪝 Webhook Secret:" -ForegroundColor Yellow
Write-Host $secrets.WEBHOOK_SECRET -ForegroundColor White
Write-Host ""

Write-Host "👤 Admin Panel Password:" -ForegroundColor Yellow
Write-Host $secrets.ADMIN_PANEL_PASSWORD -ForegroundColor White
Write-Host ""

Write-Host "🔒 Session Secret:" -ForegroundColor Yellow
Write-Host $secrets.SESSION_SECRET -ForegroundColor White
Write-Host ""

# Generar archivo de solo secretos (para KMS/Vault)
$secretsOnlyContent = @"
# DRAKKARPRESS SECRETS ONLY
# Generado: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
# ALMACENAR EN: AWS Secrets Manager / Azure Key Vault / HashiCorp Vault

JWT_SECRET=$($secrets.JWT_SECRET)
JWT_REFRESH_SECRET=$($secrets.JWT_REFRESH_SECRET)
DATABASE_PASSWORD=$($secrets.DATABASE_PASSWORD)
ENCRYPTION_KEY=$($secrets.ENCRYPTION_KEY)
API_KEY=$($secrets.API_KEY)
WEBHOOK_SECRET=$($secrets.WEBHOOK_SECRET)
ADMIN_PANEL_PASSWORD=$($secrets.ADMIN_PANEL_PASSWORD)
SESSION_SECRET=$($secrets.SESSION_SECRET)
"@

$secretsPath = Join-Path $PSScriptRoot "SECRETS_ONLY.txt"
$secretsOnlyContent | Out-File -FilePath $secretsPath -Encoding UTF8 -NoNewline

Write-Host "=" * 60 -ForegroundColor Cyan
Write-Host ""
Write-Host "📁 Archivos generados:" -ForegroundColor Green
Write-Host "  - .env.production (completo)" -ForegroundColor White
Write-Host "  - SECRETS_ONLY.txt (solo secretos)" -ForegroundColor White
Write-Host ""
Write-Host "⚠️  IMPORTANTE:" -ForegroundColor Red
Write-Host "  1. NUNCA commitear estos archivos a Git" -ForegroundColor Yellow
Write-Host "  2. Guardar SECRETS_ONLY.txt en gestor de claves (Vault/KMS)" -ForegroundColor Yellow
Write-Host "  3. Agregar a .gitignore: .env.production, SECRETS_ONLY.txt" -ForegroundColor Yellow
Write-Host "  4. Cambiar contraseñas de terceros (Stripe, AWS, SMTP)" -ForegroundColor Yellow
Write-Host "  5. Configurar rotación de secretos cada 90 días" -ForegroundColor Yellow
Write-Host ""
Write-Host "✅ Generación completada exitosamente!" -ForegroundColor Green
Write-Host ""

# Agregar a .gitignore
$gitignorePath = Join-Path (Split-Path $PSScriptRoot) ".gitignore"
$gitignoreEntries = @"

# Secretos de producción (generados automáticamente)
backend/.env.production
backend/SECRETS_ONLY.txt
.env.production
SECRETS_ONLY.txt
"@

if (Test-Path $gitignorePath) {
    Add-Content -Path $gitignorePath -Value $gitignoreEntries
    Write-Host "📝 Actualizado .gitignore" -ForegroundColor Green
} else {
    Write-Host "⚠️  No se encontró .gitignore, crear manualmente" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "🚀 Próximos pasos:" -ForegroundColor Cyan
Write-Host "  1. Revisar y completar .env.production con credenciales de terceros" -ForegroundColor White
Write-Host "  2. Configurar servidor PostgreSQL con DATABASE_PASSWORD" -ForegroundColor White
Write-Host "  3. Subir secretos a AWS Secrets Manager o similar" -ForegroundColor White
Write-Host "  4. Configurar variables de entorno en servidor de producción" -ForegroundColor White
Write-Host "  5. Probar conexión a base de datos" -ForegroundColor White
Write-Host ""
