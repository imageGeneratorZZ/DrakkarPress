# Guía de Pruebas Endpoints - DrakkarPress

Backend corriendo en `http://localhost:8080` con perfil H2.

## 1. Obtener Token Admin

El servidor seedó un admin con:
- **Usuario**: admin@drakkarpress.local
- **Password**: Admin123!

### Login

```powershell
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method POST -ContentType "application/json" -Body '{"email":"admin@drakkarpress.local","password":"Admin123!"}'
$token = $loginResponse.data.token
$token
```

O usando curl:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@drakkarpress.local","password":"Admin123!"}'
```

Guarda el token devuelto en variable `$token` (PowerShell) o exporta en bash:

```bash
export TOKEN="<tu-token-aqui>"
```

---

## 2. Endpoints de Comisiones (Admin)

### GET /api/admin/commission-config

Lista todas las configuraciones de comisiones.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/commission-config" -Method GET -Headers @{Authorization="Bearer $token"}
```

Filtrar por contexto:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/commission-config?context=DIRECT" -Method GET -Headers @{Authorization="Bearer $token"}
```

### POST /api/admin/commission-config

Crear nueva configuración de comisión.

```powershell
$commissionBody = @{
    context = "RESELLER"
    authorPercent = 65
    resellerPercent = 25
    platformPercent = 10
    printShopPercent = 0
    minVolume = 2000
    isActive = $true
    effectiveFrom = (Get-Date).ToString("yyyy-MM-ddTHH:mm:ss")
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/admin/commission-config" -Method POST -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $commissionBody
```

### PATCH /api/admin/commission-config/{id}/deactivate

Desactivar configuración de comisión (reemplaza `{id}` con UUID real).

```powershell
$configId = "<UUID-aqui>"
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/commission-config/$configId/deactivate" -Method PATCH -Headers @{Authorization="Bearer $token"}
```

---

## 3. Endpoints de Descuentos (Admin)

### GET /api/admin/discount-rules

Lista todas las reglas de descuento.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/discount-rules" -Method GET -Headers @{Authorization="Bearer $token"}
```

Filtrar por tipo:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/discount-rules?type=VOLUME" -Method GET -Headers @{Authorization="Bearer $token"}
```

### POST /api/admin/discount-rules

Crear nueva regla de descuento.

**Descuento por volumen 50+ unidades:**

```powershell
$discountBody = @{
    ruleType = "VOLUME"
    minQuantity = 50
    percentOff = 10
    description = "10% off al comprar 50+ copias"
    isActive = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/admin/discount-rules" -Method POST -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $discountBody
```

**Descuento por cupón:**

```powershell
$couponBody = @{
    ruleType = "COUPON"
    couponCode = "LAUNCH2025"
    percentOff = 25
    description = "Cupón de lanzamiento 2025"
    isActive = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/admin/discount-rules" -Method POST -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $couponBody
```

### PATCH /api/admin/discount-rules/{id}/deactivate

Desactivar regla de descuento.

```powershell
$ruleId = "<UUID-aqui>"
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/discount-rules/$ruleId/deactivate" -Method PATCH -Headers @{Authorization="Bearer $token"}
```

---

## 4. Endpoint Perfil Público

### GET /api/profile/{username}

Obtiene perfil público de un usuario.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/profile/admin" -Method GET
```

No requiere autenticación. Devuelve username, fullName, country, language, plan, premium status, rolesCount, createdAt.

---

## 5. Endpoints Dashboards por Rol

### GET /api/author/dashboard

Dashboard de autor con métricas de ventas.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/author/dashboard" -Method GET -Headers @{Authorization="Bearer $token"}
```

Devuelve `revenue` y `salesLast30Days`.

### GET /api/reseller/dashboard

Dashboard de revendedor.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/reseller/dashboard" -Method GET -Headers @{Authorization="Bearer $token"}
```

Devuelve `revenue` total del revendedor.

### GET /api/print-shop/dashboard

Dashboard de imprenta.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/print-shop/dashboard" -Method GET -Headers @{Authorization="Bearer $token"}
```

Devuelve `pendingJobs`, `completedJobs`, `avgTurnaroundDays` (placeholder con 0s por ahora).

---

## 6. Endpoint Otorgar Membresía Premium (Admin)

### POST /api/admin/memberships/grant

Otorga membresía premium a un usuario.

```powershell
$grantBody = @{
    username = "admin"
    plan = "PREMIUM_PHASE_2"
    isCourtesy = $true
    courtesyReason = "Staff member"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/admin/memberships/grant" -Method POST -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $grantBody
```

---

## 7. Endpoint Métricas Sociales (Admin)

### GET /api/admin/metrics/social

Obtiene contadores de login social.

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/metrics/social" -Method GET -Headers @{Authorization="Bearer $token"}
```

Devuelve `newSocialUsers` y `existingSocialUsers`.

---

## 8. Endpoint Generar Reporte (Admin)

### POST /api/admin/reports/generate

Genera reporte estratégico en formato Markdown.

```powershell
$reportBody = @{
    periodDays = 30
    includeMetrics = $true
    includeRecommendations = $true
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/admin/reports/generate" -Method POST -ContentType "application/json" -Headers @{Authorization="Bearer $token"} -Body $reportBody
```

Devuelve markdown del reporte generado.

---

## 9. Verificar Seeds

Consulta H2 Console (http://localhost:8080/h2-console):

- JDBC URL: `jdbc:h2:mem:drakkar`
- User: `sa`
- Password: *(vacío)*

```sql
SELECT * FROM commission_config;
SELECT * FROM discount_rules;
SELECT * FROM users;
SELECT * FROM memberships;
```

Deberías ver:
- 4 configs de comisión (DIRECT base y high volume, RESELLER base y high volume).
- 4 reglas de descuento (PHASE FOUNDER, VOLUME 10+, COUPON SAVE10, COURTESY 100%).
- 1 usuario admin con rol ADMIN y membresía PREMIUM_PHASE_1.

---

## Notas

- Todos los endpoints admin requieren rol `ADMIN` en el token JWT.
- Dashboards requieren roles específicos (`AUTHOR`, `RESELLER`, `PRINT_SHOP`) o `ADMIN`.
- Perfil público `/api/profile/{username}` es público (sin auth).
- Para crear más usuarios de prueba con diferentes roles, usa `/api/auth/register` y luego otorga roles mediante insert manual o extiende seeder.
