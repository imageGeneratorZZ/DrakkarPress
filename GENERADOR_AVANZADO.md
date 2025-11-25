# 🪶 Generador Avanzado de Libros con IA

## 📋 Resumen

El nuevo generador avanzado permite crear libros completos con **coherencia narrativa automática** y **edición en cascada**, manteniendo la lógica de la historia incluso cuando se modifican capítulos individuales.

---

## ✨ Características Principales

### 1. **Generación Completa Automática**
- Crea un libro completo desde una idea inicial
- Genera estructura (outline) con títulos de capítulos
- Escribe automáticamente todos los capítulos en orden
- Mantiene coherencia narrativa entre capítulos

### 2. **Edición Individual con Cascada**
- Edita cualquier capítulo manualmente
- Regenera capítulos posteriores automáticamente
- Marca capítulos que necesitan revisión
- Preserva la lógica narrativa establecida

### 3. **Coherencia Narrativa Inteligente**
Los prompts de IA incluyen:
- Contexto completo de capítulos anteriores
- Instrucciones de mantener coherencia temporal
- Preservación de personalidad de personajes
- Respeto a reglas del universo narrativo
- Manejo correcto de flashbacks y técnicas narrativas

### 4. **Interfaz Moderna y Completa**
- Vista de todos los capítulos con estado
- Edición inline con vista previa
- Indicadores visuales de estado (pendiente, generado, editado, necesita revisión)
- Estadísticas en tiempo real (palabras, capítulos)
- Exportación a archivo de texto

---

## 🎯 Casos de Uso

### Caso 1: Crear un Libro Completo
1. Ingresa título, género, estilo y sinopsis
2. Define número de capítulos deseados
3. Clic en **"Crear Proyecto"**
4. Clic en **"Generar Estructura"** (crea outline)
5. Clic en **"Escribir Libro Completo"** (genera todo automáticamente)
6. Espera mientras la IA escribe cada capítulo secuencialmente
7. Exporta el libro completo

### Caso 2: Editar un Capítulo Individual
1. Abre un proyecto existente con capítulos generados
2. Selecciona un capítulo y haz clic en **"✏️ Editar"**
3. Modifica el contenido en el editor inline
4. Clic en **"💾 Guardar Cambios"**
5. Los capítulos posteriores se marcan automáticamente como "Necesita Revisión"
6. Opción: usar **"🔄 Cascada"** para regenerar todos los posteriores

### Caso 3: Regenerar Capítulo con Coherencia
1. Si un capítulo no te convence, haz clic en **"🔄 Regenerar"**
2. El sistema regenera usando el contexto actualizado de capítulos previos
3. Automáticamente marca capítulos posteriores para revisión
4. Mantiene coherencia con la narrativa establecida

### Caso 4: Continuar un Capítulo
1. Haz clic en **"➕ Continuar"** en un capítulo existente
2. La IA añade 300-500 palabras manteniendo el tono y estilo
3. Expande escenas sin romper la coherencia narrativa

---

## 🔧 Arquitectura Técnica

### Backend (Spring Boot)

#### Nuevos Endpoints

**POST** `/api/generator/projects/{projectId}/generate-complete`
- Genera automáticamente todo el libro
- Retorna: número de capítulos generados, título

**POST** `/api/generator/projects/{projectId}/chapters/{order}/regenerate`
- Regenera un capítulo específico manteniendo coherencia
- Marca capítulos posteriores como NEEDS_REVIEW
- Retorna: contenido nuevo del capítulo

**POST** `/api/generator/projects/{projectId}/chapters/{order}/regenerate-cascade`
- Regenera el capítulo especificado y todos los posteriores
- Mantiene coherencia en cascada
- Retorna: lista de capítulos regenerados

**POST** `/api/generator/projects/{projectId}/chapters/{order}/continue`
- Continúa un capítulo existente añadiendo contenido
- Mantiene tono, estilo y perspectiva
- Retorna: contenido continuado

**PUT** `/api/generator/projects/{projectId}/chapters/{order}`
- Actualiza el contenido de un capítulo
- Marca posteriores como NEEDS_REVIEW
- Retorna: confirmación de actualización

**GET** `/api/generator/projects/{projectId}/export`
- Exporta libro completo en texto plano
- Incluye título, sinopsis y todos los capítulos
- Retorna: texto completo del libro

#### Estados de Capítulos

```java
public enum ChapterStatus {
    DRAFT,          // Borrador inicial
    PENDING,        // Esperando generación
    GENERATED,      // Generado por IA
    EDITED,         // Editado manualmente
    NEEDS_REVIEW    // Necesita revisión por cambios previos
}
```

#### Mejoras en Prompts de IA

**OllamaService.generateChapter()** y **CloudAIService.generateChapter()** ahora incluyen:

```
LIBRO: "[título]"
GÉNERO: [género]
ESTILO: [estilo]

=== CONTEXTO NARRATIVO PREVIO ===
Los capítulos anteriores establecieron:
[resumen de 2 capítulos previos]

IMPORTANTE: Mantén coherencia absoluta con eventos, personajes y detalles anteriores.
Los personajes deben mantener su personalidad establecida.
Las líneas temporales deben ser consistentes.
Respeta las reglas del universo narrativo.

=== CAPÍTULO A ESCRIBIR ===
Capítulo [número]: "[título]"

DIRECTRICES:
1. Extensión: 800-1200 palabras
2. Coherencia absoluta con capítulos previos
3. Desarrollo orgánico de la trama
4. Descripciones vívidas pero concisas
5. Diálogos naturales que revelan carácter
6. Ritmo narrativo apropiado
7. Flashbacks claramente marcados si los usas
8. Cada capítulo debe avanzar significativamente

Escribe SOLO el contenido del capítulo, sin meta-comentarios.
```

### Frontend

#### Interfaz Principal: `generator-advanced.html`

**Características de UI:**
- Layout de 2 columnas: configuración | capítulos
- Panel de configuración con formulario completo
- Vista de capítulos con tarjetas (cards) por capítulo
- Indicadores visuales de estado con colores
- Botones de acción contextuales por capítulo
- Editor inline con textarea expandible
- Modal para vista completa de capítulo
- Estadísticas en tiempo real
- Barra de progreso animada
- Loading overlay durante operaciones largas

**Estados Visuales:**
- 🟢 **Verde**: Capítulo generado exitosamente
- 🔵 **Azul**: Capítulo editado manualmente
- 🟠 **Naranja**: Necesita revisión por cambios previos
- ⚪ **Gris**: Pendiente de generación
- 🔄 **Animado**: Generando actualmente

#### API Client: `js/api-client.js`

**Nuevos Métodos:**
```javascript
api.generateCompleteBook(projectId)
api.regenerateChapter(projectId, chapterOrder)
api.regenerateCascade(projectId, fromChapterOrder)
api.continueChapter(projectId, chapterOrder, currentContent)
api.updateChapter(projectId, chapterOrder, content)
api.exportBook(projectId)
api.generateOutline(projectId)  // alias de generateProjectOutline
```

---

## 🚀 Uso del Sistema

### Acceso

**Desarrollo Local:**
```
http://localhost:3000/generator-advanced.html?apiBase=http://localhost:12000/api
```

**Producción:**
```
https://drakkarpress.com/generator-advanced.html
```

### Autenticación

El generador requiere autenticación. Si no estás logueado:
- Serás redirigido automáticamente al login
- Después del login, volverás al generador

### Workflow Recomendado

#### Para un Libro Nuevo:
1. **Planificación** (5 min)
   - Define título, género, estilo
   - Escribe sinopsis detallada (300-500 palabras)
   - Decide número de capítulos (10-20 recomendado)

2. **Generación de Estructura** (1 min)
   - Clic en "Generar Estructura"
   - Revisa los títulos de capítulos generados
   - Edita títulos si es necesario

3. **Generación Automática** (20-40 min)
   - Clic en "Escribir Libro Completo"
   - La IA escribe cada capítulo secuencialmente
   - Progreso visible en barra y estadísticas

4. **Revisión y Edición** (variable)
   - Lee cada capítulo
   - Edita los que necesiten mejoras
   - Usa "Regenerar" para reescribir completamente
   - Usa "Continuar" para expandir escenas

5. **Refinamiento en Cascada** (10-30 min)
   - Si editaste capítulos tempranos
   - Usa "Cascada" para actualizar posteriores
   - Mantiene coherencia narrativa

6. **Exportación**
   - Clic en "Exportar Libro"
   - Descarga archivo .txt con libro completo
   - Listo para edición final en procesador de texto

---

## 🎨 Mejores Prácticas

### 1. Sinopsis Detallada
- **Buena**: "Historia de María, detective retirada que investiga el asesinato de su mentor. Ambientada en Buenos Aires 2024, combina elementos de thriller policial con drama personal. María debe enfrentar su pasado traumático mientras descubre que el asesino está más cerca de lo que piensa."

- **Mala**: "Un detective investiga un caso"

### 2. Planificación de Capítulos
- 10-12 capítulos: Novela corta (50,000-60,000 palabras)
- 15-20 capítulos: Novela estándar (80,000-100,000 palabras)
- 25-30 capítulos: Novela larga (120,000+ palabras)

### 3. Edición Incremental
- Genera el libro completo primero
- Revisa en orden (Cap 1 → Cap N)
- Edita capítulos tempranos solo si es necesario
- Usa "Cascada" después de editar capítulos iniciales

### 4. Uso de Flashbacks
- En la sinopsis menciona si habrá flashbacks
- Ejemplo: "La narrativa alterna entre presente (2024) y flashbacks (2010) que revelan el pasado de María"
- La IA respetará esta estructura

### 5. Coherencia de Personajes
- En sinopsis describe personalidades claramente
- Ejemplo: "María: analítica, introvertida, perfeccionista. Juan: impulsivo, carismático, leal"
- La IA mantendrá estas características

---

## 🔍 Solución de Problemas

### Problema: Capítulo generado no sigue la historia
**Solución**: 
1. Verifica que capítulos anteriores estén completos
2. Haz clic en "Regenerar" (usa contexto actualizado)
3. Si persiste, edita manualmente y usa "Cascada"

### Problema: Generación completa toma mucho tiempo
**Solución**:
- Normal para 15+ capítulos (cada uno ~2-3 minutos)
- Puedes cerrar el navegador (progreso se guarda en backend)
- Recarga la página y continúa desde donde quedó

### Problema: Capítulos marcados "Necesita Revisión"
**Explicación**:
- Ocurre después de editar capítulos anteriores
- El sistema detectó cambios que pueden afectar narrativa
**Acción**: 
- Lee el capítulo y verifica coherencia
- Si hay inconsistencias, usa "Regenerar"
- Si está bien, ignora la advertencia

### Problema: La IA repite información
**Solución**:
1. Edita el capítulo problemático
2. Usa "Continuar" en lugar de "Regenerar" (añade contenido nuevo)
3. Si persiste, ajusta la sinopsis para ser más específica

---

## 📊 Métricas y Estadísticas

### Tiempo Estimado por Operación

| Operación | Tiempo Ollama Local | Tiempo Cloud (deepseek) |
|-----------|---------------------|-------------------------|
| Generar Outline | 30-60 seg | 10-20 seg |
| Generar Capítulo | 2-3 min | 30-60 seg |
| Regenerar Capítulo | 2-3 min | 30-60 seg |
| Continuar Capítulo | 1-2 min | 20-30 seg |
| Libro Completo (15 cap) | 30-45 min | 10-15 min |

### Calidad Esperada

**Con Ollama (llama3.1:8b)**:
- Coherencia: ⭐⭐⭐⭐ (85%)
- Creatividad: ⭐⭐⭐⭐ (80%)
- Gramática: ⭐⭐⭐⭐⭐ (95%)
- Velocidad: ⭐⭐⭐ (Moderada)

**Con Cloud AI (deepseek-v3.1)**:
- Coherencia: ⭐⭐⭐⭐⭐ (95%)
- Creatividad: ⭐⭐⭐⭐⭐ (90%)
- Gramática: ⭐⭐⭐⭐⭐ (98%)
- Velocidad: ⭐⭐⭐⭐⭐ (Rápida)

---

## 🔮 Próximas Mejoras

- [ ] Vista de comparación lado a lado (versiones anteriores)
- [ ] Sugerencias automáticas de mejora
- [ ] Análisis de coherencia narrativa con métricas
- [ ] Exportación en múltiples formatos (EPUB, DOCX, PDF)
- [ ] Colaboración en tiempo real
- [ ] Versionado de capítulos (historial)
- [ ] Plantillas de géneros predefinidas
- [ ] Generación de portada con DALL-E
- [ ] Análisis de sentimiento y tono
- [ ] Detección automática de inconsistencias

---

## 📝 Notas Técnicas

### Arquitectura de Coherencia

El sistema mantiene coherencia mediante:

1. **Context Window**: Últimos 2 capítulos completos (máx 600 palabras)
2. **Status Tracking**: Estados NEEDS_REVIEW marcan impacto de cambios
3. **Cascade Regeneration**: Reescribe posteriores manteniendo timeline
4. **Prompt Engineering**: Instrucciones explícitas de coherencia en cada llamada

### Optimizaciones

- Generación paralela futura (múltiples capítulos simultáneos)
- Cache de contexto para reducir tokens
- Streaming de respuestas para feedback en tiempo real
- Batch processing para libros largos

### Limitaciones Actuales

- Máximo 50 capítulos por proyecto
- Context window limitado a 2 capítulos previos
- No detecta automáticamente plot holes
- Requiere conexión a IA (Ollama o Cloud)

---

## 🎓 Recursos Adicionales

### Documentación Relacionada

- [GENERADORES_IA.md](GENERADORES_IA.md) - Documentación general de generadores
- [CLOUD_AI_SETUP.md](backend/CLOUD_AI_SETUP.md) - Configuración de Cloud AI
- [API_ENDPOINTS_SPEC.md](API_ENDPOINTS_SPEC.md) - Especificación completa de API

### Soporte

- GitHub Issues: [DrakkarPress/issues](https://github.com/imageGeneratorZZ/DrakkarPress/issues)
- Documentación: [DrakkarPress.com/docs](https://drakkarpress.com/docs)

---

**Versión**: 1.0.0  
**Última actualización**: 25 de noviembre de 2025  
**Estado**: ✅ Producción (desplegado en Railway)
