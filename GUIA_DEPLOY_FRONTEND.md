# 🚀 Guía de Despliegue - Frontend DrakkarPress

## ✅ Estado Actual

- **Frontend**: ✅ Listo para despliegue
- **Configuración**: ✅ `netlify.toml` y `vercel.json` configurados
- **Dependencias Backend**: ✅ No hay (sitio estático puro)
- **Idiomas**: ✅ 6 idiomas soportados (ES, EN, PT, FR, DE, IT)

---

## 🎯 Opciones de Despliegue

### Opción 1: Netlify (Recomendado - Más Simple)

#### Paso 1: Preparar Repositorio Git
```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com

# Inicializar Git si no existe
git init

# Agregar archivos
git add .

# Commit inicial
git commit -m "Frontend DrakkarPress listo para deploy"
```

#### Paso 2: Crear Repositorio en GitHub
1. Ve a https://github.com/new
2. Nombre: `drakkarpress-frontend`
3. Privacidad: **Privado** (o público según prefieras)
4. NO inicialices con README (ya tienes archivos)
5. Clic en **"Create repository"**

#### Paso 3: Conectar y Subir
```powershell
# Agregar origen remoto (reemplaza TU_USUARIO con tu usuario GitHub)
git remote add origin https://github.com/TU_USUARIO/drakkarpress-frontend.git

# Subir código
git branch -M main
git push -u origin main
```

#### Paso 4: Desplegar en Netlify
1. Ve a https://app.netlify.com/
2. Clic en **"Add new site"** → **"Import an existing project"**
3. Selecciona **GitHub** y autoriza
4. Busca y selecciona `drakkarpress-frontend`
5. Configuración de build:
   - **Build command**: `echo 'Static site'` (o déjalo vacío)
   - **Publish directory**: `.` (punto = raíz)
6. Clic en **"Deploy site"**

#### Paso 5: Configurar Dominio Personalizado
1. En el dashboard de Netlify, ve a **"Domain settings"**
2. Clic en **"Add custom domain"**
3. Ingresa: `drakkarpress.com`
4. Netlify te dará los nameservers o registros DNS a configurar
5. Ve a tu registrador de dominio (GoDaddy, Namecheap, etc.)
6. Configura los DNS según las instrucciones de Netlify

**DNS típico para Netlify:**
```
Tipo: A
Host: @
Valor: 75.2.60.5

Tipo: CNAME
Host: www
Valor: tu-sitio.netlify.app
```

#### Paso 6: HTTPS Automático
- Netlify activa HTTPS automáticamente con Let's Encrypt
- Espera 5-10 minutos después de configurar el dominio

---

### Opción 2: Vercel (Alternativa Rápida)

#### Paso 1: Preparar Git (igual que Netlify)
```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com
git init
git add .
git commit -m "Frontend DrakkarPress listo para deploy"
```

#### Paso 2: Subir a GitHub (igual que Netlify)
```powershell
git remote add origin https://github.com/TU_USUARIO/drakkarpress-frontend.git
git branch -M main
git push -u origin main
```

#### Paso 3: Desplegar en Vercel
1. Ve a https://vercel.com/
2. Clic en **"Add New..."** → **"Project"**
3. Importa tu repositorio desde GitHub
4. Configuración de build:
   - **Framework Preset**: Other
   - **Build Command**: Dejar vacío
   - **Output Directory**: `.`
5. Clic en **"Deploy"**

#### Paso 4: Configurar Dominio
1. En el dashboard de Vercel, ve a **"Settings"** → **"Domains"**
2. Agrega `drakkarpress.com`
3. Configura los DNS en tu registrador:

**DNS típico para Vercel:**
```
Tipo: A
Host: @
Valor: 76.76.21.21

Tipo: CNAME
Host: www
Valor: cname.vercel-dns.com
```

---

## 🔧 Actualizaciones Futuras

### Método Rápido (Línea de Comandos)
```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com

# Hacer cambios en archivos HTML/CSS/JS
# ...

# Subir cambios
git add .
git commit -m "Descripción de cambios"
git push

# Netlify/Vercel detecta y redespliega automáticamente
```

### Netlify CLI (Opcional)
```powershell
# Instalar CLI
npm install -g netlify-cli

# Login
netlify login

# Deploy directo desde terminal
netlify deploy --prod
```

---

## 📋 Checklist Pre-Deploy

- [x] Archivos HTML verificados
- [x] `i18n.js` funcionando correctamente
- [x] Sin referencias a `localhost` o APIs locales
- [x] `netlify.toml` configurado
- [x] `vercel.json` configurado
- [x] Seguridad headers configurados
- [x] Git inicializado

---

## 🚨 Notas Importantes

### 1. **Backend Desconectado**
El frontend actualmente NO hace llamadas a APIs. Cuando el backend esté listo:
- Agregar archivo `js/api.js` con endpoints
- Configurar variables de entorno para URLs de API
- Actualizar formularios de registro/login

### 2. **Variables de Entorno (Futuro)**
Cuando conectes el backend, agrega en Netlify/Vercel:
```env
VITE_API_URL=https://api.drakkarpress.com
VITE_STRIPE_KEY=pk_live_...
```

### 3. **Funcionalidades Limitadas Sin Backend**
Sin backend funcionan:
- ✅ Navegación completa
- ✅ Cambio de idiomas
- ✅ Visualización de páginas estáticas
- ✅ Diseño responsive

Sin backend NO funcionan (por ahora):
- ❌ Registro de usuarios
- ❌ Login
- ❌ Catálogo dinámico
- ❌ Compras
- ❌ Generadores IA

### 4. **Prioridad: Probar UX/UI**
Con el frontend desplegado, puedes:
- ✅ Probar navegación y flujos de usuario
- ✅ Verificar diseño en móviles/tablets
- ✅ Mostrar a potenciales usuarios/inversores
- ✅ Obtener feedback sobre UX
- ✅ Probar sistema de idiomas

---

## 🎨 Personalización de Dominio

### Subdominios Recomendados
```
drakkarpress.com          → Landing principal
app.drakkarpress.com      → Aplicación (cuando backend esté listo)
api.drakkarpress.com      → Backend API
blog.drakkarpress.com     → Blog (futuro)
docs.drakkarpress.com     → Documentación
```

---

## 📞 Soporte

### Netlify
- Docs: https://docs.netlify.com/
- Community: https://answers.netlify.com/

### Vercel
- Docs: https://vercel.com/docs
- Support: https://vercel.com/support

---

## ✅ Próximos Pasos Después del Deploy

1. **Verificar despliegue**: Visita `https://drakkarpress.com`
2. **Probar idiomas**: Cambia entre ES/EN/PT/FR/DE/IT
3. **Probar responsive**: Abre en móvil y tablet
4. **Compartir URL**: Obtén feedback de usuarios
5. **Paralelamente**: Resolver problema de Lombok en backend
6. **Integrar**: Conectar frontend con backend cuando esté compilado

---

## 🐛 Troubleshooting

### Error: "Site not found"
- Verifica que el dominio esté correctamente configurado
- Espera 24-48h para propagación de DNS

### Error: "404 en páginas internas"
- Verifica que `netlify.toml` o `vercel.json` tengan las reglas de redirección

### Error: "Mixed content" (HTTP/HTTPS)
- Verifica que todas las URLs usen HTTPS
- Netlify/Vercel fuerzan HTTPS automáticamente

---

**Creado:** 2025-01-XX  
**Última actualización:** Antes del primer deploy  
**Estado Backend:** Compilación bloqueada (Lombok) - en progreso paralelo
