# ⚔️ DrakkarPress - La Flota Editorial Digital

![DrakkarPress Logo](https://img.shields.io/badge/DrakkarPress-Publishing%20Platform-1A4D7A?style=for-the-badge)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Version](https://img.shields.io/badge/version-1.0.0-green.svg)](https://github.com/drakkarpress/platform)
[![GitHub](https://img.shields.io/badge/GitHub-DrakkarPress-181717?logo=github)](https://github.com/yourusername/DrakkarPress)

> **Plataforma editorial completa con multi-sitio, autenticación OAuth 2.0, sistema de comisiones automatizado y herramientas de IA para escritores independientes.**

---

## 🌟 Descripción General

DrakkarPress es una **flota editorial digital** inspirada en los intrépidos navegantes vikingos que conecta cuatro tipos de usuarios en una comunidad global de publicación y distribución de libros:

### 🚢 Los 4 Tipos de Usuario

#### 1. ✍️ **Autores / Escritores**
Crean y publican sus libros en la plataforma. Usan la IA de DrakkarPress para generar ideas, extender textos y crear sinopsis. Reciben **90%** de regalías en ventas directas o **60%** cuando hay revendedor (la plataforma retiene 10%). Sus libros se venden automáticamente en Shopify, MercadoLibre y la tienda DrakkarPress.

#### 2. 💼 **Revendedores (Afiliados)**
Eligen qué libros del catálogo quieren vender. Arman sus propios catálogos personalizados y generan enlaces con tracking para compartir en sus redes sociales. Cobran **30%** de comisión por cada venta (plataforma retiene 10%, autor recibe 60%). Sin inventario, sin inversión inicial. La IA les genera contenido para marketing.

#### 3. 🏭 **Red de Imprentas**
Reciben pedidos automáticos de impresión bajo demanda. Imprimen libros según especificaciones y los envían a clientes locales. Distribuidas en distintos países para envío rápido (3-7 días). Actualizan estados y tracking. Cobran por cada trabajo realizado con pagos automáticos.

#### 4. 📚 **Clientes / Lectores**
Compran libros en formato digital o físico. Si eligen digital: descarga inmediata y acceso a biblioteca personal. Si eligen físico: se imprime localmente y llega rápido. Pueden dejar reseñas y valoraciones.

## Características Principales

### 🔍 Portal Tipo Inmobiliario para Libros
- **Búsqueda avanzada** con filtros múltiples (categoría, precio, idioma, rating, etc.)
- **Vista Grid y Lista** similar a portales inmobiliarios
- **Mapa de imprentas** por ubicación geográfica
- **Comparador** de precios y formatos
- **Alertas** personalizadas para nuevos libros

### 👥 Multi-Rol de Usuarios
- **Escritores:** Publican y gestionan sus libros con ayuda de IA
- **Revendedores/Afiliados:** Catálogo personalizado con enlaces de tracking
- **Imprentas:** Panel para gestionar pedidos de impresión bajo demanda
- **Lectores:** Biblioteca digital y tracking de pedidos físicos

### 🤖 IA de DrakkarPress
- Generación de ideas de libros por categoría
- Extensión de textos y capítulos
- Creación de sinopsis y títulos atractivos
- Sugerencias de marketing para redes sociales
- Estructuras para libros infantiles y colorear

### 🛒 Integraciones E-commerce
- **Shopify:** Tienda oficial con checkout completo
- **MercadoLibre:** Publicación automática en marketplace LATAM
- **Lulu.com:** Impresión bajo demanda global
- **Imprentas locales:** Red propia de imprentas por país

### 📚 Categorías Editoriales
- **Scryptorium:** Libros infantiles y para colorear
- **Erótica:** Ficción erótica para adultos
- **Thriller/Suspenso:** Misterio y tensión
- **Romance:** Historias de amor
- **Fantasía/Sci-Fi:** Mundos imaginarios
- **Cocina:** Recetas y gastronomía
- **No Ficción:** Desarrollo personal y conocimiento

## 📁 Estructura del Proyecto

```
DrakkarPress.com/
├── index.html              # Landing page principal
├── 🔐 Autenticación (3 páginas)
│   ├── login.html
│   ├── register.html
│   └── forgot-password.html
├── 🛒 E-Commerce (4 páginas)
│   ├── catalogo.html
│   ├── libro.html
│   ├── cart.html
│   └── checkout.html
├── 👥 Portales (4 páginas)
│   ├── escritores.html
│   ├── revendedores.html
│   ├── imprentas.html
│   └── biblioteca.html
├── 🌐 Servicios (3 páginas)
│   ├── servicios-marketing.html
│   ├── servicios-web.html
│   └── servicios-research.html
├── ℹ️ Info (3 páginas)
│   ├── about.html
│   ├── faq.html
│   └── contact.html
├── js/i18n.js              # Sistema multiidioma
└── docs/ (16 archivos MD)  # Documentación completa
```

**Total: 18 páginas HTML funcionales** ✅

## 🚀 Instalación y Uso

### Opción 1: Servidor Local Rápido (Python)

```bash
# Clona el repositorio
git clone https://github.com/drakkarpress/platform.git
cd DrakkarPress.com

# Inicia servidor HTTP
python -m http.server 8000

# Abre en navegador: http://localhost:8000
```

### Opción 2: Live Server (VS Code)

```bash
# Instala extensión Live Server en VS Code
# Click derecho en index.html > "Open with Live Server"
```

### Opción 3: Node.js

```bash
npm install -g http-server
http-server -p 8000
# Abre http://localhost:8000
```

## 📄 Páginas Disponibles

| Página | URL | Descripción |
|--------|-----|-------------|
| Landing | `/index.html` | Página principal |
| Catálogo | `/catalogo.html` | 8 libros con filtros |
| Libro | `/libro.html` | 4 formatos disponibles |
| Carrito | `/cart.html` | Gestión + cupones |
| Checkout | `/checkout.html` | 3 métodos de pago |
| Login | `/login.html` | OAuth 2.0 |
| Escritores | `/escritores.html` | Dashboard + IA tools |
| Afiliados | `/revendedores.html` | Links + comisiones |
| Imprentas | `/imprentas.html` | Órdenes + producción |
| Biblioteca | `/biblioteca.html` | E-reader integrado |
| Marketing | `/servicios-marketing.html` | Desde $599 |
| Web Dev | `/servicios-web.html` | Desde $2,999 |
| Research | `/servicios-research.html` | Desde $1,499 |
| About | `/about.html` | Historia y equipo |
| FAQ | `/faq.html` | 20+ preguntas |
| Contacto | `/contact.html` | Formulario |

## 📚 Documentación Completa

Consulta la carpeta `/docs` (16 archivos):

- **`00-resumen-ejecutivo.md`** - Overview del proyecto
- **`01-vision-general.md`** - Visión y alcance
- **`02-arquitectura-multisitio.md`** - 5 portales independientes con SSO
- **`03-modelo-comisiones.md`** - 90%/60% escritor, 30% afiliado, 10% plataforma
- **`04-flujo-compra.md`** - Journey del comprador
- **`05-oauth-autenticacion.md`** - 6 providers OAuth 2.0
- **`06-procesamiento-pagos.md`** - Stripe Connect + PayPal
- **`07-distribucion-productos.md`** - Físico vs Digital
- **`08-sistema-entregas.md`** - Imprentas bajo demanda
- **`09-dashboards-analytics.md`** - Métricas por rol
- **`10-herramientas-ia.md`** - ChatGPT integrado
- **`11-integracion-imprentas.md`** - Red de producción
- **`12-servicio-marketing.md`** - FB/IG/Google Ads
- **`13-servicio-desarrollo-web.md`** - Sitios para autores
- **`14-servicio-investigacion.md`** - Histórica/Científica
- **`15-campanas-publicidad-digital.md`** - Estrategias completas

## 🛠️ Stack Tecnológico

### Frontend (Actual - MVP)
- **HTML5/CSS3/JavaScript**: Vanilla JS
- **Responsive**: Móvil-first
- **i18n**: 6 idiomas (ES, EN, PT, FR, DE, IT)

### Backend (Propuesto)
- **Java 17+**: Spring Boot
- **PostgreSQL**: Base de datos
- **Redis**: Caché
- **OAuth 2.0**: 6 providers

### Pagos
- **Stripe Connect**: Splits automáticos
- **PayPal**: Alternativa
- **OXXO**: México

### Deployment
- **Vercel/Netlify**: Frontend
- **AWS/Azure**: Backend
- **GitHub Pages**: Demo

## 🚀 Deployment

### Vercel (1 minuto)

```bash
npm i -g vercel
vercel --prod
```

### Netlify

```bash
npm install -g netlify-cli
netlify deploy --prod
```

### GitHub Pages

```bash
# Settings > Pages > Source: main branch
# https://username.github.io/drakkarpress
```

## 📊 Métricas

| Métrica | Valor |
|---------|-------|
| Páginas HTML | 18 |
| Líneas de Código | ~15,000 |
| Documentación | 16 archivos |
| Idiomas | 6 |
| OAuth Providers | 6 |
| Estado | ✅ MVP Completo |

## 📞 Soporte

- 📧 Email: contacto@drakkarpress.com
- 📞 Teléfono: +52 55 1234 5678
- 🏢 Oficina: Av. Reforma 123, CDMX

## 📝 Licencia

MIT License - Copyright (c) 2025 DrakkarPress

---

**⚔️ Navega hacia el éxito con DrakkarPress** 🌍📚
