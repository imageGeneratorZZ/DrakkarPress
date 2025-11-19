# 🚀 Estado del Deployment - DrakkarPress

## ✅ COMPLETADO - Railway Automation
**Commit:** `1e390fd` | **Fecha:** Diciembre 2024

### 📦 Archivos Creados

#### 1. `.env.example` - Template Completo
```bash
# DATABASE (Railway auto-provisioning)
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${PGUSER}
SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}

# JWT Configuration
JWT_SECRET=your-jwt-secret-here  # Generar con: openssl rand -base64 64

# Stripe Configuration
STRIPE_API_KEY=sk_test_...
STRIPE_PUBLISHABLE_KEY=pk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...

# Frontend URLs
APP_FRONTEND_URL=https://tu-frontend.netlify.app
APP_FRONTEND_SUCCESS_URL=https://tu-frontend.netlify.app/my-books.html

# Shopify Configuration
SHOPIFY_STORE_URL=https://tu-tienda.myshopify.com
SHOPIFY_API_KEY=your-shopify-api-key
SHOPIFY_API_SECRET=your-shopify-api-secret
SHOPIFY_ACCESS_TOKEN=shpat_...

# Lulu.com Configuration
LULU_API_URL=https://api.lulu.com
LULU_API_KEY=your-lulu-api-key
LULU_API_SECRET=your-lulu-api-secret
LULU_SANDBOX=false

# Gmail SMTP Configuration
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu-email@gmail.com
SPRING_MAIL_PASSWORD=tu-app-password

# Optional OAuth2
# SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=
# SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=
```

#### 2. `scripts/set-railway-env.ps1` - Script Interactivo
```powershell
# ✅ FEATURES:
# - Detecta railway CLI automáticamente
# - Verifica login y vinculación del proyecto
# - Prompts interactivos para cada variable
# - Soporte para contraseñas seguras (Read-Host -AsSecureString)
# - Valida y configura todas las variables requeridas
# - Mapea DATABASE_URL de Railway a Spring properties
# - Resumen de next steps al finalizar

# USAGE:
.\scripts\set-railway-env.ps1
```

#### 3. `RAILWAY_SETUP.md` - Guía Completa
**200+ líneas incluyendo:**
- ✅ Resumen quick-start (6 pasos)
- ✅ Instalación Railway CLI (npm/scoop + PATH refresh)
- ✅ Login y vinculación (railway login, link, service)
- ✅ 3 métodos para configurar variables (Dashboard/Script/CLI manual)
- ✅ Build y Deploy (mvnw package + railway up)
- ✅ Verificación post-deploy (curl health checks)
- ✅ Configuración webhooks (Stripe + Shopify endpoints)
- ✅ Troubleshooting completo (CLI not found, build failures, DB errors, CORS)
- ✅ Comandos útiles (status, variables, logs, restart, open, domain)
- ✅ Checklist de deploy (10 items)
- ✅ Alternativa sin CLI (GitHub integration)

---

## 🎯 PRÓXIMOS PASOS

### 1. Completar Railway Link (EN PROGRESO)
```powershell
# Usuario está en el prompt interactivo para seleccionar proyecto
# Presionar Enter para seleccionar "overflowing-consideration"
railway link

# Luego configurar el servicio:
railway service overflowing-consideration
```

### 2. Configurar Variables de Entorno
**MÉTODO RECOMENDADO:** Railway Dashboard (primera vez)
```
1. Ir a: https://railway.app/project/overflowing-consideration
2. Seleccionar servicio "backend" o "overflowing-consideration"
3. Tab "Variables"
4. Agregar todas las variables de .env.example
```

**ALTERNATIVA:** Script Automatizado
```powershell
.\scripts\set-railway-env.ps1
```

### 3. Build Backend
```powershell
cd backend
.\mvnw.cmd -q -DskipTests clean package
```

### 4. Deploy a Railway
```powershell
railway up
railway logs  # Verificar deployment
railway domain  # Obtener URL pública
```

### 5. Verificar Health Endpoints
```bash
# Reemplazar <BACKEND_URL> con la URL de railway domain
curl https://<BACKEND_URL>/api/auth/health
curl https://<BACKEND_URL>/actuator/health
```

### 6. Configurar Webhooks

#### Stripe Dashboard
```
URL: https://<BACKEND_URL>/api/payments/webhook
Events:
  - checkout.session.completed
  - checkout.session.async_payment_succeeded
  - checkout.session.async_payment_failed
```
**Copiar el webhook secret a Railway variables:** `STRIPE_WEBHOOK_SECRET=whsec_...`

#### Shopify Admin
```
URL: https://<BACKEND_URL>/api/shopify/webhooks/orders
Event: Order creation
Format: JSON
```

### 7. Update Frontend Config
Actualizar `config.js` en Netlify con la Railway URL:
```javascript
const API_BASE_URL = 'https://<BACKEND_URL>';
```

### 8. Testing End-to-End
- [ ] Login/Register (JWT token válido)
- [ ] Comprar ebook (Stripe checkout → webhook → email con PDF)
- [ ] Premium upgrade (Stripe checkout → membership activa)
- [ ] My Books (descargas seguras con token)
- [ ] Pedido físico (Shopify order → Lulu print job → tracking sync)

---

## 📊 ESTADO ACTUAL

### ✅ Completado
- [x] Backend source code (Spring Boot 3.5.3, Java 21)
- [x] Auth system (JWT + Spring Security)
- [x] Ebook purchase flow (Stripe + Email con PDF)
- [x] Premium memberships (Stripe + Database)
- [x] Shopify integration (orders, inventory, fulfillments)
- [x] Lulu integration (print jobs, cost calculation, tracking)
- [x] Admin panel (admin.html con Lulu workflow fixed)
- [x] Frontend pages (shop, my-books, login, register, etc.)
- [x] Error handling system (error-handler.js toast notifications)
- [x] Railway CLI installed (4.11.1)
- [x] Railway login (GeneradorZZ)
- [x] Deployment automation created (.env.example, script, guide)
- [x] README v3.0 (comprehensive features + stack + status)

### 🔄 En Progreso
- [ ] Railway project link (user at interactive prompt)
- [ ] Environment variables configuration
- [ ] Backend build + deploy to Railway
- [ ] Stripe webhook configuration
- [ ] Shopify webhook configuration

### ⏳ Pendiente
- [ ] Consolidar LuluService vs LuluPrintService (overlap)
- [ ] Fix SecurityConfig deprecation warnings (non-blocking)
- [ ] Production testing (ebook purchase, premium, physical books)

---

## 🔧 Troubleshooting Rápido

### Railway CLI not found
```powershell
# Refresh PATH en PowerShell actual:
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Verificar instalación:
railway --version
```

### Build Failures
```powershell
# Limpiar caché Maven y rebuild:
cd backend
.\mvnw.cmd clean
.\mvnw.cmd package -DskipTests
```

### Database Connection Errors
```bash
# Verificar que Railway PostgreSQL está activo:
railway variables  # Buscar DATABASE_URL, PGUSER, PGPASSWORD

# Conectar manualmente para test:
railway connect postgres
```

### CORS Errors (Frontend → Backend)
Verificar que `SecurityConfig.java` permite tu frontend URL:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    configuration.setAllowedOrigins(Arrays.asList(
        "https://tu-frontend.netlify.app",
        "http://localhost:5500"
    ));
}
```

---

## 📚 Referencias

- **Railway Dashboard:** https://railway.app/
- **Railway Docs:** https://docs.railway.app/
- **Stripe Dashboard:** https://dashboard.stripe.com/webhooks
- **Shopify Admin:** https://admin.shopify.com/settings/notifications
- **Gmail App Passwords:** https://myaccount.google.com/apppasswords (requiere 2FA)
- **Lulu Support:** https://developers.lulu.com/support

---

## 💡 Notas Importantes

1. **JWT_SECRET:** NUNCA commitear en .env. Usar `openssl rand -base64 64` para generar uno único.
2. **Stripe Webhook Secret:** Copiar desde Stripe Dashboard después de crear el endpoint.
3. **Gmail App Password:** NO es tu contraseña de Google. Generar en: https://myaccount.google.com/apppasswords
4. **DATABASE_URL:** Railway lo provisiona automáticamente. Solo mapear a `SPRING_DATASOURCE_*`.
5. **Frontend URL:** Debe coincidir con el origin permitido en CORS (sin trailing slash).

---

**Última actualización:** Commit 1e390fd - Railway automation complete
**Next Action:** Complete `railway link` → Configure variables → Build → Deploy → Webhooks
