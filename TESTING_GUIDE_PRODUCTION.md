# 🧪 Guía de Pruebas - Producción DrakkarPress

**URL Frontend**: https://www.drakkarpress.com  
**URL Backend**: https://overflowing-consideration-production.up.railway.app

---

## Pre-requisitos

✅ Railway deploy completado (monitor-railway-deploy.ps1 debe mostrar ✅)  
✅ Netlify actualizado con nuevo proxy  
✅ Browser con DevTools (F12) abierto para debug

---

## 1️⃣ Test Autenticación Email/Password

### Registro Nuevo Usuario
1. Ir a https://www.drakkarpress.com/register.html
2. Completar formulario:
   - Email: `test-$(Get-Date -Format 'hhmmss')@drakkarpress.com`
   - Password: `TestPass123!`
   - Username: (auto-generado del email)
3. Click "Registrarse"
4. **Resultado esperado**: 
   - Redirect a `/index.html`
   - Sidebar muestra username
   - localStorage tiene `token` y `user`

### Login Usuario Existente
1. Ir a https://www.drakkarpress.com/login.html
2. Email/password de usuario existente
3. Click "Iniciar Sesión"
4. **Resultado esperado**:
   - Redirect a `/index.html`
   - Sidebar muestra username correcto
   - Console sin errores CORS

---

## 2️⃣ Test Social Login (NUEVO ✨)

### Google Demo
1. Ir a https://www.drakkarpress.com/login.html
2. Click botón "Continuar con Google"
3. **Resultado esperado**:
   - Toast notification "Login exitoso"
   - Redirect a `/index.html`
   - Sidebar muestra username tipo "google_user_xxx"

### Facebook Demo
1. Logout: click perfil → Cerrar Sesión
2. Volver a login.html
3. Click "Continuar con Facebook"
4. **Resultado esperado**:
   - Toast "Login exitoso"
   - Redirect y sidebar muestra "facebook_user_xxx"

---

## 3️⃣ Test Perfil Usuario (NUEVO ✨)

### Ver Perfil
1. Estando logueado, click "Mi Perfil" en sidebar
2. **Resultado esperado**:
   - Carga `/profile.html`
   - Muestra avatar (inicial del username)
   - Display: username, email, plan
   - Campos editables: Nombre, Bio, Foto

### Editar Perfil
1. En profile.html, modificar:
   - Nombre: "Usuario de Prueba"
   - Bio: "Escritor apasionado de ciencia ficción"
2. Click "Guardar Cambios"
3. **Resultado esperado**:
   - Toast "Perfil actualizado"
   - Cambios visibles inmediatamente

### Persistencia
1. Recargar página (F5)
2. **Resultado esperado**:
   - Sesión persiste (no pide login)
   - Cambios de perfil aún visibles
   - Sidebar sigue mostrando username

---

## 4️⃣ Test Navegación

### Rutas Protegidas
1. Logout
2. Intentar ir a `/profile.html` directamente
3. **Resultado esperado**: Redirect a login

### Instagram Feed
1. Login
2. Ir a `/index.html`
3. **Resultado esperado**:
   - Layout Instagram con sidebar vertical
   - "Mi Perfil" clickeable
   - Username en header

---

## 5️⃣ Test API Directa (DevTools)

Abrir Console (F12) y ejecutar:

```javascript
// 1. Verificar token almacenado
console.log('Token:', localStorage.getItem('authToken'));
console.log('User:', JSON.parse(localStorage.getItem('user')));

// 2. Test endpoint profile
const token = localStorage.getItem('authToken');
fetch('https://overflowing-consideration-production.up.railway.app/api/profile/me', {
  headers: { 'Authorization': `Bearer ${token}` }
})
.then(r => r.json())
.then(data => console.log('Profile:', data));

// 3. Test social login
fetch('https://overflowing-consideration-production.up.railway.app/api/auth/social', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ provider: 'google', externalToken: 'demo123' })
})
.then(r => r.json())
.then(data => console.log('Social Login:', data));
```

**Resultados esperados:**
- Profile: `{ data: { id, username, email, bio, ... } }`
- Social Login: `{ data: { token: "jwt...", userId: ..., username: "google_user_..." } }`

---

## 6️⃣ Test CORS

1. Login desde www.drakkarpress.com
2. Abrir DevTools → Network
3. Filter: XHR
4. Hacer cualquier operación (login, profile, etc)
5. **Resultado esperado**:
   - Status 200 en requests a Railway
   - No errores CORS en console
   - Headers incluyen `Access-Control-Allow-Origin`

---

## 7️⃣ Test Performance

### Lighthouse Audit
1. DevTools → Lighthouse tab
2. Seleccionar: Performance, Accessibility, SEO
3. Click "Analyze page load"
4. **Target mínimo**:
   - Performance: > 80
   - Accessibility: > 90
   - SEO: > 90

### Cache / CDN
1. Recargar con cache (F5)
2. Verificar Network tab
3. **Resultado esperado**:
   - Static assets (CSS/JS) con status 304 (cached)
   - API calls siempre fresh (200)

---

## ❌ Troubleshooting

### Login no funciona
- **Check**: DevTools Console errores
- **Fix**: Limpiar localStorage: `localStorage.clear()` y reload

### Profile no carga
- **Check**: Token válido en localStorage
- **Fix**: Logout y volver a login

### Social login 500 error
- **Check**: Railway backend actualizado (monitor-railway-deploy.ps1)
- **Fix**: Esperar deploy completo o forzar redeploy en Railway

### CORS errors
- **Check**: netlify.toml tiene Railway URL correcta
- **Fix**: Verificar proxy en Netlify dashboard

---

## ✅ Checklist Final

- [ ] Registro nuevo usuario funciona
- [ ] Login email/password funciona
- [ ] Social login Google funciona
- [ ] Social login Facebook funciona
- [ ] Perfil carga correctamente
- [ ] Edición perfil guarda cambios
- [ ] Sesión persiste tras reload
- [ ] Logout funciona
- [ ] No hay errores CORS
- [ ] Lighthouse score > 80
- [ ] Mobile responsive (probar en viewport pequeño)

---

## 📊 Métricas Esperadas

**Tiempos de respuesta:**
- Health: < 200ms
- Login: < 500ms
- Profile GET: < 300ms
- Profile PUT: < 500ms

**Disponibilidad:**
- Uptime: > 99%
- Zero downtime durante normal operation

---

**Última actualización**: Nov 22, 2025  
**Deploy commit**: `3123c81`
