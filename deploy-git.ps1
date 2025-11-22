<#
.SYNOPSIS
    Deploy rápido usando Git push a Netlify

.DESCRIPTION
    Hace commit y push de los cambios para deployment automático
#>

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "   DrakkarPress - Git Deploy           " -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Verificar que estamos en un repo git
if (-not (Test-Path ".git")) {
    Write-Host "[ERROR] No es un repositorio Git" -ForegroundColor Red
    exit 1
}

# Ver status
Write-Host "[1/5] Estado del repositorio:" -ForegroundColor Yellow
git status --short

# Agregar archivos
Write-Host "`n[2/5] Agregando archivos modificados..." -ForegroundColor Yellow
git add index.html
git add login.html
git add js/api-client.js
git add netlify.toml
git add deploy-netlify.ps1
git add START-INSTAGRAM.ps1
git add README-INSTAGRAM.md
git add NETLIFY_ENV.md

Write-Host "  [OK] Archivos agregados" -ForegroundColor Green

# Hacer commit
Write-Host "`n[3/5] Creando commit..." -ForegroundColor Yellow
$commitMessage = "feat: Nueva interfaz Instagram Edition

- Diseño moderno estilo Instagram con sidebar vertical
- Feed de posts con likes y comentarios
- Login moderno integrado con backend
- Cliente API con autenticación JWT
- Configuración CORS para producción
- Proxy API en Netlify para backend
- Scripts de deployment automatizados
"

git commit -m $commitMessage

if ($LASTEXITCODE -eq 0) {
    Write-Host "  [OK] Commit creado" -ForegroundColor Green
} else {
    Write-Host "  [!] No hay cambios para commitear" -ForegroundColor Yellow
}

# Ver branch actual
Write-Host "`n[4/5] Branch actual:" -ForegroundColor Yellow
$currentBranch = git branch --show-current
Write-Host "  -> $currentBranch" -ForegroundColor Cyan

# Push
Write-Host "`n[5/5] Haciendo push a GitHub..." -ForegroundColor Yellow
git push origin $currentBranch

if ($LASTEXITCODE -eq 0) {
    Write-Host "`n========================================" -ForegroundColor Green
    Write-Host "        PUSH EXITOSO                    " -ForegroundColor Green
    Write-Host "========================================`n" -ForegroundColor Green
    
    Write-Host "Netlify desplegara automaticamente desde GitHub" -ForegroundColor Cyan
    Write-Host "`nMonitorea el deploy en:" -ForegroundColor Yellow
    Write-Host "  https://app.netlify.com/sites/drakkarpress/deploys" -ForegroundColor White
    Write-Host "`nSitio estara disponible en:" -ForegroundColor Yellow
    Write-Host "  https://www.drakkarpress.com" -ForegroundColor White
    Write-Host "  (Espera 1-2 minutos para el build)`n" -ForegroundColor Gray
} else {
    Write-Host "`n[ERROR] Push fallido" -ForegroundColor Red
    Write-Host "Verifica tu configuracion de Git" -ForegroundColor Yellow
    exit 1
}
