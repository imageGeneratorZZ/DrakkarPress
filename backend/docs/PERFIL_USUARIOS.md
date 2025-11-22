# Perfiles de Usuario y Paneles

## Tipos de Rol
- CLIENT: Base obligatorio.
- AUTHOR_PUBLISHER: Publica y monetiza libros.
- RESELLER: Revende libros de autores.
- PRINT_SHOP: Gestiona producción física.

## Comisiones (Dinámicas)
Tabla `commission_config` define porcentajes por contexto y volumen. `CommissionService` selecciona el tier aplicable.

## Descuentos
Tabla `discount_rules` soporta tipos: PHASE, VOLUME, COUPON, COURTESY.

## Endpoints
- Perfil público: `GET /api/profile/{username}`
- Autor dashboard: `GET /api/author/dashboard`
- Reseller dashboard: `GET /api/reseller/dashboard`
- Print shop dashboard: `GET /api/print-shop/dashboard`
- Admin comisiones: `GET/POST/PATCH /api/admin/commission-config`
- Admin descuentos: `GET/POST/PATCH /api/admin/discount-rules`

## Flujo Venta (Futuro)
1. Base price.
2. `DiscountService.applyDiscounts` -> precio final.
3. `CommissionService.applyCommissions` -> distribución.
4. Persistir `Sale`.

## Próximos Pasos
- Integrar CommissionService en creación real de ventas.
- Añadir métricas avanzadas (top libros, evolución ingresos).
- Extender PrintShop con entidades de órdenes físicas.
