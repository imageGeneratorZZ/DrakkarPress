# Export & Distribution Integrations

## Plataformas Objetivo
1. **Amazon KDP** (Ebooks + Paperback).
2. **Google Play Books Partner** (EPUB/PDF).
3. **Lulu** (Impresión bajo demanda, hardcover, paperback, coil binding).
4. **IngramSpark** (Opcional expansión distribución global). 
5. **Shopify** (Tienda propia: ya pseudo-integrado; sync inventario y covers). 
6. Futuro: MercadoLibre, Apple Books.

## Formatos
- Master interno: EPUB3 (estructura semántica) + PDF (maquetación fija).
- Conversión:
  - MOBI/AZW3 → (KindleGen / calibre library) para legacy.
  - KPF → Para KDP preview (opcional).
  - Lulu: PDF print-ready (bleed settings, CMYK). 

## Metadata Canonical
| Campo | Descripción | Mapeo KDP | Mapeo Google | Mapeo Lulu |
|-------|-------------|-----------|--------------|-----------|
| title | Título obra | Title | VolumeTitle | Title |
| subtitle | Subtítulo | Subtitle | Subtitle | Subtitle |
| authorName | Nombre público | Author | Contributor | Author |
| description | Sinopsis | Description | Description | Description |
| language | ISO 639-1 | Language | Language | Language |
| isbn | ISBN opcional | ISBN | ISBN | ISBN |
| genres | Lista | BrowseCategories | Subjects | Categories |
| keywords | SEO | SearchKeywords | Keywords | Keywords |
| coverImageUrl | URL alta resolución | CoverFile | Cover | Cover |
| price | Precio base | ListPrice | Price | RetailPrice |
| royaltyPlan | MODE | RoyaltyOption | N/A | N/A |

## Estrategia de Exportación
- `ExportJob` entidad con estados: PENDING, BUILDING, CONVERTING, UPLOADING, VERIFYING, COMPLETED, FAILED.
- Worker asíncrono (cola) procesa cada plataforma destino.
- Retries exponenciales en errores transitorios (HTTP 5xx, timeouts).

## Flujo General
1. Usuario marca libro "Listar Externamente".
2. Validaciones: moderación SAFE, KYC OK si monetiza, archivos generados.
3. Crear `ExportJob` por plataforma seleccionada.
4. Generar/convertir formatos necesarios (EPUB3 master -> adaptaciones).
5. Subir vía API (KDP: App/Web? se usa ingestion service; Google: Content API; Lulu: Print API v2).
6. Guardar externalId / trackingId en Book.
7. Actualizar estado y emitir FeedEvent EXPORT_STARTED / EXPORT_COMPLETED.

## Errores & Recuperación
- FAILED permanente si 3 retries agotados. Usuario puede relanzar.
- Versionado: si libro se actualiza (nueva edición) -> crear nuevo ExportJob revision.

## Seguridad
- Secretos por plataforma almacenados en Vault (scoped roles). Rotación 90 días.
- Logs de subida excluyen contenido completo (solo IDs y checksums).

## Rate Limiting
- Cada integración con límites propios → throttle central para evitar bloqueos.
