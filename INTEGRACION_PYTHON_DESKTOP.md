# DrakkarPress Desktop - Integración Python IA

## 📋 Descripción

Integración completa del backend Python de generación de contenido con IA (plantilla_expansion.py, GENERADOR_MAESTRO_GROQ.py) con la aplicación de escritorio Electron.

## 🏗️ Arquitectura

```
DrakkarPress.com/
├── backend_python/          # Backend Flask con generadores IA
│   ├── server.py           # Servidor REST API
│   ├── requirements.txt    # Dependencias Python
│   └── README.md          # Documentación API
│
├── desktop-app/            # App Electron
│   ├── src/
│   │   ├── main/          # Proceso principal (Node.js)
│   │   │   ├── index.ts           # Entry point + inicio backend Python
│   │   │   ├── services/
│   │   │   │   └── ai.service.ts  # Cliente para backend Python
│   │   │   └── ipc/
│   │   │       └── handlers.ts    # Handlers IPC para AI
│   │   │
│   │   ├── preload/       # Bridge seguro
│   │   │   └── index.ts           # Expose AI API al renderer
│   │   │
│   │   └── renderer/      # Frontend React
│   │       └── components/
│   │           └── Generators/
│   │               └── Generators.tsx  # UI para generadores
│   │
│   └── package.json
│
└── start-desktop-app.ps1   # Script inicio automático
```

## 🔌 Flujo de Comunicación

```
┌─────────────────┐         IPC          ┌──────────────────┐
│  Generators.tsx │ ◄────────────────────► │  handlers.ts     │
│  (React UI)     │   ai:generateIdea     │  (IPC Handlers)  │
└─────────────────┘                       └──────────────────┘
                                                   │
                                                   ▼
                                          ┌──────────────────┐
                                          │  ai.service.ts   │
                                          │  (HTTP Client)   │
                                          └──────────────────┘
                                                   │
                                        HTTP POST  │
                                                   ▼
                                          ┌──────────────────┐
                                          │  server.py       │
                                          │  (Flask Backend) │
                                          └──────────────────┘
                                                   │
                                          ┌────────┴────────┐
                                          ▼                 ▼
                                  ┌────────────┐   ┌──────────────┐
                                  │ plantilla_ │   │ GENERADOR_   │
                                  │ expansion  │   │ MAESTRO_GROQ │
                                  └────────────┘   └──────────────┘
```

## 🚀 Inicio Rápido

### Opción 1: Script Automático (Recomendado)
```powershell
# Desde el directorio raíz
.\start-desktop-app.ps1
```

El script automáticamente:
- ✅ Verifica Python y Node.js
- ✅ Crea entorno virtual Python
- ✅ Instala dependencias (Python + Node)
- ✅ Compila TypeScript
- ✅ Inicia backend Python en segundo plano
- ✅ Inicia Electron app
- ✅ Limpia procesos al cerrar

### Opción 2: Inicio Manual

#### 1. Backend Python
```bash
# Crear entorno virtual
python -m venv backend_python/venv

# Activar entorno
backend_python\venv\Scripts\activate  # Windows
source backend_python/venv/bin/activate  # Linux/Mac

# Instalar dependencias
pip install -r backend_python/requirements.txt

# Configurar API key
set GROQ_API_KEY=tu_api_key_aqui  # Windows
export GROQ_API_KEY=tu_api_key_aqui  # Linux/Mac

# Iniciar servidor
python backend_python/server.py
```

Backend estará en: `http://127.0.0.1:5000`

#### 2. Desktop App
```bash
cd desktop-app

# Instalar dependencias (solo primera vez)
npm install

# Compilar TypeScript
npm run build:main

# Iniciar app
npm run dev
```

## 🧪 Testing

### Verificar Backend Python
```bash
# Health check
curl http://127.0.0.1:5000/health

# Probar generador de ideas
curl -X POST http://127.0.0.1:5000/api/ai/idea \
  -H "Content-Type: application/json" \
  -d '{"prompt": "Una historia sobre redención", "genre": "Drama"}'
```

### Verificar Integración Electron
1. Iniciar app con `.\start-desktop-app.ps1`
2. Ir a "Generadores" en sidebar
3. Verificar indicador: 🟢 Backend Conectado
4. Seleccionar cualquier generador
5. Ingresar prompt y generar

## 📡 API Endpoints Disponibles

### Basic Generators
- `POST /api/ai/idea` - Generar idea de libro
- `POST /api/ai/character` - Crear personaje
- `POST /api/ai/synopsis` - Escribir sinopsis
- `POST /api/ai/dialogue` - Generar diálogo
- `POST /api/ai/titles` - Sugerir títulos
- `POST /api/ai/recipe` - Crear receta
- `POST /api/ai/report` - Generar informe

### Advanced Generators
- `POST /api/ai/chapter/expand` - Expandir capítulo (plantilla_expansion.py)
- `POST /api/ai/book/generate-maestro` - Generar con Groq Maestro

Ver [backend_python/README.md](backend_python/README.md) para detalles de cada endpoint.

## 🔧 Configuración

### Variables de Entorno

Crear `.env` en raíz:
```bash
GROQ_API_KEY=gsk_xxxxxxxxxxxxx
BACKEND_PORT=5000
```

### Configuración Python (plantilla_expansion.py)

```python
DraftConfig(
    pov="tercera",                    # Punto de vista
    tone="sobrio-sensual",           # Tono narrativo
    target_words=2000,               # Palabras objetivo
    erotica_style="explicito-sutil", # Estilo erótico
    include_references=True          # Refs culturales
)
```

### Configuración Groq (GENERADOR_MAESTRO_GROQ.py)

```python
client = Groq(api_key=os.environ.get('GROQ_API_KEY'))
model = "llama-3.3-70b-versatile"
temperature = 0.9
max_tokens = 8192
```

## 🐛 Troubleshooting

### Backend no conecta
```powershell
# Verificar que Python está instalado
python --version

# Verificar proceso Python corriendo
Get-Process python

# Ver logs del backend
cat backend_python\output.log
cat backend_python\error.log
```

### Error de dependencias Python
```bash
# Reinstalar dependencias
pip install --force-reinstall -r backend_python/requirements.txt
```

### Error de compilación TypeScript
```bash
cd desktop-app
npm run build:main -- --clean
```

### Puerto 5000 ocupado
```powershell
# Cambiar puerto en .env
echo "BACKEND_PORT=5001" > .env

# O matar proceso en puerto 5000
netstat -ano | findstr :5000
taskkill /PID <PID> /F
```

## 📦 Build para Producción

```bash
cd desktop-app

# Build completo
npm run build

# Generar instalador Windows
npm run build:win

# Generar para todas las plataformas
npm run build:all
```

El instalador incluirá:
- ✅ Electron app compilada
- ✅ Backend Python embebido
- ✅ Dependencias Python empaquetadas
- ✅ Startup automático del backend

## 🎯 Roadmap

- [x] Backend Flask con endpoints básicos
- [x] Integración plantilla_expansion.py
- [x] Integración GENERADOR_MAESTRO_GROQ.py
- [x] AI service en Electron
- [x] IPC handlers para AI
- [x] UI Generators.tsx actualizada
- [x] Script de inicio automático
- [ ] Implementar generación real con Groq (actualmente placeholders)
- [ ] Agregar streaming para generación larga
- [ ] Caché de resultados
- [ ] Rate limiting
- [ ] Panel de configuración en UI
- [ ] Historial de generaciones
- [ ] Export de resultados
- [ ] Empaquetar backend Python con PyInstaller para distribución

## 📚 Documentación Adicional

- [Backend Python API](backend_python/README.md)
- [Desktop App Architecture](desktop-app/README.md)
- [Generadores de Contenido](ESTRUCTURA_COMPLETA_8_TOMOS.md)

## 👥 Soporte

Para reportar issues o contribuir:
1. Revisar logs: `backend_python/output.log` y `backend_python/error.log`
2. Verificar estado del backend: UI muestra 🟢/🔴
3. Consultar documentación de endpoints
4. Abrir issue con logs completos

---

**Estado**: ✅ Integración Completa  
**Última actualización**: 2024  
**Mantenido por**: DrakkarPress Team
