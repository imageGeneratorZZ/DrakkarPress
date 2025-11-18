# ⚔️ DrakkarPress - Plataforma Editorial Digital Completa

**Versión:** 3.0 - Sistema Completo: Ebooks + POD + Pagos + Membresías  
**Última actualización:** 18 de Noviembre, 2025

**© 2025 DrakkarPress. Todos los derechos reservados.**

> **Plataforma completa para publicación digital y física: venta de ebooks con Stripe, integración Shopify para físicos, Lulu.com POD, membresías premium, y sistema de autenticación robusto. Red colaborativa de escritores con generadores IA.**

---

## 🌟 ¿Qué es DrakkarPress?

**DrakkarPress** es una **plataforma editorial digital completa** que permite a escritores publicar y vender tanto ebooks como libros físicos, con sistema de pagos integrado, generadores de contenido con IA, y red colaborativa.

### ✨ Características Implementadas:

- 📚 **Ebooks Digitales** - Compra/venta con Stripe, descarga segura, biblioteca personal
- 🖨️ **Libros Físicos POD** - Integración Shopify + Lulu.com para print-on-demand
- 💳 **Pagos Premium** - Membresías Basic/Pro/Enterprise con Stripe subscriptions
- 🔐 **Auth Completo** - JWT, login/register, navbar dinámico, logout
- 📧 **Email Automático** - Envío de ebooks tras compra, confirmaciones
- 🎨 **Shop Público** - Catálogo de libros con búsqueda, ordenamiento, checkout
- 📖 **Biblioteca Personal** - Descarga ebooks, regenerar links expirados
- 🛠️ **Admin Panel** - Sync productos Shopify, crear print jobs Lulu, gestión
- 🌐 **Frontend Moderno** - HTML5, JavaScript vanilla, responsive design
- ⚡ **Backend Robusto** - Spring Boot 3.5, PostgreSQL, REST APIs, webhooks

---

## 🎯 Sistema de Perfiles

### 4 Tipos de Perfiles:

#### 1. 👤 **Cliente** (Base)
Todos empiezan aquí. Acceso a compras, comunidad, generadores IA según plan.

#### 2. ✍️ **Autor/Editorial**
- **Persona:** Autor individual autopublicando
- **Empresa:** Editorial publicando para terceros
- Vende obras, busca imprentas, networking

#### 3. 🏭 **Imprenta**
- Ofrece servicios de impresión
- Requiere certificación y documentación
- Portfolio de trabajos

#### 4. 🤝 **Revendedor**
- Distribuye libros de otros
- Comisiones automáticas
- Red de ventas geográfica

---

## � Membresías

### 🆓 GRATUITO
```
Generación completa:  ❌ BLOQUEADA
Portadas con IA:      ✅ 3/mes
Asistente escritura:  ✅ 10/mes
Corrección texto:     ✅ 5 capítulos/mes
```

### ⭐ PREMIUM (Acceso Ilimitado a IA)

#### Fase 1: Fundadores (1-1,000)
```
💵 $5/mes  |  $50/año
🏆 Badge "Fundador" + Runa Othala (ᛟ)
⭐ Precio bloqueado DE POR VIDA
```

#### Fase 2: Early Adopters (1,001-10,000)
```
💵 $10/mes  |  $100/año
⭐ Badge "Early Adopter" + Runa Sowilo (ᛊ)
⭐ Precio bloqueado DE POR VIDA
```

#### Fase 3: Regular (10,001+)
```
💵 $19.99/mes  |  $199/año
✨ Badge "Premium"
📈 Precio estándar
```

---

## 🔮 Sistema de Runas (Elder Futhark)

Cada usuario **Premium** elige una runa nórdica que representa su identidad como creador.

### Runas Destacadas:

| Runa | Nombre | Significado | Categoría |
|------|--------|-------------|-----------|
| **ᚲ** | Kenaz | Creatividad, inspiración | 🎨 Creatividad |
| **ᛊ** | Sowilo | Éxito, victoria | 💪 Éxito |
| **ᛟ** | Othala | Legado, herencia | 🏠 Legado |
| **ᚨ** | Ansuz | Sabiduría, palabra | 🎨 Conocimiento |
| **ᛞ** | Dagaz | Despertar, transformación | 🌱 Transformación |

*+ 19 runas más disponibles*

### Reglas:
- ✅ Solo usuarios Premium
- ✅ Cambio permitido: **1 vez al mes**
- ✅ Aparece en perfil, posts, comentarios

---

## 📁 Estructura del Proyecto

```
DrakkarPress.com/
├── README.md
├── backend/                              # Spring Boot 3.5 + Java 21
│   ├── pom.xml                          # Maven dependencies
│   ├── src/main/java/com/drakkarpress/
│   │   ├── model/                       # ✅ Entities (User, Book, BookPurchase, etc.)
│   │   ├── repository/                  # ✅ JPA repositories
│   │   ├── backend/                     # ✅ Services (Payment, Email, Shopify, Lulu)
│   │   ├── controller/                  # ✅ REST APIs (Auth, Books, Payments)
│   │   ├── platform/config/            # ✅ Security, CORS, JWT
│   │   └── dto/                         # ✅ DTOs (BookPublicResponse, etc.)
│   └── src/main/resources/
│       └── application.properties       # ✅ Database, Stripe, Shopify, Lulu config
├── frontend/                             # HTML + Vanilla JS
│   ├── index.html                       # ✅ Landing page
│   ├── shop.html                        # ✅ Ebook catalog
│   ├── my-books.html                    # ✅ User library
│   ├── admin.html                       # ✅ Shopify/Lulu management
│   ├── login.html / register.html       # ✅ Auth pages
│   └── assets/
│       ├── js/
│       │   ├── config.js               # ✅ API base URL
│       │   ├── auth-v2.js              # ✅ Session manager
│       │   └── error-handler.js        # ✅ Toast notifications
│       └── css/style.css                # ✅ Responsive design
├── database/
│   ├── schema.sql                       # ✅ PostgreSQL schema
│   └── init-db.sql                      # ✅ Seed data
├── docs/
│   ├── DEPLOYMENT_GUIDE.md             # ✅ Railway + Netlify + webhooks
│   ├── ARQUITECTURA_ECOSISTEMA_COMPLETO.md
│   ├── ROADMAP_COMPLETO.md
│   └── QUICK_START_GUIDE.md
└── scripts/                              # PowerShell automation
    ├── deploy-maestro.ps1
    └── setup-completo.ps1
```

## 🚀 Quick Start

### Para Desarrolladores:

```powershell
# 1. Clonar repositorio
git clone https://github.com/imageGeneratorZZ/DrakkarPress.git
cd DrakkarPress.com

# 2. Ver documentación completa
start DEPLOYMENT_GUIDE.md            # ✅ NUEVO: Railway + Netlify deployment
start docs/QUICK_START_GUIDE.md      # Guía de inicio rápido
start docs/INDICE_DOCUMENTACION.md   # Índice completo

# 3. Backend local (opcional - desarrollo)
cd backend
docker-compose up -d postgres        # PostgreSQL local
.\mvnw.cmd spring-boot:run           # Spring Boot en :8080

# 4. Frontend local
# Abrir index.html en navegador o usar Live Server de VS Code

# 5. Deploy producción (recomendado)
# Ver DEPLOYMENT_GUIDE.md para Railway (backend) + Netlify (frontend)
```

---

## 📚 Documentación

### 📑 [INDICE_DOCUMENTACION.md](./docs/INDICE_DOCUMENTACION.md)
**→ Índice completo de toda la documentación** 👈 **Empieza aquí**

### Guías Principales:

| Documento | Descripción | Para quién |
|-----------|-------------|------------|
| **[DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)** ✨ | ✅ **NUEVO**: Railway + Netlify + webhooks | 🚀 DevOps / Deploy |
| **[QUICK_START_GUIDE.md](./docs/QUICK_START_GUIDE.md)** | Guía de inicio rápido | 👨‍💻 Desarrolladores |
| **[ARQUITECTURA_ECOSISTEMA_COMPLETO.md](./docs/ARQUITECTURA_ECOSISTEMA_COMPLETO.md)** | Arquitectura completa | 🏗️ Arquitectos |
| **[ROADMAP_COMPLETO.md](./docs/ROADMAP_COMPLETO.md)** | Plan de trabajo detallado | 📋 PMs / Devs |
| **[RESUMEN_EJECUTIVO_COMPLETO.md](./docs/RESUMEN_EJECUTIVO_COMPLETO.md)** | Presentación ejecutiva | 💼 Stakeholders |

---

## 🏗️ Stack Tecnológico

### Backend (Spring Boot 3.5)
```
☕ Java 21
🍃 Spring Boot 3.5.x
🗄️ PostgreSQL 14+ (Railway auto-provisioned)
🔐 JWT Auth (io.jsonwebtoken 0.12.3)
💳 Stripe (stripe-java 24.7.0)
🛒 Shopify API (com.shopify.shopify-sdk)
🖨️ Lulu.com API (RestTemplate + Basic Auth)
📧 Email (JavaMailSender + Gmail SMTP)
🧩 JPA/Hibernate + Lombok
📦 Maven 3.9+
🚀 Deploy: Railway
```

### Frontend (Static HTML + Vanilla JS)
```
🌐 HTML5 + CSS3 + JavaScript ES6+
📱 Responsive Design (mobile-first)
🔐 Session Manager (auth-v2.js)
🎨 Toast Notifications (error-handler.js)
⚙️ Config Management (config.js)
🚀 Deploy: Netlify (auto-deploy from GitHub)
```

### Integraciones
```
💳 Stripe Checkout + Webhooks
🛒 Shopify API (products, orders, tracking)
🖨️ Lulu.com POD (print jobs, tracking)
📧 Gmail SMTP (ebook delivery)
🔑 JWT Authentication (stateless)
```

---

## 📊 Estado del Proyecto v3.0

```
Core Features:     ████████████████████ 100%
  - Auth System:   ████████████████████ 100% ✅ JWT + login/register
  - Ebooks:        ████████████████████ 100% ✅ Shop + library + downloads
  - Payments:      ████████████████████ 100% ✅ Stripe + webhooks
  - Admin Panel:   ████████████████████ 100% ✅ Shopify/Lulu management
  - Email:         ████████████████████ 100% ✅ Ebook delivery
  - Error UX:      ████████████████████ 100% ✅ Toast notifications

Integrations:      ████████████████░░░░  85%
  - Stripe:        ████████████████████ 100% ✅ Checkout + webhooks
  - Shopify:       ████████████████░░░░  85% ✅ API + webhooks (pendiente: config prod)
  - Lulu POD:      ████████████████░░░░  85% ✅ Service completo (pendiente: credentials)
  - Email SMTP:    ████████████████░░░░  85% ✅ Code ready (pendiente: Gmail app password)

Deployment:        ███████████████░░░░░  75%
  - Backend:       ███████████████░░░░░  75% ✅ Railway ready (pendiente: env vars)
  - Frontend:      ████████████████████ 100% ✅ Netlify auto-deploy
  - Database:      ████████████████████ 100% ✅ PostgreSQL (Railway)
  - Docs:          ████████████████████ 100% ✅ DEPLOYMENT_GUIDE.md

Documentation:     ████████████████████ 100%
  - Arquitectura:  ████████████████████ 100% ✅ Docs completos
  - Deployment:    ████████████████████ 100% ✅ DEPLOYMENT_GUIDE.md
  - API Docs:      ████████████░░░░░░░░  65% 🚧 Swagger/OpenAPI (futuro)

Testing:           ████░░░░░░░░░░░░░░░░  20%
  - Unit Tests:    ░░░░░░░░░░░░░░░░░░░░   0% 🚧 Pendiente
  - Integration:   ░░░░░░░░░░░░░░░░░░░░   0% 🚧 Pendiente
  - E2E Manual:    ████████████████████ 100% ✅ Flujo completo validado

──────────────────────────────────────────────
TOTAL:             ████████████████░░░░  82%
```

### ✅ Funcionalidad Implementada (v3.0):

#### Frontend:
- ✅ `shop.html` - Catálogo de ebooks con Stripe checkout
- ✅ `my-books.html` - Biblioteca personal con downloads
- ✅ `admin.html` - Panel Shopify/Lulu management
- ✅ `login.html` / `register.html` - Auth completo
- ✅ `auth-v2.js` - Session manager global
- ✅ `error-handler.js` - Toast notifications
- ✅ `config.js` - API base URL auto-detection

#### Backend:
- ✅ `AuthController` - JWT login/register
- ✅ `BookController` - CRUD ebooks
- ✅ `PublicBooksController` - Catálogo público
- ✅ `PaymentController` - Stripe webhooks
- ✅ `ShopifyController` - Sync + webhooks
- ✅ `LuluController` / `LuluService` - POD integration
- ✅ `BookPurchaseService` - Ebook purchases
- ✅ `EmailService` - Ebook delivery

#### Integraciones:
- ✅ Stripe: Checkout + webhooks completos
- ✅ Shopify: API integration + webhook handlers
- ✅ Lulu.com: Print jobs + tracking sync
- ✅ Gmail SMTP: Email con attachments

### 🚧 Pendiente Deployment:
- ⏳ Railway env vars (JWT_SECRET, Stripe, Shopify, Lulu, Gmail)
- ⏳ Stripe webhook URL → Railway backend
- ⏳ Shopify webhook URL → Railway backend
- ⏳ Lulu API credentials (contactar soporte)
- ⏳ Gmail app password (2FA requerido)
- ⏳ Testing E2E en producción

---

## 🚀 Próximos Pasos

### Deploy Inmediato:
1. **Railway Backend**:
   ```bash
   # Conectar repo GitHub a Railway
   # Configurar env vars en dashboard (ver DEPLOYMENT_GUIDE.md)
   # Trigger deployment automático
   ```

2. **Netlify Frontend**:
   ```bash
   # Ya configurado con GitHub auto-deploy
   # Actualizar FRONTEND_URL en Railway env vars
   ```

3. **Webhooks**:
   - Stripe: `https://tu-backend.railway.app/api/payments/webhook`
   - Shopify: `https://tu-backend.railway.app/api/shopify/webhooks/orders`

4. **Testing E2E**:
   - Comprar ebook → recibir email → descargar
   - Crear producto Shopify desde admin panel
   - Simular orden Shopify → crear print job Lulu

### Roadmap Futuro:
- 🔮 **Runas System**: Elder Futhark personalización (ver docs originales)
- 🤝 **Social Features**: Networking estilo LinkedIn para escritores
- 🤖 **AI Generators**: DrakkarPress (general) + Scriptorium (infantil)
- 🏪 **Marketplace**: Plataforma de compra/venta entre usuarios
- 📊 **Analytics**: Dashboard de ventas, reportes, métricas
- 🌐 **Internationalization**: Soporte multi-idioma (ES/EN/FR/DE)

**Timeline completo:** Ver `docs/ROADMAP_COMPLETO.md`

---

## 📞 Contacto

**Repositorio:** [github.com/imageGeneratorZZ/DrakkarPress](https://github.com/imageGeneratorZZ/DrakkarPress)  
**Branch principal:** `main`

---

## 📝 Licencia

**© 2025 DrakkarPress. Todos los derechos reservados.**

---

**DrakkarPress - Donde los escritores forjan su legado** ⚔️📚

*"Cada palabra escrita es una runa de poder que perdura en el tiempo"*
