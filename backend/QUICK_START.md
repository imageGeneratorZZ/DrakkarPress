# ========================================
# DrakkarPress Platform - Quick Start Guide
# ========================================

## 📦 Prerequisitos

### 1. Java 17+
```powershell
# Verificar instalación
java -version

# Debe mostrar: java version "17" o superior
```

### 2. Maven 3.9+
```powershell
# Verificar instalación
mvn -version

# Debe mostrar: Apache Maven 3.9.x
```

### 3. PostgreSQL 14+
```powershell
# Verificar que está corriendo
Get-Service -Name postgresql*

# Si está detenido:
Start-Service postgresql-x64-14
```

## 🚀 Inicio Rápido (3 pasos)

### Paso 1: Setup Base de Datos
```powershell
# Crear base de datos
psql -U postgres
CREATE DATABASE drakkarpress_db;
CREATE USER drakkarpress_user WITH PASSWORD 'change_this_password_123';
GRANT ALL PRIVILEGES ON DATABASE drakkarpress_db TO drakkarpress_user;
\q

# Ejecutar scripts SQL
cd backend
psql -U drakkarpress_user -d drakkarpress_db -f src/main/resources/db/schema.sql
psql -U drakkarpress_user -d drakkarpress_db -f src/main/resources/db/init-data.sql
```

### Paso 2: Configurar Credenciales
Editar `backend/src/main/resources/application.properties`:
```properties
# Línea 5: Cambiar contraseña
spring.datasource.password=TU_CONTRASEÑA_SEGURA

# Línea 20: Cambiar JWT secret
jwt.secret=TU_SECRET_KEY_MINIMO_256_BITS
```

### Paso 3: Iniciar Backend
```powershell
# Opción A: Usar script automático
cd backend
./start.bat

# Opción B: Manual
cd backend
mvn clean install
mvn spring-boot:run
```

## ✅ Verificar que funciona

### 1. Health Check
```powershell
curl http://localhost:8080/api/auth/health
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Success",
  "data": "OK"
}
```

### 2. Registrar primer usuario
```powershell
curl -X POST http://localhost:8080/api/auth/register `
  -H "Content-Type: application/json" `
  -d '{
    "email": "test@drakkarpress.com",
    "username": "testuser",
    "password": "password123",
    "displayName": "Test User"
  }'
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci...",
    "tokenType": "Bearer",
    "expiresIn": 900,
    "userId": "uuid-here",
    "email": "test@drakkarpress.com",
    "username": "testuser",
    "displayName": "Test User",
    "userNumber": 1,
    "membershipPlan": "FREE"
  }
}
```

### 3. Login
```powershell
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{
    "emailOrUsername": "test@drakkarpress.com",
    "password": "password123"
  }'
```

## 🎉 ¡Listo!

El backend está corriendo en **http://localhost:8080**

### Endpoints disponibles:
- ✅ `POST /api/auth/register` - Registrar usuario
- ✅ `POST /api/auth/login` - Login
- ✅ `POST /api/auth/refresh` - Refresh token
- ✅ `POST /api/auth/logout` - Logout
- ✅ `GET /api/auth/health` - Health check

## 🔧 Troubleshooting

### Error: "Cannot connect to database"
```powershell
# Verificar que PostgreSQL está corriendo
Get-Service -Name postgresql*

# Si está detenido:
Start-Service postgresql-x64-14
```

### Error: "Access denied for user"
```sql
-- Reconectar como postgres
psql -U postgres -d drakkarpress_db

-- Otorgar permisos
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO drakkarpress_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO drakkarpress_user;
```

### Error: "Port 8080 already in use"
```powershell
# Matar proceso en puerto 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# O cambiar puerto en application.properties:
server.port=8081
```

## 📚 Documentación Completa

Ver: `backend/README_BACKEND_COMPLETE.md`
