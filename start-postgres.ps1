# QUICK FIX: Instalar PostgreSQL con Docker

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "  INSTALANDO POSTGRESQL CON DOCKER" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Docker
try {
    docker --version | Out-Null
    Write-Host "✅ Docker instalado" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker no encontrado" -ForegroundColor Red
    Write-Host "   Instala Docker Desktop: https://www.docker.com/products/docker-desktop" -ForegroundColor Yellow
    Write-Host "   O ejecuta PostgreSQL manualmente" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Deteniendo contenedor antiguo (si existe)..." -ForegroundColor Gray
docker stop drakkarpress-postgres 2>&1 | Out-Null
docker rm drakkarpress-postgres 2>&1 | Out-Null

Write-Host "Iniciando PostgreSQL en Docker..." -ForegroundColor Cyan
docker run -d `
    --name drakkarpress-postgres `
    -e POSTGRES_DB=drakkarpress_db `
    -e POSTGRES_USER=drakkarpress_user `
    -e POSTGRES_PASSWORD=change_this_password_123 `
    -p 5432:5432 `
    postgres:15-alpine

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ PostgreSQL iniciado!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Detalles de conexión:" -ForegroundColor Yellow
    Write-Host "  Host: localhost" -ForegroundColor Gray
    Write-Host "  Puerto: 5432" -ForegroundColor Gray
    Write-Host "  Database: drakkarpress_db" -ForegroundColor Gray
    Write-Host "  Usuario: drakkarpress_user" -ForegroundColor Gray
    Write-Host "  Password: change_this_password_123" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Esperando 5 segundos para que PostgreSQL inicie..." -ForegroundColor Gray
    Start-Sleep -Seconds 5
    Write-Host ""
    Write-Host "✅ Listo! Ahora ejecuta el backend:" -ForegroundColor Green
    Write-Host "   cd backend" -ForegroundColor Cyan
    Write-Host "   java -jar target\drakkarpress-platform-1.0.0.jar" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "❌ Error iniciando PostgreSQL" -ForegroundColor Red
}
