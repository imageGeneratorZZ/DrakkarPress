# Arquitectura del Ecosistema DrakkarPress

**Fecha de actualización:** 11 de Noviembre, 2025  
**Versión:** 2.0 - Sistema de Perfiles Multi-rol con Runas

---

## 🎯 Visión General

**DrakkarPress** es una plataforma comunitaria para escritores, editoriales, imprentas y revendedores, que combina:
- **Red social especializada** estilo "Facebook de escritores"
- **Herramientas de IA** para generación de contenido literario
- **Marketplace** con marketing sinérgico
- **Sistema de membresías** escalonado por fases de lanzamiento

---

## 🏗️ Componentes del Ecosistema

### 1️⃣ **DrakkarPress.com** (Plataforma Principal)

#### A. Comunidad & Networking
- Perfiles públicos personalizables
- Sistema de conexiones/seguidores
- Feed de actividad comunitario
- Mensajería interna
- Grupos por género/interés
- Marketing sinérgico entre usuarios

#### B. Generadores de IA (Mismo Dominio)

**DrakkarPress Generator** (Generación completa - Adultos/General)
- Novelas y libros de todos los géneros excepto infantil
- Corrección y edición con IA
- Generación de portadas
- Asistente de escritura

**Scriptorium Generator** (Especializado en Infantil)
- Libros para niños
- Ilustraciones apropiadas
- Contenido educativo
- Cuentos personalizables

### 2️⃣ **ODRBrand** (Servicios de Marketing - Separado)

- Servicios de marketing profesional para la comunidad DrakkarPress
- **Desarrollador de Perfiles** (herramienta interna exclusiva de ODRBrand)
- **Pick My Van** (proyecto separado de ODRBrand)

---

## 👥 Sistema de Perfiles Multi-rol

### Filosofía de Diseño
**Todos empiezan como CLIENTE** y pueden activar roles adicionales según necesidad.

### 4 Tipos de Perfiles

#### 1. 👤 **CLIENTE** (Base - Obligatorio)
**Todos los usuarios tienen este perfil mínimo**

**Funcionalidades:**
- Comprar libros de otros usuarios
- Acceso a generadores IA (según plan)
- Participar en comunidad
- Perfil público básico

**Sin verificación requerida** (solo email + contraseña)

---

#### 2. ✍️ **AUTOR/EDITORIAL** (Opcional - Para quien publica)

**Aplica a:**
- Autores individuales que publican sus propias obras
- Editoriales (empresas) que publican obras de terceros

**Campo diferenciador:**
- Checkbox: "Soy una editorial/empresa"
  - ✅ Marcado → Pide razón social, logo, equipo
  - ❌ Sin marcar → Autor individual

**Funcionalidades:**
- Portfolio/Catálogo de obras publicadas
- Vender libros propios o de terceros
- Buscar servicios de impresión
- Networking con otros autores/editoriales
- Marketing sinérgico destacado

**Verificación requerida para vender:**
- ✅ Método de pago (PayPal, Stripe, transferencia)
- ✅ Datos bancarios / cuenta de cobro
- ✅ Datos fiscales (RFC/NIT/Tax ID)
- ✅ Dirección fiscal
- ✅ Si es empresa: Razón social + documentos legales

**Beneficios:**
- Badge "Verificado" ✓ al completar datos
- Aparece en búsquedas de autores/editoriales
- Puede recibir comisiones de revendedores

---

#### 3. 🏭 **IMPRENTA** (Opcional - Proveedores de servicios)

**Funcionalidades:**
- Ofrecer servicios de impresión bajo demanda
- Mostrar catálogo de capacidades (formatos, acabados, tiempos)
- Recibir pedidos de autores/editoriales
- Portfolio de trabajos anteriores

**Certificación obligatoria:**
- ✅ Licencia comercial / Registro empresarial
- ✅ Certificaciones de calidad (ISO, etc.) *opcional pero recomendado*
- ✅ Capacidad de producción documentada
- ✅ Muestras de trabajos anteriores (fotos/PDF)
- ✅ Seguros / garantías

**Información para pagos:**
- ✅ Datos bancarios
- ✅ Datos fiscales
- ✅ Tarifas por servicio

**Beneficios:**
- Badge "Certificado" ⚡
- Aparece en directorio de imprentas
- Marketing destacado en búsquedas

---

#### 4. 🤝 **REVENDEDOR** (Opcional - Distribuidores)

**Funcionalidades:**
- Revender libros de autores/editoriales
- Gestionar comisiones automáticas
- Catálogo personalizado de distribución
- Red de ventas geográfica

**Verificación requerida:**
- ✅ Datos bancarios para recibir comisiones
- ✅ Datos fiscales
- ✅ Modelo de comisión preferido (%)
- ✅ Zona/región de operación
- ✅ Referencias comerciales (opcional)
- ✅ Volumen estimado de ventas

**Beneficios:**
- Badge "Verificado" ✓
- Acceso a API de distribución
- Dashboard de comisiones

---

## ⭐ Sistema de Membresías y Pricing

### Filosofía: Restricciones en Uso de IA

**Free** tiene acceso limitado a IA  
**Premium** tiene acceso ilimitado + beneficios extra

---

### 🆓 **PLAN GRATUITO** (Todos los perfiles)

**Límites de IA:**
| Función | Límite Mensual |
|---------|----------------|
| Generación completa de libros | ❌ **Bloqueado** |
| Portadas con IA | ✅ **3/mes** |
| Asistente de escritura | ✅ **10 consultas/mes** |
| Corrección de texto | ✅ **5 capítulos/mes** |

**Acceso completo:**
- ✅ Comunidad y networking
- ✅ Perfil público básico
- ✅ Compra/venta de libros
- ✅ Todos los roles disponibles

---

### ⭐ **PLAN PREMIUM** (Membresía Paga)

**IA sin límites:**
| Función | Límite |
|---------|--------|
| Generación completa de libros | ✅ **Ilimitada** |
| Portadas con IA | ✅ **Ilimitadas** |
| Asistente de escritura | ✅ **Ilimitado** |
| Corrección de texto | ✅ **Ilimitada** |
| Generación de series | ✅ **Ilimitada** |
| Traducción automática | ✅ **Incluida** |

**Beneficios exclusivos:**
- 🎯 Badge Premium con runa personalizada
- 📊 Analytics avanzados de tus obras
- 🚀 Prioridad en networking (perfil destacado)
- 💼 Marketing sinérgico con mayor visibilidad
- 🎨 Plantillas premium exclusivas
- 🔧 Acceso anticipado a nuevas funciones

---

### 💰 **Estrategia de Pricing por Fases**

#### 🚀 **FASE 1: Fundadores** (Usuarios 1 - 1,000)
```
Mensual:  $5 USD
Anual:    $50 USD (equivalente a 10 meses)

✨ Beneficios especiales:
- Badge "Fundador" 🏆 + Runa Othala (ᛟ) + Tu runa personal
- Precio bloqueado de POR VIDA (grandfathered)
- Reconocimiento especial en toda la plataforma
- Voz prioritaria en desarrollo de features
```

#### 📈 **FASE 2: Early Adopters** (Usuarios 1,001 - 10,000)
```
Mensual:  $10 USD
Anual:    $100 USD

✨ Beneficios especiales:
- Badge "Early Adopter" ⭐ + Runa Sowilo (ᛊ) + Tu runa personal
- Precio bloqueado de POR VIDA (grandfathered)
- Reconocimiento como usuario temprano
```

#### 💼 **FASE 3: Precio Regular** (Usuario 10,001+)
```
Mensual:  $19.99 USD
Anual:    $199 USD (ahorro 17%)

✨ Beneficios:
- Badge "Premium" ✨ + Tu runa personal
- Precio estándar (puede cambiar en el futuro)
```

#### 🎁 **ACCESO CORTESÍA** (Admin)
```
Gratuito (otorgado manualmente por admin)

✨ Características:
- Badge "Invitado Especial" 👑 + Runa Ansuz (ᚨ) + Tu runa personal
- Premium completo sin costo
- Sin fecha de expiración
- Razón documentada: partnerships, influencers, prensa, etc.
```

---

## 🔮 Sistema de Runas y Badges

### Concepto
Cada usuario **Premium** elige una runa del Futhark Antiguo que representa su identidad/valores como creador.

### Elder Futhark - 24 Runas Disponibles

#### 🎨 **CREATIVIDAD & CONOCIMIENTO**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᚲ | Kenaz | Creatividad, inspiración, luz interior | ✨ MÁS POPULAR |
| ᚨ | Ansuz | Sabiduría, comunicación, palabra divina | 📖 Recomendada |
| ᛗ | Mannaz | Intelecto, el yo creador, humanidad | 🧠 |

#### 💪 **ÉXITO & LOGRO**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᛊ | Sowilo | Éxito, victoria, poder solar | ⚡ RECOMENDADA |
| ᛃ | Jera | Cosecha, recompensa del esfuerzo, ciclos | 🌾 |
| ᚹ | Wunjo | Alegría, perfección, éxito alcanzado | 😊 |
| ᛏ | Tiwaz | Honor, victoria justa, liderazgo | ⚔️ |

#### 🌱 **CRECIMIENTO & TRANSFORMACIÓN**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᛒ | Berkano | Nuevo comienzo, crecimiento, renacimiento | 🌸 |
| ᛞ | Dagaz | Despertar, transformación, iluminación | ☀️ |
| ᛁ | Isa | Concentración, enfoque, cristalización | ❄️ |

#### 🛡️ **PROTECCIÓN & FUERZA**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᚦ | Thurisaz | Protección del trabajo, defensa | 🛡️ |
| ᚢ | Uruz | Fuerza vital, resistencia, poder primitivo | 💪 |
| ᛉ | Algiz | Protección espiritual, conexión divina | 🙏 |

#### 🌊 **INTUICIÓN & MISTERIO**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᛚ | Laguz | Intuición, flujo creativo, lo oculto | 🌊 |
| ᛈ | Perthro | Misterio, destino, secretos revelados | 🎲 |

#### 🏠 **LEGADO & ABUNDANCIA**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᛟ | Othala | Herencia, legado, patrimonio ancestral | 🏛️ |
| ᚠ | Fehu | Abundancia, riqueza, prosperidad | 💰 |

#### 🤝 **COLABORACIÓN & PROGRESO**
| Runa | Nombre | Significado | Uso |
|------|--------|-------------|-----|
| ᛖ | Ehwaz | Progreso, colaboración, asociación | 🤝 |
| ᚷ | Gebo | Intercambio, generosidad, reciprocidad | 🎁 |
| ᚱ | Raidho | Viaje, movimiento, camino del héroe | 🛤️ |

---

### Reglas de Runas
- ✅ Solo usuarios **Premium** pueden elegir runa
- ✅ Cambio permitido: **1 vez al mes**
- ✅ Tooltip con significado al hacer hover
- ✅ Aparece en perfil, comentarios, posts, feed

---

### Sistema de Badges Completo

| Badge | Símbolo | Quién lo recibe | Significado Runa Base |
|-------|---------|-----------------|----------------------|
| **Fundador** 🏆 | ᛟ + [Tu runa] | Primeros 1,000 usuarios Premium | Othala (legado fundacional) |
| **Early Adopter** ⭐ | ᛊ + [Tu runa] | Usuarios 1,001-10,000 Premium | Sowilo (éxito temprano) |
| **Premium** ✨ | [Tu runa] | Usuario Premium regular | Tu runa elegida |
| **Invitado Especial** 👑 | ᚨ + [Tu runa] | Cortesía de admin | Ansuz (sabiduría divina) |
| **Verificado** ✓ | ✓ | Datos de pago/fiscales completos | - |
| **Certificado** ⚡ | ⚡ | Imprentas con documentación aprobada | - |

---

### Visualización en Perfil

```
┌─────────────────────────────────────────┐
│  Ana García  ᚲ                          │ ← Runa personal (Kenaz)
│  Autora · Premium                       │
│  🏆 Fundadora ᛟ                         │ ← Badge + Runa del badge
│  ✓ Verificada                           │
│  Miembro desde Noviembre 2025          │
│                                         │
│  "Escribo fantasía épica y romance"    │
│                                         │
│  📚 15 obras publicadas                 │
│  👥 1,234 conexiones                    │
│  ⭐ 4.8/5 (892 reseñas)                 │
└─────────────────────────────────────────┘
```

---

## 🔧 Panel de Administración

### Dashboard Principal

```
╔══════════════════════════════════════════════════════════╗
║  📊 DRAKKARPRESS - PANEL DE ADMINISTRACIÓN              ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  📈 RESUMEN GENERAL                                      ║
║  ┌────────────────────────────────────────────────┐    ║
║  │ Total usuarios: 1,234                          │    ║
║  │                                                 │    ║
║  │ Por plan:                                       │    ║
║  │   Free: 800 (64.8%)                            │    ║
║  │   Premium: 434 (35.2%)                         │    ║
║  │     ├─ Fase 1 ($5): 156 🏆                     │    ║
║  │     ├─ Fase 2 ($10): 89 ⭐                     │    ║
║  │     ├─ Regular ($19.99): 145 ✨                │    ║
║  │     └─ Cortesía: 44 👑                         │    ║
║  │                                                 │    ║
║  │ Por rol activo:                                 │    ║
║  │   Solo Cliente: 600                            │    ║
║  │   Autor/Editorial: 450                         │    ║
║  │   Imprenta: 89                                 │    ║
║  │   Revendedor: 95                               │    ║
║  └────────────────────────────────────────────────┘    ║
║                                                          ║
║  📊 USO DE IA (Últimas 24h)                             ║
║  ┌────────────────────────────────────────────────┐    ║
║  │ Generaciones completas: 456                    │    ║
║  │ Portadas: 1,234                                │    ║
║  │ Asistente: 2,890 consultas                     │    ║
║  │ Correcciones: 678 capítulos                    │    ║
║  └────────────────────────────────────────────────┘    ║
║                                                          ║
║  💰 INGRESOS (Mes actual)                               ║
║  ┌────────────────────────────────────────────────┐    ║
║  │ MRR: $4,290 USD                                │    ║
║  │ Nuevos suscriptores: 45                        │    ║
║  │ Cancelaciones: 12                              │    ║
║  │ Churn rate: 2.8%                               │    ║
║  └────────────────────────────────────────────────┘    ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

### Gestión de Usuarios

```
╔══════════════════════════════════════════════════════════╗
║  👥 GESTIÓN DE USUARIOS                                  ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  🔍 Buscar: [email/nombre/ID] [Buscar]                  ║
║                                                          ║
║  Filtros:                                                ║
║  [ Plan ▼ ] [ Rol ▼ ] [ Badge ▼ ] [ Estado ▼ ]        ║
║                                                          ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  TABLA DE USUARIOS:                                      ║
║                                                          ║
║  Email             │ Nombre   │ Plan  │ Fase │ Roles    ║
║  ──────────────────┼──────────┼───────┼──────┼─────────║
║  ana@mail.com      │ Ana G.   │ Premium│ 1 🏆│ Autor   ║
║                    │          │ $5/mes │      │ ✓       ║
║  [Ver] [Editar] [💎 Otorgar Premium] [❌ Suspender]    ║
║  ──────────────────┼──────────┼───────┼──────┼─────────║
║  juan@mail.com     │ Juan P.  │ Free  │  -   │ Cliente ║
║                    │          │       │      │         ║
║  [Ver] [Editar] [💎 Otorgar Premium] [❌ Suspender]    ║
║  ──────────────────┼──────────┼───────┼──────┼─────────║
║  editorial@mx.com  │ Ed. MX   │ Premium│ 2 ⭐│ Editoria║
║                    │          │ $10/mes│      │ ✓       ║
║  [Ver] [Editar] [💎 Modificar Plan] [❌ Suspender]     ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

### Acciones de Admin

#### 1. **Otorgar Premium Cortesía**
```
╔══════════════════════════════════════════════╗
║  💎 OTORGAR PREMIUM CORTESÍA                 ║
╠══════════════════════════════════════════════╣
║                                              ║
║  Usuario: juan@mail.com (Juan Pérez)        ║
║                                              ║
║  Duración:                                   ║
║  ○ Permanente                                ║
║  ○ Temporal                                  ║
║    └─ Fecha fin: [___________] 📅           ║
║                                              ║
║  Razón (obligatoria):                        ║
║  ┌────────────────────────────────────────┐ ║
║  │ Partnership con influencer              │ ║
║  │                                         │ ║
║  └────────────────────────────────────────┘ ║
║                                              ║
║  Badge: 👑 Invitado Especial ᚨ              ║
║                                              ║
║  [Confirmar] [Cancelar]                     ║
║                                              ║
╚══════════════════════════════════════════════╝
```

#### 2. **Modificar Plan**
```
╔══════════════════════════════════════════════╗
║  ⚙️ MODIFICAR PLAN DE USUARIO                ║
╠══════════════════════════════════════════════╣
║                                              ║
║  Usuario: ana@mail.com (Ana García)         ║
║  Plan actual: Premium Fase 1 ($5/mes) 🏆   ║
║                                              ║
║  Cambiar a:                                  ║
║  ○ Free                                      ║
║  ○ Premium Fase 1 ($5) 🏆 [ACTUAL]         ║
║  ○ Premium Fase 2 ($10) ⭐                  ║
║  ○ Premium Regular ($19.99) ✨              ║
║  ○ Cortesía 👑                              ║
║                                              ║
║  ⚠️ Nota: Cambiar fase puede afectar        ║
║     el precio grandfathered del usuario     ║
║                                              ║
║  Razón del cambio:                           ║
║  ┌────────────────────────────────────────┐ ║
║  │ [descripción...]                        │ ║
║  └────────────────────────────────────────┘ ║
║                                              ║
║  [Confirmar] [Cancelar]                     ║
║                                              ║
╚══════════════════════════════════════════════╝
```

#### 3. **Ver Historial de Usuario**
```
╔══════════════════════════════════════════════════════════╗
║  📋 HISTORIAL: ana@mail.com (Ana García)                 ║
╠══════════════════════════════════════════════════════════╣
║                                                          ║
║  📊 USO DE IA (Mes actual)                              ║
║  ─────────────────────────────────────────────────────  ║
║  Generaciones completas: 87 (Ilimitado ✓)              ║
║  Portadas: 145 (Ilimitado ✓)                           ║
║  Asistente: 890 consultas (Ilimitado ✓)                ║
║  Correcciones: 234 capítulos (Ilimitado ✓)             ║
║                                                          ║
║  💰 PAGOS REALIZADOS                                     ║
║  ─────────────────────────────────────────────────────  ║
║  • Nov 2025: $5.00 USD - Premium Fase 1                ║
║  • Oct 2025: $5.00 USD - Premium Fase 1                ║
║  • Sep 2025: $5.00 USD - Premium Fase 1                ║
║  • Ago 2025: $5.00 USD - Premium Fase 1 (FIRST)        ║
║                                                          ║
║  Total pagado: $20.00 USD                               ║
║  LTV proyectado: $300.00 USD (5 años estimado)         ║
║                                                          ║
║  📝 CAMBIOS DE PLAN                                      ║
║  ─────────────────────────────────────────────────────  ║
║  • Ago 15, 2025: Free → Premium Fase 1                 ║
║  • Ago 01, 2025: Registro (Free)                       ║
║                                                          ║
║  ✍️ ACTIVIDAD                                            ║
║  ─────────────────────────────────────────────────────  ║
║  • Obras publicadas: 15                                 ║
║  • Conexiones: 1,234                                    ║
║  • Ventas totales: $2,890 USD                           ║
║  • Calificación promedio: ⭐ 4.8/5 (892 reviews)       ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
```

#### 4. **Gestión de Fases**
```
╔══════════════════════════════════════════════╗
║  🎯 GESTIÓN DE FASES DE LANZAMIENTO          ║
╠══════════════════════════════════════════════╣
║                                              ║
║  📊 ESTADO ACTUAL                            ║
║                                              ║
║  Usuario actual: #1,234                     ║
║                                              ║
║  ┌──────────────────────────────────────┐   ║
║  │ FASE 1: Fundadores (1-1,000)         │   ║
║  │ ████████████████████ 100% (1,000)    │   ║
║  │ $5/mes · ✅ COMPLETADA               │   ║
║  └──────────────────────────────────────┘   ║
║                                              ║
║  ┌──────────────────────────────────────┐   ║
║  │ FASE 2: Early Adopters (1,001-10k)   │   ║
║  │ ██░░░░░░░░░░░░░░░░░░ 2.6% (234)      │   ║
║  │ $10/mes · 🔄 EN PROGRESO             │   ║
║  └──────────────────────────────────────┘   ║
║                                              ║
║  ┌──────────────────────────────────────┐   ║
║  │ FASE 3: Regular (10,001+)            │   ║
║  │ ░░░░░░░░░░░░░░░░░░░░ 0% (0)          │   ║
║  │ $19.99/mes · ⏳ PENDIENTE            │   ║
║  └──────────────────────────────────────┘   ║
║                                              ║
║  ⚙️ ACCIONES                                ║
║  [ Forzar cambio de fase ]                  ║
║  [ Ver lista de usuarios por fase ]         ║
║  [ Exportar grandfathered users ]           ║
║                                              ║
╚══════════════════════════════════════════════╝
```

---

## 🗄️ Arquitectura Técnica

### Stack Tecnológico

**Backend:**
- Java Spring Boot (ya existente en `/backend`)
- PostgreSQL (base de datos principal)
- Redis (caché y sesiones)
- JWT para autenticación

**Frontend:**
- HTML/CSS/JavaScript (ya existente)
- Framework moderno a implementar (React/Vue sugerido)
- Responsive design

**Servicios Externos:**
- Stripe/PayPal para pagos
- AWS S3 / Cloudinary para imágenes
- OpenAI API para generadores IA
- SendGrid para emails

**DevOps:**
- Docker + docker-compose (ya existente)
- GitHub Actions para CI/CD
- Netlify/Vercel para frontend estático

---

### Estructura de Directorios

```
DrakkarPress.com/
├── backend/                    # API Spring Boot
│   ├── src/main/java/
│   │   └── com/drakkarpress/
│   │       ├── auth/          # Autenticación y seguridad
│   │       ├── users/         # Gestión de usuarios
│   │       ├── profiles/      # Sistema de perfiles multi-rol
│   │       ├── memberships/   # Membresías y pagos
│   │       ├── runes/         # Sistema de runas y badges
│   │       ├── ai/            # Limitadores y tracking de IA
│   │       ├── networking/    # Social features
│   │       ├── admin/         # Panel de administración
│   │       └── marketplace/   # Compra/venta de libros
│   ├── pom.xml
│   └── docker-compose.yml
│
├── frontend/                   # UI/UX
│   ├── public/
│   │   ├── index.html
│   │   ├── register.html
│   │   ├── login.html
│   │   └── assets/
│   ├── src/
│   │   ├── components/
│   │   │   ├── profiles/      # Componentes de perfil
│   │   │   ├── runes/         # Selector y display de runas
│   │   │   ├── admin/         # Panel de admin
│   │   │   └── networking/    # Features sociales
│   │   ├── pages/
│   │   └── utils/
│   └── js/
│
├── database/
│   ├── schema.sql             # Esquema completo de BD
│   ├── seeds/
│   │   ├── runes.sql          # 24 runas del Futhark
│   │   └── badges.sql         # Badges del sistema
│   └── migrations/
│
└── docs/
    ├── API.md                 # Documentación de endpoints
    ├── DEPLOYMENT.md          # Guía de deploy
    └── ADMIN_GUIDE.md         # Manual de admin
```

---

## 🔐 Seguridad y Privacidad

### Autenticación
- JWT tokens con refresh tokens
- Sesiones con expiración configurable
- 2FA opcional para cuentas Premium/Admin

### Datos Sensibles
- Datos fiscales encriptados en BD
- PCI compliance para pagos (delegado a Stripe/PayPal)
- Logs de acceso a datos sensibles
- GDPR compliance (export/delete de datos)

### Roles y Permisos
```
ADMIN:
  - Gestión completa de usuarios
  - Modificación de planes/fases
  - Acceso a analytics completos
  - Otorgar premium cortesía

USUARIO:
  - Gestión de su propio perfil
  - Activación/desactivación de roles
  - Selección de runa (si Premium)
  
PÚBLICO:
  - Ver perfiles públicos
  - Búsqueda de autores/editoriales/imprentas
  - Catálogo de libros
```

---

## 🚀 Roadmap de Implementación

### Fase Alpha (MVP - Semanas 1-4)
- [x] Arquitectura y documentación
- [ ] Base de datos y modelos
- [ ] Sistema de autenticación básico
- [ ] Registro y login
- [ ] Perfiles multi-rol (CRUD básico)
- [ ] Sistema de membresías (lógica de fases)

### Fase Beta (Semanas 5-8)
- [ ] Sistema de runas completo
- [ ] Badges automáticos
- [ ] Panel de administración básico
- [ ] Integración de pagos (Stripe)
- [ ] Limitadores de IA
- [ ] Perfiles públicos (frontend)

### Fase Gamma (Semanas 9-12)
- [ ] Sistema de networking (conexiones/seguidores)
- [ ] Feed de actividad
- [ ] Mensajería interna
- [ ] Marketplace de libros
- [ ] Marketing sinérgico

### Fase Release (Semanas 13-16)
- [ ] Testing completo
- [ ] Optimización de performance
- [ ] Documentación de API
- [ ] Guía de usuario
- [ ] Deploy a producción
- [ ] 🎉 Lanzamiento público

---

## 📊 Métricas Clave (KPIs)

### Crecimiento
- Usuarios totales
- Usuarios activos diarios/mensuales (DAU/MAU)
- Tasa de conversión Free → Premium
- Retención por cohortes

### Monetización
- MRR (Monthly Recurring Revenue)
- ARR (Annual Recurring Revenue)
- LTV (Lifetime Value) por fase
- Churn rate
- ARPU (Average Revenue Per User)

### Engagement
- Uso de IA por tipo de función
- Libros generados/publicados
- Conexiones creadas
- Mensajes enviados
- Grupos activos

### Calidad
- Tiempo de respuesta de API
- Uptime
- Errores reportados
- Satisfacción del usuario (NPS)

---

## 🆘 Soporte y Escalabilidad

### Canales de Soporte
- Email: support@drakkarpress.com
- Chat en vivo para Premium
- Centro de ayuda / FAQ
- Comunidad en Discord (opcional)

### Escalabilidad
- Arquitectura de microservicios (futuro)
- CDN para assets estáticos
- Load balancing para backend
- Sharding de base de datos por geografía
- Caché agresivo con Redis

---

## 📝 Notas Importantes

1. **Grandfathering:** Los usuarios de Fase 1 y 2 mantienen su precio de POR VIDA
2. **Verificación:** Solo se requiere para roles que venden/ofrecen servicios
3. **Runas:** Exclusivas para Premium, cambio limitado a 1/mes
4. **Admin:** Puede otorgar Premium cortesía sin límite (documentando razón)
5. **IA:** Límites se resetean el 1ro de cada mes a las 00:00 UTC

---

## 🔗 Relación con Otros Proyectos

### ODRBrand
- Proveedor de servicios de marketing para DrakkarPress
- Proyectos independientes:
  - **Desarrollador de Perfiles:** Herramienta interna de ODRBrand
  - **Pick My Van:** App separada de logística

### Scriptorium (Dentro de DrakkarPress)
- Generador especializado en libros infantiles
- Mismo dominio, misma base de usuarios
- Comparte sistema de membresías y runas

---

**Documento vivo - Se actualiza según evolución del proyecto**

---

*DrakkarPress - Donde los escritores forjan su legado* ⚔️📚
