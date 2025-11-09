# Arquitectura Técnica - DrakkarPress

## Visión General

DrakkarPress es una **flota editorial digital** que conecta cuatro tipos de usuarios:

### 1. **Escritores / Autores**
- Crean y publican libros
- Utilizan IA de DrakkarPress para asistencia creativa
- Reciben **90%** en ventas directas o **60%** con revendedor

### 2. **Revendedores (Afiliados)**
- Seleccionan libros para su catálogo personalizado
- Generan enlaces con tracking
- Cobran **30%** de comisión por venta

### 3. **Red de Imprentas**
- Reciben pedidos automáticos de impresión bajo demanda
- Imprimen y envían a clientes locales
- Distribuidas en múltiples países

### 4. **Clientes / Lectores**
- Compran digital o físico
- Acceden a biblioteca digital
- Reciben impresión local rápida

### Arquitectura Técnica

DrakkarPress utiliza una arquitectura de microservicios basada en Java (Spring Boot) con frontend React/Next.js, diseñada para escalabilidad, mantenibilidad y fácil integración con servicios externos.

## Stack Tecnológico

### Backend
- **Framework:** Java 17+ con Spring Boot 3.x
- **Arquitectura:** Microservicios
- **API Gateway:** Spring Cloud Gateway
- **Autenticación:** Spring Security + JWT (compartido entre todos los portales)
- **Base de datos:** PostgreSQL 15+
- **Mensajería:** RabbitMQ / Apache Kafka
- **Cache:** Redis
- **Búsqueda:** Elasticsearch

### Frontend (Multi-Sitio)
- **Framework:** React 18 + Next.js 14
- **Arquitectura:** 
  - **Monorepo** con 5 aplicaciones Next.js independientes
  - Componentes compartidos en `/packages/ui`
  - Utilidades compartidas en `/packages/common`
- **UI Library:** Material-UI / Tailwind CSS
- **State Management:** Redux Toolkit / Zustand
- **API Client:** Axios / React Query
- **SSO:** Single Sign-On entre todos los portales

### Aplicaciones Frontend

```
/apps
├── /marketplace       → www.drakkarpress.com
├── /writer-portal     → escritores.drakkarpress.com
├── /reseller-portal   → afiliados.drakkarpress.com
├── /printer-portal    → imprentas.drakkarpress.com
└── /reader-portal     → biblioteca.drakkarpress.com

/packages
├── /ui               → Componentes compartidos
├── /common           → Utilidades, hooks, types
├── /api-client       → Cliente API compartido
└── /auth             → Lógica de autenticación SSO
```

### Infraestructura
- **Contenedores:** Docker + Docker Compose
- **Orquestación:** Kubernetes (producción)
- **CI/CD:** Jenkins / GitHub Actions
- **Monitoreo:** Prometheus + Grafana
- **Logs:** ELK Stack (Elasticsearch, Logstash, Kibana)

### Almacenamiento
- **Archivos:** AWS S3 / Azure Blob Storage
- **CDN:** CloudFlare / AWS CloudFront

## Arquitectura de Microservicios

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Spring Cloud)                │
│                  (Enrutamiento y Autenticación)              │
└───────────────────────┬─────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│   User       │ │   Book       │ │   Order      │
│   Service    │ │   Service    │ │   Service    │
└──────────────┘ └──────────────┘ └──────────────┘
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  PostgreSQL  │ │  PostgreSQL  │ │  PostgreSQL  │
└──────────────┘ └──────────────┘ └──────────────┘

        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  Payment     │ │   AI         │ │  Publishing  │
│  Service     │ │   Service    │ │   Service    │
└──────────────┘ └──────────────┘ └──────────────┘
        │               │               │
        ▼               ▼               ▼
  Shopify API    IA DrakkarPress   Lulu.com API
```

## Microservicios Principales

### 1. User Service
**Puerto:** 8081  
**Responsabilidades:**
- Registro y autenticación de usuarios
- Gestión de perfiles (Escritor, Revendedor, Imprenta, Lector)
- Roles y permisos
- Gestión de sesiones JWT

**Tecnologías clave:**
- Spring Security
- JWT Token
- BCrypt para passwords
- PostgreSQL para almacenamiento

**Endpoints principales:**
```
POST   /api/users/register
POST   /api/users/login
GET    /api/users/profile
PUT    /api/users/profile
GET    /api/users/{id}
POST   /api/users/refresh-token
```

### 2. Book Service
**Puerto:** 8082  
**Responsabilidades:**
- Gestión del catálogo de libros
- Categorías editoriales (Scryptorium, Erótica, Thriller, etc.)
- Metadata de libros (título, autor, sinopsis, ISBN)
- Gestión de archivos (PDF, EPUB, portadas)
- Versionado de libros

**Tecnologías clave:**
- Spring Data JPA
- Elasticsearch para búsquedas
- AWS S3 para almacenamiento de archivos
- PostgreSQL

**Endpoints principales:**
```
POST   /api/books
GET    /api/books
GET    /api/books/{id}
PUT    /api/books/{id}
DELETE /api/books/{id}
GET    /api/books/category/{category}
POST   /api/books/{id}/upload-cover
POST   /api/books/{id}/upload-content
GET    /api/books/search?q={query}
```

### 3. Order Service
**Puerto:** 8083  
**Responsabilidades:**
- Gestión de pedidos (digital y físico)
- Estados de pedidos
- Historial de compras
- Integración con sistema de pagos

**Tecnologías clave:**
- Spring State Machine
- PostgreSQL
- RabbitMQ para eventos

**Endpoints principales:**
```
POST   /api/orders
GET    /api/orders
GET    /api/orders/{id}
PUT    /api/orders/{id}/status
GET    /api/orders/user/{userId}
POST   /api/orders/{id}/cancel
```

### 4. Payment Service
**Puerto:** 8084  
**Responsabilidades:**
- Integración con Shopify para pagos
- Procesamiento de transacciones
- Gestión de comisiones (revendedores)
- Cálculo de regalías (autores)
- Reportes financieros

**Integraciones:**
- Shopify API
- Stripe (opcional adicional)
- PayPal (opcional adicional)

**Endpoints principales:**
```
POST   /api/payments/create-checkout
POST   /api/payments/process
GET    /api/payments/{orderId}
POST   /api/payments/webhooks/shopify
GET    /api/payments/commissions/reseller/{id}
GET    /api/payments/royalties/author/{id}
```

### 5. AI Service
**Puerto:** 8085  
**Responsabilidades:**
- Integración con IA de DrakkarPress
- Generación de contenido (ideas, títulos, sinopsis)
- Extensión de textos
- Sugerencias de marketing
- Análisis de contenido

**Integraciones:**
- API de IA de DrakkarPress (propia)
- OpenAI (como respaldo o complemento)

**Endpoints principales:**
```
POST   /api/ai/generate-ideas
POST   /api/ai/extend-text
POST   /api/ai/generate-synopsis
POST   /api/ai/generate-title
POST   /api/ai/improve-text
POST   /api/ai/marketing-copy
POST   /api/ai/book-structure
```

### 6. Publishing Service
**Puerto:** 8086  
**Responsabilidades:**
- Integración con Lulu.com
- Gestión de impresión bajo demanda
- Envío de archivos a imprentas
- Tracking de pedidos de impresión
- Gestión de red de imprentas

**Integraciones:**
- Lulu.com API
- APIs de imprentas asociadas

**Endpoints principales:**
```
POST   /api/publishing/print-order
GET    /api/publishing/orders/{id}
PUT    /api/publishing/orders/{id}/status
GET    /api/publishing/printers
POST   /api/publishing/printers/assign
GET    /api/publishing/tracking/{orderId}
```

### 7. Affiliate Service (Revendedores)
**Puerto:** 8087  
**Responsabilidades:**
- Gestión de afiliados/revendedores
- Generación de enlaces de tracking
- Generación de códigos QR
- Estadísticas de ventas por afiliado
- Catálogos personalizados

**Endpoints principales:**
```
POST   /api/affiliates/register
GET    /api/affiliates/{id}/catalog
POST   /api/affiliates/{id}/add-book
GET    /api/affiliates/{id}/generate-link
GET    /api/affiliates/{id}/generate-qr
GET    /api/affiliates/{id}/stats
GET    /api/affiliates/{id}/sales
```

### 8. Notification Service
**Puerto:** 8088  
**Responsabilidades:**
- Envío de emails
- Notificaciones push
- Alertas de sistema
- Comunicaciones automáticas

**Tecnologías:**
- Spring Mail
- SendGrid / AWS SES
- Firebase Cloud Messaging

**Endpoints principales:**
```
POST   /api/notifications/email
POST   /api/notifications/push
GET    /api/notifications/user/{userId}
PUT    /api/notifications/{id}/read
```

## Base de Datos

### Esquema por Microservicio

Cada microservicio tiene su propia base de datos PostgreSQL independiente siguiendo el patrón Database-per-Service.

#### User Service DB
```sql
- users
- roles
- permissions
- user_roles
- sessions
- refresh_tokens
```

#### Book Service DB
```sql
- books
- categories
- book_files
- book_versions
- reviews
- ratings
```

#### Order Service DB
```sql
- orders
- order_items
- order_status_history
- shipping_addresses
```

#### Payment Service DB
```sql
- transactions
- commissions
- royalties
- payment_methods
- invoices
```

#### Publishing Service DB
```sql
- print_orders
- printers
- printer_assignments
- tracking_info
```

#### Affiliate Service DB
```sql
- affiliates
- affiliate_catalogs
- tracking_links
- affiliate_sales
```

## Seguridad

### Autenticación y Autorización
- **JWT Token:** Access token (15 min) + Refresh token (7 días)
- **Spring Security:** Filtros de seguridad
- **HTTPS:** Obligatorio en producción
- **CORS:** Configuración restrictiva

### Roles del Sistema
```java
public enum UserRole {
    ADMIN,
    WRITER,
    RESELLER,
    PRINTER,
    READER
}
```

### Permisos por Rol

| Acción | Admin | Writer | Reseller | Printer | Reader |
|--------|-------|--------|----------|---------|--------|
| Crear libro | ✓ | ✓ | ✗ | ✗ | ✗ |
| Editar libro propio | ✓ | ✓ | ✗ | ✗ | ✗ |
| Ver catálogo | ✓ | ✓ | ✓ | ✓ | ✓ |
| Comprar libro | ✓ | ✓ | ✓ | ✗ | ✓ |
| Generar link afiliado | ✓ | ✗ | ✓ | ✗ | ✗ |
| Ver pedidos impresión | ✓ | ✗ | ✗ | ✓ | ✗ |
| Usar IA DrakkarPress | ✓ | ✓ | ✓ | ✗ | ✗ |

## Comunicación entre Microservicios

### Síncrona
- **REST API:** Para operaciones CRUD
- **OpenFeign:** Cliente HTTP declarativo

### Asíncrona
- **RabbitMQ:** Para eventos del sistema
- **Eventos principales:**
  - `UserRegistered`
  - `BookPublished`
  - `OrderCreated`
  - `PaymentProcessed`
  - `PrintOrderAssigned`
  - `BookPurchased`

## Escalabilidad

### Estrategias
1. **Horizontal Scaling:** Múltiples instancias por microservicio
2. **Load Balancing:** NGINX / AWS ALB
3. **Caching:** Redis para sesiones, catálogo frecuente
4. **CDN:** Para archivos estáticos y portadas
5. **Database Replication:** Read replicas para consultas

### Capacidad Estimada
- **Usuarios concurrentes:** 10,000+
- **Libros en catálogo:** 100,000+
- **Pedidos/día:** 5,000+
- **Requests/segundo:** 1,000+

## Monitoreo y Observabilidad

### Métricas
- **Prometheus:** Recolección de métricas
- **Grafana:** Visualización de dashboards
- **Métricas clave:**
  - Latencia de APIs
  - Tasa de errores
  - Throughput
  - Uso de recursos (CPU, memoria)

### Logs
- **Formato:** JSON structured logs
- **Centralización:** ELK Stack
- **Niveles:** ERROR, WARN, INFO, DEBUG

### Alertas
- Caída de servicios
- Latencia > 2 segundos
- Tasa de error > 5%
- Disco > 80% capacidad

## Deployment

### Entornos
1. **Development:** Local Docker Compose
2. **Staging:** Kubernetes cluster
3. **Production:** Kubernetes cluster (HA)

### Pipeline CI/CD
```
Code Push → Build (Maven) → Unit Tests → 
Docker Image → Push Registry → Deploy to K8s → 
Integration Tests → Health Check
```

### Estrategia de Deploy
- **Blue-Green Deployment:** Para cero downtime
- **Rolling Updates:** Actualización gradual
- **Rollback automático:** Si health checks fallan

## Backup y Recuperación

### Base de datos
- **Backup diario:** Full backup a las 2 AM
- **Backup incremental:** Cada 6 horas
- **Retención:** 30 días
- **Almacenamiento:** AWS S3 / Azure Blob

### Archivos
- **Replicación:** Multi-región
- **Versionado:** Habilitado en S3
- **Lifecycle:** Archivos antiguos a Glacier después de 1 año

## Costos Estimados (Mensual)

### Infraestructura AWS (ejemplo)
- **EC2 Instances:** $500-800 (6-8 instancias)
- **RDS PostgreSQL:** $300-500 (Multi-AZ)
- **S3 Storage:** $100-200 (1TB libros)
- **CloudFront CDN:** $50-100
- **Load Balancers:** $50-80
- **ElastiCache Redis:** $100-150
- **Total estimado:** $1,100 - $1,830/mes

(Costos pueden variar según tráfico y volumen de datos)

## Próximos Pasos

1. Configurar repositorios Git
2. Setup entorno de desarrollo Docker
3. Implementar API Gateway
4. Desarrollar User Service (MVP)
5. Implementar autenticación JWT
6. Desarrollar Book Service
7. Integrar frontend React
8. Conectar con Shopify
9. Integrar IA de DrakkarPress
10. Conectar con Lulu.com
