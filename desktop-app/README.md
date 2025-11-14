# 🖥️ DrakkarPress Desktop - Aplicación de Escritorio

Aplicación de escritorio multiplataforma para autores con inteligencia artificial integrada, gestión de biblioteca personal, control por voz, tienda y comunidad.

## 🚀 Características Principales

- ✍️ **Generadores de IA**: 25+ géneros combinables, recetas, informes técnicos
- 📚 **Biblioteca Personal**: Gestión completa de creaciones locales
- 🎤 **Control por Voz**: Comandos y dictado por micrófono
- 🛍️ **Tienda Personal**: Gestión de productos y ventas
- 👥 **Comunidad**: Chat en tiempo real y colaboración
- 💾 **Trabajo Offline**: Funciona sin conexión a Internet
- 🔄 **Sincronización Cloud**: Backup automático (opcional)
- 📊 **Estadísticas**: Analíticas de escritura y ventas

## 📋 Requisitos del Sistema

### Windows
- Windows 10/11 (64-bit)
- RAM: 4GB mínimo, 8GB recomendado
- Espacio en disco: 500MB + espacio para creaciones
- Conexión a Internet: Para sincronización (opcional)

### macOS
- macOS 10.14 (Mojave) o superior
- Procesador Intel o Apple Silicon (M1/M2)
- RAM: 4GB mínimo, 8GB recomendado

### Linux
- Ubuntu 18.04+ / Debian 10+ / Fedora 32+
- Procesador x64
- RAM: 4GB mínimo, 8GB recomendado

## 🛠️ Instalación para Desarrollo

### 1. Clonar el repositorio

```bash
git clone https://github.com/imageGeneratorZZ/DrakkarPress.git
cd DrakkarPress/desktop-app
```

### 2. Instalar dependencias

```bash
npm install
```

### 3. Variables de entorno

Crear archivo `.env` en la raíz:

```env
NODE_ENV=development
API_BASE_URL=http://localhost:8080/api
WS_URL=ws://localhost:8080/ws
```

### 4. Ejecutar en modo desarrollo

```bash
npm run dev
```

Esto iniciará:
- Main Process (Electron)
- Renderer Process (React + Vite en http://localhost:3000)
- Hot reload automático

## 📦 Compilar para Producción

### Compilar para Windows

```bash
npm run build:win
```

Esto generará:
- `release/DrakkarPress-Setup-1.0.0.exe` - Instalador NSIS
- `release/DrakkarPress-1.0.0-portable.exe` - Versión portable

### Compilar para todas las plataformas

```bash
npm run build:all
```

Esto generará instaladores para Windows, macOS y Linux.

## 🏗️ Estructura del Proyecto

```
desktop-app/
├── src/
│   ├── main/               # Main Process (Node.js)
│   │   ├── index.ts
│   │   ├── database/
│   │   ├── services/
│   │   └── ipc/
│   ├── renderer/           # Renderer Process (React)
│   │   ├── App.tsx
│   │   ├── components/
│   │   ├── hooks/
│   │   └── services/
│   ├── preload/            # Preload Scripts
│   │   └── index.ts
│   └── shared/             # Código compartido
│       └── types/
├── public/
│   └── icons/
├── dist/                   # Build output
└── release/                # Instaladores
```

## 🔧 Scripts Disponibles

```bash
# Desarrollo
npm run dev              # Modo desarrollo con hot reload
npm run dev:main         # Solo Main Process
npm run dev:renderer     # Solo Renderer Process

# Build
npm run build            # Compilar todo
npm run build:main       # Compilar Main Process
npm run build:renderer   # Compilar Renderer Process

# Distribución
npm run build:win        # Generar instalador Windows
npm run build:mac        # Generar instalador macOS
npm run build:linux      # Generar instalador Linux
npm run build:all        # Generar todos los instaladores

# Calidad
npm run lint             # Linting
npm test                 # Ejecutar tests
```

## 🎨 Tecnologías Utilizadas

### Frontend
- **Electron** - Framework de app de escritorio
- **React** - Librería UI
- **TypeScript** - Tipado estático
- **Vite** - Build tool rápido
- **Material-UI** - Componentes UI

### Backend Local
- **Node.js** - Runtime para Main Process
- **SQLite** - Base de datos local
- **Sequelize** - ORM
- **Electron IPC** - Comunicación entre procesos

### Funcionalidades
- **Web Speech API** - Reconocimiento de voz
- **WebSocket** - Chat en tiempo real
- **Axios** - Cliente HTTP
- **electron-updater** - Auto-actualizaciones

## 🔐 Seguridad

- Context Isolation habilitado
- Node Integration deshabilitado
- Preload script con API segura
- Comunicación IPC controlada
- Base de datos local encriptada (opcional)

## 📖 Documentación API

### Electron API (expuesta en window.electronAPI)

#### Creations API

```typescript
// Obtener todas las creaciones
const creations = await window.electronAPI.creations.getAll({
  type: 'book',
  favorite: true
});

// Crear nueva creación
const newCreation = await window.electronAPI.creations.create({
  title: 'Mi Libro',
  type: 'book',
  content: 'Contenido...',
  metadata: { genre: 'romance' },
  tags: 'romance,fiction'
});

// Actualizar creación
await window.electronAPI.creations.update(1, {
  title: 'Nuevo Título'
});

// Buscar
const results = await window.electronAPI.creations.search('romance');

// Exportar
await window.electronAPI.creations.export(1, 'pdf');

// Estadísticas
const stats = await window.electronAPI.creations.getStats();
```

#### Shop API

```typescript
// Obtener productos
const products = await window.electronAPI.shop.getAllProducts();

// Crear producto
const product = await window.electronAPI.shop.createProduct({
  creationId: 1,
  title: 'Mi Libro',
  price: 9.99,
  format: 'ebook'
});

// Publicar producto
await window.electronAPI.shop.publishProduct(1);

// Estadísticas de tienda
const stats = await window.electronAPI.shop.getStats();
```

#### Settings API

```typescript
// Obtener configuración
const theme = await window.electronAPI.settings.get('theme');

// Guardar configuración
await window.electronAPI.settings.set('theme', 'dark', 'string');

// Obtener todas las configuraciones
const allSettings = await window.electronAPI.settings.getAll();
```

## 🎤 Control por Voz

### Comandos Soportados

```
"Generar idea de libro de thriller"
"Crear personaje protagonista"
"Extender capítulo con más tensión"
"Buscar mis libros de romance"
"Abrir mi tienda"
"Dictar nuevo capítulo"
"Guardar creación"
"Exportar a PDF"
```

### Configuración

```typescript
// Habilitar control por voz
await window.electronAPI.settings.set('voiceEnabled', true, 'boolean');
await window.electronAPI.settings.set('voiceLanguage', 'es-ES', 'string');
```

## 🔄 Sincronización con Cloud

La aplicación puede sincronizar datos con el backend cloud de DrakkarPress:

```typescript
// Sincronización se maneja automáticamente
// Configurar auto-sync
await window.electronAPI.settings.set('autoSync', true, 'boolean');
```

## 🐛 Debug

### Abrir DevTools

En desarrollo, las DevTools se abren automáticamente. En producción:

- Windows/Linux: `Ctrl + Shift + I`
- macOS: `Cmd + Option + I`

### Logs

Los logs se guardan en:
- Windows: `%APPDATA%\drakkarpress-desktop\logs\`
- macOS: `~/Library/Logs/drakkarpress-desktop/`
- Linux: `~/.config/drakkarpress-desktop/logs/`

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama de feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📝 Licencia

Este proyecto es privado y pertenece a DrakkarPress.

## 👥 Soporte

- Email: support@drakkarpress.com
- Discord: [DrakkarPress Community](https://discord.gg/drakkarpress)
- Documentación: [docs.drakkarpress.com](https://docs.drakkarpress.com)

## 🗺️ Roadmap

### Versión 1.0 (MVP) - ✅ Actual
- [x] Generadores básicos
- [x] Biblioteca local
- [x] Exportación PDF/DOCX
- [x] Sincronización básica

### Versión 1.5 - 🚧 En Desarrollo
- [ ] Control por voz
- [ ] Gestión de tienda completa
- [ ] Chat comunitario
- [ ] Temas personalizables

### Versión 2.0 - 📅 Planeado
- [ ] Co-autoría colaborativa
- [ ] IA mejorada (GPT-4 Turbo)
- [ ] Generación de portadas con IA
- [ ] Integración con imprentas POD
- [ ] Marketplace integrado

---

**Desarrollado con ❤️ por el equipo DrakkarPress**
