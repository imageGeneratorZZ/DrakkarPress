# ⚡ Railway Quick Setup - DrakkarPress

## 🎯 Resumen
1. **Link proyecto**: `railway link` → seleccionar `overflowing-consideration`
2. **Set service**: `railway service overflowing-consideration`
3. **Variables**: Copiar de `.env.example` al Dashboard o usar `scripts/set-railway-env.ps1`
4. **Build**: `cd backend && .\mvnw.cmd -q -DskipTests clean package`
5. **Deploy**: `railway up`
6. **Verify**: `railway logs` y `railway domain`

---

## 📦 Instalación Railway CLI (Windows)

### Opción 1: npm (recomendado)
```powershell
# Instalar Node.js LTS desde https://nodejs.org
node -v
npm -v

# Instalar Railway CLI
npm install -g @railway/cli

# Refrescar PATH si no reconoce "railway"
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Verificar
railway --version
```

### Opción 2: Scoop
```powershell
scoop install railway
```

---

## 🔐 Login y Vinculación

```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com

# Login (abre navegador)
railway login

# Link al proyecto
railway link
# → Seleccionar: GeneradorZZ's Projects
# → Seleccionar: overflowing-consideration

# Set service (si tiene múltiples services)
railway service overflowing-consideration

# Verificar
railway status
```

---

## ⚙️ Variables de Entorno

### Método 1: Dashboard (recomendado para primera vez)
1. Ir a https://railway.app → Proyecto → Variables
2. Copiar valores de `.env.example`
3. Reemplazar placeholders con valores reales

### Método 2: Script automatizado
```powershell
.\scripts\set-railway-env.ps1
```

### Método 3: CLI manual
```powershell
# Database (Railway auto-provee, solo mapear)
railway variables set 'SPRING_DATASOURCE_URL=${DATABASE_URL}'
railway variables set 'SPRING_DATASOURCE_USERNAME=${PGUSER}'
railway variables set 'SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}'

# JWT (generar con: openssl rand -base64 64)
railway variables set JWT_SECRET=tu_jwt_secret_muy_largo_256_bits

# Stripe
railway variables set STRIPE_API_KEY=sk_live_xxx
railway variables set STRIPE_PUBLISHABLE_KEY=pk_live_xxx
railway variables set STRIPE_WEBHOOK_SECRET=whsec_xxx

# Frontend
railway variables set APP_FRONTEND_URL=https://tu-sitio.netlify.app
railway variables set APP_FRONTEND_SUCCESS_URL=https://tu-sitio.netlify.app/checkout-success.html

# Shopify
railway variables set SHOPIFY_STORE_URL=https://tu-tienda.myshopify.com
railway variables set SHOPIFY_ACCESS_TOKEN=shpat_xxx

# Lulu
railway variables set LULU_API_URL=https://api.lulu.com
railway variables set LULU_API_KEY=xxx
railway variables set LULU_API_SECRET=xxx
railway variables set LULU_SANDBOX=false

# Email (Gmail App Password: https://myaccount.google.com/apppasswords)
railway variables set SPRING_MAIL_HOST=smtp.gmail.com
railway variables set SPRING_MAIL_PORT=587
railway variables set SPRING_MAIL_USERNAME=tu-email@gmail.com
railway variables set SPRING_MAIL_PASSWORD=tu-app-password

# Listar todas
railway variables
```

---

## 🚀 Build y Deploy

```powershell
cd backend

# Build (opcional local, Railway lo hace automático)
.\mvnw.cmd -q -DskipTests clean package

# Deploy
railway up

# Ver logs en tiempo real
railway logs

# Obtener URL del backend
railway domain
```

---

## ✅ Verificación Post-Deploy

```powershell
# Obtener URL
$BACKEND_URL = railway domain

# Health checks
curl "$BACKEND_URL/api/auth/health"
curl "$BACKEND_URL/api/payments/health"
curl "$BACKEND_URL/api/shopify/health"

# Test público
curl "$BACKEND_URL/api/public/books"
```

---

## 🔗 Configurar Webhooks

### Stripe
1. Dashboard: https://dashboard.stripe.com/webhooks
2. Add endpoint: `https://TU-BACKEND.railway.app/api/payments/webhook`
3. Events: `checkout.session.completed`, `checkout.session.async_payment_succeeded`, `checkout.session.async_payment_failed`
4. Copiar Webhook Secret → Railway variable `STRIPE_WEBHOOK_SECRET`

### Shopify
1. Admin: Apps → Webhooks
2. Create webhook:
   - URL: `https://TU-BACKEND.railway.app/api/shopify/webhooks/orders`
   - Event: `Order creation`
   - Format: JSON
3. Verificación HMAC automática (usa `SHOPIFY_API_SECRET`)

---

## 🐛 Troubleshooting

### "railway: command not found"
```powershell
# Refrescar PATH
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# O usar npx
npx @railway/cli@latest login
npx @railway/cli@latest up
```

### Deploy falla con error de build
```powershell
# Ver logs detallados
railway logs

# Build local para verificar
cd backend
.\mvnw.cmd clean package
```

### Database connection error
```powershell
# Verificar que las variables de mapeo estén correctas
railway variables | Select-String DATASOURCE
# Debe mostrar: SPRING_DATASOURCE_URL=${DATABASE_URL}
```

### CORS errors en frontend
- Verificar `APP_FRONTEND_URL` en Railway variables
- Debe coincidir exactamente con la URL de Netlify
- SecurityConfig.java ya incluye el origen

---

## 📚 Comandos Útiles

```powershell
# Status del proyecto
railway status

# Ver variables
railway variables

# Ver logs en vivo
railway logs --follow

# Restart service
railway restart

# Abrir dashboard en navegador
railway open

# Ver dominio
railway domain

# Conectar a PostgreSQL
railway connect postgres
```

---

## 🎯 Checklist de Deploy

- [ ] Railway CLI instalado y `railway --version` funciona
- [ ] `railway login` completado
- [ ] `railway link` vinculado a `overflowing-consideration`
- [ ] Variables de entorno configuradas (mínimo: JWT, Stripe, Frontend URL)
- [ ] Build exitoso: `.\mvnw.cmd -q -DskipTests clean package`
- [ ] Deploy: `railway up` sin errores
- [ ] Health check: `curl https://TU-BACKEND.railway.app/api/auth/health` → `OK`
- [ ] Stripe webhook configurado apuntando a Railway URL
- [ ] Shopify webhook configurado apuntando a Railway URL
- [ ] Frontend en Netlify actualizado con Railway backend URL en `config.js`

---

## 🆘 Alternativa Sin CLI

Si Railway CLI no funciona, usar GitHub integration:
1. Railway Dashboard → New Service → GitHub Repo
2. Seleccionar branch `appmod/java-migration-20251117192852`
3. Root directory: `/backend`
4. Build command: `./mvnw package -DskipTests`
5. Start command: `java -jar target/*.jar`
6. Configurar variables en Variables tab
7. Deploy automático en cada push
