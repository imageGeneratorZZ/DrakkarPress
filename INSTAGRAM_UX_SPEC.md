# Instagram-Style UX Specification

## Principios
- Visual first: rejilla de portadas y snippets.
- Interacción rápida: likes, guardar, compartir, comentarios breves.
- Descubrimiento: Explore basado en género, tags semánticos y trending.
- Contenido efímero: Stories (24h): avances, citas, arte conceptual.
- Microvideo / Audio: Reels libros (15-30s) narración AI o lectura autor.

## Componentes
1. **Feed Principal**
   - Mezcla: BOOK_PUBLISHED, REEL_POSTED, STORY_CREATED, BADGE_EARNED.
   - Algoritmo: peso por afinidad (seguidores), frescura, engagement velocity.
2. **Profile Grid**
   - Layout 3 columnas de portadas + ícono tipo reel (▶) sobre multimedia.
3. **Story Bar**
   - Avatares circulares con anillo si story activa.
4. **Reel Viewer**
   - Reproducción vertical (scroll snap). Métricas visibles (likes, shares).
5. **Explore**
   - Mosaico dinámico: géneros, trending tags (#fantasía, #thriller).
6. **Book Detail Compact**
   - Portada grande, botones: Like, Guardar, Compartir, Comprar / Leer.

## Estados Visuales de Moderación
- Contenido en revisión: overlay "Revisión" + icono reloj.
- Contenido bloqueado (solo autor): banner rojo con motivo.

## Accesibilidad
- Alt text obligatorio en portadas y imágenes historias.
- Subtítulos auto-generados en Reels con opción editar.

## Métricas UX
- Tiempo hasta primera interacción < 5s tras carga feed.
- LCP objetivo < 2.5s en móvil.

## Gamificación
- Badges: "Primer Libro", "100 Ventas", "Top Género".
- Influye en feed (ligero boost inicial a nuevos autores con badges recientes).

## Stories
- Duración 24h; almacenamiento persistente privado 30 días para auditoría.
- Tipos: TEXT_QUOTE, IMAGE_CONCEPT, AUDIO_SNIPPET.

## Reels
- Preprocesado: normalizar volumen, limitar a 30s.
- Opcional: generar waveform visual.
