<#
.SYNOPSIS
    Script de deployment a Netlify para DrakkarPress

.DESCRIPTION
    Despliega la nueva interfaz Instagram a www.drakkarpress.com
#>

param(
    [switch]$Production,
    [switch]$SkipBuild
)

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   DrakkarPress - Deploy a Netlify     " -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Verificar que estamos en el directorio correcto
if (-not (Test-Path "index.html")) {
    Write-Host "[ERROR] No se encuentra index.html" -ForegroundColor Red
    Write-Host "Ejecuta este script desde la raiz del proyecto" -ForegroundColor Yellow
    exit 1
}

# Verificar si Netlify CLI está instalado
Write-Host "[1/4] Verificando Netlify CLI..." -ForegroundColor Yellow
$netlifyCli = Get-Command netlify -ErrorAction SilentlyContinue

if (-not $netlifyCli) {
    Write-Host "  [!] Netlify CLI no encontrado. Instalando..." -ForegroundColor Yellow
    npm install -g netlify-cli
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  [ERROR] No se pudo instalar Netlify CLI" -ForegroundColor Red
        exit 1
    }
    Write-Host "  [OK] Netlify CLI instalado" -ForegroundColor Green
} else {
    Write-Host "  [OK] Netlify CLI encontrado" -ForegroundColor Green
}

# Verificar archivos necesarios
Write-Host "`n[2/4] Verificando archivos..." -ForegroundColor Yellow
$requiredFiles = @(
    "index.html",
    "login.html",
    "js/api-client.js",
    "netlify.toml"
)

$allPresent = $true
foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "  [OK] $file" -ForegroundColor Green
    } else {
        Write-Host "  [X] $file NO ENCONTRADO" -ForegroundColor Red
        $allPresent = $false
    }
}

if (-not $allPresent) {
    Write-Host "`n[ERROR] Faltan archivos necesarios" -ForegroundColor Red
    exit 1
}

# Crear archivo _redirects si no existe (backup de netlify.toml)
Write-Host "`n[3/4] Preparando configuracion..." -ForegroundColor Yellow
$redirectsContent = @"
# SPA redirect
/*  /index.html  200

# API proxy
/api/*  https://drakkarpress-backend.up.railway.app/api/:splat  200!
"@

Set-Content -Path "_redirects" -Value $redirectsContent -Force
Write-Host "  [OK] Archivo _redirects creado" -ForegroundColor Green

# Deploy
Write-Host "`n[4/4] Desplegando a Netlify..." -ForegroundColor Yellow

if ($Production) {
    Write-Host "  -> Deploy a PRODUCCION (www.drakkarpress.com)..." -ForegroundColor Cyan
    netlify deploy --prod --dir=. --message="Instagram Edition $(Get-Date -Format 'yyyy-MM-dd HH:mm')"
} else {
    Write-Host "  -> Deploy a PREVIEW (URL temporal)..." -ForegroundColor Cyan
    netlify deploy --dir=. --message="Instagram Edition Preview $(Get-Date -Format 'yyyy-MM-dd HH:mm')"
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n========================================" -ForegroundColor Green
    Write-Host "        DEPLOY EXITOSO                  " -ForegroundColor Green
    Write-Host "========================================`n" -ForegroundColor Green
    
    if ($Production) {
        Write-Host "Sitio desplegado en:" -ForegroundColor Cyan
        Write-Host "  https://www.drakkarpress.com" -ForegroundColor White
        Write-Host "  https://drakkarpress.com`n" -ForegroundColor White
    } else {
        Write-Host "Preview desplegado. URL mostrada arriba." -ForegroundColor Cyan
        Write-Host "`nPara desplegar a produccion:" -ForegroundColor Yellow
        Write-Host "  .\deploy-netlify.ps1 -Production`n" -ForegroundColor White
    }
} else {
    Write-Host "`n[ERROR] Deploy fallido" -ForegroundColor Red
    Write-Host "Verifica tu autenticacion con: netlify login" -ForegroundColor Yellow
    exit 1
}
