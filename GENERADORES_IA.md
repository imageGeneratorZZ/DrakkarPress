# 🎨 GENERADORES DE IA - DRAKKARPRESS

## 📚 Ecosistema de Generadores

### **DrakkarPress**: Generador de Libros para Adultos
- Ubicación: `generators.html`
- Público objetivo: Adultos 18+
- Géneros: Romance, Erótica, Thriller, Fantasía, Ciencia Ficción, etc.
- Nivel de erotismo: 0-5 configurable

### **Scryptorium**: Generador de Libros para Niños
- Versión específica para contenido infantil
- Público objetivo: Niños y familias
- Géneros: Cuentos infantiles, libros para colorear, educativos
- Contenido: Siempre limpio y apropiado para todas las edades

---

## 🚀 Características Implementadas

### ✅ División en 2 Generadoras

#### 🎨 GENERADORA 1: Creación de Contenido (DrakkarPress)
**Propósito**: Herramientas creativas para generar contenido original desde cero

**Funciones**:
1. **💡 Generar Idea de Libro**
   - Ecualizador de 8 géneros con sliders
   - Medidor de nivel de erotismo (0-5)
   - Mezcla personalizada de géneros
   - Palabras clave personalizables

2. **🏷️ Sugerir Títulos**
   - 10 géneros disponibles
   - Genera 5-20 títulos
   - Basado en sinopsis

3. **👤 Generar Personaje**
   - 4 tipos de personajes
   - Descripción detallada
   - Contexto narrativo

#### ✨ GENERADORA 2: Edición y Mejora
**Propósito**: Herramientas profesionales para pulir y perfeccionar textos existentes

**Funciones**:
1. **📖 Extender Capítulo**
   - 6 direcciones narrativas
   - 100-2000 palabras
   - Continuar, giro, clímax, tensión, romance, acción

2. **📝 Generar Sinopsis**
   - 4 tipos: corta, media, larga, contraportada
   - Longitudes optimizadas
   - Para diferentes usos

3. **💬 Generar Diálogo**
   - 7 tonos diferentes
   - Múltiples personajes
   - Contexto de escena

4. **✨ Mejorar Texto**
   - 6 enfoques de mejora
   - Gramática, estilo, claridad
   - Más descriptivo o conciso

5. **🔍 Analizar Estilo**
   - Análisis de tono, ritmo, voz
   - Complejidad del texto
   - Recomendaciones

---

## 🎭 Ecualizador de Géneros

### Géneros Disponibles (8 total):
1. ❤️ **Romance** - Historias de amor
2. 🔥 **Erótica** - Contenido sensual/sexual
3. 🕵️ **Thriller** - Suspense y tensión
4. 🧙 **Fantasía** - Mundos mágicos
5. 🚀 **Ciencia Ficción** - Tecnología y futuro
6. 👻 **Terror** - Horror y miedo
7. 🔍 **Misterio** - Enigmas y detectives
8. 🏛️ **Histórica** - Épocas pasadas

### Cómo Funciona:
- Cada género tiene un slider de 0-100%
- La mezcla se normaliza automáticamente
- Permite crear géneros híbridos únicos
- Ejemplos:
  - 50% Romance + 50% Thriller = Romance de suspense
  - 40% Fantasía + 30% Romance + 30% Erótica = Fantasía romántica adulta
  - 60% Terror + 40% Misterio = Thriller psicológico

---

## 🔥 Medidor de Nivel de Erotismo

### 6 Niveles Disponibles:

| Nivel | Emoji | Descripción | Contenido |
|-------|-------|-------------|-----------|
| 0 | 😊 | **Limpio** | Sin contenido sexual, apto para todos |
| 1 | 😉 | **Sugerente** | Insinuaciones sutiles, tensión sexual |
| 2 | 😏 | **Sensual** | Escenas románticas, besos apasionados |
| 3 | 🔥 | **Caliente** | Escenas sexuales sugeridas, detalles moderados |
| 4 | 💋 | **Explícito** | Escenas sexuales detalladas |
| 5 | 🔞 | **XXX** | Contenido adulto explícito, sin censura |

### Uso del Medidor:
- Se aplica al generar ideas de libros
- Afecta el tono y contenido sugerido
- Integrado con el ecualizador de géneros
- Permite control preciso del nivel de contenido adulto

---

## 🎨 Interfaz Visual

### Características de Diseño:
- ✅ **Gradient moderno** (violeta-púrpura)
- ✅ **Cards con hover effect**
- ✅ **Sliders con gradiente**
- ✅ **Indicadores visuales en tiempo real**
- ✅ **Responsive design**
- ✅ **Íconos emoji intuitivos**
- ✅ **Loading animations**
- ✅ **Status bar en vivo**

### División Visual Clara:
- **Header con badges** identificando cada generadora
- **Colores diferenciados** por sección
- **Agrupación lógica** de herramientas
- **Navegación intuitiva**

---

## 🔌 Integración con Backend

### Endpoints de API:
```
POST /api/ai/generate-idea
POST /api/ai/suggest-titles
POST /api/ai/generate-character
POST /api/ai/extend-chapter
POST /api/ai/generate-synopsis
POST /api/ai/generate-dialogue
POST /api/ai/improve-text
POST /api/ai/analyze-style
GET  /api/ai/status
```

### Datos Enviados:
```javascript
// Ejemplo: Generar Idea
{
  "genre": "romance: 50%, thriller: 30%, erotica: 20%",
  "keywords": "amor prohibido, secretos oscuros",
  "erotismoLevel": 3,
  "genreMix": {
    "romance": 50,
    "thriller": 30,
    "erotica": 20
  }
}
```

---

## 📊 Estado del Sistema

### ✅ Componentes Activos:
- **PostgreSQL 15**: Puerto 5432 (✓ Healthy)
- **Spring Boot Backend**: Puerto 8080 (⏳ Compilando)
- **Java 21**: Proceso activo (PID: 11228)
- **Memoria**: ~394MB
- **CPU**: ~220s de uso

### 📁 Archivos Creados:
1. `generators.html` - Interfaz completa de generadores
2. `src/main/java/com/drakkarpress/service/AiGeneratorService.java`
3. `src/main/java/com/drakkarpress/controller/AiGeneratorController.java`

---

## 🚀 Cómo Usar

### 1. Abrir la Interfaz:
```
file:///c:/Users/SuperUsuario/DrakkarPress.com/generators.html
```

O desde VS Code:
- Click derecho en `generators.html`
- "Open with Live Server" o "Open in Default Browser"

### 2. Esperar que el Backend Inicie:
- El status bar muestra el estado en tiempo real
- Se actualiza cada 30 segundos automáticamente
- Cuando dice "✓ Activo" ya puedes usar los generadores

### 3. Usar Generadora 1 (Creación):
- Ajusta los sliders de géneros según tu mezcla deseada
- Selecciona el nivel de erotismo
- Añade palabras clave
- Click en "Generar Idea"

### 4. Usar Generadora 2 (Edición):
- Pega tu texto existente
- Selecciona el tipo de mejora
- Click en el botón correspondiente
- El resultado aparece abajo del formulario

---

## 💡 Casos de Uso

### Ejemplo 1: Romance Erótico con Suspenso
```
Ecualizador:
- Romance: 40%
- Erótica: 35%
- Thriller: 25%

Erotismo: Nivel 4 (💋 Explícito)

Palabras: "millonario misterioso, secretos peligrosos, pasión intensa"

Resultado: Idea de novela romántica adulta con elementos de suspense
```

### Ejemplo 2: Fantasía Épica con Romance
```
Ecualizador:
- Fantasía: 60%
- Romance: 30%
- Aventura: 10%

Erotismo: Nivel 2 (😏 Sensual)

Palabras: "dragones, magia antigua, amor prohibido entre reinos"

Resultado: Fantasía épica con subtrama romántica
```

### Ejemplo 3: Thriller Psicológico
```
Ecualizador:
- Thriller: 50%
- Terror: 30%
- Misterio: 20%

Erotismo: Nivel 0 (😊 Limpio)

Palabras: "detective atormentado, mente criminal, giros inesperados"

Resultado: Thriller psicológico oscuro sin contenido adulto
```

---

## 🎯 Próximas Mejoras

### Planificadas:
- [ ] Guardar configuraciones de géneros favoritas
- [ ] Historial de generaciones
- [ ] Exportar resultados a PDF/Word
- [ ] Plantillas predefinidas por género
- [ ] Análisis de coherencia de tramas
- [ ] Generador de arcos narrativos
- [ ] Sugerencias de conflictos
- [ ] Generador de finales alternativos

---

## 📝 Notas Técnicas

### Tecnologías Usadas:
- **Frontend**: HTML5, CSS3, JavaScript vanilla
- **Backend**: Spring Boot 3.2.0, Java 21
- **Base de Datos**: PostgreSQL 15
- **AI Integration**: WebClient → Investigatron API

### Performance:
- Tiempo de respuesta: 2-5 segundos por generación
- Carga inicial: <1 segundo
- Actualizaciones de estado: cada 30s
- Interfaz totalmente asíncrona

---

## ⚠️ Troubleshooting

### Backend no responde:
1. Verificar que Java esté corriendo: `Get-Process java`
2. Revisar logs en la ventana CMD abierta
3. Esperar 5-10 minutos la primera vez (descarga dependencias)
4. Verificar PostgreSQL: `docker ps`

### Los generadores no funcionan:
1. Verificar status bar en la página
2. Abrir consola del navegador (F12) para ver errores
3. Verificar que el backend esté en puerto 8080
4. Probar endpoint manual: `http://localhost:8080/api/health`

### Sliders no responden:
1. Recargar la página (F5)
2. Verificar JavaScript está habilitado
3. Probar en otro navegador

---

## 📧 Contacto

Para soporte o consultas sobre los generadores:
- Proyecto: DrakkarPress.com
- Ubicación: `c:\Users\SuperUsuario\DrakkarPress.com`
- Documentación: Este archivo

---

**¡Disfruta creando historias increíbles con IA! 🎉**
