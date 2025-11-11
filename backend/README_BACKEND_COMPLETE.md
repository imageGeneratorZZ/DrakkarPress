# DrakkarPress Platform v2.0 - Backend

## 🚀 Arquitectura Completa

**Backend Spring Boot con:**
- ✅ 17 Entities JPA con relaciones completas
- ✅ 17 Repositories con queries personalizadas
- ✅ Security Layer con JWT (access + refresh tokens)
- ✅ AuthService + AuthController (Register, Login, Refresh, Logout)
- ✅ DTOs Request/Response
- ✅ Main Application class

## 📊 Estructura de la Base de Datos

### Entidades Implementadas (17)
1. **User** - Usuario base con user_number para tracking de fases
2. **Membership** - Membresías con grandfathering y fases de precios
3. **Rune** - 24 runas Elder Futhark (pre-cargadas)
4. **Badge** - Sistema de insignias (pre-cargados)
5. **UserRune** - Relación usuario-runa con límite de cambio (1/mes)
6. **UserBadge** - Badges obtenidos por usuarios
7. **UserRole** - Sistema multi-rol (CLIENT, AUTHOR_PUBLISHER, PRINT_SHOP, RESELLER)
8. **RoleVerification** - Verificación de documentos para roles especiales
9. **AiUsageLimit** - Límites de IA por plan
10. **AiUsageTracking** - Tracking detallado de uso de IA
11. **AiUsageMonthlySummary** - Resumen mensual agregado
12. **Connection** - Red social (seguidores/seguidos)
13. **UserActivityFeed** - Feed de actividades
14. **Message** - Mensajería interna
15. **PaymentTransaction** - Transacciones de pago
16. **AdminAuditLog** - Log de auditoría administrativa
17. **SessionToken** - Tokens JWT con refresh tokens

## 🛠️ Prerequisitos

### 1. Instalar PostgreSQL 14+
```powershell
# Descargar desde: https://www.postgresql.org/download/windows/
# Instalar con configuración por defecto (puerto 5432)
```

### 2. Crear Base de Datos
```powershell
# Abrir psql como usuario postgres
psql -U postgres

# Dentro de psql:
CREATE DATABASE drakkarpress_db;
CREATE USER drakkarpress_user WITH PASSWORD 'change_this_password_123';
GRANT ALL PRIVILEGES ON DATABASE drakkarpress_db TO drakkarpress_user;
\q
```

### 3. Ejecutar Scripts SQL
```powershell
# Aplicar schema
psql -U drakkarpress_user -d drakkarpress_db -f backend/src/main/resources/db/schema.sql

# Cargar datos iniciales (24 runas + 8 badges + AI limits + test users)
psql -U drakkarpress_user -d drakkarpress_db -f backend/src/main/resources/db/init-data.sql
```

### 4. Verificar Instalación
```sql
-- Conectar a la base de datos
psql -U drakkarpress_user -d drakkarpress_db

-- Verificar tablas
\dt

-- Verificar runas (debe retornar 24)
SELECT COUNT(*) FROM runes;

-- Verificar badges (debe retornar 8)
SELECT COUNT(*) FROM badges;

-- Salir
\q
```

## 🔧 Configuración del Backend

### 1. Actualizar application.properties
```properties
# Ubicación: backend/src/main/resources/application.properties

# Cambiar contraseña de la base de datos (línea 9)
spring.datasource.password=TU_CONTRASEÑA_SEGURA

# Cambiar JWT secret (línea 20)
jwt.secret=TU_SECRET_KEY_DE_MINIMO_256_BITS
```

### 2. Instalar Maven (si no está instalado)
```powershell
# Verificar si Maven está instalado
mvn -version

# Si no está instalado, descargar desde:
# https://maven.apache.org/download.cgi
```

### 3. Compilar el Proyecto
```powershell
cd backend
mvn clean install
```

### 4. Ejecutar el Backend
```powershell
# Opción 1: Usando Maven
mvn spring-boot:run

# Opción 2: Usando JAR compilado
java -jar target/drakkarpress-platform-0.0.1-SNAPSHOT.jar

# Opción 3: Usando el script (si existe)
./run-local.bat
```

## 📡 API Endpoints Disponibles

### Authentication (`/api/auth/*`)

#### 1. Registrar Usuario
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "usuario@example.com",
  "username": "usuario123",
  "password": "password123",
  "displayName": "Nombre Usuario"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "userId": "uuid-here",
    "email": "usuario@example.com",
    "username": "usuario123",
    "displayName": "Nombre Usuario",
    "userNumber": 1,
    "membershipPlan": "FREE"
  }
}
```

#### 2. Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "emailOrUsername": "usuario@example.com",
  "password": "password123"
}
```

#### 3. Refresh Token
```http
POST http://localhost:8080/api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

#### 4. Logout
```http
POST http://localhost:8080/api/auth/logout
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

#### 5. Health Check
```http
GET http://localhost:8080/api/auth/health
```

## 🔐 Sistema de Autenticación

### JWT Tokens
- **Access Token**: Válido 15 minutos
- **Refresh Token**: Válido 30 días
- Almacenados en tabla `session_tokens` para invalidación remota

### Seguridad
- Passwords hasheados con BCrypt
- Refresh tokens hasheados con SHA-256
- CORS configurado para localhost:3000 y drakkarpress.com
- Rutas públicas: `/api/auth/**`, `/api/runes/public/**`, `/api/badges/public/**`
- Rutas admin: `/api/admin/**` (requiere rol ADMIN)

## 🎯 Sistema de Membresías

### Fases de Precio (Grandfathering)
- **PHASE_1**: Usuarios 1-1,000 → $5/mes (precio de por vida)
- **PHASE_2**: Usuarios 1,001-10,000 → $10/mes (precio de por vida)
- **PHASE_3**: Usuarios 10,001+ → $19.99/mes

### Planes
- **FREE**: Acceso básico, sin runas, límites de IA básicos
- **PREMIUM**: Acceso completo, selección de runas, límites de IA extendidos

## 🔮 Sistema de Runas (Elder Futhark)

### 24 Runas Pre-cargadas
- Categorías: Wealth, Protection, Power, Joy, Defense, Knowledge, Harvest, Initiative
- Solo usuarios PREMIUM pueden seleccionar runa
- Límite de cambio: **1 vez cada 30 días**
- Runa por defecto: **Fehu** (Wealth, primera runa)

### Cambio de Runa
Query en `UserRuneRepository`:
```java
canUserChangeRune(userId) // Verifica límite de 30 días
```

## 🏆 Sistema de Badges

### 8 Badges Pre-cargados
1. **Founder** - Primeros 100 usuarios (auto-asignado)
2. **Early Adopter** - Primeros 1000 usuarios (auto-asignado)
3. **Verified Author** - Autor verificado
4. **Bestseller** - Libro en top ventas
5. **Print Partner** - Imprenta certificada
6. **Reseller Pro** - Revendedor activo
7. **Premium Member** - Membresía premium activa (auto-asignado)
8. **Community Leader** - Líder comunitario

## 🤖 Sistema de IA

### Límites por Plan (tabla `ai_usage_limits`)
- **FREE**: 10 generaciones/mes de PDF
- **PREMIUM**: 100 generaciones/mes de PDF

### Tracking
- `ai_usage_tracking`: Log detallado por operación
- `ai_usage_monthly_summary`: Resumen mensual agregado
- Queries para verificar límites en `AiUsageMonthlySummaryRepository`

## 👥 Sistema Multi-Rol

### Roles Disponibles
1. **CLIENT** (obligatorio, todos los usuarios)
2. **AUTHOR_PUBLISHER** (requiere verificación de datos de pago)
3. **PRINT_SHOP** (requiere certificación)
4. **RESELLER** (requiere verificación de datos de pago)

### Verificación de Roles
Tabla `role_verifications` con:
- Upload de documentos
- Estados: PENDING, APPROVED, REJECTED, EXPIRED
- Renovación anual para certificaciones

## 🌐 Red Social

### Conexiones
- Seguidores/Seguidos
- Estados: PENDING, ACCEPTED, REJECTED, BLOCKED
- Query para conexiones mutuas

### Activity Feed
- Posts públicos/privados
- Pinned posts
- Feed personalizado (posts de usuarios seguidos)

### Mensajería
- Mensajes privados entre usuarios
- Soft delete (independiente para sender/recipient)
- Hilos de respuestas

## 💰 Pagos

### PaymentTransaction
- Integración preparada para Stripe
- Estados: PENDING, COMPLETED, FAILED, REFUNDED
- Cálculo de revenue por período
- Estadísticas por plan

## 🔍 Admin Panel

### Audit Log
- Tracking de todas las acciones administrativas
- IP, dispositivo, ubicación
- Estadísticas por admin y tipo de acción

## 📈 Próximos Pasos

### Servicios Pendientes
- [ ] UserService (CRUD usuarios)
- [ ] MembershipService (upgrade, grandfathering)
- [ ] RuneService (selección, cambio con límite)
- [ ] BadgeService (asignación automática/manual)
- [ ] RoleService (activación, verificación)
- [ ] ConnectionService (seguir/dejar de seguir)
- [ ] MessageService (enviar, leer, eliminar)
- [ ] PaymentService (integración Stripe)

### Controllers Pendientes
- [ ] UserController
- [ ] MembershipController
- [ ] RuneController
- [ ] BadgeController
- [ ] ProfileController
- [ ] AdminController

### Frontend
- [ ] React/Next.js application
- [ ] Integración con API REST
- [ ] UI para selección de runas
- [ ] Dashboard de usuario
- [ ] Admin panel

## 🐛 Troubleshooting

### Error: "Cannot connect to database"
```powershell
# Verificar que PostgreSQL está corriendo
Get-Service -Name postgresql*

# Si está detenido, iniciarlo
Start-Service postgresql-x64-14
```

### Error: "Access denied for user"
```sql
-- Verificar permisos
psql -U postgres -d drakkarpress_db
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO drakkarpress_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO drakkarpress_user;
```

### Error: "JWT signature does not match"
- Verificar que `jwt.secret` en `application.properties` tenga mínimo 256 bits (32 caracteres)
- No cambiar el secret después de generar tokens (invalidará sesiones)

### Error: "Port 8080 already in use"
```powershell
# Cambiar puerto en application.properties
server.port=8081

# O matar proceso usando puerto 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

## 📚 Documentación Adicional

Ver documentos en raíz del proyecto:
- `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` - 100 páginas de arquitectura
- `ROADMAP_COMPLETO.md` - Plan de implementación 22 semanas
- `RESUMEN_EJECUTIVO_COMPLETO.md` - Resumen ejecutivo con proyecciones
- `QUICK_START_GUIDE.md` - Guía rápida para desarrolladores

## 💪 Estado del Proyecto

```
✅ Documentación (100%)
✅ Database Schema (100%)
✅ Database Seeds (100%)
✅ JPA Entities (100% - 17/17)
✅ JPA Repositories (100% - 17/17)
✅ Security Layer (100%)
✅ Auth Service + Controller (100%)
✅ Application Configuration (100%)
✅ Main Application Class (100%)
⏳ Additional Services (0%)
⏳ Additional Controllers (0%)
⏳ Frontend (0%)
──────────────────────────────
BACKEND CORE: 75% COMPLETE
```

## 🎉 ¡Backend Listo para Arrancar!

El backend está **100% funcional** para autenticación y registro. Puedes:
1. Registrar usuarios
2. Login con JWT
3. Refresh tokens
4. Logout con invalidación de sesión

Los usuarios nuevos reciben automáticamente:
- Membresía FREE
- Runa por defecto (Fehu)
- user_number secuencial para tracking de fases

---

**DrakkarPress Platform v2.0**  
*Elder Futhark Community for Content Creators*  
🛡️ Built with Spring Boot 3.2.0, PostgreSQL 14+, JWT Authentication
