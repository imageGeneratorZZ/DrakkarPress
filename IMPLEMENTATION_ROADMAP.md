# Implementation Roadmap Phases

## Phase 1 – Safety Foundation (Semana 1–2)
- Integrar pipeline moderación automático (texto + imágenes + audio).
- Persistencia `ModerationFlag`, endpoints decisión.
- Age gating y parental consent básico.
- Comisiones internas y registro `RoyaltySplit` (ya parcialmente implementado).

## Phase 2 – Core Social Layer (Semana 3–4)
- Feed API (agregación de eventos libro, reels, stories, badges).
- Controladores Stories/Reels (MVP listo) + endpoints de interacción (likes, comentarios).
- Optimizaciones de caché (Redis) para feed popular.

## Phase 3 – Export Automation (Semana 5–6)
- Servicio conversión EPUB3/PDF → variantes (MOBI/AZW3, print-ready PDF).
- Worker de `ExportJob` con integración KDP, Google, Lulu.
- Retrys exponenciales + backoff.
- Estados y métricas de exportación.

## Phase 4 – Mobile & Realtime (Semana 7–8)
- App React Native: auth, feed, publicar libro, stories.
- WebSocket / SSE para actualizaciones feed y contador likes.
- Push notifications (FCM / APNS) para nuevos seguidores y ventas.

## Phase 5 – Advanced Analytics & Recommender (Semana 9–10)
- Recomendador híbrido (colaborativo + contenido género / keywords embeddings).
- Panel autor: ventas, conversion funnel, desempeño historias/reels.
- Transparencia: métricas públicas moderación + reporte trimestral.

## Phase 6 – External Royalty Import & Accounting (Semana 11–12)
- Ingesta masiva de reportes KDP / Google / Lulu.
- Batch crear `RoyaltySplit` externos.
- Dashboard financiero y export CSV.

## Riesgos & Mitigaciones
- Latencia moderación: optimizar con colas y thresholds.
- Fallos exportación: circuit breaker por plataforma.
- Escalamiento almacenamiento multimedia: S3 lifecycle policies.
