# 🚀 PREPARACIÓN LANZAMIENTO BETA - DrakkarPress Desktop

## ✅ Checklist Pre-Lanzamiento

### 1. Backend (Spring Boot)
- [ ] Backend compilado y ejecutándose en `localhost:8080`
- [ ] Endpoints de API funcionando (`/api/health`, `/api/ai/*`)
- [ ] PostgreSQL conectado y configurado
- [ ] CORS habilitado para `localhost` (desarrollo)
- [ ] Variables de entorno configuradas (`.env`)

### 2. Desktop App (Electron)
- [ ] Dependencias instaladas (`npm install`)
- [ ] Base de datos SQLite funcionando
- [ ] IPC handlers registrados
- [ ] UI de React renderizando
- [ ] Comunicación con backend testeada
- [ ] Modo offline funcionando

### 3. Testing
- [ ] Crear creación desde generador
- [ ] Guardar en biblioteca local
- [ ] Exportar creación
- [ ] Ver estadísticas en dashboard
- [ ] Configuración guardándose correctamente
- [ ] Modo offline con ejemplos demo

### 4. Empaquetado
- [ ] Build de producción (`npm run build`)
- [ ] Generar EXE (`npm run build:win`)
- [ ] Instalar y probar EXE
- [ ] Verificar auto-updater (opcional)

---

## 📋 Pasos para Lanzamiento BETA

### PASO 1: Preparar Backend

```powershell
# Ir a la carpeta del backend
cd c:\Users\SuperUsuario\DrakkarPress.com\backend

# Compilar backend
mvn clean package -DskipTests

# Ejecutar backend
java -jar target\drakkarpress-platform-1.0.0.jar
```

**Verificar**: Abrir navegador → `http://localhost:8080/api/health`

---

### PASO 2: Preparar Desktop App

```powershell
# Ir a la carpeta desktop-app
cd c:\Users\SuperUsuario\DrakkarPress.com\desktop-app

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

**Verificar**: La aplicación debe abrir y mostrar el dashboard

---

### PASO 3: Testing Manual

#### 3.1 Test Generadores (Modo Demo)
1. Clic en "Generadores" en sidebar
2. Seleccionar "Generar Idea de Libro"
3. Escribir prompt
4. Clic en "Generar"
5. Verificar que aparece resultado demo

#### 3.2 Test Biblioteca
1. Ir a "Biblioteca"
2. Verificar que se muestran creaciones guardadas
3. Probar búsqueda
4. Probar exportación

#### 3.3 Test Tienda
1. Ir a "Mi Tienda"
2. Ver estadísticas
3. Verificar lista de productos

#### 3.4 Test Configuración
1. Ir a "Configuración"
2. Cambiar tema
3. Cambiar idioma
4. Habilitar/deshabilitar voz
5. Verificar que se guarda

---

### PASO 4: Build para Producción

```powershell
# Compilar todo
npm run build

# Generar instalador Windows
npm run build:win

# El instalador estará en:
# desktop-app\release\DrakkarPress-Setup-1.0.0.exe
```

---

### PASO 5: Distribución Beta

#### Opción A: Compartir EXE Directamente
1. Subir `DrakkarPress-Setup-1.0.0.exe` a Google Drive/Dropbox
2. Compartir link con beta testers
3. Incluir README con instrucciones

#### Opción B: Release en GitHub
```powershell
# Crear release en GitHub
git tag v1.0.0-beta
git push origin v1.0.0-beta

# Subir EXE como asset del release
```

---

## 📝 Instrucciones para Beta Testers

### Requisitos del Sistema
- Windows 10/11 (64-bit)
- 4GB RAM mínimo
- 500MB espacio en disco
- (Opcional) Backend ejecutándose en `localhost:8080`

### Instalación

1. **Descargar** `DrakkarPress-Setup-1.0.0.exe`
2. **Ejecutar** el instalador
3. **Seguir** el asistente de instalación
4. **Iniciar** DrakkarPress desde el escritorio

### Primer Uso

1. La app abrirá en modo **OFFLINE** por defecto
2. Puedes usar los generadores en **modo demo**
3. Todas las creaciones se guardan **localmente**
4. Para activar IA real, ejecuta el backend:
   ```powershell
   # En otra terminal
   cd backend
   java -jar target\drakkarpress-platform-1.0.0.jar
   ```
5. La app detectará automáticamente el backend

### ¿Qué Probar?

✅ **Generadores**: Crear libros, recetas, informes
✅ **Biblioteca**: Guardar, buscar, exportar
✅ **Tienda**: Ver productos (próximamente publicación)
✅ **Configuración**: Cambiar tema, idioma, IA
✅ **Modo Offline**: Funcionalidad sin Internet

### Reportar Bugs

Crear issue en GitHub con:
- **Título**: Descripción corta del problema
- **Pasos**: Cómo reproducir
- **Esperado**: Qué debería pasar
- **Actual**: Qué pasa realmente
- **Capturas**: Screenshots si aplica
- **Logs**: En `%APPDATA%\drakkarpress-desktop\logs\`

---

## 🐛 Problemas Conocidos (v1.0 BETA)

### 1. Backend No Conecta
**Síntoma**: "⏳ Modo Offline" siempre visible
**Solución**:
- Verificar que backend esté ejecutándose
- Verificar puerto 8080 disponible
- Verificar firewall

### 2. Base de Datos No Guarda
**Síntoma**: Creaciones desaparecen al cerrar
**Solución**:
- Verificar permisos de escritura en `%APPDATA%`
- Reinstalar aplicación

### 3. Generadores Lentos
**Síntoma**: Tarda mucho en generar
**Causa**: Backend procesando con IA
**Normal**: Primera llamada puede tardar 10-30 segundos

---

## 🔄 Próximas Actualizaciones (v1.1)

- [ ] Control por voz real (actualmente UI demo)
- [ ] Chat comunitario con WebSocket
- [ ] Exportación a EPUB/MOBI
- [ ] Sincronización cloud automática
- [ ] Generación de portadas con IA
- [ ] Marketplace integrado
- [ ] Co-autoría colaborativa

---

## 📊 Métricas de Éxito Beta

### Objetivos
- **10+ beta testers** activos
- **50+ creaciones** generadas
- **5+ reportes de bugs** útiles
- **Feedback positivo** sobre UX

### KPIs
- Tiempo promedio de primera generación
- Creaciones por usuario
- Tasa de retención (7 días)
- NPS (Net Promoter Score)

---

## 🎯 Plan de Lanzamiento

### Semana 1 (Beta Privada)
- Invitar a 5-10 usuarios de confianza
- Recopilar feedback intensivo
- Corregir bugs críticos

### Semana 2 (Beta Expandida)
- Invitar a 20-30 usuarios adicionales
- Implementar mejoras de UX
- Preparar documentación

### Semana 3 (Beta Pública)
- Anuncio en redes sociales
- Landing page con descarga
- Video demo en YouTube

### Semana 4 (Pre-Launch v1.0)
- Ultimos ajustes
- Preparar campaña de lanzamiento
- Definir pricing

---

## 📞 Contacto Soporte Beta

- **Email**: beta@drakkarpress.com
- **Discord**: discord.gg/drakkarpress-beta
- **Issues**: github.com/imageGeneratorZZ/DrakkarPress/issues

---

## 🎉 ¡LISTO PARA LANZAR!

Una vez completados todos los pasos, la versión BETA estará lista para distribuir.

**Próximo Comando**:
```powershell
npm run build:win
```

¡Éxito con el lanzamiento! 🚀
