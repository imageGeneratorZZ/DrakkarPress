# 🚀 DrakkarPress - Nueva Interfaz Instagram

## ✨ Cambios Implementados

### 🎨 Frontend Modernizado
- ✅ Diseño tipo Instagram con sidebar vertical
- ✅ Feed de posts con cards modernas
- ✅ Stories horizontales
- ✅ Sistema de likes, comentarios y guardados
- ✅ Filtros por categoría
- ✅ Búsqueda integrada
- ✅ Diseño 100% responsive (Desktop, Tablet, Mobile)
- ✅ Login moderno estilo Instagram

### 🔧 Backend Configurado
- ✅ Spring Boot 3.5.3 con Java 21
- ✅ PostgreSQL corriendo en Docker
- ✅ JWT Authentication
- ✅ CORS configurado para localhost
- ✅ Endpoints de Auth funcionando

### 📦 Scripts de Gestión
- ✅ `START-INSTAGRAM.ps1` - Arranca todo automáticamente
- ✅ `manage-backend.ps1` - Gestiona el backend (start/stop/status)
- ✅ `test-endpoints.ps1` - Prueba los endpoints del API

## 🚀 Inicio Rápido

### Opción 1: Script Automático (Recomendado)
```powershell
.\START-INSTAGRAM.ps1
```

### Opción 2: Manual

1. **Iniciar PostgreSQL:**
```powershell
docker start drakkarpress-db
```

2. **Iniciar Backend:**
```powershell
cd backend
.\manage-backend.ps1 -Action start
```

3. **Abrir Frontend:**
- Usa Live Server en VS Code
- O abre `index.html` directamente

## 📁 Archivos Principales

```
DrakkarPress.com/
├── index.html                    # Interfaz principal (Instagram style)
├── login.html                    # Login moderno
├── js/
│   └── api-client.js            # Cliente para comunicarse con el backend
├── backend/
│   ├── manage-backend.ps1       # Script de gestión del backend
│   └── test-endpoints.ps1       # Tests de API
└── START-INSTAGRAM.ps1          # Inicio automático
```

## 🎯 URLs

- **Frontend:** http://localhost:5500
- **Backend API:** http://localhost:12000
- **PostgreSQL:** localhost:5432

## 🔑 Funcionalidades

### Sidebar (Menú Vertical)
- 🏠 Inicio
- 🔍 Explorar
- 🧭 Descubrir
- 🔖 Guardados
- 📚 Mi Biblioteca
- 👥 Comunidad
- 📊 Estadísticas
- ➕ Crear

### Feed
- 📖 Posts de libros estilo Instagram
- ❤️ Sistema de likes
- 💬 Comentarios
- 🔄 Compartir
- 🔖 Guardar

### Autenticación
- ✅ Login/Registro funcional
- ✅ JWT Tokens
- ✅ Persistencia en localStorage
- ✅ Protección de rutas

## 🧪 Probar el API

```powershell
cd backend
.\test-endpoints.ps1
```

Esto probará:
- ✅ Health Check
- ✅ CORS
- ✅ Registro de usuario
- ✅ Login
- ✅ Endpoints protegidos

## 🛠️ Gestión del Backend

```powershell
# Ver estado
.\backend\manage-backend.ps1 -Action status

# Iniciar
.\backend\manage-backend.ps1 -Action start

# Detener
.\backend\manage-backend.ps1 -Action stop

# Reiniciar
.\backend\manage-backend.ps1 -Action restart

# Ver logs en tiempo real
.\backend\manage-backend.ps1 -Action tail
```

## 📱 Responsive Design

- **Desktop (>768px):** Sidebar completo con texto
- **Tablet (768px):** Sidebar con solo iconos
- **Mobile (<480px):** Bottom navigation bar

## 🎨 Paleta de Colores

- **Primary:** #1A4D7A (Azul Vikingo)
- **Secondary:** #D4AF37 (Oro Nórdico)
- **Background:** #FAFAFA (Instagram style)
- **Text:** #262626

## 🔄 Respaldos

El `index.html` anterior se guardó como:
- `index-old-backup.html`

## 📝 Próximos Pasos

1. [ ] Conectar feed con datos reales del backend
2. [ ] Implementar sistema de comentarios
3. [ ] Agregar upload de imágenes de libros
4. [ ] Implementar perfil de usuario completo
5. [ ] Agregar notificaciones en tiempo real
6. [ ] Implementar búsqueda avanzada

## 🐛 Troubleshooting

### El backend no arranca
```powershell
# Verificar que el puerto no esté ocupado
netstat -ano | Select-String "12000"

# Compilar de nuevo
cd backend
$env:JAVA_HOME="$PWD\.java\jdk21\jdk-21.0.9+10"
.\mvnw.cmd clean package -DskipTests
```

### Error de CORS
- Verifica que el backend esté corriendo
- Revisa `SecurityConfig.java` para ver orígenes permitidos

### No se ve el diseño
- Usa Live Server de VS Code
- Verifica la consola del navegador (F12) para errores

## 📞 Soporte

Para cualquier problema, revisa:
1. Logs del backend: `backend/app.log`
2. Consola del navegador (F12)
3. Estado del backend: `.\backend\manage-backend.ps1 -Action status`

---

**Desarrollado con ⚔️ por DrakkarPress Team**
