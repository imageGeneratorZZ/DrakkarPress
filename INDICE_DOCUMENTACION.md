# 📑 DrakkarPress - Índice de Documentación

**Sistema de Perfiles Multi-rol con Runas del Elder Futhark**  
**Versión:** 2.0  
**Fecha:** 11 de Noviembre, 2025

---

## 🎯 Guías de Inicio Rápido

### 📘 [QUICK_START_GUIDE.md](./QUICK_START_GUIDE.md)
**Para desarrolladores que continúan el proyecto**

- ✅ Cómo iniciar la base de datos
- ✅ Estructura del backend Java
- ✅ Templates de código
- ✅ Pasos inmediatos siguientes
- ✅ Troubleshooting

**Leer primero si vas a programar** 👈

---

## 📊 Documentación Ejecutiva

### 🎯 [RESUMEN_EJECUTIVO_COMPLETO.md](./RESUMEN_EJECUTIVO_COMPLETO.md)
**Presentación del proyecto para stakeholders**

- Visión y concepto
- Sistema de perfiles (4 tipos)
- Membresías por fases
- 24 runas del Elder Futhark
- Sistema de badges
- Panel de administración
- Proyección de ingresos
- Estado actual y timeline

**Ideal para presentaciones** 📊

---

## 🏗️ Arquitectura Técnica

### 📘 [ARQUITECTURA_ECOSISTEMA_COMPLETO.md](./ARQUITECTURA_ECOSISTEMA_COMPLETO.md)
**Documento maestro de arquitectura**

#### Contenido Completo:

**1. Visión General**
- DrakkarPress como plataforma comunitaria
- Componentes del ecosistema
- Relación con ODRBrand

**2. Sistema de Perfiles Multi-rol**
- Cliente (base obligatorio)
- Autor/Editorial (persona o empresa)
- Imprenta (certificada)
- Revendedor (distribución)
- Verificaciones por rol

**3. Membresías y Pricing**
- Plan FREE (límites de IA)
- Plan PREMIUM (ilimitado)
- Fase 1: Fundadores ($5/mes, 1-1,000)
- Fase 2: Early Adopters ($10/mes, 1,001-10,000)
- Fase 3: Regular ($19.99/mes, 10,001+)
- Cortesía (admin)

**4. Sistema de Runas (Elder Futhark)**
- 24 runas organizadas por categoría:
  - Creatividad & Conocimiento (ᚲ Kenaz, ᚨ Ansuz, ᛗ Mannaz)
  - Éxito & Logro (ᛊ Sowilo, ᛃ Jera, ᚹ Wunjo, ᛏ Tiwaz)
  - Crecimiento & Transformación (ᛒ Berkano, ᛞ Dagaz, ᛁ Isa)
  - Protección & Fuerza (ᚦ Thurisaz, ᚢ Uruz, ᛉ Algiz)
  - Intuición & Misterio (ᛚ Laguz, ᛈ Perthro)
  - Legado & Abundancia (ᛟ Othala, ᚠ Fehu)
  - Colaboración & Progreso (ᛖ Ehwaz, ᚷ Gebo, ᚱ Raidho)
  - + 4 runas adicionales
- Reglas de cambio (1/mes)
- Significados curados para escritores

**5. Sistema de Badges**
- Fundador 🏆 + ᛟ (primeros 1,000)
- Early Adopter ⭐ + ᛊ (1,001-10,000)
- Premium ✨ (regular)
- Invitado Especial 👑 + ᚨ (cortesía)
- Verificado ✓
- Certificado ⚡
- Bestseller 📚 + ᛃ
- Prolífico ✍️ + ᚲ

**6. Panel de Administración**
- Dashboard con métricas
- Gestión de usuarios
- Otorgar premium cortesía
- Modificar planes
- Ver historial completo
- Gestión de fases
- Analytics

**7. Arquitectura Técnica**
- Stack: Spring Boot + PostgreSQL + JWT
- 17 tablas principales
- Frontend HTML/CSS/JS
- Servicios externos (Stripe, AWS, SendGrid)

**8. Seguridad y Privacidad**
- Autenticación JWT
- Encriptación de datos sensibles
- GDPR compliance
- Roles y permisos

**9. Roadmap de Implementación**
- Fase Alpha: MVP (Semanas 1-4)
- Fase Beta: Features (Semanas 5-8)
- Fase Gamma: Networking (Semanas 9-12)
- Fase Release: Producción (Semanas 13-16)

**10. Métricas Clave (KPIs)**
- Crecimiento
- Monetización
- Engagement
- Calidad

**Documento más completo - 100+ páginas** 📖

---

## 📋 Plan de Trabajo

### 📋 [ROADMAP_COMPLETO.md](./ROADMAP_COMPLETO.md)
**Roadmap detallado con tareas y timeline**

#### Secciones:

**1. Completado (100%)**
- ✅ Documentación de arquitectura
- ✅ Esquema de base de datos (17 tablas)
- ✅ Seeds con runas y badges
- ✅ Modelos Java (2/17)

**2. En Progreso (20%)**
- 🚧 Backend - Modelos Java

**3. Pendiente (0%)**
- ⏳ Repositorios JPA (15 interfaces)
- ⏳ DTOs (20+ clases)
- ⏳ Services (12 services)
- ⏳ Controllers (11 controllers)
- ⏳ Security & Middleware (5 componentes)
- ⏳ Frontend (15+ páginas HTML)
- ⏳ Componentes JS (7 componentes)
- ⏳ Integraciones externas (4 servicios)
- ⏳ Testing (4 tipos de tests)
- ⏳ Documentación (5 documentos)
- ⏳ DevOps (7 configuraciones)

**4. Plan de Trabajo por Semanas**
- Semana 1-2: Backend Core
- Semana 3-4: Autenticación
- Semana 5-6: Membresías
- Semana 7-8: Runas y Badges
- Semana 9-10: Roles Multi-rol
- Semana 11-12: Límites de IA
- Semana 13-14: Panel Admin
- Semana 15-16: Networking
- Semana 17-18: Frontend
- Semana 19-20: Testing
- Semana 21-22: Deploy

**5. Hitos Clave**
- Milestone 1: MVP Backend (6 semanas)
- Milestone 2: Características Premium (6 semanas)
- Milestone 3: Comunidad (4 semanas)
- Milestone 4: Producción (6 semanas)

**6. Progreso Visual**
- Gráficos de progreso por área
- Progreso total: 17%

**Para planning y tracking** 📅

---

## 🗄️ Base de Datos

### 📊 [database/schema.sql](./database/schema.sql)
**Esquema completo de PostgreSQL**

#### Contenido:

**ENUMs (8 tipos personalizados)**
- `membership_plan` (FREE, PREMIUM_PHASE_1/2/3, COURTESY)
- `membership_status` (ACTIVE, EXPIRED, CANCELLED, SUSPENDED)
- `payment_frequency` (MONTHLY, ANNUAL, LIFETIME)
- `user_role_type` (CLIENT, AUTHOR_PUBLISHER, PRINT_SHOP, RESELLER)
- `verification_status` (PENDING, APPROVED, REJECTED, EXPIRED)
- `entity_type` (INDIVIDUAL, COMPANY)
- `badge_status` (ACTIVE, INACTIVE, REVOKED)
- `ai_usage_type` (6 tipos de uso)
- `connection_status` (PENDING, ACCEPTED, BLOCKED)

**Tablas (17 principales)**
1. `users` - Base de usuarios
2. `runes` - 24 runas del Futhark
3. `badges` - Tipos de badges
4. `memberships` - Historial de membresías
5. `user_runes` - Runas seleccionadas
6. `user_badges` - Badges asignados
7. `user_roles` - Roles multi-rol
8. `role_verification` - Verificación de documentos
9. `ai_usage_limits` - Límites por plan
10. `ai_usage_tracking` - Tracking detallado
11. `ai_usage_monthly_summary` - Resumen mensual
12. `connections` - Red social
13. `user_activity_feed` - Feed de actividad
14. `messages` - Mensajería interna
15. `payment_transactions` - Transacciones
16. `admin_audit_log` - Auditoría admin
17. `session_tokens` - Tokens JWT

**Funciones y Triggers**
- Auto-actualización de `updated_at`
- Auto-asignación de badges según fase
- Contador de selección de runas

**Vistas**
- `v_user_full_profile` - Perfil completo con runas y badges
- `v_user_ai_usage_current_month` - Uso mensual de IA

**Índices optimizados para performance**

**Listo para ejecutar en PostgreSQL** ⚡

---

### 🌱 [database/seeds/init-data.sql](./database/seeds/init-data.sql)
**Datos iniciales del sistema**

#### Contenido:

**24 Runas del Elder Futhark**
- Símbolo, nombre, significado (ES/EN)
- Descripción extendida por runa
- Categoría y orden de visualización
- Ejemplos:
  - ᚲ Kenaz (Creatividad) - "La antorcha que ilumina el camino del escritor"
  - ᛊ Sowilo (Éxito) - "El sol que no se oculta"
  - ᛟ Othala (Legado) - "La tierra ancestral"

**8 Badges del Sistema**
- Fundador 🏆 con runa Othala
- Early Adopter ⭐ con runa Sowilo
- Premium ✨
- Invitado Especial 👑 con runa Ansuz
- Verificado ✓
- Certificado ⚡
- Bestseller 📚 con runa Jera
- Prolífico ✍️ con runa Kenaz

**Límites de IA por Plan**
- FREE: limitado (0, 3, 10, 5, 0, 0)
- PREMIUM_*: ilimitado (NULL)

**Usuarios de Prueba**
- `admin@drakkarpress.com` (Admin con cortesía)
  - Password: Admin123!@# (cambiar en producción)
  - Badge: Invitado Especial 👑
  - Runa: Ansuz (sabiduría)
  
- `founder@test.com` (Fundador)
  - Password: Test123!@#
  - Badge: Fundador 🏆
  - Runa: Kenaz (creatividad)
  - Rol: Autor Individual
  
- `free@test.com` (Usuario Free)
  - Password: Test123!@#
  - Sin badges
  - Sin runa

**Funciones Útiles**
- `get_user_ai_limits(user_id)` - Obtener límites y uso actual

**Scripts de Verificación**
- Cuenta de runas, badges, usuarios

**Listo para ejecutar después de schema.sql** 🎲

---

## 💻 Código Backend

### 📁 Estructura del Backend Java

```
backend/src/main/java/com/drakkarpress/platform/
├── model/                      # Entities (JPA)
│   ├── ✅ Rune.java           (Completado)
│   ├── ✅ Badge.java          (Completado)
│   ├── ❌ User.java           (Pendiente)
│   ├── ❌ Membership.java     (Pendiente)
│   └── ... (13 más pendientes)
│
├── repository/                 # Repositorios (JPA)
│   └── ... (15 pendientes)
│
├── dto/                        # Data Transfer Objects
│   └── ... (20+ pendientes)
│
├── service/                    # Lógica de negocio
│   └── ... (12 pendientes)
│
├── controller/                 # REST API
│   └── ... (11 pendientes)
│
├── security/                   # JWT & Auth
│   └── ... (5 pendientes)
│
└── config/                     # Configuración
    └── ... (3 pendientes)
```

### ✅ Archivos Java Completados

#### [Rune.java](./backend/src/main/java/com/drakkarpress/platform/model/Rune.java)
Entity de runas del Elder Futhark
- 24 runas con símbolo, nombre, significado
- Relaciones con `UserRune` y `Badge`
- Contador de veces seleccionada
- Timestamps automáticos

#### [Badge.java](./backend/src/main/java/com/drakkarpress/platform/model/Badge.java)
Entity de badges del sistema
- 8 badges con código, nombre, icono
- Runa asociada opcional
- Auto-asignación configurable
- Relación con `UserBadge`

---

## 🔧 Configuración

### ⚙️ [backend/pom.xml](./backend/pom.xml)
**Maven dependencies (ya existente)**

Incluye:
- Spring Boot 3.2.0
- Spring Data JPA
- PostgreSQL Driver
- Spring Security + OAuth2
- JWT (io.jsonwebtoken 0.12.3)
- Lombok
- Stripe Java SDK
- Thymeleaf
- Validation

### 🐳 [backend/docker-compose.yml](./backend/docker-compose.yml)
**Docker Compose para PostgreSQL (ya existente)**

Servicios:
- PostgreSQL 14
- pgAdmin (opcional)
- Redis (caché)

---

## 📖 Guías Adicionales

### 🚀 Próximamente:
- [ ] API_DOCUMENTATION.md (Swagger/OpenAPI)
- [ ] ADMIN_GUIDE.md (Manual de administrador)
- [ ] USER_GUIDE.md (Manual de usuario)
- [ ] DEVELOPER_GUIDE.md (Guía para devs)
- [ ] DEPLOYMENT_GUIDE.md (Deploy a producción)

---

## 📊 Estado General del Proyecto

### ✅ Completado (17%)
```
Documentación:    ████████████████████ 100%
Base de Datos:    ████████████████████ 100%
Backend (Models): ████░░░░░░░░░░░░░░░░  20%
Backend (Logic):  ░░░░░░░░░░░░░░░░░░░░   0%
Frontend:         ░░░░░░░░░░░░░░░░░░░░   0%
Testing:          ░░░░░░░░░░░░░░░░░░░░   0%
DevOps:           ░░░░░░░░░░░░░░░░░░░░   0%
────────────────────────────────────────────
TOTAL:            ███░░░░░░░░░░░░░░░░░  17%
```

### 📁 Archivos Creados
```
✅ ARQUITECTURA_ECOSISTEMA_COMPLETO.md    (~100 páginas)
✅ ROADMAP_COMPLETO.md                    (~50 páginas)
✅ RESUMEN_EJECUTIVO_COMPLETO.md          (~20 páginas)
✅ QUICK_START_GUIDE.md                   (~15 páginas)
✅ INDICE_DOCUMENTACION.md                (este archivo)
✅ database/schema.sql                    (~800 líneas)
✅ database/seeds/init-data.sql           (~600 líneas)
✅ backend/.../model/Rune.java            (~80 líneas)
✅ backend/.../model/Badge.java           (~70 líneas)
────────────────────────────────────────────────────────
Total: 9 archivos | ~2,000 líneas de código
```

---

## 🎯 Cómo Usar Esta Documentación

### Para Desarrolladores:
1. Leer **[QUICK_START_GUIDE.md](./QUICK_START_GUIDE.md)** primero
2. Consultar **[ARQUITECTURA_ECOSISTEMA_COMPLETO.md](./ARQUITECTURA_ECOSISTEMA_COMPLETO.md)** para entender el sistema
3. Seguir **[ROADMAP_COMPLETO.md](./ROADMAP_COMPLETO.md)** para el plan de trabajo

### Para Stakeholders:
1. Leer **[RESUMEN_EJECUTIVO_COMPLETO.md](./RESUMEN_EJECUTIVO_COMPLETO.md)**
2. Consultar métricas y timeline

### Para DBAs:
1. Revisar **[database/schema.sql](./database/schema.sql)**
2. Ejecutar **[database/seeds/init-data.sql](./database/seeds/init-data.sql)**

---

## 📞 Recursos

**Repositorio:** [github.com/imageGeneratorZZ/DrakkarPress](https://github.com/imageGeneratorZZ/DrakkarPress)  
**Branch:** main  
**Última actualización:** 11 de Noviembre, 2025

---

## 🔍 Búsqueda Rápida

### Buscar por Tema:

**Sistema de Perfiles:**
- Arquitectura: `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` → Sección 2
- DB Schema: `database/schema.sql` → Tabla `user_roles`

**Membresías:**
- Arquitectura: `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` → Sección 3
- DB Schema: `database/schema.sql` → Tabla `memberships`
- Pricing: `RESUMEN_EJECUTIVO_COMPLETO.md` → Sección Membresías

**Runas:**
- Arquitectura: `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` → Sección 4
- DB Schema: `database/schema.sql` → Tabla `runes`
- Seeds: `database/seeds/init-data.sql` → Seed Runas
- Entity: `backend/.../model/Rune.java`

**Badges:**
- Arquitectura: `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` → Sección 5
- DB Schema: `database/schema.sql` → Tabla `badges`
- Seeds: `database/seeds/init-data.sql` → Seed Badges
- Entity: `backend/.../model/Badge.java`

**Panel Admin:**
- Arquitectura: `ARQUITECTURA_ECOSISTEMA_COMPLETO.md` → Sección 6
- Resumen: `RESUMEN_EJECUTIVO_COMPLETO.md` → Panel Admin

**Backend:**
- Quick Start: `QUICK_START_GUIDE.md` → Sección Backend
- Roadmap: `ROADMAP_COMPLETO.md` → Pendiente

---

**DrakkarPress - Donde los escritores forjan su legado** ⚔️📚

*Última actualización: 11 de Noviembre, 2025*
