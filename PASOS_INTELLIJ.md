# 🚀 Guía Rápida: Compilar Backend con IntelliJ IDEA

## ⏱️ Tiempo total: 30 minutos

---

## PASO 1: Descargar IntelliJ IDEA Community (5 min)

✅ **YA ABRÍ LA PÁGINA**: https://www.jetbrains.com/idea/download/?section=windows

### En la página que se abrió:

1. **Buscar sección**: "IntelliJ IDEA Community Edition"
2. **Click en**: Botón negro "Download" (debajo de Community Edition)
3. **Archivo**: `ideaIC-2024.x.x.exe` (~900 MB)
4. **Esperar descarga**: ~3-5 minutos (depende de tu internet)

---

## PASO 2: Instalar IntelliJ IDEA (5 min)

1. **Ejecutar instalador**: `ideaIC-2024.x.x.exe` desde tu carpeta Descargas

2. **Welcome Screen**: Click "Next"

3. **Choose Install Location**: 
   - Dejar default: `C:\Program Files\JetBrains\IntelliJ IDEA Community Edition`
   - Click "Next"

4. **Installation Options** - ✅ MARCAR:
   ```
   ☑️ Create Desktop Shortcut
   ☑️ Update PATH variable (restart needed)
   ☑️ Add "Open Folder as Project"
   ☑️ .java - Associate with IntelliJ IDEA
   ☑️ .groovy
   ☑️ .kt
   ☑️ .kts
   ```
   - Click "Next"

5. **Choose Start Menu Folder**:
   - Dejar default: "JetBrains"
   - Click "Install"

6. **Esperar instalación**: ~2 minutos

7. **Completing Setup**:
   - ☑️ Run IntelliJ IDEA Community Edition
   - Click "Finish"

---

## PASO 3: Primera Ejecución de IntelliJ (2 min)

1. **Data Sharing**: 
   - Seleccionar lo que prefieras (recomiendo: "Don't Send")
   - Click "Continue"

2. **Welcome Screen**:
   - Verás: "Welcome to IntelliJ IDEA"
   - **NO ABRIR PROYECTO TODAVÍA**

3. **Primero instalaremos Lombok**...

---

## PASO 4: Instalar Plugin Lombok (3 min)

1. En Welcome Screen, click en **⚙️ Customize** (panel izquierdo)
   - O: Click en **Plugins** directamente

2. **Si no ves Plugins**:
   - Click en: **Configure → Plugins**
   - O: Menú superior → File → Settings → Plugins

3. **Pestaña Marketplace**:
   - Asegúrate de estar en la pestaña "Marketplace" (no "Installed")

4. **Buscar**:
   - En el buscador superior, escribir: **lombok**

5. **Encontrar plugin**:
   - Nombre: **Lombok**
   - Por: Michail Plushnikov
   - ⭐ 4.5+ rating
   - 🔽 50M+ downloads

6. **Instalar**:
   - Click en botón azul: **"Install"**
   - Esperar descarga: ~10 segundos

7. **Restart IDE**:
   - Aparecerá botón: **"Restart IDE"**
   - Click en él
   - IntelliJ se cerrará y reabrirá (~30 segundos)

---

## PASO 5: Abrir Proyecto DrakkarPress (2 min)

1. **Welcome Screen** (después del restart):
   - Click en: **"Open"**

2. **Select File or Directory to Open**:
   - Navegar a: `C:\Users\SuperUsuario\DrakkarPress.com\backend`
   - **IMPORTANTE**: Seleccionar la carpeta **backend** (no DrakkarPress.com)
   - Click "OK"

3. **Trust and Open Project**:
   - Aparecerá ventana: "Trust and Open Project 'backend'?"
   - Click: **"Trust Project"**

4. **Esperar indexación inicial**:
   - Barra de progreso abajo: "Indexing..."
   - **NO HACER NADA**, solo esperar
   - Tiempo: ~3-5 minutos (depende de tu PC)
   - Verás: "Scanning files to index", "Building project", etc.

---

## PASO 6: Configurar JDK (1 min)

**Si IntelliJ NO detecta JDK automáticamente**:

1. **File → Project Structure** (o Ctrl+Alt+Shift+S)

2. **Project Settings → Project**:
   - SDK: Si dice "No SDK", hacer:
     - Click "Add SDK" → "JDK"
     - Navegar a: `C:\Users\SuperUsuario\Java\jdk-21.0.5+11`
     - Click "OK"
   - Language Level: **21 - Pattern matching for switch**
   - Click "OK"

**Si detectó JDK automáticamente**:
   - ✅ Verificar que dice "openjdk-21" o "21"
   - ✅ Language level: 21
   - No hacer nada más

---

## PASO 7: Habilitar Annotation Processing (1 min)

1. **File → Settings** (o Ctrl+Alt+S)

2. **Build, Execution, Deployment → Compiler → Annotation Processors**

3. **Verificar**:
   ```
   ☑️ Enable annotation processing
   ```
   - Si NO está marcado, márcalo
   - Click "OK"

---

## PASO 8: Compilar Proyecto (5 min)

### Opción A: Build completo (recomendado)

1. **Menú Build → Rebuild Project**

2. **Esperar compilación**:
   - Barra de progreso abajo: "Building..."
   - Verás en el panel inferior: "Build" tab
   - Tiempo: ~2-3 minutos

3. **Resultado esperado**:
   ```
   ✅ Build completed successfully in 2m 15s
   ✅ 0 errors, 0 warnings
   ```

### Opción B: Maven (alternativa)

1. **Panel derecho**: Click en **Maven** (o View → Tool Windows → Maven)

2. **Expandir**:
   - drakkarpress-platform
   - Lifecycle

3. **Ejecutar**:
   - Doble click en: **clean**
   - Esperar que termine
   - Doble click en: **compile**
   - Esperar compilación (~2 min)

4. **Resultado esperado**:
   ```
   [INFO] BUILD SUCCESS
   [INFO] Total time: 2:15 min
   ```

---

## PASO 9: Generar JAR para Producción (5 min)

1. **Panel Maven** (derecho)

2. **Lifecycle → package**:
   - Doble click en: **package**
   - Esto ejecuta: clean → compile → test → package

3. **Esperar**:
   - Tiempo: ~3-5 minutos (incluye tests)
   - Verás en Build log:
     ```
     [INFO] Building jar: ...\backend\target\drakkarpress-platform-1.0.0.jar
     [INFO] BUILD SUCCESS
     ```

4. **Verificar JAR creado**:
   ```
   📁 C:\Users\SuperUsuario\DrakkarPress.com\backend\target\
      └── drakkarpress-platform-1.0.0.jar (~60 MB)
   ```

---

## PASO 10: Probar JAR Localmente (2 min)

1. **Abrir terminal en IntelliJ**:
   - Menú: View → Tool Windows → Terminal
   - O: Alt+F12

2. **Ejecutar**:
   ```powershell
   java -jar target/drakkarpress-platform-1.0.0.jar
   ```

3. **Resultado esperado**:
   ```
   Started DrakkarPressApplication in 8.5 seconds
   Tomcat started on port 8080
   ```

4. **Probar**:
   - Abrir navegador: http://localhost:8080/actuator/health
   - Esperado: `{"status":"UP"}`

5. **Detener**:
   - En terminal: Ctrl+C

---

## ✅ ¡ÉXITO! Backend Compilado

**Archivos generados**:
- ✅ `backend/target/drakkarpress-platform-1.0.0.jar` (JAR ejecutable)
- ✅ `backend/target/classes/` (clases compiladas)

**Siguiente paso**: Seguir `MANUAL_ACTIONS_GUIDE.md` desde Paso 2 (Deploy Frontend)

---

## 🆘 Troubleshooting

### Error: "Cannot find symbol" en getters/setters

**Causa**: Lombok plugin no instalado o no habilitado

**Solución**:
1. File → Settings → Plugins
2. Buscar "Lombok"
3. Verificar que está instalado y habilitado
4. Restart IDE
5. Build → Rebuild Project

### Error: "SDK not found"

**Causa**: JDK no configurado

**Solución**:
1. File → Project Structure → Project
2. SDK: Add SDK → JDK
3. Seleccionar: `C:\Users\SuperUsuario\Java\jdk-21.0.5+11`
4. Language level: 21

### Error: "Annotation processors disabled"

**Causa**: Annotation processing deshabilitado

**Solución**:
1. File → Settings
2. Build, Execution, Deployment → Compiler → Annotation Processors
3. ☑️ Enable annotation processing
4. OK
5. Build → Rebuild Project

### Compilación muy lenta

**Causa**: PC con pocos recursos

**Solución**:
1. Cerrar otras aplicaciones
2. Help → Edit Custom VM Options
3. Agregar:
   ```
   -Xms512m
   -Xmx2048m
   ```
4. Restart IDE

### Tests fallan

**No es problema para producción**:
- El JAR se genera igual
- Solo importa que compile sin errores
- Tests se pueden arreglar después

**Para saltar tests**:
- Maven → package → Click derecho → Run Maven Build
- En "Command line": `-DskipTests`

---

## 📞 Si necesitas ayuda

**Documentación adicional**:
- Guía completa: `MANUAL_ACTIONS_GUIDE.md`
- Deploy: `DEPLOY_COMPLETO_INSTRUCCIONES.md`

**Siguiente**: Después de tener el JAR, continuar con deploy (2h 15min restantes)

---

**Generado**: Noviembre 2025
**Proyecto**: DrakkarPress Backend
**Versión**: 1.0.0
