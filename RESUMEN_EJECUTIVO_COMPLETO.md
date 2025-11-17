# 🎯 DrakkarPress - Resumen Ejecutivo

**Fecha:** 11 de Noviembre, 2025  
**Versión:** 2.0 - Sistema de Perfiles Multi-rol con Runas

---

## 🌟 VISIÓN

**DrakkarPress** es una plataforma comunitaria para escritores, editoriales, imprentas y revendedores que combina:
- 🤝 **Red social especializada** (estilo "Facebook de escritores")
- 🤖 **Generadores de IA** (DrakkarPress + Scriptorium)
- 💰 **Marketplace literario** con marketing sinérgico
- ⚡ **Sistema de membresías** escalonado por fases

---

## 👥 SISTEMA DE PERFILES (4 Tipos)

### 1. 👤 Cliente (Base - Todos)
Compra libros, accede a IA según plan, participa en comunidad

### 2. ✍️ Autor/Editorial (Publica)
- **Persona:** Autor individual
- **Empresa:** Editorial corporativa
- Publica obras propias o de terceros

### 3. 🏭 Imprenta (Servicios)
Ofrece impresión bajo demanda, requiere certificación

### 4. 🤝 Revendedor (Distribución)
Distribuye libros, gestiona comisiones automáticas

---

## 💎 MEMBRESÍAS POR FASES

### 🆓 GRATUITO
- ❌ Generación completa: **BLOQUEADA**
- ✅ Portadas: **3/mes**
- ✅ Asistente: **10/mes**
- ✅ Corrección: **5 capítulos/mes**

### ⭐ PREMIUM (Acceso Ilimitado)

#### Fase 1: Fundadores (1-1,000)
```
$5/mes  |  $50/año
🏆 Badge "Fundador" + Runa Othala
Precio bloqueado DE POR VIDA
```

#### Fase 2: Early Adopters (1,001-10,000)
```
$10/mes  |  $100/año
⭐ Badge "Early Adopter" + Runa Sowilo
Precio bloqueado DE POR VIDA
```

#### Fase 3: Regular (10,001+)
```
$19.99/mes  |  $199/año
✨ Badge "Premium"
Precio estándar
```

#### Cortesía (Admin)
```
GRATIS (sin expiración)
👑 Badge "Invitado Especial" + Runa Ansuz
Por partnerships, influencers, prensa
```

---

## 🔮 SISTEMA DE RUNAS (Elder Futhark)

### Concepto
Cada usuario **Premium** elige una runa nórdica que representa su identidad como creador.

### 24 Runas Disponibles (Categorías)

#### 🎨 Creatividad & Conocimiento
- **ᚲ Kenaz** - Creatividad, inspiración (MÁS POPULAR)
- **ᚨ Ansuz** - Sabiduría, palabra divina
- **ᛗ Mannaz** - Intelecto, el yo creador

#### 💪 Éxito & Logro
- **ᛊ Sowilo** - Éxito, victoria (RECOMENDADA)
- **ᛃ Jera** - Cosecha, recompensa
- **ᚹ Wunjo** - Alegría, perfección
- **ᛏ Tiwaz** - Honor, liderazgo

#### 🌱 Crecimiento & Transformación
- **ᛒ Berkano** - Nuevo comienzo
- **ᛞ Dagaz** - Despertar, iluminación
- **ᛁ Isa** - Concentración, enfoque

#### 🛡️ Protección & Fuerza
- **ᚦ Thurisaz** - Protección del trabajo
- **ᚢ Uruz** - Fuerza vital, resistencia
- **ᛉ Algiz** - Protección espiritual

*+ 10 runas más (Intuición, Legado, Colaboración, etc.)*

### Reglas
- ✅ Solo usuarios Premium
- ✅ Cambio: **1 vez al mes**
- ✅ Aparece en perfil, posts, comentarios

---

## 🏆 SISTEMA DE BADGES

| Badge | Símbolo | Para quién | Runa base |
|-------|---------|-----------|-----------|
| **Fundador** | 🏆 | Primeros 1,000 Premium | ᛟ Othala |
| **Early Adopter** | ⭐ | Usuarios 1,001-10,000 | ᛊ Sowilo |
| **Premium** | ✨ | Premium regular | Tu runa |
| **Invitado Especial** | 👑 | Cortesía admin | ᚨ Ansuz |
| **Verificado** | ✓ | Datos completos | - |
| **Certificado** | ⚡ | Imprenta aprobada | - |
| **Bestseller** | 📚 | Ventas destacadas | ᛃ Jera |
| **Prolífico** | ✍️ | Muchas obras | ᚲ Kenaz |

---

## 🖥️ PANEL DE ADMINISTRACIÓN

### Dashboard Principal
```
📊 Resumen
├─ Total usuarios: 1,234
├─ Free: 800 (64.8%)
└─ Premium: 434 (35.2%)
   ├─ Fase 1 ($5): 156 🏆
   ├─ Fase 2 ($10): 89 ⭐
   ├─ Regular ($19.99): 145 ✨
   └─ Cortesía: 44 👑

📈 Uso de IA (24h)
├─ Generaciones: 456
├─ Portadas: 1,234
└─ Asistente: 2,890

💰 Ingresos (MRR)
├─ MRR: $4,290
├─ Nuevos: 45
└─ Churn: 2.8%
```

### Acciones de Admin
- ✅ Otorgar Premium Cortesía (permanente/temporal)
- ✅ Modificar plan de usuario
- ✅ Cambiar fase manualmente
- ✅ Ver historial completo (uso, pagos, cambios)
- ✅ Gestionar fases (tracking de usuarios 1-1000, etc.)
- ✅ Suspender/activar usuarios
- ✅ Ver analytics completos

---

## 🏗️ ARQUITECTURA TÉCNICA

### Backend
```
Spring Boot 3.2.0
├─ Java 21
├─ PostgreSQL 14+
├─ JWT Auth (io.jsonwebtoken)
├─ Stripe Integration
└─ JPA/Hibernate
```

### Base de Datos
```
17 Tablas principales
├─ users, memberships, user_roles
├─ runes, badges, user_badges
├─ connections, messages
├─ ai_usage_tracking
└─ payment_transactions
```

### Frontend
```
HTML/CSS/JavaScript
├─ Responsive Design
├─ REST API Integration
└─ Framework moderno (React/Vue)
```

---

## 📊 ESTADO ACTUAL

### ✅ Completado (100%)
- [x] Arquitectura completa documentada
- [x] Esquema de base de datos (17 tablas)
- [x] Seeds con 24 runas + 8 badges
- [x] Triggers y funciones automáticas
- [x] Modelos Java (2/17)

### 🚧 En Progreso (20%)
- [ ] Modelos Java restantes (15/17)
- [ ] Repositorios JPA
- [ ] Services y Controllers
- [ ] Frontend

### ⏳ Pendiente (0%)
- [ ] Testing completo
- [ ] Deploy a producción
- [ ] Documentación API
- [ ] CI/CD

**Progreso general: 17%**

---

## 🎯 PRÓXIMOS HITOS

### Milestone 1: MVP Backend (6 semanas)
Base de datos + Autenticación + Membresías

### Milestone 2: Características Premium (6 semanas)
Runas + Badges + Roles + Límites IA

### Milestone 3: Comunidad (4 semanas)
Panel admin + Red social + Mensajería

### Milestone 4: Producción (6 semanas)
Frontend + Testing + Deploy

**Timeline total: ~22 semanas (5.5 meses)**

---

## 💰 PROYECCIÓN DE INGRESOS

### Escenario Conservador (Año 1)
```
Fundadores (1,000):    $5 × 1,000  = $5,000/mes
Early Adopters (5,000): $10 × 5,000 = $50,000/mes
Regular (2,000):        $20 × 2,000 = $40,000/mes
───────────────────────────────────────────────
MRR Año 1:                          = $95,000/mes
ARR Año 1:                          = $1,140,000/año
```

### Escenario Optimista (Año 2)
```
Fundadores (1,000):     $5 × 1,000   = $5,000/mes
Early Adopters (9,000): $10 × 9,000  = $90,000/mes
Regular (15,000):       $20 × 15,000 = $300,000/mes
────────────────────────────────────────────────
MRR Año 2:                           = $395,000/mes
ARR Año 2:                           = $4,740,000/año
```

*Nota: Usuarios grandfathered mantienen precio de por vida*

---

## 🔑 FACTORES CLAVE DE ÉXITO

### 1. Timing de Fases
- Crear urgencia en Fase 1 (solo 1,000 spots)
- Marketing agresivo pre-lanzamiento
- Early access para influencers (cortesía)

### 2. Valor Percibido
- Runas = identidad única y personalización
- Comunidad = networking real entre autores
- IA = herramienta, no producto principal

### 3. Retention
- Precio grandfathered = incentivo a quedarse
- Comunidad activa = sticky feature
- Marketing sinérgico = value add

### 4. Escalabilidad
- Arquitectura sólida desde día 1
- Límites de IA por plan (control de costos)
- Infraestructura cloud (AWS/Azure)

---

## 📞 CONTACTO

**Repositorio:** github.com/imageGeneratorZZ/DrakkarPress  
**Branch:** main  
**Fecha:** 11 de Noviembre, 2025

---

**DrakkarPress - Donde los escritores forjan su legado** ⚔️📚
