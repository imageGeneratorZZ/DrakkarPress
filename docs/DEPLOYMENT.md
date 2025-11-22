# 🚀 DrakkarPress Deployment Guide

## 📋 Table of Contents
- [Overview](#overview)
- [Production Environment](#production-environment)
- [Local Development](#local-development)
- [CI/CD Pipeline](#cicd-pipeline)
- [Environment Variables](#environment-variables)
- [Database Management](#database-management)
- [Monitoring & Logs](#monitoring--logs)
- [Troubleshooting](#troubleshooting)

---

## Overview

DrakkarPress uses Railway for backend hosting with automatic deployments from GitHub. The platform is built with:

- **Backend**: Java 21 + Spring Boot 3.5.3
- **Database**: PostgreSQL (Railway-managed)
- **Container**: Docker multi-stage build
- **CI/CD**: GitHub Actions
- **Frontend**: Static hosting on Netlify with API proxy

---

## Production Environment

### Railway Configuration

**Project Details:**
- **Project**: overflowing-consideration
- **Project ID**: `887d34c6-8453-4745-b4c6-52000a0c80b3`
- **Service ID**: `5c1f4d66-7bcd-4e9f-8420-cc2efb14cc32`
- **Region**: asia-southeast1
- **Production URL**: `https://overflowing-consideration-production.up.railway.app`

**Health Check:**
- **Path**: `/api/health`
- **Timeout**: 100 seconds
- **Retry Window**: 1m40s
- **Expected Response**: `{"status":"UP","timestamp":"..."}`

### Manual Deployment

If automatic deployment fails or you need to force a deploy:

```powershell
# From backend directory
cd backend
railway up

# Monitor deployment
railway logs --tail
```

### Deployment Verification

Use the E2E verification script to test all endpoints:

```powershell
# Basic verification
.\backend\verify-production.ps1

# Include refresh token test
.\backend\verify-production.ps1 -IncludeRefresh

# Full test including social login
.\backend\verify-production.ps1 -IncludeRefresh -IncludeSocial
```

**Expected Output:**
```
=== Verificación Producción DrakkarPress ===
[HEALTH] Status: 200
[PING] Status: 200 Marker: ping-controller-v1
[REGISTER] Status: 200 User: e2e+XXXXX@example.com
[LOGIN] Status: 200 TokenPresent: True RefreshPresent: True
[PROFILE GET] Status: 200
[PROFILE PUT] Status: 200
[REFRESH] Status: 200 NewToken: True

E2E básico OK ✓
```

---

## Local Development

### Prerequisites

- **Java 21** (JDK 21.0.9+)
- **Maven 3.9.6+** (or use Maven wrapper: `./mvnw`)
- **PostgreSQL 15+** (local or Docker)
- **Railway CLI** (for production deployments)

### Setup

1. **Clone repository:**
   ```bash
   git clone https://github.com/imageGeneratorZZ/DrakkarPress.git
   cd DrakkarPress
   ```

2. **Configure environment variables:**
   Create `backend/.env` file:
   ```env
   DATABASE_URL=jdbc:postgresql://localhost:5432/drakkarpress
   DB_USERNAME=postgres
   DB_PASSWORD=your_password
   JWT_SECRET=your-super-secret-jwt-key-min-32-chars
   CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5500
   JPA_DDL_AUTO=update
   PORT=8080
   ```

3. **Start PostgreSQL** (if using Docker):
   ```bash
   docker run --name drakkarpress-db \
     -e POSTGRES_DB=drakkarpress \
     -e POSTGRES_PASSWORD=your_password \
     -p 5432:5432 \
     -d postgres:15-alpine
   ```

4. **Build and run backend:**
   ```powershell
   cd backend
   
   # Build only
   .\mvnw clean package -DskipTests
   
   # Build and run
   .\mvnw spring-boot:run
   
   # Or run JAR directly
   java -jar target/app.jar
   ```

5. **Verify local backend:**
   ```powershell
   # Health check
   curl http://localhost:8080/api/health
   
   # Ping check
   curl http://localhost:8080/api/ping
   ```

### Known Issues

#### PublicationOrchestrationService Error

If you encounter build errors related to `PublicationOrchestrationService.java.skip`:

**Temporary fix:**
```bash
# Skip tests during build
./mvnw clean package -DskipTests

# Or disable problematic service with profile
# Add to application.properties:
spring.profiles.active=dev
# Then annotate service with @Profile("production")
```

**Permanent fix** (TODO):
- Review external service dependencies (Investigatron, Lulu, Shopify APIs)
- Create mock implementations for local development
- Use `@ConditionalOnProperty` to make services optional

---

## CI/CD Pipeline

### GitHub Actions Workflow

The project uses GitHub Actions for automated testing and deployment.

**Workflow file:** `.github/workflows/deploy.yml`

**Trigger:** Push to `main` branch (when backend files change)

**Jobs:**
1. **Test**: Run unit tests with Maven
2. **Deploy**: Deploy to Railway and verify health

### Setup Instructions

1. **Get Railway Token:**
   ```bash
   railway login
   railway tokens
   ```

2. **Add secrets to GitHub:**
   - Go to repository Settings → Secrets and variables → Actions
   - Add secrets:
     - `RAILWAY_TOKEN`: Your Railway API token
     - `RAILWAY_SERVICE_ID`: `5c1f4d66-7bcd-4e9f-8420-cc2efb14cc32`

3. **Test workflow:**
   ```bash
   # Make a change and push
   git add .
   git commit -m "test: trigger CI/CD workflow"
   git push origin main
   ```

4. **Monitor workflow:**
   - Go to Actions tab in GitHub repository
   - View workflow run logs
   - Check deployment status

### Manual Workflow Trigger

You can manually trigger deployment from GitHub:
- Go to Actions → Deploy to Railway
- Click "Run workflow"
- Select branch (main)
- Click "Run workflow"

---

## Environment Variables

### Required Variables (Production)

Configure in Railway → Settings → Variables:

| Variable | Description | Example |
|----------|-------------|---------|
| `DATABASE_URL` | PostgreSQL connection string | Auto-set by Railway |
| `JWT_SECRET` | Secret key for JWT signing | 32+ character random string |
| `PORT` | Server port | 8080 (auto-set by Railway) |

### Optional Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JPA_DDL_AUTO` | Hibernate DDL mode | `update` |
| `CORS_ALLOWED_ORIGINS` | Comma-separated CORS origins | Production domains |
| `JWT_ACCESS_TOKEN_EXPIRATION` | Access token lifetime (ms) | 900000 (15min) |
| `JWT_REFRESH_TOKEN_EXPIRATION` | Refresh token lifetime (ms) | 2592000000 (30 days) |
| `LOGGING_LEVEL_ROOT` | Root logging level | WARN |
| `LOGGING_LEVEL_DRAKKARPRESS` | App logging level | INFO |

### Generating JWT Secret

```bash
# Linux/Mac
openssl rand -base64 32

# PowerShell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Minimum 0 -Maximum 256 }))

# Or use online generator
https://generate-secret.vercel.app/32
```

---

## Database Management

### Railway PostgreSQL

Railway automatically provisions and manages PostgreSQL database.

**Access credentials:**
- Available in Railway → Database → Connect
- Automatically injected as `DATABASE_URL` environment variable

**Connection format:**
```
postgresql://user:password@host:port/database
```

### Migrations

The application uses Hibernate with `JPA_DDL_AUTO=update` for automatic schema updates.

**For production-grade migrations**, consider:
- Flyway
- Liquibase
- Manual SQL migration scripts

### Backup Strategy

**Railway automatic backups:**
- Daily snapshots (retained for 7 days)
- Point-in-time recovery available

**Manual backup:**
```bash
# Export database
railway run pg_dump > backup.sql

# Restore database
railway run psql < backup.sql
```

---

## Monitoring & Logs

### Railway Dashboard

Access metrics and logs at:
`https://railway.com/project/887d34c6-8453-4745-b4c6-52000a0c80b3`

**Available metrics:**
- CPU usage
- Memory usage
- Network traffic
- Request count
- Response times

### Live Logs

```bash
# Tail logs in real-time
railway logs --tail

# View specific deployment logs
railway logs --deployment <deployment-id>

# Filter logs by level
railway logs --tail | grep ERROR
```

### Application Logging

Configured in `application.properties`:

```properties
# Framework logs: WARN (reduce verbosity)
logging.level.root=WARN
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN

# Application logs: INFO
logging.level.com.drakkarpress=INFO

# Security logs: ERROR only for access decisions
logging.level.org.springframework.security=WARN
logging.level.org.springframework.security.web.access.intercept=ERROR
```

### Health Monitoring

**Endpoints:**
- **Health**: `GET /api/health` (200 if UP)
- **Ping**: `GET /api/ping` (includes deployment marker)

**External monitoring options:**
- UptimeRobot (free tier: 50 monitors)
- BetterStack (free tier: 10 monitors)
- Railway built-in uptime monitoring

---

## Troubleshooting

### Deployment Fails at Health Check

**Symptom:**
```
[1/1] Healthcheck failed!
Attempt #1 failed with service unavailable
```

**Causes:**
1. Application takes too long to start (>100s)
2. Health endpoint returning non-200 status
3. Port mismatch (app not listening on $PORT)

**Solutions:**
```bash
# Check logs for startup errors
railway logs --deployment <deployment-id>

# Verify health endpoint locally
curl http://localhost:8080/api/health

# Ensure PORT alignment
# Dockerfile EXPOSE and application.properties should match
```

### Bean Definition Conflicts

**Symptom:**
```
ConflictingBeanDefinitionException: Annotation-specified bean name 'controller' 
conflicts with existing, non-compatible bean definition
```

**Solution:**
- Check for duplicate controller classes in different packages
- Ensure each controller has a unique bean name
- Remove legacy/backup controller files

### Database Connection Errors

**Symptom:**
```
org.postgresql.util.PSQLException: Connection refused
```

**Solutions:**
1. Verify `DATABASE_URL` is set correctly
2. Check Railway database service is running
3. Ensure database service is linked to backend service
4. Test connection manually:
   ```bash
   railway run psql $DATABASE_URL -c "SELECT 1"
   ```

### JWT Token Errors

**Symptom:**
```
401 Unauthorized on authenticated endpoints
```

**Solutions:**
1. Verify `JWT_SECRET` is set and same across deployments
2. Check token expiration (access tokens expire after 15min)
3. Use refresh token endpoint to get new access token
4. Ensure `Authorization: Bearer <token>` header format is correct

### CORS Errors

**Symptom:**
```
Access to fetch at '...' from origin '...' has been blocked by CORS policy
```

**Solutions:**
1. Add origin to `CORS_ALLOWED_ORIGINS` environment variable
2. Verify CORS configuration in `SecurityConfig.java`
3. Check request includes proper headers:
   ```javascript
   headers: {
     'Content-Type': 'application/json',
     'Authorization': 'Bearer token'
   }
   ```

### High Memory Usage

**Symptom:**
Railway dashboard shows memory usage above 512MB consistently

**Solutions:**
1. Review logging verbosity (reduce to WARN)
2. Check for memory leaks in application code
3. Increase Railway plan memory limits
4. Add JVM heap limits:
   ```bash
   # In Dockerfile or railway.json
   JAVA_OPTS="-Xmx384m -Xms256m"
   ```

### Build Timeout

**Symptom:**
```
Build failed: Timeout after 10 minutes
```

**Solutions:**
1. Enable Maven dependency caching
2. Use `-o` offline mode if dependencies already downloaded
3. Optimize Docker build layers
4. Consider upgrading Railway plan for more build resources

---

## Support & Resources

### Documentation
- **API Documentation**: `docs/FRONTEND_INTEGRATION.md`
- **Architecture**: `ARQUITECTURA_ECOSISTEMA_COMPLETO.md`
- **Testing Guide**: `TESTING_GUIDE_PRODUCTION.md`

### Tools
- **Railway CLI**: `railway --help`
- **Maven Wrapper**: `./mvnw --help`
- **Verification Script**: `.\backend\verify-production.ps1 -?`

### Railway Support
- **Dashboard**: https://railway.com/dashboard
- **Documentation**: https://docs.railway.com
- **Community**: https://discord.gg/railway

### GitHub
- **Repository**: https://github.com/imageGeneratorZZ/DrakkarPress
- **Issues**: https://github.com/imageGeneratorZZ/DrakkarPress/issues
- **Actions**: https://github.com/imageGeneratorZZ/DrakkarPress/actions

---

**Last Updated**: 2025-01-22  
**Platform Version**: Spring Boot 3.5.3  
**Java Version**: 21  
**Railway Region**: asia-southeast1
