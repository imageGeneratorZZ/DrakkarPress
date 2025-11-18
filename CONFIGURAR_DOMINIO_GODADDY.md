# Configurar drakkarpress.com en GoDaddy para Netlify

## Paso 1: Acceder a GoDaddy DNS

1. Ve a https://dcc.godaddy.com/control/portfolio
2. Busca el dominio **drakkarpress.com**
3. Haz clic en los 3 puntos (⋮) → **Manage DNS** o **Administrar DNS**

## Paso 2: Configurar registros DNS

### Opción A: Dominio con www (RECOMENDADO)

#### 1. Agregar registro para www.drakkarpress.com

| Tipo  | Nombre | Valor                              | TTL    |
|-------|--------|-------------------------------------|--------|
| CNAME | www    | drakkarpress-platform.netlify.app  | 600    |

**Pasos:**
- Haz clic en **Add** o **Agregar**
- Selecciona **CNAME** en Type
- En Name escribe: `www`
- En Value/Data escribe: `drakkarpress-platform.netlify.app`
- TTL: 600 segundos (o default)
- Guarda

#### 2. Redirigir dominio apex (drakkarpress.com) a www

**Opción 1 - Forwarding en GoDaddy:**
- Ve a la sección **Forwarding** o **Reenvío de dominio**
- Configura redirección 301 de `drakkarpress.com` → `https://www.drakkarpress.com`

**Opción 2 - Registro A:**
| Tipo | Nombre | Valor      | TTL |
|------|--------|------------|-----|
| A    | @      | 75.2.60.5  | 600 |

### Opción B: Solo dominio apex (sin www)

Si prefieres usar solo `drakkarpress.com` (sin www):

| Tipo | Nombre | Valor      | TTL |
|------|--------|------------|-----|
| A    | @      | 75.2.60.5  | 600 |

**Nota:** Algunos proveedores permiten usar registro ALIAS apuntando a `drakkarpress-platform.netlify.app`

## Paso 3: Eliminar registros conflictivos

**IMPORTANTE:** Elimina estos registros si existen:
- ❌ A record existente para `@` (si no es el de Netlify)
- ❌ CNAME para `@` (no permitido)
- ❌ Otros A o CNAME que apunten a servicios antiguos

## Paso 4: Configurar en Netlify

1. Ve a https://app.netlify.com/projects/drakkarpress-platform/settings/domain
2. Haz clic en **Add custom domain**
3. Agrega: `www.drakkarpress.com` (o `drakkarpress.com` si usas apex)
4. Netlify verificará los DNS automáticamente
5. Una vez verificado, Netlify provisionará **certificado SSL gratuito** (Let's Encrypt)

## Paso 5: Verificar

**Espera 5-30 minutos para propagación DNS**, luego prueba:

```bash
# Verificar DNS
nslookup www.drakkarpress.com

# Verificar en navegador
https://www.drakkarpress.com
```

## Configuración DNS recomendada completa

```
Tipo    Nombre    Valor                              TTL
------  --------  ---------------------------------  -----
CNAME   www       drakkarpress-platform.netlify.app  600
A       @         75.2.60.5                          600
```

Luego configura forwarding de `drakkarpress.com` → `https://www.drakkarpress.com`

## Solución de problemas

### Error "DNS verification failed"
- Espera más tiempo (hasta 48h en casos raros)
- Verifica que no haya registros conflictivos
- Usa https://dnschecker.org para verificar propagación

### Error "Domain already registered"
- El dominio ya está en otro proyecto Netlify
- Elimínalo del proyecto anterior primero

### Certificado SSL no se provisiona
- Verifica que los DNS apunten correctamente
- Netlify genera el certificado automáticamente al verificar el dominio
- Puede tardar hasta 24h

## Resultado final

✅ **Frontend:** https://www.drakkarpress.com (Netlify)
✅ **Backend:** https://overflowing-consideration-production.up.railway.app (Railway)
✅ **SSL:** Automático via Let's Encrypt
✅ **Redirects:** De HTTP → HTTPS automático
