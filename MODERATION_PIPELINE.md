# Moderation & Content Safety Pipeline

## Objetivos
- Prevención y bloqueo absoluto de CSAM / explotación de menores.
- Mitigar riesgos legales (GDPR, COPPA, DSA, Online Safety Act) y reputacionales.
- Mantener experiencia fluida para creadores legítimos (baja fricción) sin sacrificar seguridad.
- Generar trazabilidad auditable de cada decisión (transparencia y apelaciones).

## Capas del Pipeline
1. **Entrada (Generación AI / Upload Usuario)**
   - Hook previo a persistir: normalización texto, extracción metadatos (idioma, longitud, entidades PERSONA + AGE).
2. **Clasificación Automática Inicial**
   - NLP Multi-etapa: detección de edad implícita ("niño", "menor", etc.) + contexto sexual → puntaje de riesgo.
   - Modelo de toxicidad / sexual explicit classifier (transformer fine-tuned) → score sexual_explicit.
   - Regla: (age_reference && sexual_explicit > threshold_minor) => bloque inmediato + flag CSAM_SUSPECT.
3. **Hash Matching (Imágenes / Multimedia)**
   - PhotoDNA / Cazador de hashes (servicio externo) para portadas, ilustraciones, thumbnails de Reels.
   - Si coincidencia positiva: cuarentena, bloqueo publicación, notificación automática cadena legal.
4. **Lista Negra / Palabras Clave**
   - Diccionario dinámico (actualizable) términos high-risk (grooming, explotación, etc.).
   - Peso adicional al score.
5. **Motor de Reglas**
   - Combina: sexual_explicit, minor_risk_score, keyword_hits, historiales del autor (reincidencias).
   - Resultado: SAFE | REVIEW | BLOCKED.
6. **Cola de Revisión Humana (Estado REVIEW)**
   - SLA: < 2h para contenido general, <30min si flag de menores.
   - Interfaz admin con diff y explicación de puntuaciones.
7. **Decisión Final**
   - Persistencia en `ModerationFlag` con: autor, recurso, scores, decisión, timestamp, reviewerId.
8. **Notificación / Reporte Externo**
   - BLOCKED por sospecha menores → reporte automático a autoridad competente (NCMEC EEUU, canal local UE) según región del usuario.
9. **Registro Auditable**
   - Event log append-only (WORM) para inspecciones regulatorias.

## Flujos
### Flujo Publicación Libro
AI draft -> PreFilter -> Scores -> (SAFE) Publica + FeedEvent BOOK_PUBLISHED.
AI draft -> (REVIEW) Cola humana -> Decisión -> Publica o Bloquea.
AI draft -> (BLOCKED) No publica, crea flag, posible reporte.

### Flujo Reels (Audio/Video Corto)
Extracción frames clave + audio transcript -> mismo pipeline texto + hash imágenes.

## Datos y Entidades Nuevas
`ModerationFlag`:
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | PK |
| resourceType | ENUM(BOOK, STORY, REEL, PROFILE_IMG) | |
| resourceId | UUID | Referencia |
| status | ENUM(PENDING, APPROVED, REJECTED, ESCALATED, AUTO_BLOCKED) | |
| scores | JSON | sexual_explicit, minor_risk, etc. |
| finalDecision | ENUM(SAFE, BLOCKED) | Resultado |
| reviewerId | UUID nullable | Humano asignado |
| escalatedTo | STRING nullable | Autoridad / sistema externo |
| createdAt | TIMESTAMP | |
| updatedAt | TIMESTAMP | |

## Integraciones
- PhotoDNA / proveedor hash: API asíncrona (webhook o polling). Timeout => fallback a revisión manual.
- NLP microservice: endpoint `/moderation/analyze` devuelve scores.

## Retención & Privacidad
- Flags retenidos 24 meses (cumplimiento auditorías, DSA). Contenido bloqueado eliminable tras 90 días salvo investigación.
- Minimización: sólo mantener extractos necesarios para justificar decisión.

## Métricas Principales
- False Positives %, False Negatives estimados (muestreo). Tiempo resolución promedio. Volumen por categoría.
- Tasa publicación sin fricción (SAFE directo) > 85% objetivo.

## Escalamiento
1. Sospecha menores + sexual contexto → AUTO_BLOCKED → Reporte.
2. Reincidencia (>=2 flags graves) → Suspensión cuenta, revisión completa catálogo.

## Apelaciones
- Usuario puede solicitar apelación dentro de 30 días. Segunda revisión por analista distinto.
- Registro de apelación añadido a flag.

## Transparencia
- Informe trimestral: número de flags, porcentajes, tiempo respuesta, apelaciones exitosas.
