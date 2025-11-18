# 🚀 Guía de Deployment - DrakkarPress

## 📋 Índice
1. [Pre-requisitos](#pre-requisitos)
2. [Backend (Railway)](#backend-railway)
3. [Frontend (Netlify)](#frontend-netlify)
4. [Configuración Stripe](#configuración-stripe)
5. [Configuración Shopify](#configuración-shopify)
6. [Configuración Lulu](#configuración-lulu)
7. [Base de Datos](#base-de-datos)
8. [Variables de Entorno](#variables-de-entorno)
9. [Testing](#testing)
10. [Troubleshooting](#troubleshooting)

---

## Pre-requisitos

✅ **Cuentas necesarias:**
- GitHub (código fuente)
- Railway (backend hosting)
- Netlify (frontend hosting)
- Stripe (pagos)
- Shopify (e-commerce físicos)
- Lulu.com (print-on-demand)
- Gmail/SMTP (emails)

---

## Backend (Railway)

### 1. Crear Proyecto en Railway

```bash
# Instalar Railway CLI
npm install -g @railway/cli

# Login
railway login

# Crear proyecto
railway init

# Link al proyecto existente
railway link
```

### 2. Configurar Variables de Entorno

En Railway Dashboard → Variables:

```properties
# Database (PostgreSQL automático en Railway)
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_DATASOURCE_USERNAME=${PGUSER}
SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}

# JWT
JWT_SECRET=tu_jwt_secret_muy_seguro_y_largo_aqui_min_256_bits

# Email (Gmail)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=tu-email@gmail.com
SPRING_MAIL_PASSWORD=tu-app-password-de-gmail

# Stripe
STRIPE_API_KEY=sk_live_tu_clave_secreta
STRIPE_WEBHOOK_SECRET=whsec_tu_webhook_secret
STRIPE_PUBLISHABLE_KEY=pk_live_tu_clave_publica

# Frontend URL
APP_FRONTEND_URL=https://drakkarpress.netlify.app
APP_FRONTEND_SUCCESS_URL=https://drakkarpress.netlify.app/checkout-success.html

# Shopify
SHOPIFY_STORE_URL=https://tu-tienda.myshopify.com
SHOPIFY_API_KEY=tu_api_key
SHOPIFY_API_SECRET=tu_api_secret
SHOPIFY_ACCESS_TOKEN=tu_access_token

# Lulu.com
LULU_API_URL=https://api.lulu.com
LULU_API_KEY=tu_lulu_api_key
LULU_API_SECRET=tu_lulu_api_secret
LULU_SANDBOX=false

# OAuth2 (opcional, si se habilita)
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=tu_google_client_id
SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=tu_google_secret
```

### 3. Deploy Backend

```bash
cd backend

# Build local (opcional, para verificar)
./mvnw clean package -DskipTests

# Deploy a Railway
railway up

# Ver logs
railway logs

# Obtener URL del servicio
railway domain
```

### 4. Verificar Health Endpoints

```bash
# Verificar que el backend esté funcionando
curl https://tu-backend.railway.app/api/auth/health
curl https://tu-backend.railway.app/api/payments/health
curl https://tu-backend.railway.app/api/shopify/health
```

---

## Frontend (Netlify)

### 1. Configurar Repositorio en Netlify

1. Ir a [Netlify](https://netlify.com)
2. Click "Add new site" → "Import an existing project"
3. Conectar con GitHub
4. Seleccionar repositorio `DrakkarPress`
5. Configurar:
   - **Base directory:** (vacío)
   - **Build command:** (vacío, es HTML estático)
   - **Publish directory:** `/`

### 2. Configurar Variables de Entorno (Opcional)

En Netlify Dashboard → Site settings → Environment variables:

```
API_BASE_URL=https://tu-backend.railway.app
```

### 3. Actualizar config.js

Editar `assets/js/config.js`:

```javascript
const PRODUCTION_API_URL = "https://tu-backend.railway.app";
```

### 4. Deploy

```bash
# Push a GitHub
git add .
git commit -m "config: actualizar URL backend production"
git push origin main

# Netlify desplegará automáticamente
```

### 5. Configurar Dominio Personalizado (Opcional)

En Netlify → Domain settings:
- Agregar dominio: `drakkarpress.com`
- Configurar DNS según instrucciones de Netlify
- SSL automático con Let's Encrypt

---

## Configuración Stripe

### 1. Obtener API Keys

1. Ir a [Stripe Dashboard](https://dashboard.stripe.com)
2. Developers → API keys
3. Copiar:
   - **Publishable key** (`pk_live_...`)
   - **Secret key** (`sk_live_...`)

### 2. Configurar Webhooks

1. Developers → Webhooks → Add endpoint
2. **Endpoint URL:** `https://tu-backend.railway.app/api/payments/webhook`
3. **Events to send:**
   - `checkout.session.completed`
   - `checkout.session.async_payment_succeeded`
   - `checkout.session.async_payment_failed`
4. Copiar **Signing secret** (`whsec_...`)
5. Agregar a variables de Railway: `STRIPE_WEBHOOK_SECRET`

### 3. Configurar Productos y Precios

```bash
# Crear productos para membresías (ejemplo con Stripe CLI)
stripe products create --name="DrakkarPress Basic" --description="Plan Basic mensual"
stripe prices create --product=prod_xxx --unit-amount=999 --currency=usd --recurring[interval]=month
```

O crearlos manualmente en Stripe Dashboard → Products.

---

## Configuración Shopify

### 1. Crear App Privada

1. Shopify Admin → Settings → Apps and sales channels
2. "Develop apps" → "Create an app"
3. Nombre: `DrakkarPress Integration`
4. Configuration → Admin API:
   - **Admin API access scopes:**
     - `read_products`, `write_products`
     - `read_inventory`, `write_inventory`
     - `read_orders`, `write_orders`
     - `read_fulfillments`, `write_fulfillments`
5. Install app → Copiar **Admin API access token**

### 2. Configurar Variables en Railway

```properties
SHOPIFY_STORE_URL=https://tu-tienda.myshopify.com
SHOPIFY_ACCESS_TOKEN=shpat_tu_access_token
```

### 3. Configurar Webhooks

En Shopify Admin → Settings → Notifications → Webhooks:

**Order creation:**
- URL: `https://tu-backend.railway.app/api/shopify/webhooks/orders`
- Format: JSON
- API version: 2024-01

---

## Configuración Lulu

### 1. Crear Cuenta y Obtener Credenciales

1. Ir a [Lulu.com](https://www.lulu.com)
2. Crear cuenta de autor/publisher
3. Contact support para solicitar API access
4. Obtener **API key** y **API secret**

### 2. Configurar Variables en Railway

```properties
LULU_API_URL=https://api.lulu.com
LULU_API_KEY=tu_api_key
LULU_API_SECRET=tu_api_secret
LULU_SANDBOX=false
```

### 3. Testing en Sandbox

Para testing inicial:

```properties
LULU_API_URL=https://api.sandbox.lulu.com
LULU_SANDBOX=true
```

---

## Base de Datos

### Opción 1: Railway PostgreSQL (Recomendado)

Railway provee PostgreSQL automáticamente:

```bash
# Ver conexión
railway variables

# Conectar desde local (troubleshooting)
railway run psql $DATABASE_URL
```

### Opción 2: PostgreSQL Externo

Si prefieres usar PostgreSQL externo (AWS RDS, DigitalOcean, etc.):

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/drakkarpress
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=contraseña
```

### Migraciones

El backend usa JPA con `spring.jpa.hibernate.ddl-auto=update` para crear tablas automáticamente en desarrollo.

**Para producción (recomendado):**

```properties
spring.jpa.hibernate.ddl-auto=validate
```

Y usar Flyway o Liquibase para migraciones controladas.

---

## Variables de Entorno

### Backend (Railway)

Archivo de referencia: `backend/.env.example`

```properties
# Core
JWT_SECRET=
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

# Email
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=

# Stripe
STRIPE_API_KEY=
STRIPE_WEBHOOK_SECRET=
STRIPE_PUBLISHABLE_KEY=

# URLs
APP_FRONTEND_URL=https://drakkarpress.netlify.app

# Shopify
SHOPIFY_STORE_URL=
SHOPIFY_ACCESS_TOKEN=

# Lulu
LULU_API_KEY=
LULU_API_SECRET=
```

### Frontend (config.js)

```javascript
const PRODUCTION_API_URL = "https://tu-backend.railway.app";
```

---

## Testing

### 1. Test Backend Localmente

```bash
cd backend
./mvnw spring-boot:run

# Verificar endpoints
curl http://localhost:8080/api/auth/health
```

### 2. Test Frontend Localmente

```bash
# Servidor HTTP simple
python -m http.server 8000

# O con Node.js
npx serve .
```

Abrir `http://localhost:8000/shop.html`

### 3. Test Integración End-to-End

1. **Registro:** Crear cuenta → verificar email (si está habilitado)
2. **Login:** Iniciar sesión → verificar token en localStorage
3. **Ebook:** Comprar ebook → verificar Stripe checkout → verificar email recibido → descargar desde biblioteca
4. **Premium:** Upgrade a premium → verificar webhook → verificar activación
5. **Físico (Shopify):** Crear orden en Shopify → verificar webhook → verificar job en Lulu
6. **Tracking:** Verificar tracking sync de Lulu a Shopify

---

## Troubleshooting

### Error: "railway: command not found"

```bash
npm install -g @railway/cli
```

### Error: CORS en Frontend

Verificar en `SecurityConfig.java` que el dominio de Netlify esté en la lista:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "https://drakkarpress.netlify.app",
    "https://tu-deploy.netlify.app"
));
```

### Error: Stripe Webhook Signature Inválida

1. Verificar que `STRIPE_WEBHOOK_SECRET` sea correcto
2. Verificar que el endpoint en Stripe Dashboard apunte a la URL correcta
3. Ver logs en Railway: `railway logs`

### Error: Email No Se Envía

1. Verificar credenciales de Gmail
2. Generar **App Password** en Google Account Security
3. Verificar `SPRING_MAIL_PASSWORD` en Railway
4. Ver logs: `railway logs | grep "Email"`

### Error: Database Connection

```bash
# Ver variables de DB
railway variables | grep DATABASE

# Conectar manualmente para debug
railway run psql $DATABASE_URL
```

### Error: Build Failed en Railway

```bash
# Ver logs completos
railway logs

# Verificar Java version en railway.toml
nixpacks.providers.java.version = "21"

# Re-deploy
railway up --force
```

---

## Comandos Útiles

```bash
# Backend
cd backend
./mvnw clean package              # Build
./mvnw spring-boot:run            # Run local
railway up                        # Deploy
railway logs                      # Ver logs
railway logs --follow             # Logs en tiempo real

# Frontend
git add .
git commit -m "update"
git push origin main              # Auto-deploy en Netlify

# Database
railway run psql $DATABASE_URL    # Conectar a DB
railway variables                 # Ver env vars

# Stripe
stripe listen --forward-to http://localhost:8080/api/payments/webhook  # Test webhooks local
stripe trigger checkout.session.completed                               # Test evento
```

---

## Checklist Final

- [ ] Backend desplegado en Railway
- [ ] Frontend desplegado en Netlify
- [ ] Variables de entorno configuradas
- [ ] Base de datos PostgreSQL funcionando
- [ ] Stripe webhooks configurados
- [ ] Shopify webhooks configurados
- [ ] Email funcionando (test con registro)
- [ ] Ebooks: compra y descarga funcionan
- [ ] Premium: upgrade funciona
- [ ] Shopify: productos sincronizados
- [ ] Lulu: print jobs se crean
- [ ] Tracking sync funciona
- [ ] Dominio personalizado (opcional)
- [ ] SSL habilitado

---

## Recursos

- [Railway Docs](https://docs.railway.app)
- [Netlify Docs](https://docs.netlify.com)
- [Stripe API](https://stripe.com/docs/api)
- [Shopify API](https://shopify.dev/api)
- [Lulu API](https://developers.lulu.com)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)

---

**¿Problemas?** Revisar logs en Railway y contactar soporte si persisten errores.
