# Mapa del Sitio (Sitemap) - DrakkarPress

## Estructura de URLs

### 🏠 Páginas Públicas

```
/ (Home)
├── /categorias
│   ├── /categoria/scryptorium
│   ├── /categoria/erotica
│   ├── /categoria/thriller
│   ├── /categoria/romance
│   ├── /categoria/fantasia-scifi
│   ├── /categoria/cocina
│   └── /categoria/no-ficcion
│
├── /catalogo (Búsqueda/Resultados)
│   └── /buscar?q={query}&cat={categoria}&precio={rango}&...
│
├── /libro/{isbn-o-slug}
│   ├── /libro/{id}/vista-previa
│   └── /libro/{id}/reviews
│
├── /imprentas
│   ├── /imprentas/mapa
│   └── /imprentas/{pais}
│
├── /ia-drakkarpress
│   └── /ia/demo
│
├── /sobre-nosotros
├── /como-funciona
├── /precios
├── /faq
├── /contacto
├── /blog
│   └── /blog/{slug}
│
└── /legal
    ├── /terminos
    ├── /privacidad
    └── /cookies
```

### 🔐 Autenticación

```
/auth
├── /login
├── /registro
│   ├── /registro/escritor
│   ├── /registro/revendedor
│   ├── /registro/imprenta
│   └── /registro/lector
├── /recuperar-password
├── /verificar-email
└── /onboarding
```

### ✍️ Dashboard Escritor

```
/dashboard/escritor
├── /dashboard/escritor/libros
│   ├── /dashboard/escritor/libros/nuevo
│   ├── /dashboard/escritor/libros/{id}/editar
│   ├── /dashboard/escritor/libros/{id}/estadisticas
│   └── /dashboard/escritor/libros/{id}/config
│
├── /dashboard/escritor/ingresos
│   ├── /dashboard/escritor/ingresos/historial
│   └── /dashboard/escritor/ingresos/solicitar-pago
│
├── /dashboard/escritor/estadisticas
│
├── /dashboard/escritor/ia
│   ├── /dashboard/escritor/ia/generar-ideas
│   ├── /dashboard/escritor/ia/extender-texto
│   ├── /dashboard/escritor/ia/sinopsis
│   ├── /dashboard/escritor/ia/titulos
│   ├── /dashboard/escritor/ia/marketing
│   └── /dashboard/escritor/ia/historial
│
├── /dashboard/escritor/integraciones
│   ├── /dashboard/escritor/integraciones/shopify
│   ├── /dashboard/escritor/integraciones/mercadolibre
│   └── /dashboard/escritor/integraciones/lulu
│
└── /dashboard/escritor/perfil
    ├── /dashboard/escritor/perfil/editar
    ├── /dashboard/escritor/perfil/seguridad
    └── /dashboard/escritor/perfil/plan
```

### 💼 Dashboard Revendedor

```
/dashboard/revendedor
├── /dashboard/revendedor/catalogo
│   ├── /dashboard/revendedor/catalogo/agregar
│   └── /dashboard/revendedor/catalogo/gestionar
│
├── /dashboard/revendedor/ventas
│   └── /dashboard/revendedor/ventas/historial
│
├── /dashboard/revendedor/comisiones
│   ├── /dashboard/revendedor/comisiones/historial
│   └── /dashboard/revendedor/comisiones/solicitar-pago
│
├── /dashboard/revendedor/marketing
│   ├── /dashboard/revendedor/marketing/contenido
│   ├── /dashboard/revendedor/marketing/enlaces
│   ├── /dashboard/revendedor/marketing/qr
│   └── /dashboard/revendedor/marketing/imagenes
│
├── /dashboard/revendedor/estadisticas
│
└── /dashboard/revendedor/perfil
```

### 🏭 Dashboard Imprenta

```
/dashboard/imprenta
├── /dashboard/imprenta/pedidos
│   ├── /dashboard/imprenta/pedidos/{id}
│   ├── /dashboard/imprenta/pedidos/pendientes
│   ├── /dashboard/imprenta/pedidos/en-produccion
│   ├── /dashboard/imprenta/pedidos/completados
│   └── /dashboard/imprenta/pedidos/historial
│
├── /dashboard/imprenta/pagos
│   ├── /dashboard/imprenta/pagos/pendientes
│   └── /dashboard/imprenta/pagos/historial
│
├── /dashboard/imprenta/estadisticas
│
├── /dashboard/imprenta/configuracion
│   ├── /dashboard/imprenta/configuracion/capacidad
│   ├── /dashboard/imprenta/configuracion/zonas
│   └── /dashboard/imprenta/configuracion/precios
│
└── /dashboard/imprenta/perfil
```

### 📚 Dashboard Lector

```
/dashboard/lector
├── /dashboard/lector/biblioteca
│   ├── /dashboard/lector/biblioteca/digitales
│   ├── /dashboard/lector/biblioteca/fisicos
│   └── /dashboard/lector/biblioteca/{bookId}/leer
│
├── /dashboard/lector/pedidos
│   ├── /dashboard/lector/pedidos/{id}
│   └── /dashboard/lector/pedidos/{id}/rastrear
│
├── /dashboard/lector/favoritos
│
├── /dashboard/lector/resenas
│   └── /dashboard/lector/resenas/nueva/{bookId}
│
└── /dashboard/lector/perfil
    ├── /dashboard/lector/perfil/editar
    └── /dashboard/lector/perfil/direcciones
```

### 🛒 E-commerce

```
/carrito
├── /checkout
│   ├── /checkout/envio
│   ├── /checkout/pago
│   └── /checkout/confirmacion
│
└── /orden/{orderId}
    └── /orden/{orderId}/comprobante
```

### 🔧 Admin (Super Usuario)

```
/admin
├── /admin/dashboard
├── /admin/usuarios
│   ├── /admin/usuarios/escritores
│   ├── /admin/usuarios/revendedores
│   ├── /admin/usuarios/imprentas
│   └── /admin/usuarios/lectores
│
├── /admin/libros
│   ├── /admin/libros/pendientes-revision
│   ├── /admin/libros/publicados
│   └── /admin/libros/reportados
│
├── /admin/ordenes
│
├── /admin/pagos
│   ├── /admin/pagos/regalias
│   ├── /admin/pagos/comisiones
│   └── /admin/pagos/imprentas
│
├── /admin/integraciones
│   ├── /admin/integraciones/shopify
│   ├── /admin/integraciones/mercadolibre
│   └── /admin/integraciones/lulu
│
├── /admin/reportes
│   ├── /admin/reportes/ventas
│   ├── /admin/reportes/usuarios
│   └── /admin/reportes/financiero
│
└── /admin/configuracion
    ├── /admin/configuracion/general
    ├── /admin/configuracion/categorias
    ├── /admin/configuracion/comisiones
    └── /admin/configuracion/ia
```

---

## 🔗 API Endpoints

### REST API Pública

```
BASE URL: https://api.drakkarpress.com/v1

/api/v1/books
├── GET    /api/v1/books                      # Listar libros
├── GET    /api/v1/books/{id}                 # Detalle de libro
├── GET    /api/v1/books/search               # Búsqueda
├── GET    /api/v1/books/category/{category}  # Por categoría
└── GET    /api/v1/books/{id}/reviews         # Reseñas

/api/v1/categories
├── GET    /api/v1/categories                 # Listar categorías
└── GET    /api/v1/categories/{slug}          # Detalle categoría

/api/v1/authors
├── GET    /api/v1/authors                    # Listar autores
├── GET    /api/v1/authors/{id}               # Perfil autor
└── GET    /api/v1/authors/{id}/books         # Libros del autor

/api/v1/printers
├── GET    /api/v1/printers                   # Listar imprentas
├── GET    /api/v1/printers/map               # Mapa de imprentas
└── GET    /api/v1/printers/country/{code}    # Por país
```

### REST API Autenticada

```
/api/v1/auth
├── POST   /api/v1/auth/register
├── POST   /api/v1/auth/login
├── POST   /api/v1/auth/logout
├── POST   /api/v1/auth/refresh-token
└── POST   /api/v1/auth/forgot-password

/api/v1/user
├── GET    /api/v1/user/profile
├── PUT    /api/v1/user/profile
├── PUT    /api/v1/user/password
└── DELETE /api/v1/user/account

/api/v1/writer/books
├── GET    /api/v1/writer/books
├── POST   /api/v1/writer/books
├── GET    /api/v1/writer/books/{id}
├── PUT    /api/v1/writer/books/{id}
├── DELETE /api/v1/writer/books/{id}
├── POST   /api/v1/writer/books/{id}/publish
└── GET    /api/v1/writer/books/{id}/stats

/api/v1/writer/earnings
├── GET    /api/v1/writer/earnings
├── GET    /api/v1/writer/earnings/summary
└── POST   /api/v1/writer/earnings/request-payment

/api/v1/reseller/catalog
├── GET    /api/v1/reseller/catalog
├── POST   /api/v1/reseller/catalog/add
├── DELETE /api/v1/reseller/catalog/{bookId}
└── GET    /api/v1/reseller/catalog/link/{bookId}

/api/v1/reseller/sales
├── GET    /api/v1/reseller/sales
└── GET    /api/v1/reseller/commissions

/api/v1/printer/orders
├── GET    /api/v1/printer/orders
├── GET    /api/v1/printer/orders/{id}
└── PUT    /api/v1/printer/orders/{id}/status

/api/v1/reader/library
├── GET    /api/v1/reader/library
├── GET    /api/v1/reader/library/{bookId}
└── GET    /api/v1/reader/library/{bookId}/download

/api/v1/reader/orders
├── GET    /api/v1/reader/orders
└── GET    /api/v1/reader/orders/{id}/tracking

/api/v1/cart
├── GET    /api/v1/cart
├── POST   /api/v1/cart/add
├── PUT    /api/v1/cart/update/{itemId}
├── DELETE /api/v1/cart/remove/{itemId}
└── DELETE /api/v1/cart/clear

/api/v1/orders
├── POST   /api/v1/orders/checkout
├── GET    /api/v1/orders/{id}
└── POST   /api/v1/orders/{id}/cancel

/api/v1/reviews
├── GET    /api/v1/reviews/book/{bookId}
├── POST   /api/v1/reviews
├── PUT    /api/v1/reviews/{id}
└── DELETE /api/v1/reviews/{id}

/api/v1/ai
├── POST   /api/v1/ai/generate-ideas
├── POST   /api/v1/ai/extend-text
├── POST   /api/v1/ai/generate-synopsis
├── POST   /api/v1/ai/generate-titles
├── POST   /api/v1/ai/improve-text
└── POST   /api/v1/ai/marketing-copy
```

### Webhooks (Recepción)

```
/webhooks/shopify
├── POST   /webhooks/shopify/order-created
├── POST   /webhooks/shopify/order-updated
└── POST   /webhooks/shopify/refund-created

/webhooks/mercadolibre
└── POST   /webhooks/mercadolibre/notifications

/webhooks/lulu
├── POST   /webhooks/lulu/order-status
└── POST   /webhooks/lulu/shipment-update

/webhooks/stripe (si se usa)
├── POST   /webhooks/stripe/payment-intent
└── POST   /webhooks/stripe/checkout-complete
```

---

## 📱 Deep Links / Enlaces Dinámicos

### Compartir Libro

```
https://drakkarpress.com/l/{short-code}
→ Redirige a /libro/{isbn}

Ejemplo: https://drakkarpress.com/l/abc123
```

### Enlaces de Afiliado

```
https://drakkarpress.com/a/{username}
→ Landing de afiliado con su catálogo

https://drakkarpress.com/a/{username}/{book-slug}
→ Libro específico con tracking de afiliado

Ejemplos:
https://drakkarpress.com/a/mariasanchez
https://drakkarpress.com/a/mariasanchez/el-mar-eterno
```

### QR Codes

```
https://drakkarpress.com/qr/{qr-code}
→ Redirige con tracking específico
```

---

## 🌐 Subdominios

### API
```
https://api.drakkarpress.com
```

### Imágenes/Assets (CDN)
```
https://cdn.drakkarpress.com
├── /covers/{isbn}.jpg
├── /avatars/{userId}.jpg
└── /marketing/{imageId}.jpg
```

### Blog (opcional)
```
https://blog.drakkarpress.com
```

### Documentación API
```
https://docs.drakkarpress.com
```

### Status Page
```
https://status.drakkarpress.com
```

---

## 📄 Sitemap XML (SEO)

**Archivo:** `/sitemap.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<sitemapindex xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
  <sitemap>
    <loc>https://drakkarpress.com/sitemap-pages.xml</loc>
  </sitemap>
  <sitemap>
    <loc>https://drakkarpress.com/sitemap-books.xml</loc>
  </sitemap>
  <sitemap>
    <loc>https://drakkarpress.com/sitemap-categories.xml</loc>
  </sitemap>
  <sitemap>
    <loc>https://drakkarpress.com/sitemap-authors.xml</loc>
  </sitemap>
  <sitemap>
    <loc>https://drakkarpress.com/sitemap-blog.xml</loc>
  </sitemap>
</sitemapindex>
```

**Prioridades:**
- Home `/`: 1.0
- Categorías `/categoria/*`: 0.9
- Libros individuales `/libro/*`: 0.8
- Páginas estáticas: 0.7
- Blog posts: 0.6

**Frecuencia de actualización:**
- Home: daily
- Catálogo: daily
- Libros: weekly
- Páginas estáticas: monthly

---

## 🔄 Redirecciones

### Redirecciones Permanentes (301)

```
/books → /catalogo
/author/{id} → /autor/{id}
/shop → /catalogo
/store → /catalogo
```

### Redirecciones por Rol (después de login)

```
Login → Detectar rol → Redirigir

ESCRITOR → /dashboard/escritor
REVENDEDOR → /dashboard/revendedor
IMPRENTA → /dashboard/imprenta
LECTOR → /dashboard/lector
ADMIN → /admin/dashboard
```

---

## 🌍 Internacionalización (i18n)

### URLs Multiidioma (futuro)

```
/es/* (Español - default)
/en/* (English)
/pt/* (Português)
/fr/* (Français)

Ejemplos:
/es/categoria/romance
/en/category/romance
/pt/categoria/romance
```

### Detección de idioma

1. URL path (`/es/`, `/en/`)
2. Header `Accept-Language`
3. Cookie/localStorage preferencia usuario
4. Geolocalización IP (fallback)

---

## 📊 Métricas y Analytics

### Google Analytics 4

**Eventos personalizados:**
- `search_books` - Búsquedas
- `view_book` - Vista de libro
- `add_to_cart` - Agregar al carrito
- `begin_checkout` - Iniciar compra
- `purchase` - Compra completada
- `share_book` - Compartir libro
- `download_book` - Descarga digital
- `write_review` - Escribir reseña
- `use_ai_tool` - Uso de IA

### Pixel de Facebook/Meta

Para retargeting y conversiones en MercadoLibre/Shopify

### Hotjar / Microsoft Clarity

Mapas de calor y grabaciones de sesión

---

## 🔒 Seguridad

### Rutas Protegidas

Todas las rutas `/dashboard/*` requieren autenticación

### Rate Limiting

```
API Pública: 100 req/min
API Autenticada: 500 req/min
Webhooks: Sin límite (verificado por firma)
```

### CORS

```
Allowed Origins:
- https://drakkarpress.com
- https://www.drakkarpress.com
- https://admin.drakkarpress.com
```

---

## 📱 Progressive Web App (PWA)

### Manifest
```
/manifest.json
```

### Service Worker
```
/sw.js
```

### Offline Pages
```
/offline
```

---

## 🎯 Enlaces Rápidos (Footer)

**Plataforma:**
- Para autores
- Para afiliados
- Para imprentas
- Catálogo

**Categorías:**
- Scryptorium
- Erótica
- Thriller
- Romance
- Fantasía
- Cocina
- No ficción

**Soporte:**
- FAQ
- Contacto
- Tutorial
- Blog

**Legal:**
- Términos
- Privacidad
- Cookies
- Derechos de autor

**Síguenos:**
- Facebook
- Instagram
- Twitter
- LinkedIn
- YouTube
- TikTok

---

## 🚀 Próximos Pasos

1. Generar sitemap XML automáticamente
2. Configurar redirects en servidor
3. Implementar routing en Next.js
4. SEO optimization por página
5. Configurar Google Search Console
6. Implementar breadcrumbs
7. Schema.org markup
8. Open Graph tags
9. Twitter Card tags
10. Canonical URLs
