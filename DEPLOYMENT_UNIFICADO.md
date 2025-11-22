# Despliegue Unificado (Frontend + Backend)

Este proyecto se despliega de forma unificada con Docker Compose:
- `nginx` sirve el frontend estático (archivos `.html`, `assets/`)
- `backend` (Spring Boot 3.5 / Java 21) expone `/api/*`
- `db` (PostgreSQL 16) persistente con volumen `db_data`
- `certbot` gestiona certificados Let’s Encrypt para producción

## Requisitos
- Docker Desktop (Windows)
- Puertos libres: 80, 443, 8080

## Variables
Crea `.env` en la raíz (o usa `deploy-local.ps1` que lo genera con defaults):

```
DB_NAME=drakkar
DB_USER=drakkar
DB_PASSWORD=drakkar
DOMAIN=localhost
```

En producción, ajusta `DOMAIN` a tu dominio real.

## Pasos (Local)
1. Abrir PowerShell en la raíz del repo.
2. Ejecutar:

```powershell
./deploy-local.ps1
```

El script:
- crea `.env` si no existe
- `docker-compose up -d --build`
- espera `http://localhost:8080/actuator/health`
- verifica `http://localhost` (Nginx + frontend)

## Pasos (Producción)
1. Configura DNS del dominio al servidor (A/AAAA).
2. Crea `.env` con `DOMAIN=tu-dominio.com` y credenciales reales.
3. Ejecuta:

```powershell
# en el servidor (o via SSH)
docker compose up -d --build
# emitir certificados (si es primera vez)
docker run --rm -it -v %cd%/certbot/conf:/etc/letsencrypt -v %cd%/certbot/www:/var/www/certbot certbot/certbot certonly --webroot -w /var/www/certbot -d tu-dominio.com -d www.tu-dominio.com --agree-tos -m tu@email.com --no-eff-email
# recargar Nginx si aplica
```

Nginx ya:
- sirve estáticos desde `/usr/share/nginx/html`
- aplica caché 30d para assets (`css|js|img|fonts`)
- proxyea `/api/*` al backend
- expone `/actuator/health`

## Notas sobre Vercel/Netlify
- `vercel.json` y `netlify.toml` quedan como opción alternativa de hosting solo-frontend.
- El flujo recomendado es Docker Compose (esta guía) para tener un solo dominio con `/api` y frontend juntos.

## Logs útiles
```powershell
docker logs drakkar_backend --tail=200
docker logs drakkar_nginx --tail=200
docker logs drakkar_db --tail=200
```

## Estructura
- `nginx/drakkarpress.conf`: configuración Nginx (ya actualizada para servir estáticos + proxy `/api`)
- `docker-compose.yml`: orquesta `db`, `backend`, `nginx`, `certbot`

## Troubleshooting
- 502/404 al abrir `/`: verifica que el volumen de Nginx monta la raíz del repo y que existe `index.html`.
- Backend DOWN: revisa `SPRING_DATASOURCE_*` del compose y conectividad a `db`.
- Certificados: el bloque `server:80` ya expone `/.well-known/acme-challenge/` para HTTP-01.
