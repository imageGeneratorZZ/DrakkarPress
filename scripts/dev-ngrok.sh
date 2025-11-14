#!/usr/bin/env bash
set -euo pipefail

# ===== Config & Args =====
PORT="${PORT:-8080}"
REGION="${NGROK_REGION:-us}"          # eu, ap, au, sa, jp, in (ngrok)
SPRING_PROFILE="${SPRING_PROFILE:-dev}"

usage() {
  echo "Uso: $0 [-p puerto] [-r region]"; exit 1;
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    -p|--port) PORT="$2"; shift 2 ;;
    -r|--region) REGION="$2"; shift 2 ;;
    -h|--help) usage ;;
    *) usage ;;
  esac
done

echo "[*] Config -> PORT=$PORT REGION=$REGION PROFILE=$SPRING_PROFILE"

# ===== Cleanup =====
BACKEND_PID=""
NGROK_PID=""

cleanup() {
  echo
  echo "[*] Limpiando..."
  [[ -n "${NGROK_PID}" ]] && kill "${NGROK_PID}" 2>/dev/null || true
  # No matamos backend si ya existía antes (solo si lo lanzamos nosotros)
  if [[ -n "${BACKEND_PID}" ]]; then
    kill "${BACKEND_PID}" 2>/dev/null || true
  fi
}
trap cleanup EXIT

# ===== Verifica ngrok =====
if ! command -v ngrok >/dev/null 2>&1; then
  echo "[!] ngrok no instalado. Instala desde https://ngrok.com/download"; exit 1;
fi

# ===== Detecta backend existente =====
if lsof -i :"$PORT" >/dev/null 2>&1; then
  echo "[*] Backend ya escuchando en puerto $PORT (no se relanza)."
else
  echo "[*] Iniciando backend Spring Boot en puerto $PORT (profile=$SPRING_PROFILE)"
  ./mvnw -q -Dspring-boot.run.profiles="$SPRING_PROFILE" -Dspring-boot.run.arguments="--server.port=$PORT" spring-boot:run &
  BACKEND_PID=$!
  echo "[*] PID backend: $BACKEND_PID"
fi

# ===== Espera backend (hasta 40s) =====
echo "[*] Esperando backend..."
for i in {1..40}; do
  if curl -s "http://127.0.0.1:${PORT}/actuator/health" | grep -q '"status":"UP"'; then
    echo "[*] Backend listo."
    break
  fi
  sleep 1
  [[ $i -eq 40 ]] && echo "[!] No se confirmó /actuator/health (continuando de todas formas)."
done

# ===== Inicia ngrok =====
echo "[*] Iniciando ngrok (región $REGION)..."
ngrok http --region="$REGION" "$PORT" > /dev/null 2>&1 &
NGROK_PID=$!
echo "[*] PID ngrok: $NGROK_PID"

# ===== Espera túnel =====
echo "[*] Esperando túnel seguro..."
URL=""
for i in {1..30}; do
  sleep 1
  URL=$(curl -s http://127.0.0.1:4040/api/tunnels | \
        grep -Eo 'https://[a-zA-Z0-9.-]+\.ngrok[^"]*' | head -n1 || true)
  [[ -n "$URL" ]] && break
done

if [[ -z "${URL:-}" ]]; then
  echo "[!] No se pudo obtener URL ngrok"; exit 1;
fi

echo "[*] URL pública: $URL"

# ===== Valida puerto real (banner puede confundir) =====
REAL_CHECK=$(curl -s -o /dev/null -w '%{http_code}' "$URL/actuator/health" || true)
if [[ "$REAL_CHECK" != "200" ]]; then
  echo "[!] Advertencia: $URL/actuator/health no respondió 200 (code=$REAL_CHECK). Verifica ruta o perfil."
fi

# ===== Exportaciones =====
echo "ALLOWED_ORIGINS=$URL,http://localhost:3000,http://localhost:5173" > .env.ngrok
echo "NEXT_PUBLIC_API_ORIGIN=$URL" > frontend.env
echo "[*] Archivos generados: .env.ngrok, frontend.env"

echo
echo "== Resumen =="
echo "Backend local:     http://localhost:$PORT"
echo "Túnel público:     $URL"
echo "Health:            $URL/actuator/health"
echo "CORS origins:      $(cat .env.ngrok | cut -d= -f2)"
echo
echo "== Uso backend (shell nueva) =="
echo "source .env.ngrok && ./mvnw -Dspring-boot.run.profiles=$SPRING_PROFILE -Dspring-boot.run.arguments=\"--server.port=$PORT\" spring-boot:run"
echo
echo "== Frontend .env.local (Next.js) =="
echo "NEXT_PUBLIC_API_ORIGIN=$URL"
echo
echo "Ctrl+C para finalizar (limpieza automática)."
