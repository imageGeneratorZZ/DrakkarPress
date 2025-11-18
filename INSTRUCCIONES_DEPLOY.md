# 🚀 Instrucciones de Despliegue - DrakkarPress

## 📋 Estado Actual

### ✅ Completado en Frontend
- Sistema de autenticación con `auth.js`
- Login y registro conectados al backend Railway
- Dashboards personalizados (escritores, imprentas, revendedores)
- Carga de perfil de usuario desde `/api/auth/me`
- Botón de cerrar sesión
- Commit: `1346753` - Push a GitHub ✅

### ✅ Completado en Backend
- Endpoint `GET /api/auth/me` implementado
- CORS actualizado con dominios de Netlify
- Método `getCurrentUser()` en `AuthService`
- Commit pendiente de push

---

## 🔧 Pasos para Completar el Despliegue

### 1. **Push del Backend a GitHub**

```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com
git add -A
git commit -m "feat: actualizar CORS y agregar soporte para Netlify"
git push origin appmod/java-migration-20251117192852
```

### 2. **Desplegar Backend en Railway**

#### Opción A: Desde Railway Dashboard (Web)
1. Ir a https://railway.app
2. Seleccionar proyecto "DrakkarPress"
3. Seleccionar servicio "overflowing-consideration"
4. Ir a "Deployments"
5. Click en "Deploy" (o esperar auto-deploy desde GitHub)

#### Opción B: Desde Railway CLI (Si está instalado)
```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com\backend
railway up
```

**Nota:** Si Railway CLI no está instalado:
```powershell
npm install -g @railway/cli
railway login
railway link
railway up
```

### 3. **Verificar Despliegue**

Una vez desplegado el backend, probar:

```bash
# Test 1: Health check
curl https://overflowing-consideration-production.up.railway.app/api/auth/health

# Test 2: Login (debe devolver token)
curl -X POST https://overflowing-consideration-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@ejemplo.com","password":"password123"}'

# Test 3: Get user info (reemplazar TOKEN con el token del login)
curl https://overflowing-consideration-production.up.railway.app/api/auth/me \
  -H "Authorization: Bearer TOKEN"
```

---

## 🌐 Frontend en Netlify

### Estado
✅ **Auto-deploy configurado desde GitHub**

Netlify detecta cambios en la rama `appmod/java-migration-20251117192852` y despliega automáticamente.

### URL de Producción
```
https://appmod-java-migration-20251117192852--drakkarpress.netlify.app
```

### Verificar Despliegue
1. Ir a https://app.netlify.com
2. Seleccionar site "drakkarpress"
3. Ver "Deploys" - debe mostrar último commit `1346753`
4. Estado debe ser "Published" con ✅ verde

---

## 🧪 Pruebas Post-Despliegue

### Test 1: Registro de Usuario
1. Ir a: https://appmod-java-migration-20251117192852--drakkarpress.netlify.app/register.html
2. Completar formulario de registro
3. Click en "Crear Cuenta"
4. Debe mostrar: "✅ Cuenta creada exitosamente"
5. Redirigir a login.html

### Test 2: Login
1. Ir a: https://appmod-java-migration-20251117192852--drakkarpress.netlify.app/login.html
2. Ingresar email y contraseña
3. Click en "Iniciar Sesión"
4. Debe redirigir al dashboard según rol

### Test 3: Perfil de Usuario
1. Después de login, verificar:
   - ✅ Header muestra: "¡Hola, [Tu Nombre]!"
   - ✅ Avatar muestra tus iniciales
   - ✅ Email visible
   - ✅ Número de usuario visible
   - ✅ Rol visible (Escritor/Imprenta/Revendedor)
   - ✅ Botón "🚪 Cerrar Sesión" en sidebar

### Test 4: Logout
1. Click en "Cerrar Sesión"
2. Confirmar en el dialog
3. Debe redirigir a login.html
4. Token debe ser eliminado del localStorage

### Test 5: Protección de Rutas
1. Cerrar sesión completamente
2. Intentar acceder directo a: `/escritores.html`
3. Debe redirigir automáticamente a `/login.html`

---

## 🔍 Troubleshooting

### Problema: "CORS Error" en consola
**Solución:**
1. Verificar que el backend en Railway tenga la última versión con CORS actualizado
2. Verificar que el dominio de Netlify esté en la lista de `allowedOrigins`
3. Hacer hard refresh: `Ctrl + Shift + R`

### Problema: "401 Unauthorized" en `/api/auth/me`
**Solución:**
1. Verificar que el token esté en localStorage: `localStorage.getItem('drakkarpress_token')`
2. Verificar que el token sea válido (no expirado)
3. Hacer login nuevamente para obtener token fresco

### Problema: Backend no responde
**Solución:**
1. Ir a Railway Dashboard
2. Ver logs del servicio "overflowing-consideration"
3. Verificar que el servicio esté "Running" (verde)
4. Verificar variables de entorno (DATABASE_URL, JWT_SECRET, etc.)

### Problema: Datos de usuario no cargan
**Solución:**
1. Abrir DevTools (F12)
2. Ir a "Console"
3. Buscar errores de JavaScript
4. Verificar que `auth.js` esté cargando: Network > JS > auth.js
5. Verificar que la llamada a `/api/auth/me` se haga correctamente

---

## 📊 Checklist de Verificación

### Backend (Railway)
- [ ] Código pusheado a GitHub
- [ ] Deployment exitoso en Railway
- [ ] Health check responde: `GET /api/auth/health`
- [ ] Login funciona: `POST /api/auth/login`
- [ ] Endpoint me funciona: `GET /api/auth/me`
- [ ] CORS configurado correctamente
- [ ] Variables de entorno configuradas

### Frontend (Netlify)
- [ ] Código pusheado a GitHub (commit `1346753`)
- [ ] Auto-deploy completado en Netlify
- [ ] Sitio accesible en URL de producción
- [ ] Login funciona desde producción
- [ ] Registro funciona desde producción
- [ ] Perfil de usuario se carga correctamente
- [ ] Botón de logout funciona
- [ ] Protección de rutas funciona

---

## 🎯 Próximos Pasos (Opcional)

### Mejoras Sugeridas
1. **Configurar SMTP** para envío de emails (SendGrid)
2. **Implementar OAuth2** (Google, Facebook, GitHub)
3. **Agregar Refresh Token** automático cuando expira access token
4. **Dashboard de admin** para gestionar usuarios
5. **Analytics** para trackear uso de la plataforma

### Optimizaciones
1. **Cache de perfil de usuario** en localStorage con TTL
2. **Lazy loading** de componentes del dashboard
3. **Service Worker** para offline support
4. **Compression** de assets en Netlify

---

## 📝 Notas Importantes

- **Token expiration:** Access tokens expiran en 15 minutos
- **Refresh tokens:** Duran 30 días
- **Session tracking:** Tokens activos se guardan en tabla `session_tokens`
- **CORS:** Configurado para localhost, drakkarpress.com y Netlify
- **Security:** CSRF deshabilitado (API REST stateless con JWT)

---

**Última actualización:** 18 de Noviembre, 2025  
**Estado:** ✅ Frontend completo | ⚠️ Backend pendiente de deploy
