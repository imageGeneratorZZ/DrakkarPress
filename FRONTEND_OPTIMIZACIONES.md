# Optimización Frontend (estático)

Cambios aplicados (Nginx):
- Gzip activo (`json, css, js`)
- Cache-Control 30 días para assets (`css|js|img|fonts`)
- Fallback `try_files` a `index.html` (soporta SPA/simple routing)
- Cabeceras de seguridad básicas (`X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`)

## Próximos pasos sugeridos
- Minificación/empacado: migrar a una carpeta `public/` generada por Vite/Parcel (opcional) para hashing de archivos (`.hash.js`).
- SRI (Subresource Integrity) para `<script>` y `<link>` externos.
- `Content-Security-Policy` estricta (eliminar `'unsafe-inline'` y usar nonce/hash) si es viable.
- Preload/Preconnect para fuentes e imágenes críticas.
- Lighthouse pass y corrección de métricas (CLS/LCP/INP) principales.
