# Resumen Ejecutivo - DrakkarPress

## 🎯 Visión del Proyecto

**DrakkarPress** es una **flota editorial digital** que conecta cuatro tipos de usuarios en un ecosistema global de publicación y distribución de libros:

### Los 4 Pilares de la Comunidad

1. **✍️ Autores / Escritores**
   - Crean y publican sus libros en la plataforma
   - Usan la IA de DrakkarPress para mejorar su contenido
   - Reciben **90%** en ventas directas o **60%** con revendedor
   - Venden en múltiples canales (Shopify, MercadoLibre)

2. **💼 Revendedores (Afiliados)**
   - Eligen qué libros del catálogo quieren vender
   - Arman sus propios catálogos personalizados
   - Generan enlaces con tracking para sus redes sociales
   - Cobran **30%** de comisión por cada venta
   - Reciben herramientas de marketing (IA genera contenido)

3. **🏭 Red de Imprentas**
   - Reciben pedidos automáticos de impresión bajo demanda
   - Imprimen y envían libros a clientes locales
   - Actualizan estados de producción y envío
   - Cobran por cada trabajo de impresión realizado
   - Distribuidas geográficamente en múltiples países

4. **📚 Clientes / Lectores**
   - Compran libros en formato digital o físico
   - Acceden a su biblioteca personal digital
   - Reciben libros físicos impresos localmente
   - Dejan reseñas y valoraciones

**DrakkarPress** es el HUB CENTRAL que orquesta toda esta red, aplicando el modelo de **portales inmobiliarios** (tipo Idealista/Fotocasa) para ofrecer una experiencia de búsqueda y descubrimiento superior.

---

## 🏗️ Arquitectura Técnica

### Backend
- **Lenguaje:** Java 21+
- **Framework:** Spring Boot 3.x
- **Arquitectura:** Microservicios
- **Base de Datos:** PostgreSQL 15+
- **Cache:** Redis
- **Mensajería:** RabbitMQ / Kafka
- **Búsqueda:** Elasticsearch

### Frontend
- **Framework:** React 18 + Next.js 14
- **UI:** Material-UI / Tailwind CSS
- **State:** Redux Toolkit
- **API Client:** React Query

### Microservicios Principales
1. **User Service** (Puerto 8081) - Autenticación y perfiles
2. **Book Service** (Puerto 8082) - Catálogo de libros
3. **Order Service** (Puerto 8083) - Gestión de pedidos
4. **Payment Service** (Puerto 8084) - Integración Shopify/Pagos
5. **AI Service** (Puerto 8085) - IA de DrakkarPress
6. **Publishing Service** (Porto 8086) - Integración Lulu.com
7. **Affiliate Service** (Puerto 8087) - Revendedores/Afiliados
8. **Notification Service** (Puerto 8088) - Emails/Notificaciones
9. **Shopify Integration** (Puerto 8089) - API Shopify
10. **MercadoLibre Integration** (Puerto 8090) - API MercadoLibre
11. **Lulu Integration** (Puerto 8091) - API Lulu.com

---

## 🌐 Diseño Web: Portal Inmobiliario de Libros

### Concepto Clave
Aplicar el modelo exitoso de portales inmobiliarios al mundo editorial:

- ✅ **Búsqueda potente** con filtros avanzados
- ✅ **Vista Grid/Lista** de resultados
- ✅ **Fichas detalladas** por libro (como anuncios de propiedades)
- ✅ **Mapa interactivo** de imprentas por ubicación
- ✅ **Comparador** de precios digital vs impreso
- ✅ **Alertas** para nuevos libros en categorías favoritas
- ✅ **Estadísticas** en tiempo real

### Página Principal

```
┌─────────────────────────────────────────────────┐
│ [Logo] [🔍 BUSCADOR GRANDE]  [Publicar][Login] │
├─────────────────────────────────────────────────┤
│                                                 │
│  🔍 ¿Qué libro buscas?                         │
│  [________________________________] [Buscar]    │
│  [Categoría ▾][Formato ▾][Precio ▾][Más ▾]     │
│                                                 │
│  📊 124,583 libros | 8,421 autores | 45 países │
│                                                 │
├─────────────────────────────────────────────────┤
│  📚 Libros Destacados (Grid tipo inmobiliario) │
│  [Card][Card][Card][Card]                       │
│                                                 │
│  🗺️ Red Global de Imprentas                    │
│  [Mapa interactivo]                             │
│                                                 │
│  🤖 IA de DrakkarPress                          │
│  Crea contenido con inteligencia artificial     │
└─────────────────────────────────────────────────┘
```

---

## 🔌 Integraciones Externas

### 1. Shopify (E-commerce)
- **Función:** Tienda oficial con checkout
- **Flujo:** 
  - Escritor publica libro → Auto-sync a Shopify
  - Cliente compra → Webhook a DrakkarPress
  - Digital: Envío automático link descarga
  - Impreso: Orden a Lulu.com/Imprenta

### 2. MercadoLibre (Marketplace LATAM)
- **Función:** Ampliar alcance en América Latina
- **Flujo:**
  - OAuth 2.0 para autorización
  - Publicación automática de libros
  - Notificaciones de ventas
  - Integración con impresión local

### 3. Lulu.com (Impresión Global)
- **Función:** POD (Print on Demand) internacional
- **Flujo:**
  - Configurar libro con specs de impresión
  - Orden de venta → API Lulu
  - Lulu imprime y envía
  - Tracking automático

### 4. IA de DrakkarPress (Propia)
- **Función:** Asistente de escritura y marketing
- **Capacidades:**
  - Generar ideas de libros
  - Extender textos
  - Crear sinopsis
  - Sugerir títulos
  - Marketing copy para redes

---

## 👥 Roles de Usuario

### 1. ✍️ Escritor/Autor
**Dashboard:**
- 📚 Gestión de libros (borrador → publicado)
- 💰 Ingresos y regalías (70% del precio)
- 📊 Estadísticas de ventas
- 🤖 Herramientas de IA
- 🔗 Integraciones (Shopify, ML, Lulu)

**Flujo Principal:**
1. Crear libro con wizard paso a paso
2. IA ayuda con sinopsis/títulos
3. Subir archivos (PDF/DOCX)
4. Configurar precio digital/impreso
5. Publicar (auto-sync a todas las plataformas)
6. Recibir regalías automáticamente

### 2. 💼 Revendedor/Afiliado
**Dashboard:**
- 📦 Catálogo personalizable
- 🔗 Enlaces con tracking
- 💰 Comisiones (15-20%)
- 📊 Estadísticas de ventas
- 🎯 Herramientas de marketing (IA genera contenido)

**Flujo Principal:**
1. Seleccionar libros del catálogo
2. Generar enlaces únicos por libro
3. IA crea posts para redes sociales
4. Compartir enlaces con código tracking
5. Cobrar comisiones por ventas

### 3. 🏭 Imprenta
**Dashboard:**
- 📦 Lista de pedidos (urgentes, en producción, completados)
- 🗺️ Zona de cobertura
- 💰 Pagos por trabajos realizados
- 📊 Estadísticas de eficiencia

**Flujo Principal:**
1. Recibir pedido automático
2. Descargar archivos de impresión
3. Actualizar estado (producción → impreso → enviado)
4. Ingresar tracking de envío
5. Recibir pago al completar

### 4. 📚 Lector/Cliente
**Dashboard:**
- 📖 Biblioteca digital
- 🛒 Historial de pedidos
- 📦 Tracking de envíos
- ⭐ Reseñas escritas
- ❤️ Favoritos

**Flujo Principal:**
1. Buscar libros con filtros avanzados
2. Ver ficha detallada + preview
3. Agregar al carrito (digital/impreso)
4. Checkout (Shopify/MercadoLibre)
5. Descargar digital o recibir físico

---

## 📊 Modelo de Negocio

### Comisiones DrakkarPress

**Venta Directa (sin revendedor):**
```
Precio venta: $12.99
├─ Autor (90%): $11.69
└─ DrakkarPress (10%): $1.30
```

**Venta con Revendedor:**
```
Precio venta: $12.99
├─ Autor (60%): $7.79
├─ Revendedor (30%): $3.90
└─ DrakkarPress (10%): $1.30
```

**Libros Impresos (ejemplo):**
```
Precio venta: $24.99
├─ Costo impresión: $5.20
├─ Precio base distribución: $19.79
    ├─ Autor (90% directo): $17.81 o (60% con revendedor): $11.87
    ├─ Revendedor (30% si aplica): $5.94
    └─ DrakkarPress (10%): $1.98
```

**Plataformas Externas:**
- **Shopify:** Comisión propia 2.9% + $0.30
- **MercadoLibre:** Comisión 13% (se resta de regalías)

### Planes de Suscripción

| Plan | Precio/Mes | Beneficios |
|------|------------|------------|
| **Free** | $0 | 1 libro, IA básica (10/día), 90/60% regalías |
| **Writer Pro** | $19 | Ilimitado, IA Pro (100/día), 90/60% regalías, Analytics |
| **Reseller Pro** | $29 | Catálogo amplio, IA marketing, 30% comisión |
| **Printer Pro** | $49 | Pedidos ilimitados, Panel avanzado, API |

---

## 🗺️ Estructura del Sitio - Multi-Sitio

### Arquitectura de Dominios Independientes

DrakkarPress opera como un **ecosistema de sitios especializados**, cada uno con su propia interfaz optimizada para el tipo de usuario:

```
SITIO PRINCIPAL (Marketplace)
www.drakkarpress.com
├── / (Home con buscador)
├── /catalogo
├── /libro/{isbn}
├── /categoria/{slug}
├── /red-imprentas (mapa: "Red de Imprentas" / "¿Quieres Imprimir?")
├── /ia-drakkarpress
└── /login (redirige según rol)

PORTAL ESCRITORES
escritores.drakkarpress.com (o writer.drakkarpress.com)
├── / (Dashboard principal)
├── /libros
├── /publicar
├── /ingresos
├── /estadisticas
├── /ia-herramientas
└── /configuracion

PORTAL REVENDEDORES/AFILIADOS
afiliados.drakkarpress.com (o reseller.drakkarpress.com)
├── / (Dashboard principal)
├── /catalogo
├── /mis-libros
├── /generar-enlaces
├── /marketing-ia
├── /comisiones
└── /estadisticas

PORTAL IMPRENTAS
imprentas.drakkarpress.com (o printer.drakkarpress.com)
├── / (Dashboard principal)
├── /pedidos
├── /en-produccion
├── /completados
├── /pagos
└── /configuracion

PORTAL LECTORES (Opcional - puede estar en el principal)
biblioteca.drakkarpress.com (o www.drakkarpress.com/biblioteca)
├── / (Mi biblioteca)
├── /mis-libros
├── /pedidos
├── /favoritos
└── /perfil
```

---

## 🗄️ Base de Datos (20 Tablas Principales)

### Core
1. **users** - Usuarios base
2. **writer_profiles** - Perfiles de escritores
3. **affiliate_profiles** - Perfiles de afiliados
4. **printer_profiles** - Perfiles de imprentas

### Contenido
5. **books** - Catálogo de libros
6. **categories** - Categorías editoriales
7. **book_files** - Archivos (PDF, EPUB, portadas)
8. **reviews** - Reseñas de lectores

### Comercio
9. **orders** - Órdenes/Pedidos
10. **order_items** - Items de órdenes
11. **payments** - Pagos
12. **print_orders** - Órdenes de impresión

### Afiliados
13. **affiliate_links** - Enlaces de tracking
14. **affiliate_clicks** - Clics registrados
15. **commissions** - Comisiones de afiliados

### Financiero
16. **royalties** - Regalías de autores

### IA y Misc
17. **ai_usage** - Uso de IA
18. **notifications** - Notificaciones
19. **favorites** - Favoritos de lectores
20. **digital_library** - Biblioteca digital

---

## 🚀 Flujo Completo de Venta

### Ejemplo: Venta de Libro Físico en MercadoLibre

```
1. CLIENTE compra libro impreso en MercadoLibre
   ↓
2. ML envía webhook a DrakkarPress
   ↓
3. DrakkarPress valida orden y crea registro
   ↓
4. 🎁 INMEDIATO: Envía PDF gratis por email al cliente
   ├─ Cliente puede empezar a leer al instante
   ├─ PDF agregado a biblioteca digital
   └─ Link de descarga válido 7 días
   ↓
5. Sistema decide: ¿Lulu.com o imprenta local?
   ├─ Internacional → Lulu.com
   └─ Local México → Imprenta CDMX
   ↓
6. Envía orden de impresión con archivos
   ↓
7. Imprenta/Lulu imprime libro
   ↓
8. Genera tracking de envío
   ↓
9. DrakkarPress actualiza ML con tracking
   ↓
10. Cliente recibe libro físico (3-7 días)
   ↓
10. Sistema calcula y reparte (precio $24.99):
    • Costo impresión: $5.20
    • Comisión ML (13%): $3.25
    • Precio base: $16.54
      - Plataforma DrakkarPress (10%): $1.65
      - Autor (90% venta directa): $14.89
      O si hay revendedor:
      - Plataforma DrakkarPress (10%): $1.65
      - Revendedor (30%): $4.96
      - Autor (60%): $9.93
```

---

## 🎨 Paleta de Colores

```css
/* Primario (Azul Vikingo) */
--primary: #1A4D7A

/* Secundario (Oro Nórdico) */
--secondary: #D4AF37

/* Por Rol */
--writer: #3498DB (Azul)
--reseller: #27AE60 (Verde)
--printer: #E67E22 (Naranja)
--reader: #9B59B6 (Morado)
```

---

## 📈 Métricas Clave (KPIs)

### Plataforma
- Número total de libros publicados
- Usuarios activos por rol
- Ventas totales (digital + impreso)
- Tiempo promedio de publicación a venta

### Por Escritor
- Libros publicados
- Total de ventas
- Ingresos acumulados
- Rating promedio

### Por Revendedor
- Clics en enlaces
- Tasa de conversión
- Comisiones ganadas
- ROI de marketing

### Por Imprenta
- Órdenes completadas
- Tiempo promedio de producción
- Rating de calidad
- Ingresos mensuales

---

## 🔐 Seguridad

- **Autenticación:** JWT (Access + Refresh tokens)
- **Roles:** RBAC (Role-Based Access Control)
- **HTTPS:** Obligatorio en producción
- **CORS:** Configuración restrictiva
- **Rate Limiting:** 100-500 req/min según plan
- **Webhooks:** Verificación por firma HMAC

---

## 💳 Sistema de Pagos y Tributación

### Distribución Automática de Fondos

**Proceso:**
1. Cliente compra en Shopify/MercadoLibre/Directo
2. Payment Service recibe webhook
3. Sistema calcula splits según modelo:
   - Venta directa: 90% autor, 10% plataforma
   - Con revendedor: 60% autor, 30% revendedor, 10% plataforma
   - Impreso: Se resta costo impresión primero
4. Calcula retenciones fiscales según país
5. Ejecuta transferencias automáticas (Stripe/Banco/PayPal)
6. Genera facturas y comprobantes fiscales

### Cumplimiento Tributario

**Por País:**
- 🇺🇸 **Estados Unidos:** W-9/W-8BEN, 1099-MISC, retención 30%
- 🇲🇽 **México:** RFC, CFDI, retención ISR 10%
- 🇦🇷 **Argentina:** CUIT, Factura E, retención 21%
- 🇪🇸 **España:** NIF, Factura IVA, retención IRPF 15%
- 🇧🇷 **Brasil:** CPF/CNPJ, NFe, retención IRRF 15%

**Documentación Completa:** Ver `docs/07-sistema-pagos-tributario.md`

---

## 🎁 Entrega Digital Automática

### Ventaja Competitiva: "Compra Físico, Recibe Digital GRATIS"

Cuando un cliente compra un libro físico, **automáticamente recibe**:

1. **📧 Email inmediato** con link de descarga del PDF
2. **📚 Acceso permanente** en su biblioteca digital
3. **⚡ Empieza a leer** mientras espera el libro físico
4. **🎯 Sin costo extra** - el PDF viene incluido

### Proceso Automático

```
COMPRA FÍSICA → PDF por email en segundos → Libro físico en 3-7 días
              ↓
         Biblioteca Digital (acceso permanente)
```

**Beneficios:**
- ✅ Cliente satisfecho al instante (gratificación inmediata)
- ✅ Diferenciación vs Amazon/competencia
- ✅ Mayor conversión de ventas
- ✅ Sin costo adicional (PDF ya existe)

**Documentación Completa:** Ver `docs/08-entrega-digital-automatica.md`

---

## 📱 Progressive Web App (PWA)

- Instalable en dispositivos móviles
- Offline-first para biblioteca digital
- Push notifications para nuevas ventas
- Service Worker para caché inteligente

---

## 🌍 Internacionalización

**Idiomas soportados:**
- 🇪🇸 Español (default)
- 🇬🇧 English
- 🇵🇹 Português
- 🇫🇷 Français

**Monedas:**
- USD, MXN, ARS, BRL, EUR

---

## 📊 Estimación de Costos (AWS, Mensual)

| Servicio | Costo Estimado |
|----------|----------------|
| EC2 Instances (6-8) | $500-800 |
| RDS PostgreSQL Multi-AZ | $300-500 |
| S3 Storage (1TB) | $100-200 |
| CloudFront CDN | $50-100 |
| Load Balancers | $50-80 |
| ElastiCache Redis | $100-150 |
| **TOTAL** | **$1,100-1,830/mes** |

*Costos aumentan con tráfico y volumen de datos*

---

## 🎯 Próximos Pasos de Implementación

### Fase 1: MVP (Mes 1-2)
- [ ] Setup repositorios Git
- [ ] Entorno Docker local
- [ ] User Service + Auth
- [ ] Book Service básico
- [ ] Frontend Home + Catálogo
- [ ] Base de datos PostgreSQL

### Fase 2: Core Features (Mes 3-4)
- [ ] Order Service
- [ ] Payment Service
- [ ] Integración Shopify
- [ ] Dashboards por rol
- [ ] Sistema de archivos S3

### Fase 3: Integraciones (Mes 5-6)
- [ ] Integración MercadoLibre
- [ ] Integración Lulu.com
- [ ] AI Service (IA de DrakkarPress)
- [ ] Affiliate Service

### Fase 4: Optimización (Mes 7-8)
- [ ] Performance optimization
- [ ] SEO completo
- [ ] Testing exhaustivo
- [ ] Documentación API
- [ ] Monitoreo y alertas

### Fase 5: Lanzamiento (Mes 9)
- [ ] Beta testing con usuarios reales
- [ ] Marketing y promoción
- [ ] Launch oficial
- [ ] Soporte 24/7

---

## 📞 Contacto y Recursos

**Documentación Técnica:**
- `docs/01-arquitectura-tecnica.md`
- `docs/02-diseno-web.md`
- `docs/03-modelo-datos.md`
- `docs/04-integraciones.md`
- `docs/05-sitemap.md`
- `docs/06-roles-usuarios.md`
- `docs/07-sistema-pagos-tributario.md`
- `docs/08-entrega-digital-automatica.md`
- `docs/09-guia-marca-identidad-visual.md`
- `docs/10-arquitectura-multi-sitio.md`
- `docs/11-sistema-autenticacion-sso.md` ⭐ **NUEVO**

**APIs Documentación:**
- Shopify: https://shopify.dev/api
- MercadoLibre: https://developers.mercadolibre.com
- Lulu.com: https://developers.lulu.com

---

## ✅ Conclusión

DrakkarPress es una plataforma editorial completa que innova aplicando el modelo de **portales inmobiliarios** al mundo de los libros, con:

✅ Búsqueda potente y experiencia similar a buscar propiedades  
✅ Arquitectura escalable de microservicios en Java/Spring Boot  
✅ Integraciones robustas con Shopify, MercadoLibre y Lulu.com  
✅ IA propia para asistir a escritores  
✅ Modelo de negocio justo con regalías del 70%  
✅ Red global de imprentas con mapa interactivo  

La plataforma está diseñada para escalar a miles de usuarios y millones de transacciones, con una arquitectura moderna y bien documentada lista para implementación.

---

**Versión:** 1.0  
**Fecha:** Noviembre 2025  
**Última actualización:** 9 nov 2025
