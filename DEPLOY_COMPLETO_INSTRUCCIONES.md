# 🚀 DEPLOY COMPLETO - INSTRUCCIONES EXACTAS

## ⚡ LO QUE YA ESTÁ HECHO ✅

- ✅ Código subido a GitHub
- ✅ Commit: "Frontend DrakkarPress v1.0"
- ✅ Branch: main
- ✅ vercel.json configurado
- ✅ 136 archivos listos

---

## 📝 LO QUE DEBES HACER (5-10 MINUTOS)

### PASO 1: Abrir Vercel
🔗 **https://vercel.com/new**

### PASO 2: Login
- Click en **"Continue with GitHub"**
- Autorizar Vercel

### PASO 3: Importar Proyecto
- Buscar: **"DrakkarPress"** o **"imageGeneratorZZ/DrakkarPress"**
- Click en **"Import"**

### PASO 4: Configuración
```
Project Name: drakkarpress

Framework Preset: Other

Root Directory: ./

Build Command: [DEJAR VACÍO]
Output Directory: .
Install Command: [DEJAR VACÍO]
```

### PASO 5: Deploy
- Click **"Deploy"**
- Esperar 1-2 minutos

---

## 🌐 CONFIGURAR DOMINIO

### En Vercel (Después del Deploy):
1. Ir a proyecto → **Settings** → **Domains**
2. Click **"Add"**
3. Escribir: `www.drakkarpress.com`
4. Vercel mostrará:
   ```
   CNAME: www → cname.vercel-dns.com
   ```

### En tu Registrador de Dominio:

#### **Configuración DNS:**
```
Tipo: CNAME
Nombre: www
Valor: cname.vercel-dns.com
TTL: Automático

Tipo: A
Nombre: @
Valor: 76.76.21.21
TTL: Automático
```

---

## 🎯 RESULTADO ESPERADO

✅ **www.drakkarpress.com** en línea en 10-15 minutos
✅ **HTTPS automático** (Let's Encrypt)
✅ **Deploy automático** en cada git push

---

## 🔍 VERIFICAR

1. https://www.drakkarpress.com (tu sitio)
2. https://dnschecker.org/ (propagación DNS)

---

**Tiempo total: 10-15 minutos**
