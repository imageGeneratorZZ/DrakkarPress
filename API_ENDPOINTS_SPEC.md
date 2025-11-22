# API Endpoints Specification (Social, Moderation, Export, Monetization)

## Notación
`(A)` Auth requerido, `(P)` Público, `(M)` Moderación role.

## Feed & Social
- GET `/api/feed` (A): paginado, mezcla eventos.
- GET `/api/users/{id}/profile` (P): datos públicos + grid covers.
- POST `/api/follow/{userId}` (A): seguir.
- DELETE `/api/follow/{userId}` (A): dejar de seguir.
- GET `/api/follow/requests` (A): solicitudes pendientes (si perfil privado).
- POST `/api/follow/{requestId}/accept` (A).
- POST `/api/follow/{requestId}/block` (A).
- POST `/api/book/{id}/like` (A).
- POST `/api/reel/{id}/like` (A).
- POST `/api/book/{id}/comment` (A).
- GET `/api/book/{id}/comments` (P).

## Stories
- POST `/api/stories` (A): crear story (validación edad si menor).
- GET `/api/stories` (A): lista stories contactos.
- GET `/api/stories/{userId}` (A): stories usuario.

## Reels
- POST `/api/reels` (A): subir audio/video corto.
- GET `/api/reels` (P): exploración.
- GET `/api/reels/{id}` (P).

## Moderación
- POST `/api/moderation/analyze` (A): (interno uso) análisis previo (retorna scores).
- GET `/api/moderation/flags` (M): listar flags.
- POST `/api/moderation/flags/{id}/decision` (M): aprobar / bloquear.
- POST `/api/report` (A): usuario reporta contenido (genera flag tipo USER_REPORT).

## Age / KYC
- GET `/api/age/status` (A): estado ageGroup, parentalConsentStatus.
- POST `/api/age/parental-consent/request` (A).
- POST `/api/age/parental-consent/verify` (A).
- POST `/api/kyc/init` (A).
- GET `/api/kyc/status` (A).

## Export / Distribution
- POST `/api/export/jobs` (A): crear jobs (lista plataformas).
- GET `/api/export/jobs` (A): listar propios.
- GET `/api/export/jobs/{id}` (A).
- POST `/api/export/jobs/{id}/retry` (A).

## Monetización / Royalty
- GET `/api/royalties` (A): resumen splits.
- GET `/api/royalties/{bookId}` (A).
- GET `/api/royalties/export/{platform}` (A): detalle plataforma externa.

## Books Publicación
- POST `/api/books/publish` (A): crear y someter a moderación.
- POST `/api/books/{id}/update` (A).
- GET `/api/books/{id}` (P).
- GET `/api/books` (P): catálogo publicado.

## Seguridad / Transparencia
- GET `/api/transparency/metrics` (P): métricas agregadas (DSA compliance).
- GET `/api/transparency/moderation-log/{resourceId}` (A/M según privacidad).

## Estructuras de Respuesta (Ejemplos)
`FeedItem`:
```json
{
  "id": "uuid",
  "type": "BOOK_PUBLISHED",
  "timestamp": "2025-11-21T10:00:00Z",
  "author": {"id": "u1", "displayName": "Autora"},
  "mediaUrl": "https://.../cover.jpg",
  "engagement": {"likes": 120, "comments": 14, "shares": 3},
  "previewText": "Primer párrafo..."
}
```

`ModerationFlag`:
```json
{
  "id": "f1",
  "resourceType": "BOOK",
  "resourceId": "b1",
  "status": "PENDING",
  "scores": {"sexual_explicit": 0.12, "minor_risk": 0.87},
  "finalDecision": null
}
```

`ExportJob`:
```json
{
  "id": "e1",
  "bookId": "b1",
  "platform": "KDP",
  "status": "UPLOADING",
  "createdAt": "2025-11-21T09:55:00Z",
  "attempts": 1
}
```

## Paginación
- Query params estándar: `?page=0&size=20`.
- Feed puede soportar cursor `?cursor=timestamp_uuid` para scroll infinito.

## Seguridad
- Todos (A) con JWT + verificación ageGroup para ciertas acciones.
- Roles: USER, MODERATOR, ADMIN.

## Próximas Extensiones
- GraphQL gateway opcional para apps móviles.
- WebSocket canal para actualizaciones feed en tiempo real.
