# 🚀 DEPLOYMENT EN 3 PASOS - DrakkarPress.com

## 📌 ESTADO ACTUAL
✅ Todo funciona localmente  
🎯 Objetivo: Subir a Internet → DrakkarPress.com

---

## PASO 1: DEPLOY FRONTEND (5 minutos) 🌐

### Opción A: Netlify (RECOMENDADO)
```powershell
# 1. Instalar CLI
npm install -g netlify-cli

# 2. Login (abre navegador)
netlify login

# 3. Deploy
netlify deploy --prod --dir=.

# 4. Configurar dominio custom
# Ir a: https://app.netlify.com
# Settings → Domain Management → Add custom domain
# Agregar: DrakkarPress.com
```

**DNS Records (en GoDaddy):**
```
Tipo: A
Nombre: @
Valor: [IP de Netlify - te la da en el panel]

Tipo: CNAME
Nombre: www
Valor: [tu-sitio].netlify.app
```

### Opción B: Vercel
```powershell
npm install -g vercel
vercel login
vercel --prod
```

### Opción C: GitHub Pages
```powershell
# 1. Crear repo en GitHub
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/imageGeneratorZZ/DrakkarPress.git
git push -u origin main

# 2. Habilitar GitHub Pages
# Settings → Pages → Source: main branch
```

---

## PASO 2: DEPLOY BACKEND (15 minutos) ⚙️

### Opción A: Railway (RECOMENDADO - Incluye PostgreSQL)
```powershell
# 1. Instalar CLI
npm install -g @railway/cli

# 2. Login
railway login

# 3. Crear proyecto
railway init

# 4. Agregar PostgreSQL
# En el dashboard de Railway:
# New → Database → PostgreSQL

# 5. Deploy backend real (Spring Boot)
cd backend
railway up

# 6. Configurar variables de entorno
# En Railway dashboard → Variables:
SPRING_DATASOURCE_URL=jdbc:postgresql://[railway-host]:5432/railway
SPRING_DATASOURCE_USERNAME=[from Railway]
SPRING_DATASOURCE_PASSWORD=[from Railway]
JWT_SECRET=[tu-secret-seguro]
CORS_ALLOWED_ORIGINS=https://drakkarpress.com,https://www.drakkarpress.com
PLATFORM_STATS_SOURCE=auto
PLATFORM_STATS_BASELINE_BOOKS=124583
PLATFORM_STATS_BASELINE_AUTHORS=8421
PLATFORM_STATS_BASELINE_RESELLERS=5120
PLATFORM_STATS_BASELINE_PRINT_SHOPS=326
PLATFORM_STATS_BASELINE_COUNTRIES=45
PLATFORM_STATS_BASELINE_ACTIVE_USERS=12840
PLATFORM_STATS_BASELINE_AI_GENERATIONS=124583
```

**Dominio custom:**
```
Settings → Networking → Custom Domain
Agregar: api.DrakkarPress.com
```

**DNS Record (en GoDaddy):**
```
Tipo: CNAME
Nombre: api
Valor: [tu-proyecto].up.railway.app
```

### Opción B: Render
```powershell
# 1. Conectar GitHub repo
# 2. New → Web Service
# 3. Select backend folder
# 4. Build: mvn clean package -DskipTests
# 5. Start: java -jar target/*.jar
```

### Opción C: Heroku
```powershell
heroku login
heroku create drakkarpress-api
git subtree push --prefix backend heroku main
```

---

## PASO 3: DESKTOP APP INSTALADOR (10 minutos) 💻

### Generar EXE para Windows
```powershell
cd desktop-app

# 1. Actualizar configuración
# Cambiar API_URL en .env o código:
# De: http://localhost:8080
# A: https://api.drakkarpress.com

# 2. Compilar
npm run build:main
npm run build:renderer

# 3. Generar instalador
npm run build:win

# Output: desktop-app/release/DrakkarPress Setup 1.0.0.exe
```

### Distribuir
```powershell
# Opción 1: GitHub Releases
# 1. Ir a: https://github.com/imageGeneratorZZ/DrakkarPress/releases
# 2. Create new release
# 3. Subir el .exe

# Opción 2: Tu sitio web
# Copiarlo a la carpeta de descargas:
Copy-Item "desktop-app\release\*.exe" -Destination "downloads\"
```

---

## 🔗 CONECTAR TODO

### Actualizar URLs en el código

**Frontend (index.html, *.html):**
```javascript
// De:
const API_URL = 'http://localhost:8080';

// A:
const API_URL = 'https://api.drakkarpress.com';
```

**Desktop App (src/renderer/config.ts o similar):**
```typescript
// De:
export const API_BASE_URL = 'http://localhost:8080';

// A:
export const API_BASE_URL = 'https://api.drakkarpress.com';
```

**Backend (application.properties):**
```properties
# Ya está configurado para leer de variables de entorno
cors.allowed-origins=${CORS_ALLOWED_ORIGINS}
```

---

## 📋 CHECKLIST DEPLOYMENT

### Pre-deployment
- [x] Sistema funciona localmente
- [ ] Cuenta en Netlify/Vercel (frontend)
- [ ] Cuenta en Railway/Render (backend)
- [ ] Acceso a DNS de DrakkarPress.com

### Frontend
- [ ] Deploy frontend a hosting
- [ ] Configurar dominio custom: DrakkarPress.com
- [ ] Configurar DNS A/CNAME records
- [ ] Probar: https://drakkarpress.com

### Backend
- [ ] Deploy backend a Railway/Render
- [ ] Crear base de datos PostgreSQL
- [ ] Configurar variables de entorno
- [ ] Configurar dominio: api.DrakkarPress.com
- [ ] Probar: https://api.drakkarpress.com/health

### Desktop App
- [ ] Actualizar API_URL a producción
- [ ] Compilar main + renderer
- [ ] Generar instalador .exe
- [ ] Subir a GitHub Releases o sitio web
- [ ] Agregar link de descarga en frontend

### Post-deployment
- [ ] Probar registro de usuario
- [ ] Probar login
- [ ] Probar crear creación
- [ ] Probar generadores
- [ ] Instalar desktop app y probar

---

## ⚡ QUICK START (TODO EN UNA SESIÓN)

```powershell
# Terminal 1: Frontend
npm install -g netlify-cli
netlify login
netlify deploy --prod --dir=.

# Terminal 2: Backend
npm install -g @railway/cli
railway login
cd backend
railway init
railway up

# Terminal 3: Desktop App
cd desktop-app
# (Actualizar API_URL primero)
npm run build:win
```

**Tiempo total:** ~30 minutos

---

## 🆘 TROUBLESHOOTING

### Frontend no carga
```powershell
# Verificar deployment
netlify status

# Ver logs
netlify logs
```

### Backend error 500
```powershell
# Ver logs en Railway
railway logs

# Verificar variables de entorno
railway variables
```

### Desktop App no conecta
```javascript
// Verificar CORS en backend
// application.properties:
cors.allowed-origins=*  // Temporalmente para debug
```

### DNS no propaga
```powershell
# Verificar DNS (tarda hasta 48h)
nslookup drakkarpress.com
nslookup api.drakkarpress.com

# Usar temporalmente:
# Frontend: [tu-sitio].netlify.app
# Backend: [tu-proyecto].up.railway.app
```

---

## 💰 COSTOS ESTIMADOS

### Tier Gratuito (Para empezar)
- Netlify: FREE (100GB bandwidth/mes)
- Railway: $5/mes crédito gratis
- GitHub: FREE
- **Total: GRATIS primer mes**

### Tier Básico (Producción ligera)
- Netlify Pro: $19/mes
- Railway Hobby: $5-20/mes
- PostgreSQL: Incluido
- **Total: ~$25-40/mes**

### Tier Profesional (Escalable)
- Netlify Business: $99/mes
- Railway Pro: $20-100/mes
- PostgreSQL Pro: Incluido
- CDN: $10-50/mes
- **Total: ~$130-250/mes**

---

## ✅ VERIFICACIÓN POST-DEPLOYMENT

```powershell
# 1. Frontend
Invoke-WebRequest https://drakkarpress.com

# 2. Backend API
Invoke-WebRequest https://api.drakkarpress.com/health

# 3. CORS
# Desde frontend, hacer fetch a backend
fetch('https://api.drakkarpress.com/health')
  .then(r => r.json())
  .then(console.log)

# 4. Desktop App
# Instalar y probar crear creación
```

---

## 🎉 ¡LISTO!

Después de completar estos pasos tendrás:
- ✅ Frontend en DrakkarPress.com
- ✅ Backend API en api.DrakkarPress.com
- ✅ Desktop App descargable
- ✅ Base de datos PostgreSQL en la nube
- ✅ Sistema completamente funcional

**Próximo paso:** ¡Beta testing con usuarios reales! 🚀
