# Estado de la Configuración de DrakkarPress Backend

## ✅ Completado

1. **PostgreSQL en Docker** - FUNCIONANDO
   - Contenedor: `drakkarpress-postgres`
   - Puerto: 5432
   - Database: `drakkarpress`
   - User: `postgres`
   - Password: `postgres`
   - Estado: UP y HEALTHY

2. **Archivos de Configuración** - CREADOS
   - ✅ `docker-compose.yml` - Configuración de PostgreSQL
   - ✅ `.env` - Variables de entorno
   - ✅ `application.yml` - Configuración Spring Boot
   - ✅ Todas las clases Java necesarias
   - ✅ Scripts de inicialización

## ⚠️ Pendiente - Instalaciones Requeridas

Para ejecutar la aplicación Spring Boot, necesitas instalar:

### 1. Java 17 o superior (Tienes Java 8)

**Descargar e instalar:**
- https://adoptium.net/temurin/releases/?version=17

**Pasos:**
1. Descarga el instalador MSI de Java 17
2. Ejecuta el instalador
3. Marca la opción "Add to PATH"
4. Reinicia el terminal de PowerShell

**Verificar instalación:**
```powershell
java -version
# Debe mostrar: openjdk version "17.x.x" o superior
```

### 2. Apache Maven 3.8+

**Opción A - Instalar con Chocolatey (Recomendado):**
```powershell
# Instalar Chocolatey si no lo tienes
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Instalar Maven
choco install maven -y
```

**Opción B - Descarga Manual:**
1. Descargar desde: https://maven.apache.org/download.cgi
2. Descomprimir en `C:\Program Files\Maven`
3. Agregar a PATH:
   - Buscar "Variables de entorno"
   - Editar "Path" en Variables del sistema
   - Agregar: `C:\Program Files\Maven\bin`
4. Reiniciar terminal

**Verificar instalación:**
```powershell
mvn -version
# Debe mostrar: Apache Maven 3.x.x
```

## 🚀 Después de Instalar Java 17 y Maven

### Opción 1: Script Automático
```powershell
cd "C:\Users\SuperUsuario\DrakkarPress.com\backend"
cmd /c start.bat
```

### Opción 2: Comandos Manuales
```powershell
cd "C:\Users\SuperUsuario\DrakkarPress.com\backend"

# Compilar el proyecto
mvn clean install -DskipTests

# Ejecutar la aplicación
mvn spring-boot:run
```

## 🌐 Endpoints para Probar

Una vez que la aplicación esté corriendo:

- **API Principal**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/api/health
- **Database Health**: http://localhost:8080/api/health/db
- **Actuator**: http://localhost:8080/actuator/health

## 📊 Comandos Útiles de Docker

```powershell
# Ver estado de PostgreSQL
docker ps

# Ver logs de PostgreSQL
docker logs drakkarpress-postgres

# Detener PostgreSQL
docker-compose down

# Iniciar PostgreSQL
docker-compose up -d

# Reiniciar PostgreSQL limpio (borra datos)
docker-compose down -v
docker-compose up -d

# Conectarse a PostgreSQL
docker exec -it drakkarpress-postgres psql -U postgres -d drakkarpress
```

## 🗄️ Comandos SQL Útiles

Después de que la aplicación cree las tablas, puedes conectarte y ejecutar:

```sql
-- Conectarse a la base de datos
docker exec -it drakkarpress-postgres psql -U postgres -d drakkarpress

-- Ver todas las tablas
\dt

-- Ver datos de la tabla users
SELECT * FROM users;

-- Ver configuración de la base de datos
\l

-- Salir
\q
```

## 📝 Credenciales

**PostgreSQL:**
- Host: localhost
- Port: 5432
- Database: drakkarpress
- Username: postgres
- Password: postgres

**Admin de la aplicación (después de la primera ejecución):**
- Username: admin
- Email: admin@drakkarpress.com
- Password: admin123

## ⏭️ Próximos Pasos

1. **Instalar Java 17** (Requerido)
2. **Instalar Maven** (Requerido)
3. **Reiniciar terminal PowerShell**
4. **Ejecutar:** `cd "C:\Users\SuperUsuario\DrakkarPress.com\backend"; mvn spring-boot:run`
5. **Probar:** http://localhost:8080/api/health

## 🆘 Ayuda Adicional

Si tienes problemas:

1. Verifica que PostgreSQL esté corriendo: `docker ps`
2. Verifica Java 17: `java -version`
3. Verifica Maven: `mvn -version`
4. Revisa logs: `docker logs drakkarpress-postgres`
5. Revisa configuración en `.env`

---

**Estado Actual:** PostgreSQL ✅ | Java ❌ (necesita actualizar) | Maven ❌ (necesita instalar)
