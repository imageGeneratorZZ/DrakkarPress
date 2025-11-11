# DrakkarPress Backend - Database Setup Guide

## Prerequisites

1. **PostgreSQL 12+** installed on your system
2. **Java 17** or higher
3. **Maven 3.8+**

## Database Setup

### Option 1: Manual Setup

1. **Start PostgreSQL** service:
```bash
# Windows (PowerShell as Administrator)
Start-Service postgresql

# Or start from Services app
services.msc
```

2. **Create the database**:
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE drakkarpress;

# Exit psql
\q
```

3. **Run initialization script**:
```bash
psql -U postgres -d drakkarpress -f init-db.sql
```

### Option 2: Using Docker

```bash
# Pull PostgreSQL image
docker pull postgres:15

# Run PostgreSQL container
docker run --name drakkarpress-db \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=drakkarpress \
  -p 5432:5432 \
  -d postgres:15

# Wait a few seconds for the database to start
timeout 10

# Run initialization script
docker exec -i drakkarpress-db psql -U postgres -d drakkarpress < init-db.sql
```

## Environment Configuration

1. **Copy the example environment file**:
```bash
cp .env.example .env
```

2. **Edit `.env`** and update the values:
   - Database credentials
   - JWT secret (use a strong 256-bit key)
   - OAuth2 credentials (optional, for social login)
   - Stripe API keys (optional, for payments)
   - Email configuration (optional, for notifications)

## Running the Application

1. **Build the project**:
```bash
mvn clean install
```

2. **Run the application**:
```bash
mvn spring-boot:run
```

Or run with specific profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

3. **Test the connection**:

Open your browser and go to:
- Health check: http://localhost:8080/api/health
- Database health: http://localhost:8080/api/health/db
- Actuator: http://localhost:8080/actuator/health

## Default Credentials

After initialization, you can login with:
- **Username**: `admin`
- **Email**: `admin@drakkarpress.com`
- **Password**: `admin123`

**⚠️ IMPORTANT**: Change this password in production!

## Database Schema

The application uses JPA/Hibernate with `ddl-auto: update`, which means:
- Tables will be created automatically on first run
- Schema will be updated automatically when entities change
- Existing data will NOT be deleted

### Main Tables Created:

- `users` - User accounts (readers, authors, printers, resellers, admins)
- `books` - Book catalog
- `sales` - Sales transactions
- `reviews` - Book reviews
- `user_library` - User's purchased books
- `marketing_campaigns` - Marketing campaigns
- `ai_generations` - AI-generated content

## Troubleshooting

### Connection Refused

If you get "Connection refused" errors:

1. Check if PostgreSQL is running:
```bash
# Windows
Get-Service postgresql*

# Start if not running
Start-Service postgresql
```

2. Verify connection parameters in `.env`:
```
DATABASE_URL=jdbc:postgresql://localhost:5432/drakkarpress
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres
```

### Authentication Failed

If authentication fails:

1. Check PostgreSQL password:
```bash
psql -U postgres -d drakkarpress
```

2. Update password if needed:
```bash
psql -U postgres
ALTER USER postgres PASSWORD 'newpassword';
\q
```

3. Update `.env` with new password

### Port Already in Use

If port 8080 is already in use:

1. Change port in `.env`:
```
PORT=8081
```

2. Or stop the service using port 8080:
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

## Production Deployment

For production deployment:

1. **Change all default passwords**
2. **Use strong JWT secret** (256-bit minimum)
3. **Set `ddl-auto: validate`** in `application.yml`
4. **Enable SSL/TLS** for database connection
5. **Configure backup strategy**
6. **Set up monitoring** with Actuator + Prometheus
7. **Use environment variables** instead of `.env` file

## API Endpoints

Once running, available endpoints:

- `GET /api/health` - Application health check
- `GET /api/health/db` - Database health check
- `GET /actuator/health` - Detailed health information
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus metrics

## Next Steps

1. Configure OAuth2 providers (optional)
2. Set up Stripe for payments (optional)
3. Configure email service (optional)
4. Set up file storage (S3 or local)
5. Create additional user accounts
6. Import book catalog

## Support

For issues or questions:
- Check logs in `logs/drakkarpress.log`
- Review Spring Boot actuator endpoints
- Check database logs in PostgreSQL log directory
