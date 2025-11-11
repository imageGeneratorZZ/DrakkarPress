# ⚡ Configurar www.drakkarpress.com en Vercel

## 📍 DESPUÉS de que el Deploy termine...

### 1️⃣ En el Dashboard de Vercel:
1. Ve a tu proyecto **"drakkarpress"**
2. Click en **"Settings"** (menú superior)
3. Click en **"Domains"** (menú lateral izquierdo)
4. Click en el botón **"Add"**
5. Escribe: **`www.drakkarpress.com`**
6. Click en **"Add"**

### 2️⃣ Vercel te mostrará:
```
Nameservers or DNS Configuration needed:

Type: CNAME
Name: www
Value: cname.vercel-dns.com
```

### 3️⃣ En tu Registrador de Dominio:

#### Si tu dominio está en **GoDaddy**:
1. Ir a: https://dcc.godaddy.com/manage/
2. Buscar **"drakkarpress.com"**
3. Click en **"DNS"** o **"Manage DNS"**
4. Agregar nuevo registro:
   - **Tipo**: CNAME
   - **Nombre**: www
   - **Valor**: cname.vercel-dns.com
   - **TTL**: 600 (o automático)
5. **Guardar**

#### Si está en **Namecheap**:
1. Ir a: Dashboard → Domain List
2. Click en **"Manage"** junto a drakkarpress.com
3. Tab **"Advanced DNS"**
4. Click **"Add New Record"**:
   - **Type**: CNAME Record
   - **Host**: www
   - **Value**: cname.vercel-dns.com
   - **TTL**: Automatic
5. **Save**

#### Si está en **Cloudflare**:
1. Dashboard → Select domain drakkarpress.com
2. Tab **"DNS"**
3. Click **"Add record"**:
   - **Type**: CNAME
   - **Name**: www
   - **Target**: cname.vercel-dns.com
   - **Proxy status**: DNS only (nube gris)
4. **Save**

### 4️⃣ Dominio Raíz (Opcional)
Para que **drakkarpress.com** (sin www) también funcione:

```
Tipo: A
Nombre: @
Valor: 76.76.21.21
TTL: Automático
```

### ⏰ Tiempo de Propagación:
- **Mínimo**: 5-10 minutos
- **Típico**: 1-2 horas
- **Máximo**: 24-48 horas

### ✅ Verificar:
Usa: https://dnschecker.org/
- Escribe: www.drakkarpress.com
- Verifica que apunte a Vercel

---

## 🎉 URLs Finales:

- **Producción**: https://www.drakkarpress.com
- **Vercel Preview**: https://drakkarpress-[hash].vercel.app

---

**HTTPS se activa automáticamente en 5-15 minutos** 🔒
