# 🔐 Frontend Integration Guide - DrakkarPress API

## 📋 Table of Contents
- [Overview](#overview)
- [Authentication Flow](#authentication-flow)
- [Token Management](#token-management)
- [API Endpoints](#api-endpoints)
- [Code Examples](#code-examples)
- [Error Handling](#error-handling)
- [Security Best Practices](#security-best-practices)

---

## Overview

The DrakkarPress API uses **JWT (JSON Web Tokens)** for authentication. All authenticated endpoints require an `Authorization` header with a Bearer token.

**Base URL (Production):** `https://overflowing-consideration-production.up.railway.app`

**Token Types:**
- **Access Token**: Short-lived (15 minutes), used for API requests
- **Refresh Token**: Long-lived (30 days), used to obtain new access tokens

---

## Authentication Flow

### 1. Registration
**Endpoint:** `POST /api/auth/register`

**Request:**
```json
{
  "email": "user@example.com",
  "username": "username123",
  "password": "SecurePass123!"
}
```

**Response (200):**
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

### 2. Login
**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "SecurePass123!"
}
```

**Response (200):**
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

### 3. Social Login (Mock)
**Endpoint:** `POST /api/auth/social`

**Request:**
```json
{
  "provider": "google",
  "externalToken": "google_oauth_token_here",
  "email": "",
  "username": ""
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Social login exitoso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "username": "google_user123",
    "provider": "google"
  }
}
```

### 4. Refresh Token
**Endpoint:** `POST /api/auth/refresh`

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200):**
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

**Response (401):**
```json
{
  "success": false,
  "message": "Refresh token inválido o expirado"
}
```

---

## Token Management

### Storage
Store tokens securely in `localStorage` or `sessionStorage`:

```javascript
// After successful login/register
localStorage.setItem('accessToken', data.data.token);
localStorage.setItem('refreshToken', data.data.refreshToken);
localStorage.setItem('userId', data.data.userId);
localStorage.setItem('username', data.data.username);

// Retrieve tokens
const accessToken = localStorage.getItem('accessToken');
const refreshToken = localStorage.getItem('refreshToken');
```

### Auto-Refresh Strategy

Implement automatic token refresh when receiving 401 errors:

```javascript
async function apiCallWithRefresh(endpoint, options) {
  let response = await fetch(endpoint, options);
  
  // If unauthorized, try refreshing token
  if (response.status === 401) {
    const refreshToken = localStorage.getItem('refreshToken');
    
    if (!refreshToken) {
      // No refresh token, redirect to login
      window.location.href = '/login.html';
      return;
    }
    
    // Attempt refresh
    const refreshResponse = await fetch('/api/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken })
    });
    
    if (refreshResponse.ok) {
      const data = await refreshResponse.json();
      localStorage.setItem('accessToken', data.data.token);
      
      // Retry original request with new token
      options.headers['Authorization'] = `Bearer ${data.data.token}`;
      response = await fetch(endpoint, options);
    } else {
      // Refresh failed, redirect to login
      window.location.href = '/login.html';
    }
  }
  
  return response;
}
```

---

## API Endpoints

### Profile Management

#### Get Own Profile
**Endpoint:** `GET /api/profile/me`  
**Auth:** Required

**Request Headers:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
```

**Response (200):**
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
    "createdAt": "2025-01-15T10:30:00Z"
  }
}
```

#### Update Own Profile
**Endpoint:** `PUT /api/profile/me`  
**Auth:** Required

**Request Headers:**
```
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "John Doe",
  "bio": "Updated bio text",
  "profilePictureUrl": "https://example.com/new-avatar.jpg",
  "country": "España",
  "language": "es"
}
```

**Response (200):**
```json
{
  "success": true,
  "message": "Perfil actualizado",
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "username123",
    "fullName": "John Doe",
    "bio": "Updated bio text",
    ...
  }
}
```

#### Get Public Profile
**Endpoint:** `GET /api/profile/{username}`  
**Auth:** Not required

**Response (200):**
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

### Health & Status

#### Health Check
**Endpoint:** `GET /api/health`  
**Auth:** Not required

**Response (200):**
```json
{
  "status": "UP",
  "timestamp": "2025-01-22T19:45:00Z"
}
```

#### Deployment Ping
**Endpoint:** `GET /api/ping`  
**Auth:** Not required

**Response (200):**
```json
{
  "status": "ok",
  "epochMs": 1737577500000,
  "marker": "ping-controller-v1"
}
```

---

## Code Examples

### Vanilla JavaScript

#### Register and Store Tokens
```javascript
async function register(email, username, password) {
  try {
    const response = await fetch('https://api.drakkarpress.com/api/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, username, password })
    });
    
    const result = await response.json();
    
    if (response.ok) {
      // Store tokens
      localStorage.setItem('accessToken', result.data.token);
      localStorage.setItem('refreshToken', result.data.refreshToken);
      localStorage.setItem('userId', result.data.userId);
      localStorage.setItem('username', result.data.username);
      
      console.log('Registration successful!');
      return result.data;
    } else {
      console.error('Registration failed:', result.message);
      return null;
    }
  } catch (error) {
    console.error('Network error:', error);
    return null;
  }
}
```

#### Authenticated Request
```javascript
async function getProfile() {
  const token = localStorage.getItem('accessToken');
  
  if (!token) {
    console.error('No access token found');
    return null;
  }
  
  try {
    const response = await fetch('https://api.drakkarpress.com/api/profile/me', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    });
    
    const result = await response.json();
    
    if (response.ok) {
      return result.data;
    } else if (response.status === 401) {
      console.error('Token expired or invalid');
      // Trigger refresh flow
      return null;
    } else {
      console.error('Failed to get profile:', result.message);
      return null;
    }
  } catch (error) {
    console.error('Network error:', error);
    return null;
  }
}
```

### React Example

```jsx
import { useState, useEffect } from 'react';

const API_BASE = 'https://overflowing-consideration-production.up.railway.app';

// Custom hook for authenticated API calls
function useAuth() {
  const [token, setToken] = useState(localStorage.getItem('accessToken'));
  const [refreshToken, setRefreshToken] = useState(localStorage.getItem('refreshToken'));

  const login = async (email, password) => {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      localStorage.setItem('accessToken', data.data.token);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      setToken(data.data.token);
      setRefreshToken(data.data.refreshToken);
    }
    
    return response.ok;
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setToken(null);
    setRefreshToken(null);
  };

  const apiCall = async (endpoint, options = {}) => {
    if (!token) throw new Error('Not authenticated');
    
    const headers = {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...options.headers
    };

    let response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
    
    // Handle token expiration
    if (response.status === 401 && refreshToken) {
      const refreshResponse = await fetch(`${API_BASE}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      });
      
      if (refreshResponse.ok) {
        const data = await refreshResponse.json();
        localStorage.setItem('accessToken', data.data.token);
        setToken(data.data.token);
        
        // Retry original request
        headers['Authorization'] = `Bearer ${data.data.token}`;
        response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
      } else {
        logout();
        throw new Error('Session expired');
      }
    }
    
    return response;
  };

  return { token, login, logout, apiCall };
}

// Usage in component
function ProfilePage() {
  const { apiCall } = useAuth();
  const [profile, setProfile] = useState(null);

  useEffect(() => {
    async function loadProfile() {
      const response = await apiCall('/api/profile/me');
      const data = await response.json();
      if (response.ok) {
        setProfile(data.data);
      }
    }
    loadProfile();
  }, []);

  return (
    <div>
      {profile ? (
        <div>
          <h1>{profile.username}</h1>
          <p>{profile.bio}</p>
        </div>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
}
```

### Vue.js Example

```javascript
// auth.js - Composable for authentication
import { ref, computed } from 'vue';

const API_BASE = 'https://overflowing-consideration-production.up.railway.app';

const accessToken = ref(localStorage.getItem('accessToken'));
const refreshToken = ref(localStorage.getItem('refreshToken'));

export function useAuth() {
  const isAuthenticated = computed(() => !!accessToken.value);

  async function login(email, password) {
    const response = await fetch(`${API_BASE}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    });
    
    const data = await response.json();
    
    if (response.ok) {
      accessToken.value = data.data.token;
      refreshToken.value = data.data.refreshToken;
      localStorage.setItem('accessToken', data.data.token);
      localStorage.setItem('refreshToken', data.data.refreshToken);
      return true;
    }
    return false;
  }

  async function apiCall(endpoint, options = {}) {
    const headers = {
      'Authorization': `Bearer ${accessToken.value}`,
      'Content-Type': 'application/json',
      ...options.headers
    };

    let response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
    
    if (response.status === 401 && refreshToken.value) {
      // Attempt refresh
      const refreshResponse = await fetch(`${API_BASE}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken: refreshToken.value })
      });
      
      if (refreshResponse.ok) {
        const data = await refreshResponse.json();
        accessToken.value = data.data.token;
        localStorage.setItem('accessToken', data.data.token);
        
        // Retry
        headers['Authorization'] = `Bearer ${accessToken.value}`;
        response = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
      }
    }
    
    return response;
  }

  function logout() {
    accessToken.value = null;
    refreshToken.value = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
  }

  return { isAuthenticated, login, logout, apiCall };
}
```

---

## Error Handling

### Common HTTP Status Codes

| Status | Meaning | Action |
|--------|---------|--------|
| 200 | Success | Process response data |
| 400 | Bad Request | Check request format/validation |
| 401 | Unauthorized | Token expired/invalid - refresh or re-login |
| 403 | Forbidden | User lacks permission |
| 404 | Not Found | Resource doesn't exist |
| 500 | Server Error | Retry or contact support |

### Error Response Format

```json
{
  "success": false,
  "message": "Error description here"
}
```

### Handling 401 Errors

```javascript
async function handleApiCall(url, options) {
  let response = await fetch(url, options);
  
  if (response.status === 401) {
    // Token expired - attempt refresh
    const refreshToken = localStorage.getItem('refreshToken');
    
    if (refreshToken) {
      const refreshResp = await fetch(`${API_BASE}/api/auth/refresh`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refreshToken })
      });
      
      if (refreshResp.ok) {
        const data = await refreshResp.json();
        localStorage.setItem('accessToken', data.data.token);
        
        // Retry original request
        options.headers['Authorization'] = `Bearer ${data.data.token}`;
        response = await fetch(url, options);
      } else {
        // Refresh failed - redirect to login
        window.location.href = '/login.html';
      }
    } else {
      // No refresh token - redirect to login
      window.location.href = '/login.html';
    }
  }
  
  return response;
}
```

---

## Security Best Practices

### 1. Token Storage
- ✅ **DO**: Store tokens in `localStorage` or `sessionStorage`
- ✅ **DO**: Clear tokens on logout
- ❌ **DON'T**: Store tokens in cookies without httpOnly flag
- ❌ **DON'T**: Expose tokens in URL parameters

### 2. HTTPS Only
- Always use HTTPS in production
- Production URL uses HTTPS: `https://overflowing-consideration-production.up.railway.app`

### 3. Token Expiration
- Access tokens expire after 15 minutes
- Refresh tokens expire after 30 days
- Implement auto-refresh before token expires

### 4. CORS
API accepts requests from:
- `http://localhost:*` (development)
- `https://drakkarpress.com`
- `https://www.drakkarpress.com`
- `https://drakkarpress.netlify.app`

### 5. Password Requirements
- Minimum 8 characters
- Include uppercase, lowercase, numbers, and special characters
- Never log or expose passwords

### 6. Input Validation
- Validate email format before submission
- Sanitize user inputs
- Handle error messages gracefully

---

## Testing Your Integration

Use the provided `frontend-integration-example.html` file to test all endpoints interactively.

**Quick Start:**
1. Open `frontend-integration-example.html` in your browser
2. Register a new user
3. Login with credentials
4. Test profile operations
5. Test token refresh

**Production Verification Script:**
```powershell
# Run full E2E test including refresh
.\backend\verify-production.ps1 -IncludeRefresh -IncludeSocial
```

---

## Support & Resources

- **API Base URL**: `https://overflowing-consideration-production.up.railway.app`
- **Health Check**: `GET /api/health`
- **Deployment Status**: `GET /api/ping`

For issues or questions, check the Railway logs or contact the development team.

---

**Last Updated**: 2025-01-22  
**API Version**: 1.0  
**JWT Implementation**: Spring Security 6.x with custom filter
