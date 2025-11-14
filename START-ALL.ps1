# 🚀 START EVERYTHING - DrakkarPress Beta

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "       DRAKKARPRESS - BETA LAUNCH" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Backend Mock API
Write-Host ">> Iniciando Backend API (Mock - No DB)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
    "Write-Host 'Backend API' -ForegroundColor Cyan; cd c:\Users\SuperUsuario\DrakkarPress.com\backend; node .\mock-server.js"

Start-Sleep -Seconds 2

# 2. Desktop App
Write-Host ">> Iniciando Desktop App (Electron)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", `
    "Write-Host 'Desktop App' -ForegroundColor Cyan; cd c:\Users\SuperUsuario\DrakkarPress.com\desktop-app; npm run build:main; npx electron ."

Start-Sleep -Seconds 2

# 3. Abrir Frontend en navegador
Write-Host ">> Abriendo Frontend (HTML)..." -ForegroundColor Yellow
Start-Process "http://localhost:8080/health"

Write-Host ""
Write-Host "================================================" -ForegroundColor Green
Write-Host "           TODO INICIADO EXITOSAMENTE!" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Backend API: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Desktop App: Ventana Electron" -ForegroundColor Cyan
Write-Host "Frontend: index.html (abre manualmente)" -ForegroundColor Cyan
Write-Host ""
Write-Host "Tips:" -ForegroundColor Yellow
Write-Host "  - Backend endpoints: /api/auth, /api/creations, /api/generators" -ForegroundColor Gray
Write-Host "  - Desktop App: datos en JSON local (electron-store)" -ForegroundColor Gray
Write-Host "  - Frontend HTML: abre index.html con Live Server" -ForegroundColor Gray
Write-Host ""
Write-Host "Para detener: Cierra las ventanas de PowerShell" -ForegroundColor Gray
Write-Host ""
