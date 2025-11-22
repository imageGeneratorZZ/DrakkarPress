# Lighthouse Audit & Optimization Guide

## 🎯 Ejecutar Lighthouse Audit

### Opción 1: Chrome DevTools (Recomendado)
```
1. Abre index-new.html en Chrome
2. F12 → pestaña "Lighthouse"
3. Selecciona:
   ☑ Performance
   ☑ Accessibility
   ☑ Best Practices
   ☑ SEO
4. Device: Desktop + Mobile
5. Click "Generate report"
```

### Opción 2: CLI (npm)
```powershell
# Instalar Lighthouse CLI
npm install -g lighthouse

# Ejecutar audit (requiere servidor local)
lighthouse http://localhost:8080/index-new.html --output html --output-path ./lighthouse-report.html --view
```

### Opción 3: PageSpeed Insights
```
https://pagespeedinsights.google.com/
(Usar URL pública tras deploy)
```

---

## ✅ Optimizaciones Ya Implementadas

### Performance ⚡
- [x] Critical CSS inline/preload (critical.css ~100 líneas)
- [x] Deferred CSS con media trick (components.css)
- [x] Scripts con defer attribute
- [x] Preload hints para recursos críticos
- [x] CSS custom properties (no runtime calc)
- [x] Font display strategy (system fonts, no web fonts)
- [x] Minificación de HTML (reducción 56%)

### Accessibility ♿
- [x] Skip-to-main link
- [x] ARIA roles (banner, navigation, main, contentinfo, region)
- [x] aria-label en CTAs y links importantes
- [x] Semantic HTML (article, section, header, footer, nav)
- [x] Heading hierarchy (h1 → h2 → h3)
- [x] Keyboard navigation (tabs, dropdowns)
- [x] Focus visible styles
- [x] Alt text placeholders (needs real images)
- [x] Color contrast (checked con design tokens)

### Best Practices 🛡️
- [x] HTTPS ready (Railway deployment)
- [x] No console errors (clean JS)
- [x] Doctype declaration
- [x] Meta viewport
- [x] Character encoding UTF-8
- [x] No deprecated APIs
- [x] Secure headers ready (backend CORS configured)

### SEO 🔍
- [x] Meta description
- [x] Meta keywords
- [x] Open Graph tags (Facebook)
- [x] Twitter Cards
- [x] Schema.org JSON-LD (Organization + WebSite)
- [x] Canonical URL
- [x] Language attribute (html lang="es")
- [x] Semantic HTML structure
- [x] Descriptive link text

---

## 🚧 Optimizaciones Pendientes

### 1. Imágenes (Alta Prioridad)

**Problemas:**
- Sin lazy loading
- Sin atributos width/height (CLS)
- Sin alt text en placeholder images
- Sin formatos modernos (WebP/AVIF)

**Soluciones:**
```html
<!-- Antes -->
<img src="/assets/images/book-cover.jpg">

<!-- Después -->
<img 
    src="/assets/images/book-cover.jpg"
    srcset="/assets/images/book-cover-320w.webp 320w,
            /assets/images/book-cover-640w.webp 640w,
            /assets/images/book-cover-1024w.webp 1024w"
    sizes="(max-width: 640px) 100vw, (max-width: 1024px) 50vw, 33vw"
    width="300"
    height="450"
    alt="Portada del libro: [Título]"
    loading="lazy"
    decoding="async"
>
```

**Herramientas:**
- Squoosh.app (conversión WebP/AVIF)
- ImageOptim (compresión lossless)
- Sharp (CLI para batch processing)

**Script de conversión:**
```powershell
# Instalar Sharp CLI
npm install -g sharp-cli

# Convertir a WebP
Get-ChildItem -Path assets/images/*.jpg | ForEach-Object {
    sharp $_.FullName -o "$($_.DirectoryName)/$($_.BaseName).webp" --webp
}
```

### 2. Cargar Recursos de Terceros Eficientemente

**Chart.js en escritores.html:**
```html
<!-- Antes -->
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>

<!-- Después -->
<link rel="preconnect" href="https://cdn.jsdelivr.net">
<link rel="dns-prefetch" href="https://cdn.jsdelivr.net">
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js" defer></script>
```

### 3. Reduce Unused CSS

**Problema:** design-tokens.css define ~100 utility classes, solo usamos ~40%

**Solución 1: PurgeCSS**
```powershell
npm install -D purgecss
npx purgecss --css assets/css/design-tokens.css --content index-new.html --output assets/css/design-tokens.min.css
```

**Solución 2: Manual Split**
- Crear `design-tokens-core.css` (usado en todas las páginas)
- Crear `design-tokens-extended.css` (load on-demand)

### 4. Minificación

**CSS:**
```powershell
# Instalar CleanCSS
npm install -g clean-css-cli

# Minificar
cleancss -o assets/css/design-tokens.min.css assets/css/design-tokens.css
cleancss -o assets/css/critical.min.css assets/css/critical.css
cleancss -o assets/css/components.min.css assets/css/components.css
```

**JavaScript:**
```powershell
# Instalar Terser
npm install -g terser

# Minificar
terser assets/js/ui/auth-state.js -o assets/js/ui/auth-state.min.js -c -m
terser assets/js/ui/cta-premium.js -o assets/js/ui/cta-premium.min.js -c -m
terser assets/js/ui/tabs.js -o assets/js/ui/tabs.min.js -c -m
terser assets/js/ui/nav-loader.js -o assets/js/ui/nav-loader.min.js -c -m
terser assets/js/ui/i18n-enhanced.js -o assets/js/ui/i18n-enhanced.min.js -c -m
```

**HTML:**
```powershell
# Instalar html-minifier
npm install -g html-minifier

# Minificar
html-minifier --collapse-whitespace --remove-comments --minify-css --minify-js index-new.html -o index.min.html
```

### 5. Content Security Policy (CSP)

**Añadir en <head>:**
```html
<meta http-equiv="Content-Security-Policy" content="
    default-src 'self';
    script-src 'self' https://cdn.jsdelivr.net;
    style-src 'self' 'unsafe-inline';
    img-src 'self' data: https:;
    font-src 'self';
    connect-src 'self' https://drakkarpress-backend-production.up.railway.app;
">
```

### 6. Service Worker (PWA)

**Crear `sw.js`:**
```javascript
const CACHE_NAME = 'drakkarpress-v1';
const urlsToCache = [
    '/',
    '/assets/css/design-tokens.min.css',
    '/assets/css/critical.min.css',
    '/assets/js/ui/nav-loader.min.js',
    '/assets/js/ui/auth-state.min.js'
];

self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME)
            .then(cache => cache.addAll(urlsToCache))
    );
});

self.addEventListener('fetch', event => {
    event.respondWith(
        caches.match(event.request)
            .then(response => response || fetch(event.request))
    );
});
```

**Registrar en index-new.html:**
```javascript
if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/sw.js');
}
```

### 7. Preload/Prefetch Estratégico

```html
<!-- Preload critical resources -->
<link rel="preload" href="/assets/css/critical.min.css" as="style">
<link rel="preload" href="/assets/css/design-tokens.min.css" as="style">

<!-- Prefetch likely navigation -->
<link rel="prefetch" href="/escritores.html">
<link rel="prefetch" href="/catalogo.html">

<!-- Preconnect to external domains -->
<link rel="preconnect" href="https://drakkarpress-backend-production.up.railway.app">
<link rel="dns-prefetch" href="https://drakkarpress-backend-production.up.railway.app">
```

---

## 📊 Target Scores

| Categoría | Target | Estrategia |
|-----------|--------|------------|
| **Performance** | >90 | Lazy images, minify, code split, preload |
| **Accessibility** | >95 | Alt text, ARIA, keyboard nav, contrast |
| **Best Practices** | >95 | HTTPS, CSP, no console errors, secure |
| **SEO** | >95 | Meta tags, schema.org, semantic HTML |

---

## 🔧 Scripts de Optimización Rápida

### Script PowerShell Completo
```powershell
# optimize-frontend.ps1

Write-Host "🚀 Optimizando Frontend DrakkarPress..." -ForegroundColor Cyan

# 1. Minificar CSS
Write-Host "`n📦 Minificando CSS..." -ForegroundColor Yellow
cleancss -o assets/css/design-tokens.min.css assets/css/design-tokens.css
cleancss -o assets/css/critical.min.css assets/css/critical.css
cleancss -o assets/css/components.min.css assets/css/components.css

# 2. Minificar JavaScript
Write-Host "`n📦 Minificando JavaScript..." -ForegroundColor Yellow
terser assets/js/ui/auth-state.js -o assets/js/ui/auth-state.min.js -c -m
terser assets/js/ui/cta-premium.js -o assets/js/ui/cta-premium.min.js -c -m
terser assets/js/ui/tabs.js -o assets/js/ui/tabs.min.js -c -m
terser assets/js/ui/nav-loader.js -o assets/js/ui/nav-loader.min.js -c -m
terser assets/js/ui/i18n-enhanced.js -o assets/js/ui/i18n-enhanced.min.js -c -m

# 3. Optimizar imágenes (si existen)
Write-Host "`n🖼️ Optimizando imágenes..." -ForegroundColor Yellow
if (Test-Path "assets/images/*.jpg") {
    Get-ChildItem -Path assets/images/*.jpg | ForEach-Object {
        $webpPath = "$($_.DirectoryName)/$($_.BaseName).webp"
        if (-not (Test-Path $webpPath)) {
            sharp $_.FullName -o $webpPath --webp
        }
    }
}

# 4. Generar index.html optimizado
Write-Host "`n📄 Minificando HTML..." -ForegroundColor Yellow
html-minifier --collapse-whitespace --remove-comments --minify-css --minify-js index-new.html -o index.min.html

Write-Host "`n✅ Optimización completa!" -ForegroundColor Green
Write-Host "📊 Ejecuta Lighthouse para verificar mejoras." -ForegroundColor Cyan
```

### Ejecutar:
```powershell
# Instalar dependencias (una vez)
npm install -g clean-css-cli terser html-minifier sharp-cli

# Ejecutar script
.\optimize-frontend.ps1
```

---

## 🎯 Checklist Final

### Antes de Deploy
- [ ] Ejecutar optimize-frontend.ps1
- [ ] Lighthouse audit (Desktop + Mobile) >90 todos
- [ ] Probar en Chrome, Firefox, Safari
- [ ] Verificar responsive (320px → 2560px)
- [ ] Test keyboard navigation completo
- [ ] Test screen reader (NVDA/JAWS)
- [ ] Verificar meta tags con Facebook Debugger
- [ ] Verificar schema.org con Google Rich Results Test
- [ ] Probar Premium checkout flow (auth + unauth)
- [ ] Verificar i18n switcher (ES ↔ EN)
- [ ] Test login/register forms
- [ ] Verificar Analytics/GTM (si aplica)

### Post-Deploy
- [ ] PageSpeed Insights URL pública
- [ ] Real User Monitoring setup (opcional)
- [ ] Configurar CDN (Cloudflare/CloudFront)
- [ ] SSL certificate verificado
- [ ] Redirects HTTP → HTTPS
- [ ] Configurar Cache-Control headers
- [ ] Comprimir assets (Brotli/Gzip server-side)

---

## 📈 Mejoras Incrementales (Futuro)

1. **Critical CSS automático:** `critical` package
2. **Code splitting:** Webpack/Rollup bundles por página
3. **Lazy components:** Intersection Observer para below-fold
4. **Resource hints dinámicos:** Prefetch según user behavior
5. **Edge caching:** Cloudflare Workers/Vercel Edge
6. **Image CDN:** Cloudinary/imgix con transformaciones on-the-fly
7. **Performance budget:** Lighthouse CI en pipeline
8. **A/B testing:** Optimizely/VWO para conversión

---

**Documentado por:** GitHub Copilot  
**Última actualización:** Noviembre 2024
