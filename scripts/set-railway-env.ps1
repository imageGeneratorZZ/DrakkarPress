# Railway CLI Setup & Deployment Script
# Run from repo root: .\scripts\set-railway-env.ps1

Write-Host "🚀 DrakkarPress - Railway Environment Setup" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan
Write-Host ""

# Check if railway CLI is available
if (-not (Get-Command railway -ErrorAction SilentlyContinue)) {
    Write-Host "❌ Railway CLI no encontrado." -ForegroundColor Red
    Write-Host "Instalar con: npm install -g @railway/cli" -ForegroundColor Yellow
    exit 1
}

# Check if logged in
$status = railway status 2>&1
if ($status -match "not linked" -or $status -match "not logged in") {
    Write-Host "⚠️  No estás logueado o vinculado a un proyecto." -ForegroundColor Yellow
    Write-Host "Ejecuta: railway login && railway link" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Railway CLI configurado correctamente" -ForegroundColor Green
Write-Host ""

# Prompt for required variables
Write-Host "📝 Ingresa las variables de entorno (Enter para saltar):" -ForegroundColor Yellow
Write-Host ""

# JWT
$JWT_SECRET = Read-Host "JWT_SECRET (genera con: openssl rand -base64 64)"
if ($JWT_SECRET) {
    railway variables set JWT_SECRET=$JWT_SECRET
    Write-Host "✅ JWT_SECRET configurado" -ForegroundColor Green
}

# Stripe
Write-Host ""
$STRIPE_API_KEY = Read-Host "STRIPE_API_KEY (sk_live_...)"
if ($STRIPE_API_KEY) {
    railway variables set STRIPE_API_KEY=$STRIPE_API_KEY
    Write-Host "✅ STRIPE_API_KEY configurado" -ForegroundColor Green
}

$STRIPE_PK = Read-Host "STRIPE_PUBLISHABLE_KEY (pk_live_...)"
if ($STRIPE_PK) {
    railway variables set STRIPE_PUBLISHABLE_KEY=$STRIPE_PK
    Write-Host "✅ STRIPE_PUBLISHABLE_KEY configurado" -ForegroundColor Green
}

$STRIPE_WH = Read-Host "STRIPE_WEBHOOK_SECRET (whsec_...)"
if ($STRIPE_WH) {
    railway variables set STRIPE_WEBHOOK_SECRET=$STRIPE_WH
    Write-Host "✅ STRIPE_WEBHOOK_SECRET configurado" -ForegroundColor Green
}

# Frontend
Write-Host ""
$FRONTEND_URL = Read-Host "APP_FRONTEND_URL (https://tu-sitio.netlify.app)"
if ($FRONTEND_URL) {
    railway variables set APP_FRONTEND_URL=$FRONTEND_URL
    railway variables set APP_FRONTEND_SUCCESS_URL="$FRONTEND_URL/checkout-success.html"
    Write-Host "✅ Frontend URLs configuradas" -ForegroundColor Green
}

# Shopify
Write-Host ""
$SHOPIFY_STORE = Read-Host "SHOPIFY_STORE_URL (https://tu-tienda.myshopify.com)"
if ($SHOPIFY_STORE) {
    railway variables set SHOPIFY_STORE_URL=$SHOPIFY_STORE
    Write-Host "✅ SHOPIFY_STORE_URL configurado" -ForegroundColor Green
}

$SHOPIFY_TOKEN = Read-Host "SHOPIFY_ACCESS_TOKEN (shpat_...)"
if ($SHOPIFY_TOKEN) {
    railway variables set SHOPIFY_ACCESS_TOKEN=$SHOPIFY_TOKEN
    Write-Host "✅ SHOPIFY_ACCESS_TOKEN configurado" -ForegroundColor Green
}

# Lulu
Write-Host ""
$LULU_KEY = Read-Host "LULU_API_KEY"
if ($LULU_KEY) {
    railway variables set LULU_API_KEY=$LULU_KEY
    railway variables set LULU_API_URL="https://api.lulu.com"
    railway variables set LULU_SANDBOX="false"
    Write-Host "✅ LULU_API_KEY configurado" -ForegroundColor Green
}

$LULU_SECRET = Read-Host "LULU_API_SECRET"
if ($LULU_SECRET) {
    railway variables set LULU_API_SECRET=$LULU_SECRET
    Write-Host "✅ LULU_API_SECRET configurado" -ForegroundColor Green
}

# Email
Write-Host ""
$MAIL_USER = Read-Host "SPRING_MAIL_USERNAME (tu-email@gmail.com)"
if ($MAIL_USER) {
    railway variables set SPRING_MAIL_USERNAME=$MAIL_USER
    railway variables set SPRING_MAIL_HOST="smtp.gmail.com"
    railway variables set SPRING_MAIL_PORT="587"
    Write-Host "✅ SPRING_MAIL_USERNAME configurado" -ForegroundColor Green
}

$MAIL_PASS = Read-Host "SPRING_MAIL_PASSWORD (app password de Gmail)" -AsSecureString
if ($MAIL_PASS.Length -gt 0) {
    $MAIL_PASS_PLAIN = [Runtime.InteropServices.Marshal]::PtrToStringAuto(
        [Runtime.InteropServices.Marshal]::SecureStringToBSTR($MAIL_PASS)
    )
    railway variables set SPRING_MAIL_PASSWORD=$MAIL_PASS_PLAIN
    Write-Host "✅ SPRING_MAIL_PASSWORD configurado" -ForegroundColor Green
}

# Database mappings (Railway auto-provides these)
Write-Host ""
Write-Host "🗄️  Configurando mapeo de base de datos..." -ForegroundColor Cyan
railway variables set 'SPRING_DATASOURCE_URL=${DATABASE_URL}'
railway variables set 'SPRING_DATASOURCE_USERNAME=${PGUSER}'
railway variables set 'SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}'
Write-Host "✅ Mapeo de base de datos configurado" -ForegroundColor Green

Write-Host ""
Write-Host "✅ Configuración completa!" -ForegroundColor Green
Write-Host ""
Write-Host "📋 Próximos pasos:" -ForegroundColor Cyan
Write-Host "  1. Verifica variables: railway variables" -ForegroundColor White
Write-Host "  2. Build backend: cd backend && .\mvnw.cmd -q -DskipTests clean package" -ForegroundColor White
Write-Host "  3. Deploy: railway up" -ForegroundColor White
Write-Host "  4. Logs: railway logs" -ForegroundColor White
Write-Host "  5. Domain: railway domain" -ForegroundColor White
Write-Host ""
Write-Host "🌐 Webhooks a configurar después del deploy:" -ForegroundColor Yellow
Write-Host "  - Stripe: https://TU-BACKEND.railway.app/api/payments/webhook" -ForegroundColor White
Write-Host "  - Shopify: https://TU-BACKEND.railway.app/api/shopify/webhooks/orders" -ForegroundColor White
