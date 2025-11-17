# ⚔️ DrakkarPress Backend - Spring Boot Platform

**Plataforma Editorial Completa con Sistema Multi-Usuario e Integración con IA**

---

## 📋 Resumen

Backend robusto de **DrakkarPress** construido con **Spring Boot 3.2.0** y **Java 21 (LTS)**. Proporciona APIs REST para gestionar autores, libros, ventas, comisiones, integración con IA (Investigatron) y servicios de marketing (OdrBrand).

---

## 🏗️ Arquitectura

```
┌──────────────────────────────────────────────────────────────┐
│                    DRAKKARPRESS BACKEND                      │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌────────────┐   ┌────────────┐   ┌────────────┐          │
│  │   Auth     │   │   Books    │   │   Sales    │          │
│  │  Service   │   │  Service   │   │  Service   │          │
│  └──────┬─────┘   └──────┬─────┘   └──────┬─────┘          │
│         │                │                │                  │
│         └────────────────┼────────────────┘                  │
│                          │                                   │
│                  ┌───────▼────────┐                         │
│                  │  JPA Repository │                         │
│                  │     Layer       │                         │
│                  └───────┬────────┘                         │
│                          │                                   │
│                  ┌───────▼────────┐                         │
│                  │   PostgreSQL    │                         │
│                  │    Database     │                         │
│                  └────────────────┘                         │
│                                                              │
│  External Integrations:                                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Investigatron│  │   OdrBrand   │  │    Stripe    │     │
│  │  (IA API)    │  │ (Marketing)  │  │   (Pagos)    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└──────────────────────────────────────────────────────────────┘
```

---

## 🗄️ Modelos de Datos

### User (Usuario)
```java
- id: UUID
- email: String
- passwordHash: String
- firstName, lastName: String
- role: UserRole (AUTHOR, RESELLER, READER, ADMIN, PRINTER)
- subscription: SubscriptionType (FREE, PREMIUM, PRO)
- oauthProvider: String (GOOGLE, FACEBOOK, GITHUB, etc.)
```

### Book (Libro)
```java
- id: UUID
- author: User
- title: String
- synopsis, description: Text
- genre: Genre (ROMANCE, THRILLER, FANTASY, etc.)
- priceDigital, pricePhysical: BigDecimal
- coverImageUrl, digitalFileUrl: String
- status: BookStatus (DRAFT, PUBLISHED, ARCHIVED)
- aiGenerated: Boolean
- views, downloads, sales: Integer
```

### Sale (Venta)
```java
- id: UUID
- book: Book
- buyer: User
- reseller: User (nullable)
- type: SaleType (DIGITAL, PHYSICAL)
- amount: BigDecimal
- commissionAuthor, commissionReseller, commissionPlatform: BigDecimal
- paymentStatus: PaymentStatus
- isDirect: Boolean
```

### AiGeneration (Generación IA)
```java
- id: UUID
- user: User
- type: GenerationType (IDEA, CHAPTER, SYNOPSIS, TITLE, etc.)
- genre: Genre
- prompt, result: Text
- tokensUsed: Integer
```

### MarketingCampaign (Campaña de Marketing)
```java
- id: UUID
- book: Book
- author: User
- serviceType: ServiceType (FACEBOOK_ADS, GOOGLE_ADS, COVER_DESIGN, etc.)
- budget: BigDecimal
- impressions, clicks, conversions: Integer
- ctr, conversionRate, roi: BigDecimal
```

### UserLibrary (Biblioteca Personal)
```java
- id: UUID
- user: User
- book: Book
- lastReadPosition: Integer
- progress: BigDecimal (0.00 - 100.00)
- notes, highlights: Text
- timesRead: Integer
```

### Review (Reseña)
```java
- id: UUID
- book: Book
- user: User
- rating: Integer (1-5)
- title, comment: String
- verified, approved: Boolean
```

---

## 📊 Sistema de Comisiones

```
┌────────────────────────────────────────────────────────┐
│             MODELO DE COMISIONES                       │
├────────────────────────────────────────────────────────┤
│                                                        │
│  VENTA DIRECTA (sin revendedor):                      │
│  • Autor:        90%                                   │
│  • Plataforma:   10%                                   │
│                                                        │
│  VENTA CON REVENDEDOR:                                 │
│  • Autor:        60%                                   │
│  • Revendedor:   30%                                   │
│  • Plataforma:   10%                                   │
│                                                        │
│  Ejemplo: Libro $10.00                                 │
│  • Directo:   Autor $9.00, Plataforma $1.00           │
│  • Revendedor: Autor $6.00, Revendedor $3.00, Plat $1 │
└────────────────────────────────────────────────────────┘
```

Implementado en `Sale.calculateCommissions()`.

---

## 🔐 Autenticación OAuth 2.0

Soporta **6 providers**:
- ✅ Google
- ✅ Facebook
- ✅ GitHub
- ✅ Twitter
- ✅ Apple
- ✅ Microsoft

**JWT Tokens** con expiración de 24 horas.  
**Refresh Tokens** con expiración de 7 días.

---

## 📡 APIs REST (Endpoints Principales)

### Autenticación
```
POST   /api/auth/register          # Registro
POST   /api/auth/login             # Login
POST   /api/auth/refresh           # Refresh token
POST   /api/auth/logout            # Logout
GET    /api/auth/oauth/{provider}  # OAuth login
```

### Libros
```
POST   /api/books                  # Crear libro
GET    /api/books                  # Listar libros
GET    /api/books/{id}             # Obtener libro
PUT    /api/books/{id}             # Actualizar libro
DELETE /api/books/{id}             # Eliminar libro
GET    /api/books/author/{id}      # Libros por autor
GET    /api/books/genre/{genre}    # Libros por género
POST   /api/books/{id}/publish     # Publicar libro
```

### Ventas
```
POST   /api/sales                  # Crear venta
GET    /api/sales                  # Listar ventas
GET    /api/sales/{id}             # Obtener venta
GET    /api/sales/author/{id}      # Ventas por autor
GET    /api/sales/book/{id}        # Ventas por libro
GET    /api/commissions/author/{id} # Comisiones autor
GET    /api/commissions/reseller/{id} # Comisiones revendedor
```

### IA (Integración con Investigatron)
```
POST   /api/ai/generate-idea       # Generar idea
POST   /api/ai/extend-chapter      # Extender capítulo
POST   /api/ai/create-synopsis     # Crear sinopsis
POST   /api/ai/suggest-titles      # Sugerir títulos
GET    /api/ai/generations/{userId} # Historial generaciones
```

### Marketing (Integración con OdrBrand)
```
POST   /api/marketing/campaigns    # Crear campaña
GET    /api/marketing/campaigns    # Listar campañas
GET    /api/marketing/campaigns/{id} # Obtener campaña
PUT    /api/marketing/campaigns/{id} # Actualizar métricas
GET    /api/marketing/book/{bookId} # Campañas de un libro
```

### Biblioteca Personal
```
GET    /api/library/{userId}       # Biblioteca del usuario
POST   /api/library/add            # Agregar a biblioteca
PUT    /api/library/{id}/progress  # Actualizar progreso
GET    /api/library/{id}/notes     # Obtener notas
POST   /api/library/{id}/highlight # Agregar highlight
```

### Reseñas
```
POST   /api/reviews                # Crear reseña
GET    /api/reviews/book/{bookId}  # Reseñas de libro
PUT    /api/reviews/{id}           # Actualizar reseña
DELETE /api/reviews/{id}           # Eliminar reseña
POST   /api/reviews/{id}/helpful   # Marcar como útil
```

---

## 🚀 Instalación y Ejecución

### Requisitos
- Java 21+
- Maven 3.6+
- PostgreSQL 14+

### 1. Clonar Repositorio
```bash
cd DrakkarPress.com/backend
```

### 2. Configurar Base de Datos PostgreSQL
```sql
CREATE DATABASE drakkarpress;
CREATE USER drakkarpress_user WITH PASSWORD 'your-password';
GRANT ALL PRIVILEGES ON DATABASE drakkarpress TO drakkarpress_user;
```

### 3. Configurar Variables de Entorno
Crear archivo `.env` o configurar en sistema:
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/drakkarpress
DATABASE_USERNAME=drakkarpress_user
DATABASE_PASSWORD=your-password

JWT_SECRET=your-super-secret-jwt-key-change-in-production

GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

STRIPE_API_KEY=your-stripe-api-key

AWS_S3_BUCKET=drakkarpress-books
AWS_ACCESS_KEY=your-aws-access-key
AWS_SECRET_KEY=your-aws-secret-key

INVESTIGATRON_API_URL=http://localhost:8000
ODRBRAND_API_URL=http://localhost:8081
```

### 4. Compilar
```bash
mvn clean install
```

### 5. Ejecutar
```bash
mvn spring-boot:run
```

O crear JAR ejecutable:
```bash
mvn package
java -jar target/drakkarpress-platform-1.0.0.jar
```

### 6. Acceder
- **Backend API:** http://localhost:8080
- **Swagger Docs:** http://localhost:8080/swagger-ui.html
- **Actuator Health:** http://localhost:8080/actuator/health

---

## 🧪 Testing

```bash
# Ejecutar todos los tests
mvn test

# Tests con cobertura
mvn test jacoco:report

# Tests de integración
mvn verify
```

---

## 📦 Dependencias Principales

| Librería | Versión | Propósito |
|----------|---------|-----------|
| Spring Boot | 3.2.0 | Framework principal |
| Spring Data JPA | 3.2.0 | ORM y repositorios |
| Spring Security | 6.2.0 | Autenticación y autorización |
| PostgreSQL Driver | 42.6.0 | Conexión a PostgreSQL |
| JWT (jjwt) | 0.12.3 | JSON Web Tokens |
| Lombok | 1.18.30 | Reduce boilerplate |
| Stripe Java | 24.7.0 | Procesamiento de pagos |
| AWS SDK S3 | 2.21.0 | Almacenamiento de archivos |

---

## 🔮 Próximas Funcionalidades

- [ ] WebSockets para notificaciones en tiempo real
- [ ] Caché con Redis
- [ ] Queue system con RabbitMQ
- [ ] Elasticsearch para búsqueda avanzada
- [ ] GraphQL API
- [ ] Microservicios (separar en módulos)
- [ ] Docker y Kubernetes
- [ ] CI/CD con GitHub Actions

---

## 📝 Licencia

© 2025 DrakkarPress - Todos los derechos reservados

---

## 👥 Equipo de Desarrollo

**DrakkarPress Backend Team**  
📧 dev@drakkarpress.com
