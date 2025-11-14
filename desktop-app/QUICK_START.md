# 🎯 GUÍA DE INICIO RÁPIDO

## Paso 1: Instalar Dependencias

```powershell
cd desktop-app
npm install
```

## Paso 2: Configurar Variables de Entorno

```powershell
Copy-Item .env.example .env
```

## Paso 3: Ejecutar en Desarrollo

```powershell
npm run dev
```

## Paso 4: Compilar EXE para Windows

```powershell
npm run build:win
```

El instalador estará en: `desktop-app/release/DrakkarPress-Setup-1.0.0.exe`

---

## 🎤 Características Clave de la App Desktop

### 1. **Biblioteca Personal**
- Base de datos SQLite local
- Guarda todas tus creaciones offline
- Búsqueda avanzada por título, contenido, tags
- Filtros por tipo, género, favoritos
- Estadísticas de escritura

### 2. **Control por Voz**
- Comandos de voz en español
- Dictado de texto
- Generación por voz
- Push-to-talk o modo siempre activo

### 3. **Gestión de Tienda**
- Catalogo de productos
- Precios e inventario
- Estados: borrador, activo, vendido
- Estadísticas de ventas
- Publicación en marketplace

### 4. **Comunidad**
- Chat en tiempo real
- Compartir creaciones
- Mensajes directos
- Notificaciones push

### 5. **Sincronización Cloud**
- Backup automático
- Sincronización multi-dispositivo
- Resolución de conflictos
- Modo offline-first

---

## 📊 Estructura de Base de Datos Local

La app crea una base de datos SQLite en:
- `%APPDATA%\drakkarpress-desktop\drakkarpress.db` (Windows)

Tablas principales:
- `creations` - Libros, recetas, informes, etc.
- `generation_history` - Historial de generaciones
- `shop_products` - Productos de tu tienda
- `user_settings` - Configuración personalizada
- `community_messages` - Caché de mensajes

---

## 🔧 Próximos Pasos

1. **Implementar UI de React** en `src/renderer/`
2. **Integrar Web Speech API** para voz
3. **Conectar con backend Spring Boot** para generadores
4. **Agregar WebSocket** para chat
5. **Diseñar temas** light/dark
6. **Agregar auto-updater**

---

Consulta `ARQUITECTURA_APP_ESCRITORIO.md` para más detalles técnicos.
