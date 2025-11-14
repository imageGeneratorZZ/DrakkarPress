# 🖥️ Arquitectura de Aplicación de Escritorio DrakkarPress

## 📋 Resumen Ejecutivo

Aplicación de escritorio multiplataforma (Windows EXE principal) que permite a los usuarios:
- ✍️ Acceder a todos los generadores de IA
- 💾 Gestionar base de datos local de creaciones
- 🎤 Interactuar por voz
- 🛍️ Administrar tienda personal
- 👥 Conectarse con la comunidad
- 🔄 Sincronizar con plataforma cloud

## 🏗️ Stack Tecnológico

### Frontend (Renderer Process)
- **Electron**: Framework principal para app de escritorio
- **React** + TypeScript: UI moderna y reactiva
- **Material-UI / Ant Design**: Componentes profesionales
- **TailwindCSS**: Estilos rápidos
- **Recharts**: Visualización de estadísticas

### Backend Local (Main Process)
- **Node.js**: Proceso principal de Electron
- **SQLite3**: Base de datos local embebida
- **Sequelize**: ORM para manejo de BD
- **Express** (opcional): Server local para IPC complejo

### Funcionalidades Especiales
- **Web Speech API**: Reconocimiento de voz (navegador)
- **Google Speech-to-Text**: Opción avanzada (API key)
- **Electron IPC**: Comunicación entre procesos
- **WebSocket**: Chat en tiempo real con comunidad
- **REST API Client**: Conexión con backend Spring Boot

### Empaquetado
- **electron-builder**: Generación de instaladores
- **electron-updater**: Auto-actualizaciones
- **Certificado de firma**: Para Windows SmartScreen

## 📊 Arquitectura de Componentes

```
┌─────────────────────────────────────────────────────────┐
│                   ELECTRON APP                           │
│                                                          │
│  ┌────────────────────────────────────────────────┐     │
│  │         RENDERER PROCESS (UI - React)          │     │
│  │                                                 │     │
│  │  ┌──────────┐  ┌──────────┐  ┌─────────────┐  │     │
│  │  │Dashboard │  │Generators│  │  Mi Tienda  │  │     │
│  │  └──────────┘  └──────────┘  └─────────────┘  │     │
│  │                                                 │     │
│  │  ┌──────────┐  ┌──────────┐  ┌─────────────┐  │     │
│  │  │Biblioteca│  │  Voice   │  │  Community  │  │     │
│  │  │Creaciones│  │ Control  │  │    Chat     │  │     │
│  │  └──────────┘  └──────────┘  └─────────────┘  │     │
│  └────────────────────────────────────────────────┘     │
│                        ▲                                 │
│                        │ IPC                             │
│                        ▼                                 │
│  ┌────────────────────────────────────────────────┐     │
│  │          MAIN PROCESS (Node.js)                │     │
│  │                                                 │     │
│  │  ┌──────────────┐    ┌──────────────┐         │     │
│  │  │  SQLite DB   │    │  File System │         │     │
│  │  │   Manager    │    │   Handler    │         │     │
│  │  └──────────────┘    └──────────────┘         │     │
│  │                                                 │     │
│  │  ┌──────────────┐    ┌──────────────┐         │     │
│  │  │   Settings   │    │   Auto-      │         │     │
│  │  │   Manager    │    │   Updater    │         │     │
│  │  └──────────────┘    └──────────────┘         │     │
│  └────────────────────────────────────────────────┘     │
│                                                          │
└─────────────────────────────────────────────────────────┘
                        ▲
                        │ HTTPS/WSS
                        ▼
┌─────────────────────────────────────────────────────────┐
│              BACKEND CLOUD (Spring Boot)                 │
│                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  API REST    │  │  WebSocket   │  │  PostgreSQL  │  │
│  │  Generadores │  │  Chat/Events │  │   Database   │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
└─────────────────────────────────────────────────────────┘
```

## 🗄️ Modelo de Base de Datos Local (SQLite)

### Tabla: `creations`
```sql
CREATE TABLE creations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    uuid TEXT UNIQUE NOT NULL,
    title TEXT NOT NULL,
    type TEXT NOT NULL, -- 'book', 'recipe', 'report', 'character', etc.
    content TEXT,
    metadata JSON, -- géneros, palabras clave, erotismo level, etc.
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    synced BOOLEAN DEFAULT 0,
    cloud_id TEXT,
    tags TEXT, -- comma-separated
    favorite BOOLEAN DEFAULT 0,
    word_count INTEGER,
    genre_mix JSON,
    thumbnail_path TEXT
);
```

### Tabla: `generation_history`
```sql
CREATE TABLE generation_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creation_id INTEGER,
    generator_type TEXT,
    prompt TEXT,
    result TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    tokens_used INTEGER,
    generation_time_ms INTEGER,
    FOREIGN KEY (creation_id) REFERENCES creations(id)
);
```

### Tabla: `user_settings`
```sql
CREATE TABLE user_settings (
    key TEXT PRIMARY KEY,
    value TEXT,
    type TEXT, -- 'string', 'number', 'boolean', 'json'
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### Tabla: `shop_products`
```sql
CREATE TABLE shop_products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    creation_id INTEGER,
    title TEXT NOT NULL,
    description TEXT,
    price DECIMAL(10,2),
    currency TEXT DEFAULT 'USD',
    status TEXT DEFAULT 'draft', -- 'draft', 'active', 'sold', 'archived'
    cover_image TEXT,
    isbn TEXT,
    format TEXT, -- 'ebook', 'print', 'audio'
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    published_at DATETIME,
    sales_count INTEGER DEFAULT 0,
    FOREIGN KEY (creation_id) REFERENCES creations(id)
);
```

### Tabla: `community_messages` (caché local)
```sql
CREATE TABLE community_messages (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    message_id TEXT UNIQUE,
    user_id TEXT,
    username TEXT,
    avatar TEXT,
    message TEXT,
    timestamp DATETIME,
    channel TEXT,
    is_read BOOLEAN DEFAULT 0
);
```

## 🎯 Funcionalidades Principales

### 1. 📚 Biblioteca de Creaciones
- **Vista de cuadrícula/lista** con thumbnails
- **Filtros avanzados**: por tipo, género, fecha, tags, favoritos
- **Búsqueda full-text** en títulos y contenido
- **Exportación**: PDF, EPUB, DOCX, TXT
- **Estadísticas**: palabras totales, proyectos completados, géneros favoritos
- **Backup automático** configurable

### 2. ✨ Panel de Generadores
- Todos los generadores de `generators.html` integrados
- **Modo offline**: generaciones en caché para demostración
- **Modo online**: conexión con backend real
- **Historial de prompts** para reutilización
- **Plantillas personalizadas** guardadas localmente
- **Batch generation**: generar múltiples variaciones

### 3. 🎤 Control por Voz
```javascript
// Comandos soportados:
- "Generar idea de libro de thriller"
- "Crear personaje protagonista"
- "Extender capítulo con más tensión"
- "Buscar mis libros de romance"
- "Abrir mi tienda"
- "Dictar nuevo capítulo"
```

**Configuración**:
- Idioma: español, inglés, otros
- Sensibilidad del micrófono
- Comandos personalizados
- Atajos de voz
- Modo "siempre escuchando" o push-to-talk

### 4. 🛍️ Gestión de Tienda Personal

**Panel de Control**:
- Lista de productos publicados
- Estados: borrador, activo, vendido
- Estadísticas de venta
- Gestión de precios e inventario

**Publicación**:
- Subir desde biblioteca de creaciones
- Editar descripción, precio, categoría
- Generar portada automática (IA)
- Preview antes de publicar
- Integración con marketplace DrakkarPress
- Opción de venta externa (Amazon KDP, Lulu, etc.)

**Sincronización**:
- Sincronizar con tienda cloud
- Notificaciones de ventas
- Gestión de pedidos
- Informes de ingresos

### 5. 👥 Comunidad y Colaboración

**Chat en Tiempo Real**:
- Canales por género/tema
- Mensajes directos
- Compartir creaciones
- Feedback de comunidad

**Colaboración**:
- Compartir proyectos con otros usuarios
- Co-autoría de libros
- Beta readers
- Grupos de escritura

**Networking**:
- Perfil público/privado
- Seguir a otros autores
- Sistema de reputación
- Eventos y concursos

## 🔧 Configuración de Usuario

### Preferencias Generales
- **Tema**: Light/Dark/Auto
- **Idioma**: Español, Inglés, otros
- **Notificaciones**: Push, sonido, visual
- **Sincronización**: Manual/Automática
- **Backup**: Frecuencia y ubicación

### Preferencias de IA
- **Modelo preferido**: GPT-4, Claude, Gemini
- **Temperatura**: Creatividad vs Precisión
- **Longitud de respuestas**: Corta, Media, Larga
- **Estilo de escritura**: Formal, Casual, Poético
- **Filtros de contenido**: Nivel de erotismo permitido

### Preferencias de Voz
- **Idioma de reconocimiento**
- **Voz de síntesis** (TTS para leer contenido)
- **Comandos personalizados**
- **Atajos de teclado**

## 📦 Estructura del Proyecto

```
desktop-app/
├── package.json
├── electron-builder.yml
├── src/
│   ├── main/                    # Main Process
│   │   ├── index.ts
│   │   ├── database/
│   │   │   ├── connection.ts
│   │   │   ├── models.ts
│   │   │   └── migrations/
│   │   ├── services/
│   │   │   ├── creations.service.ts
│   │   │   ├── sync.service.ts
│   │   │   ├── shop.service.ts
│   │   │   └── community.service.ts
│   │   ├── ipc/
│   │   │   └── handlers.ts
│   │   └── utils/
│   │       ├── file-system.ts
│   │       └── auto-updater.ts
│   │
│   ├── renderer/                # Renderer Process (React)
│   │   ├── index.tsx
│   │   ├── App.tsx
│   │   ├── components/
│   │   │   ├── Dashboard/
│   │   │   ├── Generators/
│   │   │   ├── Library/
│   │   │   ├── Shop/
│   │   │   ├── Community/
│   │   │   ├── VoiceControl/
│   │   │   └── Settings/
│   │   ├── hooks/
│   │   │   ├── useVoiceCommands.ts
│   │   │   ├── useDatabase.ts
│   │   │   └── useWebSocket.ts
│   │   ├── services/
│   │   │   ├── api.service.ts
│   │   │   └── speech.service.ts
│   │   └── styles/
│   │       └── globals.css
│   │
│   ├── preload/                 # Preload Scripts
│   │   └── index.ts
│   │
│   └── shared/                  # Código compartido
│       ├── types/
│       └── constants/
│
├── public/
│   ├── icons/
│   └── assets/
│
└── dist/                        # Build output
    └── DrakkarPress-Setup.exe
```

## 🚀 Instalación y Distribución

### Requisitos del Sistema
- **Windows**: 10/11 (64-bit)
- **RAM**: Mínimo 4GB, recomendado 8GB
- **Espacio en disco**: 500MB + espacio para creaciones
- **Conexión a Internet**: Para sincronización (opcional)

### Proceso de Instalación
1. Descargar `DrakkarPress-Setup.exe`
2. Ejecutar instalador
3. Aceptar permisos (micrófono, red)
4. Crear cuenta o iniciar sesión
5. Configuración inicial (tema, preferencias)
6. Sincronizar datos existentes (opcional)

### Auto-actualizaciones
- Verificación automática al iniciar
- Descarga en segundo plano
- Instalación al reiniciar
- Changelog visible antes de actualizar

## 🔐 Seguridad y Privacidad

### Datos Locales
- Base de datos SQLite encriptada (opcional)
- Credenciales guardadas en keychain del SO
- Backup encriptado

### Comunicación
- HTTPS para API REST
- WSS (WebSocket Secure) para chat
- JWT tokens con refresh
- Rate limiting

### Privacidad
- Modo offline completo disponible
- Control de qué datos sincronizar
- Opción de cuenta anónima
- No tracking sin consentimiento

## 📊 Métricas y Analytics (Opcional)

### Analytics Locales
- Tiempo de uso
- Generadores más usados
- Palabras generadas
- Proyectos completados

### Analytics Cloud (Anónimo)
- Uso de funcionalidades
- Rendimiento de la app
- Errores y crashes
- Solo si el usuario acepta

## 🎨 Diseño Visual

### Tema Principal
- **Paleta**: Púrpura (#667eea) como color primario
- **Tipografía**: Inter, Roboto, SF Pro
- **Iconos**: Material Icons o Feather Icons
- **Animaciones**: Framer Motion para transiciones suaves

### Layouts
- **Dashboard**: Vista de tarjetas con stats
- **Biblioteca**: Grid masonry con portadas
- **Generadores**: Wizard step-by-step
- **Tienda**: Tabla de productos estilo e-commerce
- **Chat**: Sidebar con lista de conversaciones

## 🛠️ Comandos de Desarrollo

```bash
# Instalar dependencias
npm install

# Modo desarrollo
npm run dev

# Build para producción
npm run build

# Generar instalador Windows
npm run build:win

# Generar para todas las plataformas
npm run build:all

# Ejecutar tests
npm test

# Linting
npm run lint
```

## 📈 Roadmap

### Versión 1.0 (MVP)
- ✅ Generadores básicos
- ✅ Biblioteca local
- ✅ Exportación PDF/DOCX
- ✅ Sincronización básica

### Versión 1.5
- ✅ Control por voz
- ✅ Gestión de tienda
- ✅ Chat comunitario
- ✅ Temas personalizables

### Versión 2.0
- ✅ Co-autoría colaborativa
- ✅ IA mejorada (GPT-4 Turbo)
- ✅ Generación de portadas con IA
- ✅ Integración con imprentas POD
- ✅ Marketplace integrado

### Versión 2.5
- ✅ Plugin system para extensiones
- ✅ Asistente de escritura en tiempo real
- ✅ Análisis de mercado y tendencias
- ✅ Herramientas de marketing

## 🤝 Integración con Ecosistema DrakkarPress

### Conexión con Backend
```typescript
// Endpoints principales
const API_ENDPOINTS = {
  auth: '/api/auth',
  generators: '/api/ai',
  library: '/api/library',
  shop: '/api/shop',
  community: '/api/community',
  sync: '/api/sync'
};

// WebSocket para tiempo real
const WS_URL = 'wss://api.drakkarpress.com/ws';
```

### Sincronización
- **Bidireccional**: Local ↔ Cloud
- **Resolución de conflictos**: Last-write-wins o manual
- **Offline-first**: Funciona sin conexión
- **Delta sync**: Solo cambios incrementales

## 💡 Casos de Uso

### Usuario Casual
1. Abre la app
2. Genera ideas con voz: "Genera una idea de romance"
3. Guarda la que le gusta
4. Exporta a PDF
5. Comparte en comunidad

### Autor Profesional
1. Gestiona biblioteca de 20+ proyectos
2. Usa generadores para superar bloqueos
3. Sincroniza con varios dispositivos
4. Publica en tienda personal
5. Analiza estadísticas de ventas
6. Colabora con beta readers

### Autor de Recetas
1. Genera recetas con IA
2. Organiza en colecciones temáticas
3. Exporta libro de cocina completo
4. Añade fotos personales
5. Publica en formato digital e impreso

## 🎁 Valor Añadido vs Versión Web

| Característica | Web | Desktop |
|----------------|-----|---------|
| Generadores IA | ✅ | ✅ |
| Biblioteca Personal | ❌ | ✅ |
| Trabajo Offline | ❌ | ✅ |
| Control por Voz | ❌ | ✅ |
| Gestión Tienda | Básica | Avanzada |
| Sincronización | - | ✅ |
| Exportación Avanzada | ❌ | ✅ |
| Chat Comunidad | Básico | Completo |
| Notificaciones Push | ❌ | ✅ |
| Backup Automático | ❌ | ✅ |

## 📞 Soporte y Documentación

- **Wiki**: docs.drakkarpress.com/desktop
- **Video tutoriales**: YouTube channel
- **FAQ**: Integrada en la app
- **Soporte**: Ticket system + Discord
- **Actualizaciones**: Blog de release notes

---

**Última actualización**: 13 de Noviembre de 2025
**Versión del documento**: 1.0
**Autor**: Equipo DrakkarPress
