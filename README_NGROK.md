# Desarrollo con ngrok

## Requisitos
- Java 21
- Maven Wrapper
- ngrok instalado (https://ngrok.com)

## Pasos rápidos (Linux/Mac)
./scripts/dev-ngrok.sh
# Copia la URL pública mostrada (ej: https://abc123.ngrok-free.app)
# Frontend: export NEXT_PUBLIC_API_ORIGIN=https://abc123.ngrok-free.app

## Windows (PowerShell)
./scripts/dev-ngrok.ps1
# Mismo procedimiento.

## CORS
La variable ALLOWED_ORIGINS se genera en .env.ngrok; puedes:
ALLOWED_ORIGINS=$(cat .env.ngrok | cut -d= -f2) ./mvnw -Dspring.profiles.active=dev spring-boot:run

## Cambio de túnel
Cada reinicio cambia subdominio. Reejecuta script y actualiza NEXT_PUBLIC_API_ORIGIN.

## Producción
No uses ngrok en producción; migra a:
- drakkarpress.com (Vercel / frontend)
- api.drakkarpress.com (backend propio)
