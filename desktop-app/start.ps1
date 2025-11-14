# Script simple para iniciar DrakkarPress Desktop en desarrollo

Write-Host "Iniciando DrakkarPress Desktop..." -ForegroundColor Cyan

# Compilar main process
Write-Host "Compilando TypeScript..." -ForegroundColor Yellow
npm run build:main

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilacion exitosa!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Iniciando Electron..." -ForegroundColor Yellow
    npx electron .
} else {
    Write-Host "Error en la compilacion" -ForegroundColor Red
}
