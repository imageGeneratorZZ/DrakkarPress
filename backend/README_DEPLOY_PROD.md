## Despliegue Producción DrakkarPress Backend

### 1. DNS
- Frontend (Netlify):
  - `www.drakkarpress.com` -> CNAME a tu subdominio Netlify.
  - Apex `drakkarpress.com` -> ALIAS/ANAME (Netlify) o usar Netlify DNS.
- API Backend: `api.drakkarpress.com` -> A record apuntando a la IP del VPS.

### 2. Servidor (Ubuntu 22.04 recomendado)
```bash
sudo apt update && sudo apt install -y docker.io docker-compose-plugin
sudo usermod -aG docker $USER
logout # volver a entrar
```

### 3. Clonar repositorio
```bash
git clone https://github.com/imageGeneratorZZ/DrakkarPress.git
cd DrakkarPress/backend
```

### 4. Variables de entorno (export antes de levantar)
```bash
export POSTGRES_PASSWORD='CHANGE_ME_STRONG'
export SPRING_DATASOURCE_URL='jdbc:postgresql://db:5432/drakkarpress'
export SPRING_DATASOURCE_USERNAME='drk_user'
export SPRING_DATASOURCE_PASSWORD="$POSTGRES_PASSWORD"
export JWT_SECRET='32+CaracteresSuperSecretosCambiarProduccionXYZ123456'
export CORS_ALLOWED_ORIGINS='https://drakkarpress.com,https://www.drakkarpress.com'
export FRONTEND_URL='https://drakkarpress.com'
export STRIPE_API_KEY='sk_live_...'
export STRIPE_WEBHOOK_SECRET='whsec_...'
export AWS_S3_BUCKET='drakkarpress-books'
export AWS_S3_REGION='us-east-1'
export AWS_ACCESS_KEY='AKIA...'
export AWS_SECRET_KEY='********'
export LULU_CLIENT_KEY='...'
export LULU_CLIENT_SECRET='...'
export LULU_API_BASE64='Basic ...'
export LULU_API_URL='https://api.lulu.com/v1'
export MAIL_USERNAME='noreply@drakkarpress.com'
export MAIL_PASSWORD='app-password'
export API_HOST='api.drakkarpress.com'
```

### 5. Levantar stack
```bash
docker compose -f docker-compose.prod.yml build
docker compose -f docker-compose.prod.yml up -d
```

### 6. Verificar
```bash
curl -I https://api.drakkarpress.com/actuator/health
docker ps
```

### 7. Logs y mantenimiento
```bash
docker compose -f docker-compose.prod.yml logs -f backend
docker compose -f docker-compose.prod.yml restart backend
```

### 8. Backups Postgres
Script rápido (cron diario):
```bash
#!/bin/bash
TS=$(date +%Y%m%d_%H%M)
docker exec drakkarpress-db pg_dump -U drk_user drakkarpress > /var/backups/drakkarpress_$TS.sql
```

### 9. Actualizar versión
```bash
git pull
docker compose -f docker-compose.prod.yml build --no-cache backend
docker compose -f docker-compose.prod.yml up -d backend
```

### 10. Seguridad rápida
- Cambia todas las claves por secretos fuertes.
- Restringe puertos abiertos (solo 80/443). Usa UFW:
```bash
sudo ufw allow 80
sudo ufw allow 443
sudo ufw enable
```
- Desactiva `spring.jpa.show-sql` en producción.

### 11. Próximos pasos
- Añadir GitHub Actions para build & push a GHCR.
- Sistema de métricas (Prometheus + Grafana).
- Monitoreo de logs centralizado.

---
Listo: API servida en HTTPS con Caddy, base de datos en contenedor local, variables externas y despliegue reproducible.
