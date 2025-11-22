# Guía Deploy Railway - Backend Actualizado

## Estado Actual
- ✅ Backend local compilado (JAR en `backend/target/`)
- ✅ Código subido a GitHub (branch `appmod/java-migration-20251117192852`)
- ❌ Railway ejecutando código antiguo (no tiene `/api/auth/social` ni `/api/profile/me`)

## URL Railway Confirmada
```
https://overflowing-consideration-production.up.railway.app
```

## Opciones de Deploy

### Opción A: Railway Auto-Deploy desde GitHub (Recomendado)

1. **Verificar qué rama observa Railway**
   - Ve a https://railway.app → tu proyecto backend
   - Settings → Source → Branch
   - Si dice `main`, necesitas merge; si dice tu branch actual, sólo push activa redeploy

2. **Si Railway escucha `main` (necesitas merge)**
   ```powershell
   # Desde C:\Users\SuperUsuario\DrakkarPress.com
   git add -A
   git commit -m "chore: preparando merge a main"
   git checkout main
   git pull origin main
   git merge appmod/java-migration-20251117192852
   git push origin main
   ```
   Railway detectará el push y redeployará automáticamente.

3. **Si Railway escucha tu branch actual**
   ```powershell
   # Ya hiciste push, sólo fuerza redeploy:
   # En Railway panel → Deployments → "Redeploy latest"
   ```

### Opción B: Deploy Manual (JAR directo)

Si Railway no hace auto-deploy:
1. Panel Railway → Settings → Deploy Command
2. Cambiar a usar JAR local (requiere configurar Railway CLI)

## Verificación Post-Deploy

Ejecuta estos tests cuando Railway termine el build:

```powershell
$RAILWAY_URL = "https://overflowing-consideration-production.up.railway.app"

# Health
Invoke-WebRequest -Uri "$RAILWAY_URL/api/health" -UseBasicParsing

# Social login (debe devolver 200 o 400, NO 500)
Invoke-WebRequest -Uri "$RAILWAY_URL/api/auth/social" -Method POST `
  -Body '{"provider":"google","externalToken":"demo12345"}' `
  -ContentType 'application/json' -UseBasicParsing

# Profile (debe dar 401 sin token)
Invoke-WebRequest -Uri "$RAILWAY_URL/api/profile/me" -UseBasicParsing
```

**Resultados esperados:**
- Health: 200 OK
- Social: 200 OK (con token) o 400 Bad Request (validación)
- Profile: 401 Unauthorized

## Actualizar Netlify

Una vez confirmado que Railway funciona, actualiza el proxy:

```powershell
# Editar netlify.toml línea 14
# Cambiar:
# to = "https://drakkarpress-backend.up.railway.app/api/:splat"
# Por:
to = "https://overflowing-consideration-production.up.railway.app/api/:splat"

git add netlify.toml
git commit -m "fix: actualizar URL backend Railway en proxy"
git push origin appmod/java-migration-20251117192852
```

Netlify redeployará automáticamente y el frontend empezará a usar el nuevo backend.

## Solución de Problemas

### Railway no detecta cambios
- Forzar redeploy desde panel: Deployments → tres puntos → Redeploy

### Merge falla con conflictos
```powershell
git checkout appmod/java-migration-20251117192852
git pull origin main
# Resolver conflictos manualmente
git add -A
git commit -m "merge: resolver conflictos con main"
git push origin appmod/java-migration-20251117192852
# Luego volver a intentar merge a main
```

### Variables de entorno faltantes
- Panel Railway → Variables
- Asegurar que existen: DATABASE_URL, JWT_SECRET, etc.

## Próximos Pasos

1. ✅ Compilar backend local (HECHO)
2. ⏳ Deploy a Railway (siguiendo Opción A o B)
3. ⏳ Verificar endpoints
4. ⏳ Actualizar netlify.toml
5. ⏳ Test frontend producción (login, perfil, social)
