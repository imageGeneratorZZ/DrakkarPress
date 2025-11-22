# 📚 DrakkarPress API Reference

## Base URL
```
Production: https://overflowing-consideration-production.up.railway.app
Development: http://localhost:8080
```

## Authentication

All authenticated endpoints require a JWT Bearer token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Token Types:**
- **Access Token**: 15-minute lifetime, used for API requests
- **Refresh Token**: 30-day lifetime, used to obtain new access tokens

---

## Response Format

All API responses follow this structure:

**Success Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Error description"
}
```

---

## Endpoints

### Authentication

#### Register New User
Create a new user account and receive authentication tokens.

**Endpoint:** `POST /api/auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "username": "username123",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Registro exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "username123"
  }
}
```

**Error Responses:**
- `400 Bad Request`: Email already exists
- `400 Bad Request`: Username already exists
- `400 Bad Request`: Invalid input format

**Example:**
```bash
curl -X POST https://overflowing-consideration-production.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "newuser@example.com",
    "username": "newuser123",
    "password": "MySecurePass123!"
  }'
```

---

#### Login
Authenticate with credentials and receive tokens.

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "username123"
  }
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid credentials
- `404 Not Found`: User not found

**Example:**
```bash
curl -X POST https://overflowing-consideration-production.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "SecurePass123!"
  }'
```

---

#### Social Login (Mock)
Authenticate via social providers (Google/Facebook).

**Endpoint:** `POST /api/auth/social`

**Request Body:**
```json
{
  "provider": "google",
  "externalToken": "google_oauth_token_here",
  "email": "",
  "username": ""
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Social login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "google_abc123",
    "provider": "google"
  }
}
```

**Notes:**
- Currently a mock implementation
- Email is generated deterministically from token hash
- Supports providers: `google`, `facebook`

**Example:**
```bash
curl -X POST https://overflowing-consideration-production.up.railway.app/api/auth/social \
  -H "Content-Type: application/json" \
  -d '{
    "provider": "google",
    "externalToken": "mock_google_token_12345",
    "email": "",
    "username": ""
  }'
```

---

#### Refresh Token
Obtain a new access token using a refresh token.

**Endpoint:** `POST /api/auth/refresh`

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Token renovado",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "username123"
  }
}
```

**Error Responses:**
- `401 Unauthorized`: Invalid or expired refresh token
- `401 Unauthorized`: User not found

**Example:**
```bash
curl -X POST https://overflowing-consideration-production.up.railway.app/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

**Usage Pattern:**
1. Access token expires (401 error on API call)
2. Call `/api/auth/refresh` with stored refresh token
3. Store new access token
4. Retry original API call with new token

---

### Profile Management

#### Get Own Profile
Retrieve the authenticated user's profile.

**Endpoint:** `GET /api/profile/me`

**Headers:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Perfil encontrado",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "username123",
    "email": "user@example.com",
    "fullName": "John Doe",
    "bio": "Fantasy writer and developer",
    "profilePictureUrl": "https://example.com/avatar.jpg",
    "country": "España",
    "language": "es",
    "role": "USER",
    "subscription": "FREE",
    "createdAt": "2025-01-15T10:30:00Z",
    "updatedAt": "2025-01-20T15:45:00Z"
  }
}
```

**Error Responses:**
- `401 Unauthorized`: Missing or invalid token
- `404 Not Found`: User not found

**Example:**
```bash
curl -X GET https://overflowing-consideration-production.up.railway.app/api/profile/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

#### Update Own Profile
Update the authenticated user's profile information.

**Endpoint:** `PUT /api/profile/me`

**Headers:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "John Doe Updated",
  "bio": "Updated bio text",
  "profilePictureUrl": "https://example.com/new-avatar.jpg",
  "country": "España",
  "language": "es"
}
```

**All fields are optional.** Only include fields you want to update.

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Perfil actualizado",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "username123",
    "email": "user@example.com",
    "fullName": "John Doe Updated",
    "bio": "Updated bio text",
    "profilePictureUrl": "https://example.com/new-avatar.jpg",
    "country": "España",
    "language": "es",
    "role": "USER",
    "subscription": "FREE",
    "updatedAt": "2025-01-22T20:15:00Z"
  }
}
```

**Error Responses:**
- `401 Unauthorized`: Missing or invalid token
- `404 Not Found`: User not found

**Example:**
```bash
curl -X PUT https://overflowing-consideration-production.up.railway.app/api/profile/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "bio": "Updated bio",
    "country": "Spain"
  }'
```

---

#### Get Public Profile
Retrieve a user's public profile by username.

**Endpoint:** `GET /api/profile/{username}`

**No authentication required.**

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Perfil público encontrado",
  "data": {
    "username": "username123",
    "fullName": "John Doe",
    "bio": "Fantasy writer",
    "profilePictureUrl": "https://example.com/avatar.jpg",
    "country": "España",
    "createdAt": "2025-01-15T10:30:00Z"
  }
}
```

**Note:** Public profiles exclude sensitive information (email, role, subscription).

**Error Responses:**
- `404 Not Found`: User not found

**Example:**
```bash
curl -X GET https://overflowing-consideration-production.up.railway.app/api/profile/username123
```

---

### Health & Status

#### Health Check
Check if the API is operational.

**Endpoint:** `GET /api/health`

**No authentication required.**

**Response (200 OK):**
```json
{
  "status": "UP",
  "timestamp": "2025-01-22T20:45:00.123456Z"
}
```

**Used by:**
- Railway health checks
- Uptime monitoring services
- Load balancers

**Example:**
```bash
curl https://overflowing-consideration-production.up.railway.app/api/health
```

---

#### Deployment Ping
Verify deployment freshness and get deployment marker.

**Endpoint:** `GET /api/ping`

**No authentication required.**

**Response (200 OK):**
```json
{
  "status": "ok",
  "epochMs": 1737577500000,
  "marker": "ping-controller-v1"
}
```

**Fields:**
- `status`: Always "ok" if responding
- `epochMs`: Current Unix timestamp in milliseconds
- `marker`: Deployment version identifier

**Usage:**
- Verify new code is deployed (check marker)
- Get server time
- Quick connectivity test

**Example:**
```bash
curl https://overflowing-consideration-production.up.railway.app/api/ping
```

---

## Rate Limiting

Currently no rate limiting is enforced. Future implementation may include:
- 100 requests per minute per IP for unauthenticated endpoints
- 1000 requests per minute per user for authenticated endpoints

---

## CORS Configuration

**Allowed Origins:**
- `http://localhost:*` (all localhost ports)
- `https://drakkarpress.com`
- `https://www.drakkarpress.com`
- `https://drakkarpress.netlify.app`

**Allowed Methods:**
- GET, POST, PUT, PATCH, DELETE, OPTIONS

**Allowed Headers:**
- All headers (`*`)

**Credentials:**
- Allowed (`Access-Control-Allow-Credentials: true`)

**Max Age:**
- 3600 seconds (1 hour)

---

## Error Codes Reference

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request succeeded |
| 400 | Bad Request | Invalid request format or validation error |
| 401 | Unauthorized | Missing, invalid, or expired authentication |
| 403 | Forbidden | Authenticated but lacking permission |
| 404 | Not Found | Resource doesn't exist |
| 500 | Internal Server Error | Unexpected server error |

### Common Error Messages

**Authentication Errors:**
- `"Invalid credentials"` - Wrong email/password
- `"Token expired or invalid"` - Access token no longer valid
- `"Refresh token inválido o expirado"` - Refresh token expired
- `"User not found"` - User account doesn't exist

**Validation Errors:**
- `"Email already exists"` - Email in use during registration
- `"Username already exists"` - Username taken
- `"Invalid email format"` - Email doesn't match pattern
- `"Password too weak"` - Password doesn't meet requirements

---

## JWT Token Structure

### Access Token Claims

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "username": "username123",
  "role": "USER",
  "subscription": "FREE",
  "iat": 1737577500,
  "exp": 1737578400
}
```

**Claims:**
- `sub`: User ID (UUID)
- `username`: Username string
- `role`: User role (USER, ADMIN, etc.)
- `subscription`: Subscription tier (FREE, PREMIUM, etc.)
- `iat`: Issued at (Unix timestamp)
- `exp`: Expiration time (Unix timestamp)

### Refresh Token Claims

```json
{
  "sub": "550e8400-e29b-41d4-a716-446655440000",
  "type": "refresh",
  "iat": 1737577500,
  "exp": 1740169500
}
```

**Claims:**
- `sub`: User ID (UUID)
- `type`: Token type identifier ("refresh")
- `iat`: Issued at (Unix timestamp)
- `exp`: Expiration time (Unix timestamp, +30 days)

---

## Versioning

**Current Version:** 1.0

API versioning is not currently enforced. Future versions may use:
- URL path versioning: `/api/v1/`, `/api/v2/`
- Header versioning: `Accept: application/vnd.drakkarpress.v1+json`

Breaking changes will be announced with migration guides.

---

## SDK & Client Libraries

**Official Clients:**
- None yet (coming soon)

**Community Clients:**
- JavaScript/TypeScript: See `frontend-integration-example.html`
- React: See `docs/FRONTEND_INTEGRATION.md`
- Vue.js: See `docs/FRONTEND_INTEGRATION.md`

---

## Postman Collection

Download the Postman collection for easy API testing:

**Collection includes:**
- All endpoints with examples
- Pre-configured environment variables
- Token management scripts
- Test assertions

**Setup:**
1. Import collection into Postman
2. Set environment variables:
   - `baseUrl`: Production or local URL
   - `accessToken`: Auto-set after login
   - `refreshToken`: Auto-set after login
3. Run collection tests

---

## Webhooks

**Coming Soon:**
- Payment webhooks for Stripe/PayPal
- Moderation webhooks for content approval
- Notification webhooks for events

---

## Support

**For API issues:**
- Check logs: `railway logs --tail`
- Verify health: `GET /api/health`
- Test with E2E script: `.\verify-production.ps1`

**For integration help:**
- See: `docs/FRONTEND_INTEGRATION.md`
- Examples: `frontend-integration-example.html`

**For deployment issues:**
- See: `docs/DEPLOYMENT.md`

---

**Last Updated**: 2025-01-22  
**API Version**: 1.0  
**Base URL**: `https://overflowing-consideration-production.up.railway.app`
