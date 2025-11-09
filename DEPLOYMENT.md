# DrakkarPress - Guía de Deployment

## 🚀 Opciones de Deployment

### Opción 1: Vercel (Recomendado - 5 minutos)

```bash
# 1. Instala Vercel CLI
npm i -g vercel

# 2. Login (abre navegador)
vercel login

# 3. Deploy
cd DrakkarPress.com
vercel

# 4. Producción (con dominio custom)
vercel --prod
```

**Resultado**: Tu sitio estará en `https://drakkarpress.vercel.app`

**Configurar dominio custom:**
1. Ve a proyecto en Vercel Dashboard
2. Settings > Domains
3. Agrega `drakkarpress.com`
4. Configura DNS según instrucciones

---

### Opción 2: Netlify (5 minutos)

```bash
# 1. Instala Netlify CLI
npm install -g netlify-cli

# 2. Login
netlify login

# 3. Inicializa proyecto
cd DrakkarPress.com
netlify init

# 4. Deploy
netlify deploy --prod
```

**Resultado**: Tu sitio estará en `https://drakkarpress.netlify.app`

**Drag & Drop (Sin CLI):**
1. Ve a [Netlify](https://app.netlify.com)
2. Arrastra carpeta `DrakkarPress.com` completa
3. ¡Listo en 30 segundos!

---

### Opción 3: GitHub Pages (10 minutos)

```bash
# 1. Crea repositorio en GitHub
# https://github.com/new

# 2. Inicializa Git
git init
git add .
git commit -m "Initial commit - DrakkarPress MVP"
git branch -M main
git remote add origin https://github.com/tu-usuario/drakkarpress.git
git push -u origin main

# 3. Activa GitHub Pages
# Repositorio > Settings > Pages
# Source: Deploy from a branch
# Branch: main
# Folder: / (root)
# Save
```

**Resultado**: `https://tu-usuario.github.io/drakkarpress`

**Dominio custom:**
1. Settings > Pages > Custom domain
2. Agrega `drakkarpress.com`
3. Configura CNAME en tu DNS

---

### Opción 4: Hosting Tradicional (cPanel/FTP)

#### Paso 1: Prepara archivos
```bash
# Comprime todo el proyecto
zip -r drakkarpress.zip DrakkarPress.com/
```

#### Paso 2: Sube a hosting
1. Accede a tu cPanel
2. File Manager > public_html
3. Sube `drakkarpress.zip`
4. Extrae archivo
5. Mueve contenido de `DrakkarPress.com/` a `public_html/`

#### Paso 3: Configura SSL
1. cPanel > SSL/TLS Status
2. Run AutoSSL (Let's Encrypt gratis)
3. Espera 5 minutos

**Resultado**: `https://drakkarpress.com`

---

### Opción 5: ngrok (Demo Instantáneo)

```bash
# 1. Inicia servidor local
python -m http.server 8000

# 2. En otra terminal
ngrok http 8000
```

**Resultado**: URL pública temporal tipo `https://abc123.ngrok.io`

**⚠️ URL cambia cada vez que reinicias ngrok**

---

## 🌍 Configuración DNS (Para dominio custom)

### Registrar dominio
- [Namecheap](https://www.namecheap.com): ~$10/año
- [GoDaddy](https://www.godaddy.com): ~$12/año
- [Google Domains](https://domains.google): ~$12/año

### Configurar DNS para Vercel

```
Type    Name    Value                   TTL
A       @       76.76.21.21             3600
CNAME   www     cname.vercel-dns.com    3600
```

### Configurar DNS para Netlify

```
Type    Name    Value                       TTL
A       @       75.2.60.5                   3600
CNAME   www     drakkarpress.netlify.app    3600
```

### Configurar DNS para GitHub Pages

```
Type    Name    Value                   TTL
A       @       185.199.108.153         3600
A       @       185.199.109.153         3600
A       @       185.199.110.153         3600
A       @       185.199.111.153         3600
CNAME   www     tu-usuario.github.io    3600
```

---

## ✅ Checklist Pre-Deployment

### Archivos necesarios
- [x] index.html (landing)
- [x] 18 páginas HTML completas
- [x] js/i18n.js (multiidioma)
- [x] docs/ (16 archivos MD)
- [x] README.md actualizado
- [x] vercel.json configurado
- [x] netlify.toml configurado

### Testing
- [ ] Todas las páginas cargan correctamente
- [ ] Enlaces funcionan (no hay 404)
- [ ] Formularios validan campos
- [ ] JavaScript sin errores en consola
- [ ] Responsive funciona en móvil
- [ ] Dropdowns se despliegan
- [ ] Cambio de idioma funciona

### SEO Básico
- [ ] Títulos únicos en cada página
- [ ] Meta descriptions
- [ ] Alt text en imágenes importantes
- [ ] Sitemap.xml (opcional)
- [ ] robots.txt (opcional)

### Performance
- [ ] Imágenes optimizadas
- [ ] CSS minificado (opcional)
- [ ] JS minificado (opcional)
- [ ] Lazy loading para imágenes grandes

---

## 🔧 Troubleshooting

### Error: "Command not found: vercel"
```bash
# Reinstala Vercel CLI
npm uninstall -g vercel
npm install -g vercel
```

### Error: Página 404 en rutas
**Netlify**: Ya configurado en `netlify.toml`
**Vercel**: Ya configurado en `vercel.json`

### CSS no carga
Verifica rutas relativas en HTML:
```html
<!-- ❌ Mal -->
<link href="/css/style.css">

<!-- ✅ Bien -->
<link href="css/style.css">
```

### JavaScript no funciona
1. Abre DevTools (F12)
2. Ve a Console
3. Busca errores en rojo
4. Corrige y vuelve a deploy

---

## 📊 Post-Deployment

### Analytics
```html
<!-- Google Analytics - Agrega en <head> -->
<script async src="https://www.googletagmanager.com/gtag/js?id=G-XXXXXXXXXX"></script>
<script>
  window.dataLayer = window.dataLayer || [];
  function gtag(){dataLayer.push(arguments);}
  gtag('js', new Date());
  gtag('config', 'G-XXXXXXXXXX');
</script>
```

### Monitoreo
- [Uptime Robot](https://uptimerobot.com): Monitoreo gratis
- [Pingdom](https://www.pingdom.com): Checks de disponibilidad

### Backups
```bash
# Backup automático cada semana
git add .
git commit -m "Backup $(date +%Y-%m-%d)"
git push
```

---

## 🎯 Next Steps

1. **Deploy MVP**: Sube a Vercel/Netlify AHORA
2. **Comparte URL**: Prueba con usuarios reales
3. **Recopila feedback**: Escucha a tus usuarios
4. **Itera rápido**: Mejora basado en datos
5. **Backend**: Implementa APIs con Java Spring Boot
6. **Database**: PostgreSQL en AWS RDS
7. **Payments**: Integra Stripe Connect
8. **Scale**: Optimiza y crece

---

**⚔️ ¡Tu plataforma está lista para conquistar el mundo!** 🚀
