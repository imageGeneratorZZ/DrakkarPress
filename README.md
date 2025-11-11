# ⚔️ DrakkarPress - Plataforma Comunitaria para Escritores

**Versión:** 2.0 - Sistema de Perfiles Multi-rol con Runas del Elder Futhark  
**Última actualización:** 11 de Noviembre, 2025

**© 2025 DrakkarPress. Todos los derechos reservados.**

> **Plataforma comunitaria completa donde escritores, editoriales, imprentas y revendedores se conectan, colaboran y crecen juntos. Con generadores de IA, sistema de runas nórdicas y membresías escalonadas.**

---

## 🌟 ¿Qué es DrakkarPress?

**DrakkarPress** es mucho más que un generador de libros con IA. Es una **plataforma comunitaria completa** donde escritores, editoriales, imprentas y revendedores se conectan, colaboran y crecen juntos.

### Características Principales:

- 🤝 **Red Social Literaria** - Networking estilo "Facebook de escritores"
- 🤖 **Generadores de IA** - DrakkarPress (general) + Scriptorium (infantil)
- 💰 **Marketplace Integrado** - Compra/venta con marketing sinérgico
- 🔮 **Sistema de Runas** - Personalización única con runas del Elder Futhark
- 🏆 **Membresías por Fases** - Pricing escalonado con beneficios de por vida
- ⚡ **Panel de Administración** - Gestión completa de usuarios y fases

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
├── backend/
│   ├── pom.xml                          # Maven config
│   ├── src/main/java/.../model/
│   │   ├── Rune.java                    # ✅ Completo
│   │   ├── Badge.java                   # ✅ Completo
│   │   └── [15 entities pending...]     # 🚧 Pendiente
│   └── src/main/resources/
│       └── application.properties
├── database/
│   ├── schema.sql                        # ✅ 100% (17 tables)
│   └── seeds/
│       └── init-data.sql                 # ✅ 100% (24 runas + 8 badges)
├── docs/                                 # Documentación antigua (v1.0)
├── ARQUITECTURA_ECOSISTEMA_COMPLETO.md  # ✅ 100 páginas
├── ROADMAP_COMPLETO.md                  # ✅ 50 páginas
├── RESUMEN_EJECUTIVO_COMPLETO.md        # ✅ 20 páginas
├── QUICK_START_GUIDE.md                 # ✅ 15 páginas
└── INDICE_DOCUMENTACION.md              # ✅ Índice completo
```

## 🚀 Quick Start

### Para Desarrolladores:

```powershell
# 1. Clonar repositorio
git clone https://github.com/imageGeneratorZZ/DrakkarPress.git
cd DrakkarPress.com

# 2. Leer documentación
start INDICE_DOCUMENTACION.md        # Índice general
start QUICK_START_GUIDE.md           # Guía de inicio rápido

# 3. Iniciar base de datos
cd backend
docker-compose up -d postgres

# 4. Ejecutar scripts SQL
$env:PGPASSWORD='drakkarpress'; psql -h localhost -p 5432 -U drakkarpress -d drakkarpress_db -f ..\database\schema.sql
$env:PGPASSWORD='drakkarpress'; psql -h localhost -p 5432 -U drakkarpress -d drakkarpress_db -f ..\database\seeds\init-data.sql

# 5. Iniciar backend
.\mvnw.cmd spring-boot:run
```

---

## 📚 Documentación

### 📑 [INDICE_DOCUMENTACION.md](./INDICE_DOCUMENTACION.md)
**→ Índice completo de toda la documentación** 👈 **Empieza aquí**

### Guías Principales:

| Documento | Descripción | Para quién |
|-----------|-------------|------------|
| **[QUICK_START_GUIDE.md](./QUICK_START_GUIDE.md)** | Guía de inicio rápido | 👨‍💻 Desarrolladores |
| **[RESUMEN_EJECUTIVO_COMPLETO.md](./RESUMEN_EJECUTIVO_COMPLETO.md)** | Presentación ejecutiva | 💼 Stakeholders |
| **[ARQUITECTURA_ECOSISTEMA_COMPLETO.md](./ARQUITECTURA_ECOSISTEMA_COMPLETO.md)** | Arquitectura completa | 🏗️ Arquitectos |
| **[ROADMAP_COMPLETO.md](./ROADMAP_COMPLETO.md)** | Plan de trabajo detallado | 📋 PMs / Devs |

---

## 🏗️ Stack Tecnológico

### Backend
```
☕ Java 17
🍃 Spring Boot 3.2.0
🗄️ PostgreSQL 14+
🔐 JWT Auth (io.jsonwebtoken 0.12.3)
💳 Stripe Integration
🧩 JPA/Hibernate + Lombok
📦 Maven 3.9+
```

### Frontend
```
🌐 HTML/CSS/JavaScript
📱 Responsive Design
🎨 Framework moderno (React/Vue - futuro)
```

---

## 📊 Estado del Proyecto

```
Documentación:     ████████████████████ 100%
Base de Datos:     ████████████████████ 100%
Backend (Models):  ████░░░░░░░░░░░░░░░░  20%
Backend (Logic):   ░░░░░░░░░░░░░░░░░░░░   0%
Frontend:          ░░░░░░░░░░░░░░░░░░░░   0%
Testing:           ░░░░░░░░░░░░░░░░░░░░   0%
──────────────────────────────────────────────
TOTAL:             ███░░░░░░░░░░░░░░░░░  17%
```

### 📁 Archivos Creados:
- ✅ 5 documentos de arquitectura (~185 páginas)
- ✅ Esquema completo de BD (~800 líneas SQL)
- ✅ Seeds con 24 runas + 8 badges (~600 líneas SQL)
- ✅ 2 modelos Java (Rune.java, Badge.java)

### 📝 Pendiente (según ROADMAP):
- 🚧 15 entidades Java adicionales
- 🚧 15 JPA repositories
- 🚧 20+ DTOs
- 🚧 12 services
- 🚧 11 controllers
- 🚧 Security components (JWT, filters, config)
- 🚧 Frontend (15+ páginas)
- 🚧 Testing suite
- 🚧 DevOps configuration

### ⏱️ Timeline:
**22 semanas** divididas en 4 milestones (ver ROADMAP_COMPLETO.md)

---

## � Proyección de Ingresos

### Año 1 (Conservador):
```
MRR Año 1:  $95,000/mes
ARR Año 1:  $1,140,000
```

### Año 2 (Optimista):
```
MRR Año 2:  $395,000/mes
ARR Año 2:  $4,740,000
```

**Detalles completos:** Ver `RESUMEN_EJECUTIVO_COMPLETO.md`

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
