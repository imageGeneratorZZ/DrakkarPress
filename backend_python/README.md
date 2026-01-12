# Backend Python para DrakkarPress Desktop App

Servidor Flask que expone endpoints de generación de contenido con IA.

## Instalación

```bash
pip install flask flask-cors groq
```

## Variables de Entorno

```bash
export GROQ_API_KEY="tu_api_key_aqui"
export BACKEND_PORT=5000  # Opcional, default 5000
```

## Iniciar Servidor

```bash
python backend_python/server.py
```

## Endpoints Disponibles

### Health Check
```
GET /health
```
Verifica estado del servidor y generadores disponibles.

### Generadores Básicos

#### Generar Idea de Libro
```
POST /api/ai/idea
Content-Type: application/json

{
  "prompt": "Una historia sobre...",
  "genre": "Romance Oscuro"
}
```

#### Generar Personaje
```
POST /api/ai/character
Content-Type: application/json

{
  "prompt": "Descripción del personaje..."
}
```

#### Generar Sinopsis
```
POST /api/ai/synopsis
Content-Type: application/json

{
  "prompt": "Historia sobre...",
  "length": "medium"  // short, medium, long
}
```

#### Generar Diálogo
```
POST /api/ai/dialogue
Content-Type: application/json

{
  "prompt": "Contexto de la escena...",
  "characters": ["Antonio", "Liza"],
  "tone": "tenso"
}
```

#### Sugerir Títulos
```
POST /api/ai/titles
Content-Type: application/json

{
  "prompt": "Descripción del libro...",
  "count": 10
}
```

### Generadores Avanzados

#### Expandir Capítulo (plantilla_expansion.py)
```
POST /api/ai/chapter/expand
Content-Type: application/json

{
  "chapter_num": 1,
  "tomo_num": 1,
  "outline": "texto del outline (opcional)",
  "pov": "tercera",
  "tone": "sobrio-sensual",
  "target_words": 2000,
  "erotica_style": "explicito-sutil",
  "include_references": true
}
```

#### Generar con Maestro Groq (GENERADOR_MAESTRO_GROQ.py)
```
POST /api/ai/book/generate-maestro
Content-Type: application/json

{
  "chapter_num": 1,
  "prev_context": "texto previo (opcional)"
}
```

## Integración con Electron

El servidor se comunica con la app Electron a través de:
1. HTTP REST API (este servidor Python)
2. IPC handlers en Electron que llaman a los endpoints
3. Componente Generators.tsx que usa IPC

## Estructura

```
backend_python/
├── server.py          # Servidor Flask principal
├── README.md          # Esta documentación
└── requirements.txt   # Dependencias Python
```

## Dependencias

Ver [requirements.txt](requirements.txt)

## TODO

- [ ] Implementar generación real con Groq API (actualmente usa placeholders)
- [ ] Agregar caché de resultados
- [ ] Implementar streaming para generación larga
- [ ] Agregar validación de inputs más robusta
- [ ] Implementar rate limiting
- [ ] Agregar autenticación si es necesario
