# ⚡ DEPLOY EXPRESS - 3 PASOS

## 🎯 Para Subir a DrakkarPress.com AHORA

### 1️⃣ Commit y Push (1 minuto)
```powershell
cd C:\Users\SuperUsuario\DrakkarPress.com
git add .
git commit -m "Frontend DrakkarPress v1.0 - listo para producción"
git push origin main
```

### 2️⃣ Conectar Netlify (2 minutos)
1. Abrir: **https://app.netlify.com/**
2. Login con GitHub
3. **"Add new site"** → **"Import an existing project"**
4. Seleccionar: **GitHub** → **"DrakkarPress"**
5. Config:
   - Publish directory: **`.`**
   - Build command: *(vacío)*
6. **"Deploy site"**

### 3️⃣ Dominio Personalizado (5 minutos)
En Netlify:
- **"Domain settings"** → **"Add custom domain"**
- Escribir: `drakkarpress.com`
- Copiar los DNS que te muestran

En tu registrador de dominio:
```
A Record:
@ → 75.2.60.5

CNAME:
www → tu-sitio.netlify.app
```

## ✅ ¡Listo! 
Tu sitio estará en **https://drakkarpress.com** en minutos

---

## 🔧 Alternativa Rápida: Vercel

1. **https://vercel.com/** → Login con GitHub
2. **"Add New"** → **"Project"** → Import "DrakkarPress"
3. Framework: **Other**, Output: **`.`**
4. **Deploy**

DNS para Vercel:
```
A: @ → 76.76.21.21
CNAME: www → cname.vercel-dns.com
```

---

## 📋 Checklist

- [x] Git inicializado
- [x] Conectado a GitHub
- [x] netlify.toml configurado
- [x] Sin dependencias de backend
- [ ] Push a GitHub
- [ ] Deploy en Netlify/Vercel
- [ ] Configurar dominio

---

## 🚨 Si Hay Problemas

**Push falla:**
```powershell
git remote -v  # Verificar conexión
```

**Netlify no detecta archivos:**
- Verificar: Publish directory = `.` (punto)

**Dominio no resuelve:**
- Esperar 1-2 horas (propagación DNS)

---

## 💡 Qué Funciona Sin Backend

✅ Todas las páginas HTML  
✅ Cambio de idiomas (6 idiomas)  
✅ Navegación completa  
✅ Diseño responsive  

❌ Login/Registro (requiere backend)  
❌ Catálogo dinámico (requiere backend)  

**¡Pero es perfecto para probar UX y mostrar la plataforma!**

---

## 📞 Soporte Rápido

- **Netlify Docs**: https://docs.netlify.com/
- **Vercel Docs**: https://vercel.com/docs
- **GitHub Personal Token**: Settings → Developer settings → Tokens

---

**Deploy estimado: 10 minutos total**  
**En línea: 3 minutos después de deploy**
