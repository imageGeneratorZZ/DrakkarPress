# Railway Hobby Plan Configuration
# Plan: $5/month
# Resources: 8GB RAM, 100GB disk, unlimited execution time

## Recursos Disponibles

### RAM: 8 GB
- Backend Spring Boot: 2-3 GB (configurado para usar hasta 70% de container asignado)
- PostgreSQL: 1-2 GB
- Margen: 3-4 GB para picos y cache

### Disco: 100 GB
- Sistema + Docker images: ~2 GB
- Base de datos inicial: ~1 GB
- Crecimiento proyectado: ~50-70 GB disponibles para datos
- Logs y cache: ~5 GB

### Límites
- Sin límite de horas de ejecución (24/7)
- Reintentos automáticos: 10 (configurado en railway.json)
- Healthcheck: 60s start period, 30s interval

## Optimizaciones Aplicadas

### JVM (Java Virtual Machine)
```
-XX:MaxRAMPercentage=70.0          # Usa hasta 70% de RAM del container
-XX:InitialRAMPercentage=40.0      # Inicia con 40% para arranque rápido
-XX:+UseG1GC                       # G1 Garbage Collector (mejor para >2GB RAM)
-XX:MaxGCPauseMillis=100           # Pausas de GC máximo 100ms
-XX:MaxMetaspaceSize=256m          # Metadata de clases (suficiente para Spring Boot)
```

### Docker
- Multi-stage build: Build stage separado de runtime
- Base image: eclipse-temurin:21-jre (optimizada, sin Alpine por compatibilidad)
- Healthcheck: Activo en /actuator/health
- Usuario no-root: appuser (seguridad)

### HikariCP (Database Connection Pool)
```
spring.datasource.hikari.maximum-pool-size=10      # Hasta 10 conexiones concurrentes
spring.datasource.hikari.minimum-idle=2            # 2 conexiones idle mínimo
spring.datasource.hikari.connection-timeout=20000  # 20s timeout
```

## Escalabilidad Futura

### Cuando escalar a Railway Pro ($20/mes):
- Múltiples réplicas del backend (alta disponibilidad)
- Regiones múltiples (latencia global)
- Database dedicado con backups automáticos
- Monitoreo avanzado

### Señales para escalar:
- Uso constante >80% RAM
- Latencia >500ms en API
- >1000 usuarios concurrentes
- Database >50GB

## Monitoreo Recomendado

- Railway Dashboard: Métricas básicas incluidas
- Logs: `railway logs --tail 100`
- Health endpoint: https://[tu-app].up.railway.app/actuator/health
- Database stats: Railway Dashboard → Postgres → Metrics
