# Commission & Royalty Model

## Objetivo
Equilibrar sostenibilidad plataforma con incentivo a creadores.

## Tipos de Usuario
| Tipo | Comisión Directa (ventas internas) | Comisión Externa (royalties) | Beneficios |
|------|------------------------------------|-------------------------------|-----------|
| Free | 10% plataforma | 10% sobre royalties recibidos | Límite exportaciones mensuales, almacenamiento estándar |
| Premium | 0% plataforma (retiene 100%) | 0% (pasa íntegro) | Mayor priorización moderación, más slots export, analytics avanzados |
| Enterprise | Negociable | Negociable | Integraciones custom, SLA soporte |

## RoyaltySplit Entidad
| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | UUID | PK |
| bookId | UUID | Libro asociado |
| userId | UUID | Autor / Co-autor |
| percentage | DECIMAL(5,2) | División (suma ≤100) |
| platformFeeApplied | BOOLEAN | Si comisión aplicada |
| netAmount | DECIMAL | Monto tras deducciones |
| grossAmount | DECIMAL | Monto original |
| source | ENUM(INTERNAL_SALE, KDP_ROYALTY, GOOGLE_ROYALTY, LULU_ROYALTY) | Origen |
| createdAt | TIMESTAMP | |

## Cálculo Venta Interna
```
gross = price
platformFee = user.type == FREE ? gross * 0.10 : 0
paymentProcessorFee (ej. 2.9% + fijo) -> se resta
netToAuthor = gross - platformFee - processorFee
```

## Cálculo Royalty Externo (ej. KDP)
```
royaltyGross = amountFromPlatform
platformFee = user.type == FREE ? royaltyGross * 0.10 : 0
netToAuthor = royaltyGross - platformFee
```

## Integración `BookPurchaseService`
- Añadir método `calculateSplit(book, user, source, grossAmount)`.
- Persistir `RoyaltySplit` tras cada transacción / import batch (externo).
- Resumen mensual: agregación por source y usuario.

## Límites Free
- Exportaciones simultáneas: máx 3 activas.
- Tamaño almacenamiento libros (PDF+assets) máx 1GB total.
- Reels por día: 5.

## Auditoría
- Cada split registrado inmutable (event sourcing opcional).
- Reportes contables trimestrales.
