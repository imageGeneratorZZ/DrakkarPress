## Autenticación JWT

La plataforma ahora utiliza JSON Web Tokens (JWT) para autenticar usuarios en lugar del header personalizado `X-User-Id`.

### Flujo Básico
1. Registro/Login: El cliente llama a `POST /api/auth/register` o `POST /api/auth/login` y recibe `{ token, userId }`.
2. Autenticación: Cada llamada subsecuente incluye `Authorization: Bearer <token>`.
3. El filtro `JwtAuthenticationFilter` valida la firma y construye un `JwtUserPrincipal` con `userId`, `username`, `role`.
4. Controladores acceden al usuario mediante `Authentication` (Spring Security) sin headers ad-hoc.

### Seguridad
- Token firmado HS256 con secreto configurable (`security.jwt.secret`).
- Expiración configurable (`security.jwt.expiration-ms`, default 24h).
- Stateless: No se mantienen sesiones en servidor (ideal para escalamiento horizontal).

### Rate Limiting
Se añadió anotación `@RateLimit` con almacenamiento en memoria para límites diarios por usuario:
```java
@RateLimit(key = "book-generation", limit = 5)
public ResponseEntity<ApiResponse<BookGenerationJob>> generateBook(...)
```
Lanza HTTP 429 si se excede el límite.

### Próximos Pasos (Hardening)
- Refresh tokens + rotación.
- Lista de revocación / cierre de sesión.
- Límite dinámico según plan (FREE vs PREMIUM).
- Almacenamiento distribuido (Redis) para rate limiting y escalado.

### Migración Realizada
- Eliminado `HeaderUserFilter` y `AuthUserResolver`.
- Reemplazado `X-User-Id` en controladores por `Authentication`.
- Añadido endpoints básicos de auth y documentación.

### Ejemplo de Petición
```
POST /api/auth/login
{ "email": "user@example.com", "password": "secret" }

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
GET /api/ai/books/generate
```

### Códigos de Error
- 401: Credenciales inválidas / token ausente.
- 429: Rate limit excedido.
- 403: Intento de acceder a endpoint admin sin rol adecuado.
