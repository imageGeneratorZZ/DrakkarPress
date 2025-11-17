# 🚀 PLAN MAESTRO DE DEPLOYMENT - DrakkarPress

## 📍 ESTADO ACTUAL (13 Nov 2025)

### ✅ Completado:
- Desktop App (Electron) funcional en local
- Frontend HTML/CSS/JS listo
- Backend Spring Boot con Java 21
- Validación Java 21 (loop completo Iteración 1, 17 Nov 2025)
- Repositorio GitHub configurado

### 🎯 Objetivo:
**Desde tu PC → Internet (DrakkarPress.com)**

---

## 🏗️ ARQUITECTURA FINAL

```
┌──────────────────────────────────────────────────────┐
│           TU PC LOCAL (Desarrollo)                   │
│                                                       │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────┐│
│  │ Desktop App  │  │   Backend    │  │  Frontend  ││
│  │  (Electron)  │→ │ Spring Boot  │  │   HTML/JS  ││
│  │              │  │ :8080        │  │            ││
│  └──────────────┘  └──────────────┘  └────────────┘│
└──────────────────────────────────────────────────────┘
                         ↓ DEPLOY
┌──────────────────────────────────────────────────────┐
│              INTERNET (Producción)                   │
│                                                       │
│  ┌─────────────────────────────────────────────────┐│
│  │  DrakkarPress.com (Frontend)                    ││
│  │  → Netlify/Vercel/GitHub Pages                  ││
│  └─────────────────────────────────────────────────┘│
│                         ↓                            │
│  ┌─────────────────────────────────────────────────┐│
│  │  api.DrakkarPress.com (Backend API)             ││
│  │  → Railway.app / Render / Heroku                ││
│  └─────────────────────────────────────────────────┘│
│                         ↓                            │
│  ┌─────────────────────────────────────────────────┐│
│  │  PostgreSQL Database (Cloud)                    ││
│  │  → Railway/Neon/Supabase                        ││
│  └─────────────────────────────────────────────────┘│
└──────────────────────────────────────────────────────┘
```

---

## 📋 CHECKLIST DE DEPLOYMENT

### FASE 1: Preparación Local ✅
- [x] Desktop App compilando
- [x] Backend con Java 21
- [x] Frontend HTML listo
- [ ] Backend corriendo localmente
- [ ] PostgreSQL local configurada
- [ ] Probar todo el flujo local

### FASE 2: Frontend a Internet
- [ ] Crear cuenta Netlify
- [ ] Configurar build settings
- [ ] Deploy frontend
- [ ] Configurar dominio DrakkarPress.com
- [ ] SSL/HTTPS automático

### FASE 3: Backend a Cloud
- [ ] Crear cuenta Railway.app
- [ ] Conectar GitHub repo
- [ ] Configurar variables de entorno
- [ ] Deploy backend
- [ ] Configurar PostgreSQL cloud
- [ ] Probar endpoints API

### FASE 4: Conectar Todo
- [ ] Actualizar CORS en backend
- [ ] Apuntar frontend → api.DrakkarPress.com
- [ ] Configurar DNS
- [ ] Probar flujo completo
- [ ] SSL en backend

### FASE 5: Desktop App Release
- [ ] Generar instalador Windows
- [ ] Subir a GitHub Releases
- [ ] Configurar auto-update
- [ ] Link de descarga en web

---

## 🛠️ COMANDOS RÁPIDOS

### Backend Local
```powershell
cd c:\Users\SuperUsuario\DrakkarPress.com\backend
mvn clean package -DskipTests
java -jar target\drakkarpress-platform-1.0.0.jar
```

### Frontend Local (Testing)
```powershell
cd c:\Users\SuperUsuario\DrakkarPress.com
npx serve .
# Abre http://localhost:3000
```

### Desktop App
```powershell
cd c:\Users\SuperUsuario\DrakkarPress.com\desktop-app
npm run build:main
npx electron .
```

### Deploy Frontend (Netlify)
```powershell
npm install -g netlify-cli
netlify login
netlify deploy --prod
```

### Deploy Backend (Railway)
```powershell
npm install -g @railway/cli
railway login
railway up
```

---

## 🌐 CONFIGURACIÓN DE DOMINIOS

### Proveedor de Dominio (ej: GoDaddy, Namecheap)

**DNS Records:**
```
Type    Name    Value                           TTL
A       @       76.76.21.21 (Netlify IP)       Auto
CNAME   www     drakkarpress.netlify.app       Auto
CNAME   api     drakkarpress.up.railway.app    Auto
```

### Verificar DNS:
```powershell
nslookup DrakkarPress.com
nslookup api.DrakkarPress.com
```

---

## 🔐 VARIABLES DE ENTORNO

### Backend (.env o Railway Config)
```env
# Database
DATABASE_URL=postgresql://user:pass@host:5432/drakkarpress
SPRING_DATASOURCE_URL=${DATABASE_URL}

# CORS
CORS_ALLOWED_ORIGINS=https://drakkarpress.com,https://www.drakkarpress.com

# OpenAI/IA
OPENAI_API_KEY=sk-...

# JWT
JWT_SECRET=tu-secret-super-seguro-aqui

# Server
PORT=8080
SPRING_PROFILES_ACTIVE=production

# Stats (opcional)
PLATFORM_STATS_SOURCE=auto
PLATFORM_STATS_BASELINE_BOOKS=124583
PLATFORM_STATS_BASELINE_AUTHORS=8421
PLATFORM_STATS_BASELINE_RESELLERS=5120
PLATFORM_STATS_BASELINE_PRINT_SHOPS=326
PLATFORM_STATS_BASELINE_COUNTRIES=45
PLATFORM_STATS_BASELINE_ACTIVE_USERS=12840
PLATFORM_STATS_BASELINE_AI_GENERATIONS=124583
```

### Frontend (netlify.toml)
```toml
[build]
  publish = "."
  command = "echo 'No build needed'"

[[redirects]]
  from = "/api/*"
  to = "https://api.drakkarpress.com/:splat"
  status = 200
  force = true

[[headers]]
  for = "/*"
  [headers.values]
    X-Frame-Options = "DENY"
    X-Content-Type-Options = "nosniff"
```

---

## 📊 COSTOS ESTIMADOS (Mensual)

### Opción GRATIS (Para empezar)
- Netlify: $0 (100GB bandwidth)
- Railway: $0 (500 horas/mes)
- Neon PostgreSQL: $0 (0.5GB storage)
- **Total: $0/mes** ✅

### Opción BÁSICA (Crecimiento)
- Netlify Pro: $19/mes
- Railway Hobby: $5/mes
- Neon Scale: $19/mes
- **Total: ~$43/mes**

### Opción PRO (Producción)
- Netlify Business: $99/mes
- Railway Pro: $20/mes
- Neon Pro: $69/mes
- **Total: ~$188/mes**

---

## 🚨 PROBLEMAS COMUNES Y SOLUCIONES

### CORS Errors
```java
// Backend: CorsConfig.java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("https://drakkarpress.com")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true);
            }
        };
    }
}
```

### Database Connection
```properties
# application.properties
spring.datasource.url=${DATABASE_URL}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### SSL Certificate
- Netlify: Automático ✅
- Railway: Automático ✅
- Custom domain: Configurar en Railway settings

---

## 📱 MONITOREO Y ANALYTICS

### Herramientas Recomendadas
- **Uptime**: UptimeRobot (gratis)
- **Analytics**: Google Analytics
- **Errors**: Sentry.io
- **Logs**: Railway/Netlify dashboards

---

## 🎯 TIMELINE ESTIMADO

**Día 1 (HOY):**
- ✅ Desktop app funcionando
- 🔄 Backend local corriendo
- 🔄 Frontend deployado a Netlify

**Día 2:**
- Backend deployado a Railway
- Database PostgreSQL configurada
- API funcionando en cloud

**Día 3:**
- Dominio configurado
- DNS apuntando correctamente
- SSL activo

**Día 4:**
- Testing completo
- Corrección de bugs
- Optimización

**Día 5:**
- Desktop app instalador en GitHub
- Link de descarga en web
- 🎉 LANZAMIENTO BETA

---

## ✅ PRÓXIMO PASO INMEDIATO

```powershell
# 1. Verificar backend existe
cd c:\Users\SuperUsuario\DrakkarPress.com\backend

# 2. Si existe, compilar y ejecutar
mvn clean package -DskipTests
java -jar target\*.jar

# 3. Probar en navegador
# http://localhost:8080
```

---

**Estado**: 📍 Listo para empezar deployment  
**Prioridad**: 🔥 Alta  
**Dificultad**: ⭐⭐ Media  
**Tiempo**: ~2 días

¿Empezamos? 🚀
