#!/usr/bin/env bash
set -e
DOMAIN="${1:?Domain requerido}"
EMAIL="${2:-admin@$DOMAIN}" # email opcional
RSA_KEY_SIZE=4096
DATA_PATH="./certbot"
if [ ! -d "$DATA_PATH" ]; then
  mkdir -p "$DATA_PATH"
fi
echo "==> Creando certificados para $DOMAIN"
docker compose run --rm certbot certonly --webroot -w /var/www/certbot \
  --agree-tos -m "$EMAIL" -d "$DOMAIN" -d "www.$DOMAIN" --rsa-key-size $RSA_KEY_SIZE --force-renewal --non-interactive
docker compose restart nginx
echo "Listo."
