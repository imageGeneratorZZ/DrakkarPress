# 🚀 Deploy DrakkarPress en Vercel - www.drakkarpress.com

## ✅ Código ya subido a GitHub

✅ **Commit**: Frontend DrakkarPress v1.0 - Listo para deploy en Vercel  
✅ **GitHub**: https://github.com/imageGeneratorZZ/DrakkarPress  
✅ **Branch**: main  

---

## 📋 Pasos para Deploy en Vercel

### 1️⃣ Acceder a Vercel
1. Ir a **https://vercel.com/**
2. Click en **"Sign Up"** o **"Log In"**
3. Seleccionar **"Continue with GitHub"**
4. Autorizar Vercel para acceder a tus repositorios

---

### 2️⃣ Importar Proyecto
1. En el Dashboard de Vercel, click en **"Add New..."** (botón arriba derecha)
2. Seleccionar **"Project"**
3. En "Import Git Repository":
   - Si no ves tu repo, click en **"Adjust GitHub App Permissions"**
   - Autorizar acceso al repositorio **"DrakkarPress"**
4. Click en **"Import"** junto a "imageGeneratorZZ/DrakkarPress"

---

### 3️⃣ Configurar Proyecto
En la página de configuración:

**Framework Preset:**
- Seleccionar: **"Other"**

**Root Directory:**
- Dejar en: **"./"** (raíz del proyecto)

**Build and Output Settings:**
- Build Command: **(dejar vacío)**
- Output Directory: **"."** (punto)
- Install Command: **(dejar vacío)**

**Environment Variables:**
- Por ahora: **ninguna** (el frontend es estático)

---

### 4️⃣ Deploy
1. Click en **"Deploy"**
2. Vercel comenzará el proceso (toma ~1-2 minutos)
3. Verás logs en tiempo real
4. Al finalizar, verás: **"🎉 Congratulations!"**

**Tu sitio estará disponible en:**
```
https://drakkar-press-[hash].vercel.app
```

---

### 5️⃣ Configurar Dominio www.drakkarpress.com

#### En Vercel:
1. En el dashboard de tu proyecto, ir a **"Settings"**
2. En el menú lateral, click en **"Domains"**
3. Click en **"Add"**
4. Escribir: **`www.drakkarpress.com`**
5. Click en **"Add"**

#### Vercel te mostrará qué configurar:
```
Type: CNAME
Name: www
Value: cname.vercel-dns.com
```

O alternativamente:
```
Type: A
Name: www
Value: 76.76.21.21
```

---

### 6️⃣ Configurar DNS en tu Registrador

**Opción A: CNAME (Recomendado)**
```
Tipo: CNAME
Nombre/Host: www
Valor/Apunta a: cname.vercel-dns.com
TTL: Automático (o 3600)
```

**Opción B: A Record**
```
Tipo: A
Nombre/Host: www
Valor/IP: 76.76.21.21
TTL: Automático (o 3600)
```

**Para dominio raíz (drakkarpress.com sin www):**
```
Tipo: A
Nombre/Host: @
Valor/IP: 76.76.21.21
```

---

### 7️⃣ HTTPS Automático
- ✅ Vercel activa **HTTPS automáticamente** con Let's Encrypt
- ⏱️ Toma **5-15 minutos** después de configurar el dominio
- 🔒 Se renueva automáticamente cada 90 días

---

## ⏰ Tiempos Estimados

| Paso | Tiempo |
|------|--------|
| 1. Login en Vercel | 1 min |
| 2. Importar proyecto | 2 min |
| 3. Configurar | 1 min |
| 4. Deploy | 2 min |
| 5. Configurar dominio en Vercel | 2 min |
| 6. Configurar DNS | 5 min |
| 7. Propagación DNS | 5 min - 24h |
| **TOTAL** | **~15-20 minutos** |

---

## 🔄 Actualizaciones Automáticas

Cada vez que hagas `git push origin main`:
- ✅ Vercel detecta el cambio automáticamente
- ✅ Redespliega en ~30-60 segundos
- ✅ Sin necesidad de hacer nada más

---

## 🎯 URLs Finales

Después de configurar todo:

- **Producción**: https://www.drakkarpress.com
- **Sin www**: https://drakkarpress.com (redirige a www)
- **Preview Vercel**: https://drakkar-press-[hash].vercel.app (siempre funciona)

---

## 📱 Verificar Deploy

### Checklist Post-Deploy:
- [ ] Sitio carga en `https://www.drakkarpress.com`
- [ ] Navegación funciona (click en menús)
- [ ] Cambio de idioma funciona (selector de idiomas)
- [ ] Páginas cargan correctamente:
  - [ ] `/` (home)
  - [ ] `/escritores.html`
  - [ ] `/imprentas.html`
  - [ ] `/revendedores.html`
  - [ ] `/catalogo.html`
  - [ ] `/login.html`
  - [ ] `/register.html`
- [ ] Diseño responsive en móvil
- [ ] HTTPS activo (candado verde)

---

## 🐛 Solución de Problemas

### "Repository not found"
**Solución**: 
1. Ir a GitHub → Settings → Applications
2. Buscar "Vercel"
3. Click en "Configure"
4. Dar acceso al repositorio "DrakkarPress"

### "Build failed"
**Solución**: 
- Verifica que Output Directory sea **"."** (punto)
- Build Command debe estar **vacío** o ser `echo 'Static site'`

### "Domain not configured correctly"
**Solución**:
1. Verificar DNS en tu registrador
2. Usar herramienta: https://dnschecker.org/
3. Esperar hasta 24h para propagación completa

### "404 on pages"
**Solución**:
- Verifica que `vercel.json` tenga la configuración de rutas
- Ya está configurado en tu proyecto ✅

### "Mixed content warnings"
**Solución**:
- Vercel fuerza HTTPS automáticamente
- Si aparece, verificar que no haya `http://` hardcodeado en HTML

---

## 📊 Panel de Control Vercel

### Información Útil:
- **Analytics**: Vercel incluye analytics básicos gratis
- **Logs**: Ver logs de deploy en "Deployments"
- **Performance**: Métricas de velocidad
- **Team**: Agregar colaboradores (plan Pro)

---

## 💡 Funcionalidades Actuales (Sin Backend)

### ✅ Funcionan:
- Navegación completa
- Sistema de idiomas (ES, EN, PT, FR, DE, IT)
- Todas las páginas HTML
- Diseño responsive
- Formularios visuales (sin submit)

### ⏳ Requieren Backend (Próximamente):
- Login/Registro funcional
- Catálogo dinámico
- Sistema de compras
- Generadores IA
- Dashboard de usuario

---

## 🔮 Próximos Pasos

### Después del Deploy Frontend:
1. ✅ Verificar sitio en producción
2. ⏳ Resolver problema de Lombok en backend
3. ⏳ Compilar backend
4. ⏳ Deploy backend en servidor separado
5. ⏳ Crear subdominios:
   - `api.drakkarpress.com` → Backend API
   - `www.drakkarpress.com` → Frontend (este)
6. ⏳ Conectar frontend con backend
7. ⏳ Configurar PostgreSQL en producción
8. ⏳ Testing completo con usuarios reales

---

## 📞 Recursos

- **Vercel Docs**: https://vercel.com/docs
- **Vercel Support**: https://vercel.com/support
- **DNS Checker**: https://dnschecker.org/
- **GitHub Repo**: https://github.com/imageGeneratorZZ/DrakkarPress

---

## ✅ Status Actual

- [x] Código en GitHub
- [x] Commit realizado
- [x] Push completado
- [ ] Proyecto importado en Vercel
- [ ] Deploy inicial
- [ ] Dominio configurado
- [ ] DNS actualizado
- [ ] Sitio en vivo

---

**Creado**: 2025-11-11  
**Última actualización**: Después de git push  
**Estado**: ⏳ Listo para importar en Vercel
