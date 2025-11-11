# 🚀 Guía de Acciones Manuales Pendientes

**Última actualización**: 2025-01-XX  
**Estado del proyecto**: 85% completado - Código implementado, compilación bloqueada por Lombok

---

## 📊 Resumen Ejecutivo

### ✅ Completado (Automatizado)
- [x] Frontend completo (30+ páginas HTML, i18n 6 idiomas)
- [x] Repositorio Git configurado y sincronizado
- [x] 13 guías de documentación técnica (5500+ líneas)
- [x] Archivo `.env.production` con 150+ variables
- [x] Secrets de producción generados (JWT, DB, Encryption, API keys)
- [x] Integración Lulu.com (DTOs, Service, Controller)
- [x] Integración Shopify (Service, Controller, webhooks)
- [x] Credenciales Lulu configuradas
- [x] Shopify App inicializada

### ⏳ Pendiente (Requiere Acción Manual)

| Tarea | Tiempo | Prioridad | Bloqueador |
|-------|--------|-----------|------------|
| **Compilar Backend** | 30 min | 🔴 CRÍTICO | Lombok + Java 21 |
| **Deploy Frontend** | 5 min | 🔴 CRÍTICO | OAuth Vercel |
| **Cuenta Stripe** | 20 min | 🟡 ALTA | Pagos |
| **Cuenta SendGrid** | 15 min | 🟡 ALTA | Emails |
| **AWS S3** | 30 min | 🟡 ALTA | Almacenamiento |
| **Provisionar DB** | 20 min | 🟠 MEDIA | Datos |
| **Deploy Backend** | 45 min | 🟠 MEDIA | Depende compilación |

**Tiempo total estimado**: **2 horas 45 minutos**

---

## 🔴 PASO 1: Resolver Compilación Backend (CRÍTICO)

### ❌ Problema Actual

```
❌ Lombok 1.18.32 + Java 21 + Maven 3.9.6 = 100+ errores de compilación
❌ Maven no encuentra símbolos generados (@Data, @Builder, @AllArgsConstructor, etc.)
❌ Annotation processor falla en Java 21 module system
❌ 6 intentos fallidos de compilación en línea de comandos
```

### ✅ Solución: IntelliJ IDEA + Plugin Lombok

**⏱️ Tiempo**: 30 minutos  
**📦 Requisitos**: 4 GB espacio en disco

#### Paso 1.1: Instalar IntelliJ IDEA Community

1. **Descargar**:
   ```
   https://www.jetbrains.com/idea/download/
   → Seleccionar: "Community Edition" (GRATIS)
   → Versión: 2024.3 o superior
   → Tamaño: ~900 MB
   ```

2. **Instalar**:
   - Ejecutar instalador `.exe`
   - Opciones recomendadas:
     - ☑️ Create Desktop Shortcut
     - ☑️ Add "bin" folder to PATH
     - ☑️ Add "Open Folder as Project"
     - ☑️ .java file association
   - Instalar: `C:\Program Files\JetBrains\IntelliJ IDEA Community Edition`

3. **Configurar JDK**:
   - Al abrir IntelliJ por primera vez, configurar JDK:
   - File → Project Structure → Project
   - SDK: Java 21 (detectará automáticamente)
   - Language Level: 21 - Pattern matching for switch

#### Paso 1.2: Instalar Plugin Lombok

1. **Abrir Plugin Manager**:
   ```
   File → Settings (Ctrl+Alt+S)
   → Plugins
   → Marketplace
   → Buscar: "Lombok"
   ```

2. **Instalar**:
   - Plugin: **Lombok** by Michail Plushnikov
   - Click: **Install**
   - Reiniciar IDE cuando lo pida

3. **Habilitar Annotation Processing**:
   ```
   File → Settings → Build, Execution, Deployment
   → Compiler → Annotation Processors
   ☑️ Enable annotation processing
   ```

#### Paso 1.3: Abrir Proyecto

1. **Abrir**:
   ```
   File → Open
   → Navegar a: C:\Users\SuperUsuario\DrakkarPress.com\backend
   → Seleccionar: pom.xml
   → Abrir como proyecto
   ```

2. **Esperar Indexación**:
   - IntelliJ descargará dependencias Maven (~5 min)
   - Barra de progreso inferior: "Indexing..."
   - Esperar hasta que termine

#### Paso 1.4: Compilar

1. **Build**:
   ```
   Build → Rebuild Project
   ```

2. **Verificar**:
   ```bash
   ✅ Build completed successfully
   ✅ 0 errors, 0 warnings
   ✅ Generated: target/drakkarpress-platform-1.0.0.jar
   ```

3. **Ejecutar Tests** (opcional):
   ```
   Run → Run 'All Tests'
   ```

#### Paso 1.5: Generar JAR (Producción)

1. **Maven Package**:
   ```
   Ventana Maven (lateral derecha)
   → drakkarpress-platform
   → Lifecycle
   → clean (doble click)
   → package (doble click)
   ```

2. **Ubicación JAR**:
   ```
   C:\Users\SuperUsuario\DrakkarPress.com\backend\target\drakkarpress-platform-1.0.0.jar
   Tamaño: ~60 MB
   ```

3. **Probar Localmente**:
   ```bash
   cd C:\Users\SuperUsuario\DrakkarPress.com\backend
   java -jar target/drakkarpress-platform-1.0.0.jar
   ```

   Abrir: http://localhost:8080/actuator/health
   Esperado: `{"status":"UP"}`

---

## 🔴 PASO 2: Deploy Frontend en Vercel (5 MINUTOS)

### Requisitos
- Cuenta GitHub: ✅ Ya tienes (imageGeneratorZZ)
- Repositorio: ✅ Ya está (DrakkarPress)
- Dominio: ✅ www.drakkarpress.com

### Paso 2.1: Deploy en Vercel

1. **Ir a Vercel**:
   ```
   https://vercel.com/new
   ```

2. **Login con GitHub**:
   - Click: **Continue with GitHub**
   - Autorizar acceso a repositorios

3. **Importar Proyecto**:
   - Buscar: `imageGeneratorZZ/DrakkarPress`
   - Click: **Import**

4. **Configuración**:
   ```
   Project Name: drakkarpress
   Framework Preset: Other
   Root Directory: ./
   Build Command: (dejar vacío)
   Output Directory: .
   Install Command: (dejar vacío)
   ```

5. **Environment Variables**:
   ```
   VITE_API_URL=https://api.drakkarpress.com
   VITE_STRIPE_PUBLIC_KEY=pk_live_... (Paso 3)
   ```

6. **Deploy**:
   - Click: **Deploy**
   - Esperar: ~2 minutos
   - ✅ Despliegue completado

### Paso 2.2: Configurar Dominio Personalizado

1. **Settings → Domains**:
   ```
   Add Domain: www.drakkarpress.com
   ```

2. **Configurar DNS** (en tu proveedor de dominio):
   ```
   Tipo: CNAME
   Nombre: www
   Valor: cname.vercel-dns.com
   TTL: 3600
   ```

3. **Verificar**:
   - Esperar propagación DNS (~10 min)
   - Abrir: https://www.drakkarpress.com
   - ✅ Frontend funcionando

---

## 🟡 PASO 3: Configurar Stripe (PAGOS - 20 MIN)

### Por Qué
- Sistema de pagos (Fase 1/2/3: $49/$149/$349)
- Webhooks para confirmación automática
- Dashboard de transacciones

### Paso 3.1: Crear Cuenta

1. **Registro**:
   ```
   https://dashboard.stripe.com/register
   ```
   - Email: tu-email@dominio.com
   - País: (tu país)
   - Tipo de negocio: Plataforma editorial
   - Completar KYC (verificación de identidad)

2. **Activar Cuenta**:
   - Stripe pedirá documentos (DNI, comprobante domicilio)
   - Tiempo aprobación: 1-3 días hábiles

### Paso 3.2: Obtener API Keys

1. **Developers → API Keys**:
   ```
   Publishable key: pk_live_... (para frontend)
   Secret key: sk_live_... (para backend)
   ```

2. **Webhook Secret**:
   ```
   Developers → Webhooks → Add endpoint
   URL: https://api.drakkarpress.com/api/payments/webhook
   Events: checkout.session.completed, payment_intent.succeeded
   Signing secret: whsec_...
   ```

### Paso 3.3: Crear Productos

Ir a **Products → Add Product**:

**Producto 1: Fase 1 - Escritor Emergente**
```
Name: Fase 1 - Escritor Emergente
Description: Publicación de 1 libro + portadas ilimitadas
Price: $49.00 USD / one-time
Price ID: price_1xxx... (copiar para .env)
```

**Producto 2: Fase 2 - Autor Profesional**
```
Name: Fase 2 - Autor Profesional
Description: 3 libros + marketing + distribución Shopify
Price: $149.00 USD / one-time
Price ID: price_2xxx... (copiar para .env)
```

**Producto 3: Fase 3 - Best-Seller**
```
Name: Fase 3 - Best-Seller
Description: 10 libros + investigación IA + mercado global
Price: $349.00 USD / one-time
Price ID: price_3xxx... (copiar para .env)
```

### Paso 3.4: Actualizar .env.production

Editar: `backend/.env.production`

```bash
# Stripe Configuration
STRIPE_API_KEY=sk_live_... # ← Pegar Secret Key
STRIPE_PUBLISHABLE_KEY=pk_live_... # ← Pegar Publishable Key
STRIPE_WEBHOOK_SECRET=whsec_... # ← Pegar Webhook Secret

# Stripe Price IDs (Productos)
STRIPE_PHASE1_PRICE_ID=price_1xxx... # ← Fase 1
STRIPE_PHASE2_PRICE_ID=price_2xxx... # ← Fase 2
STRIPE_PHASE3_PRICE_ID=price_3xxx... # ← Fase 3
STRIPE_PHASE1_MONTHLY_PRICE_ID=price_1xxx... # ← Fase 1 Mensual
STRIPE_PHASE2_MONTHLY_PRICE_ID=price_2xxx... # ← Fase 2 Mensual
STRIPE_PHASE3_MONTHLY_PRICE_ID=price_3xxx... # ← Fase 3 Mensual
```

---

## 🟡 PASO 4: Configurar SendGrid (EMAILS - 15 MIN)

### Por Qué
- Emails de confirmación (registro, compra)
- Notificaciones de generación de libros
- 100 emails/día GRATIS

### Paso 4.1: Crear Cuenta

1. **Registro**:
   ```
   https://signup.sendgrid.com/
   ```
   - Email: tu-email@dominio.com
   - Completar captcha
   - Verificar email

2. **Setup Guide**:
   - Tipo: Transactional Email
   - Framework: Java
   - Skip integration (ya tenemos el código)

### Paso 4.2: Crear API Key

1. **Settings → API Keys**:
   ```
   Create API Key
   Name: DrakkarPress Production
   Permissions: Full Access
   ```

2. **Copiar Key**:
   ```
   SG.xxxxxxxxxxxxxxxxxxxxxxxx (empieza con "SG.")
   ⚠️ IMPORTANTE: Solo se muestra una vez
   ```

### Paso 4.3: Verificar Sender

1. **Settings → Sender Authentication**:
   ```
   Verify a Single Sender
   
   From Name: DrakkarPress
   From Email: noreply@drakkarpress.com
   Reply To: support@drakkarpress.com
   Company Address: (tu dirección)
   ```

2. **Verificar Email**:
   - SendGrid enviará email a `noreply@drakkarpress.com`
   - Abrir email y click en link de verificación
   - ✅ Sender verified

### Paso 4.4: Actualizar .env.production

```bash
# SMTP Configuration (SendGrid)
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=SG.xxxxxxxxxxxxxxxxxxxxxxxx # ← Pegar API Key
SMTP_FROM_EMAIL=noreply@drakkarpress.com
SMTP_FROM_NAME=DrakkarPress
```

---

## 🟡 PASO 5: Configurar AWS S3 (ALMACENAMIENTO - 30 MIN)

### Por Qué
- Almacenar PDFs de libros generados
- CDN para portadas (Amazon CloudFront)
- Avatares de usuarios
- **Costo**: ~$5/mes (primeros 5 GB gratis)

### Paso 5.1: Crear Cuenta AWS

1. **Registro**:
   ```
   https://aws.amazon.com/free/
   → Create Free Account
   ```
   - Email
   - Contraseña
   - Nombre de cuenta AWS
   - Tarjeta de crédito (no se cobra hasta exceder free tier)

2. **Verificación**:
   - Verificar email
   - Verificar teléfono (código SMS)
   - Seleccionar plan: **Basic Support (Free)**

### Paso 5.2: Crear Usuario IAM

1. **IAM → Users → Add User**:
   ```
   User name: drakkarpress-s3-user
   Access type: ☑️ Programmatic access
   ```

2. **Permisos**:
   ```
   Attach existing policies:
   ☑️ AmazonS3FullAccess
   ```

3. **Crear**:
   ```
   Review → Create user
   
   ✅ User created
   Access key ID: AKIAXXXXXXXXXXXXXXXX
   Secret access key: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   ⚠️ Download .csv (no se volverá a mostrar)
   ```

### Paso 5.3: Crear 3 Buckets

**Bucket 1: drakkarpress-books-prod**
```
S3 → Create bucket

Name: drakkarpress-books-prod
Region: us-east-1
Block all public access: ☑️ (privado, requiere signed URLs)
Versioning: Enable
Encryption: Enable (SSE-S3)

Create bucket
```

**Bucket 2: drakkarpress-covers-prod**
```
Name: drakkarpress-covers-prod
Region: us-east-1
Block all public access: ☐ (público, para CDN)
Static website hosting: Disable
CORS: Enable

CORS Configuration:
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "HEAD"],
    "AllowedOrigins": ["https://www.drakkarpress.com", "https://drakkarpress.com"],
    "ExposeHeaders": ["ETag"]
  }
]
```

**Bucket 3: drakkarpress-avatars-prod**
```
Name: drakkarpress-avatars-prod
Region: us-east-1
Block all public access: ☐ (público)
```

### Paso 5.4: Configurar Bucket Policies

**drakkarpress-covers-prod** (público):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicRead",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::drakkarpress-covers-prod/*"
    }
  ]
}
```

**drakkarpress-avatars-prod** (público):
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicRead",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::drakkarpress-avatars-prod/*"
    }
  ]
}
```

### Paso 5.5: Configurar CloudFront CDN (Opcional)

1. **CloudFront → Create Distribution**:
   ```
   Origin domain: drakkarpress-covers-prod.s3.amazonaws.com
   Origin access: Public
   Viewer protocol policy: Redirect HTTP to HTTPS
   Price class: Use only North America and Europe
   
   Create distribution
   ```

2. **Obtener URL**:
   ```
   Distribution domain name: dxxxxxxxxxxxxx.cloudfront.net
   ```

### Paso 5.6: Actualizar .env.production

```bash
# AWS S3 Configuration
AWS_ACCESS_KEY_ID=AKIAXXXXXXXXXXXXXXXX # ← Pegar Access Key ID
AWS_SECRET_ACCESS_KEY=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx # ← Pegar Secret Key
AWS_REGION=us-east-1
AWS_S3_BUCKET_BOOKS=drakkarpress-books-prod
AWS_S3_BUCKET_COVERS=drakkarpress-covers-prod
AWS_S3_BUCKET_AVATARS=drakkarpress-avatars-prod
AWS_CLOUDFRONT_DOMAIN=dxxxxxxxxxxxxx.cloudfront.net # ← (opcional, si configuraste CDN)
```

---

## 🟠 PASO 6: Provisionar Base de Datos PostgreSQL (20 MIN)

### Opciones Recomendadas

#### Opción A: DigitalOcean Managed PostgreSQL (Recomendado)

**Ventajas**: Backups automáticos, monitoreo, fácil escalado  
**Costo**: $15/mes (1 GB RAM, 10 GB storage, 1 nodo)

1. **Crear Cuenta**:
   ```
   https://cloud.digitalocean.com/registrations/new
   ```

2. **Create → Databases**:
   ```
   Database engine: PostgreSQL 15
   Datacenter: New York 1 (NYC1)
   Plan: Basic ($15/mo)
   Database name: drakkarpress
   ```

3. **Obtener Credenciales**:
   ```
   Host: db-postgresql-nyc1-xxxxx.ondigitalocean.com
   Port: 25060
   User: doadmin
   Password: xxxxxxxxxxxxxxxxxxxxxxxx
   Database: drakkarpress
   SSL Mode: require
   
   Connection String:
   postgresql://doadmin:xxxxxxxx@db-postgresql-nyc1-xxxxx.ondigitalocean.com:25060/drakkarpress?sslmode=require
   ```

#### Opción B: ElephantSQL (Free Tier)

**Ventajas**: GRATIS hasta 20 MB  
**Limitación**: Solo para pruebas, no producción real

1. **Crear Cuenta**:
   ```
   https://www.elephantsql.com/
   ```

2. **Create New Instance**:
   ```
   Name: DrakkarPress
   Plan: Tiny Turtle (Free)
   Region: US-East-1
   ```

3. **Obtener URL**:
   ```
   postgres://username:password@hostname/database
   ```

#### Opción C: AWS RDS PostgreSQL

**Ventajas**: Integración con S3, CloudWatch  
**Costo**: $13/mes (db.t3.micro)

1. **RDS → Create Database**:
   ```
   Engine: PostgreSQL 15
   Templates: Free tier
   DB instance identifier: drakkarpress-prod
   Master username: postgres
   Master password: (generar contraseña fuerte)
   DB instance class: db.t3.micro
   Storage: 20 GB gp2
   VPC security group: default (abrir puerto 5432)
   ```

2. **Obtener Endpoint**:
   ```
   Endpoint: drakkarpress-prod.xxxxxxxxxxxxx.us-east-1.rds.amazonaws.com
   Port: 5432
   ```

### Paso 6.2: Ejecutar Script de Base de Datos

1. **Instalar pgAdmin** (GUI para PostgreSQL):
   ```
   https://www.pgadmin.org/download/
   → Windows Installer
   ```

2. **Conectar a DB**:
   ```
   Right-click Servers → Create → Server
   
   General:
   Name: DrakkarPress Production
   
   Connection:
   Host: (pegar endpoint del paso anterior)
   Port: 25060 (o 5432 para RDS)
   Username: doadmin (o postgres para RDS)
   Password: (pegar contraseña)
   SSL Mode: Require
   ```

3. **Ejecutar Script**:
   - Abrir archivo: `backend/DATABASE_PRODUCTION.md`
   - Copiar todo el SQL (desde `CREATE DATABASE` hasta el final)
   - pgAdmin → Tools → Query Tool
   - Pegar SQL → Execute (F5)
   - ✅ Query returned successfully

4. **Verificar**:
   ```sql
   SELECT COUNT(*) FROM runas; -- Esperado: 24
   SELECT COUNT(*) FROM badges; -- Esperado: 8
   SELECT COUNT(*) FROM book_templates; -- Esperado: 5
   ```

### Paso 6.3: Actualizar .env.production

```bash
# Database Configuration
DATABASE_URL=postgresql://doadmin:xxxxxxxx@db-postgresql-nyc1-xxxxx.ondigitalocean.com:25060/drakkarpress?sslmode=require
DATABASE_USERNAME=doadmin # ← Usuario DB
DATABASE_PASSWORD=xxxxxxxxxxxxxxxxxxxxxxxx # ← Contraseña DB (cambiar la generada automáticamente)
DATABASE_HOST=db-postgresql-nyc1-xxxxx.ondigitalocean.com
DATABASE_PORT=25060
DATABASE_NAME=drakkarpress
```

---

## 🟠 PASO 7: Deploy Backend en Producción (45 MIN)

### Requisitos Previos
- ✅ Backend compilado (IntelliJ Lombok - Paso 1)
- ✅ Base de datos provisionada (Paso 6)
- ✅ Archivo `.env.production` completo

### Opción A: DigitalOcean App Platform (Recomendado)

**Ventajas**: Auto-scaling, zero-downtime deploys, logs, SSL gratis  
**Costo**: $12/mo (Basic plan)

#### 7A.1: Preparar Repositorio

1. **Crear `Dockerfile` en backend/**:
   ```dockerfile
   FROM eclipse-temurin:21-jre-alpine
   
   WORKDIR /app
   
   COPY target/drakkarpress-platform-1.0.0.jar app.jar
   
   EXPOSE 8080
   
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Crear `.dockerignore`**:
   ```
   target/
   .mvn/
   *.log
   .env*
   ```

3. **Commit y Push**:
   ```bash
   cd C:\Users\SuperUsuario\DrakkarPress.com\backend
   git add Dockerfile .dockerignore
   git commit -m "feat: Add Dockerfile for production deployment"
   git push origin main
   ```

#### 7A.2: Deploy en App Platform

1. **Create App**:
   ```
   https://cloud.digitalocean.com/apps/new
   → GitHub → Authorize → Select Repository: DrakkarPress
   ```

2. **Configuración**:
   ```
   Name: drakkarpress-backend
   Branch: main
   Source Directory: /backend
   Type: Dockerfile
   Dockerfile Path: Dockerfile
   HTTP Port: 8080
   Health Check: /actuator/health
   ```

3. **Resources**:
   ```
   Plan: Basic ($12/mo)
   Instance Size: 512 MB RAM, 1 vCPU
   Instances: 1
   ```

4. **Environment Variables**:
   - Click: **Edit** → **Bulk Editor**
   - Copiar contenido de `.env.production`
   - Pegar en el editor
   - ⚠️ **IMPORTANTE**: Actualizar `DATABASE_URL` con la URL real del Paso 6

5. **Deploy**:
   - Review → Create Resources
   - Esperar: ~10 minutos (build + deploy)
   - ✅ App live

6. **Obtener URL**:
   ```
   https://drakkarpress-backend-xxxxx.ondigitalocean.app
   ```

7. **Probar**:
   ```
   https://drakkarpress-backend-xxxxx.ondigitalocean.app/actuator/health
   Esperado: {"status":"UP"}
   ```

#### 7A.3: Configurar Dominio Personalizado

1. **Settings → Domains**:
   ```
   Add Domain: api.drakkarpress.com
   ```

2. **DNS Configuration** (en tu proveedor):
   ```
   Tipo: CNAME
   Nombre: api
   Valor: drakkarpress-backend-xxxxx.ondigitalocean.app
   TTL: 3600
   ```

3. **SSL**:
   - DigitalOcean genera certificado automáticamente (~5 min)
   - ✅ https://api.drakkarpress.com funcionando

### Opción B: AWS EC2 + Nginx (Manual)

**Ventajas**: Más control, menor costo con Reserved Instances  
**Desventajas**: Requiere configuración manual de servidor

#### 7B.1: Crear Instancia EC2

1. **Launch Instance**:
   ```
   AMI: Ubuntu 22.04 LTS
   Instance type: t3.micro ($0.0104/hour = ~$7.50/mes)
   Key pair: Create new (download .pem)
   Security group: Allow SSH (22), HTTP (80), HTTPS (443), Custom TCP (8080)
   Storage: 20 GB gp3
   ```

2. **Obtener IP**:
   ```
   Elastic IP: 54.xxx.xxx.xxx (asignar a instancia)
   ```

#### 7B.2: Conectar y Configurar Servidor

1. **Conectar via SSH**:
   ```bash
   ssh -i drakkarpress-key.pem ubuntu@54.xxx.xxx.xxx
   ```

2. **Instalar Java 21**:
   ```bash
   sudo apt update
   sudo apt install -y openjdk-21-jre-headless
   java -version # Verificar: 21.x.x
   ```

3. **Crear Usuario**:
   ```bash
   sudo useradd -m -s /bin/bash drakkarpress
   sudo mkdir /opt/drakkarpress
   sudo chown drakkarpress:drakkarpress /opt/drakkarpress
   ```

4. **Subir JAR**:
   ```bash
   # En tu PC Windows:
   scp -i drakkarpress-key.pem C:\Users\SuperUsuario\DrakkarPress.com\backend\target\drakkarpress-platform-1.0.0.jar ubuntu@54.xxx.xxx.xxx:/home/ubuntu/
   
   # En el servidor:
   sudo mv /home/ubuntu/drakkarpress-platform-1.0.0.jar /opt/drakkarpress/app.jar
   sudo chown drakkarpress:drakkarpress /opt/drakkarpress/app.jar
   ```

5. **Crear .env**:
   ```bash
   sudo nano /opt/drakkarpress/.env
   # Pegar contenido de .env.production
   # Ctrl+X, Y, Enter para guardar
   ```

#### 7B.3: Configurar Systemd Service

1. **Crear servicio**:
   ```bash
   sudo nano /etc/systemd/system/drakkarpress.service
   ```

   Contenido:
   ```ini
   [Unit]
   Description=DrakkarPress Backend
   After=network.target
   
   [Service]
   Type=simple
   User=drakkarpress
   WorkingDirectory=/opt/drakkarpress
   EnvironmentFile=/opt/drakkarpress/.env
   ExecStart=/usr/bin/java -jar /opt/drakkarpress/app.jar
   Restart=always
   RestartSec=10
   StandardOutput=journal
   StandardError=journal
   
   [Install]
   WantedBy=multi-user.target
   ```

2. **Iniciar servicio**:
   ```bash
   sudo systemctl daemon-reload
   sudo systemctl enable drakkarpress
   sudo systemctl start drakkarpress
   sudo systemctl status drakkarpress # Verificar: active (running)
   ```

3. **Ver logs**:
   ```bash
   sudo journalctl -u drakkarpress -f
   ```

#### 7B.4: Configurar Nginx Reverse Proxy

1. **Instalar Nginx**:
   ```bash
   sudo apt install -y nginx
   ```

2. **Configurar**:
   ```bash
   sudo nano /etc/nginx/sites-available/drakkarpress
   ```

   Contenido:
   ```nginx
   server {
       listen 80;
       server_name api.drakkarpress.com;
       
       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```

3. **Activar**:
   ```bash
   sudo ln -s /etc/nginx/sites-available/drakkarpress /etc/nginx/sites-enabled/
   sudo nginx -t # Verificar sintaxis
   sudo systemctl restart nginx
   ```

#### 7B.5: Configurar SSL (Certbot)

1. **Instalar Certbot**:
   ```bash
   sudo apt install -y certbot python3-certbot-nginx
   ```

2. **Obtener certificado**:
   ```bash
   sudo certbot --nginx -d api.drakkarpress.com
   # Email: tu-email@dominio.com
   # Agree to Terms: Yes
   # Share email: No
   # Redirect HTTP to HTTPS: Yes
   ```

3. **Auto-renovación**:
   ```bash
   sudo systemctl status certbot.timer # Verificar: active
   # Certbot renueva automáticamente cada 60 días
   ```

4. **Probar**:
   ```bash
   curl https://api.drakkarpress.com/actuator/health
   # Esperado: {"status":"UP"}
   ```

#### 7B.6: Configurar DNS

En tu proveedor de dominio:
```
Tipo: A
Nombre: api
Valor: 54.xxx.xxx.xxx (IP Elastic del EC2)
TTL: 3600
```

---

## ✅ Checklist Final

### Frontend
- [ ] Vercel deploy completado
- [ ] Dominio `www.drakkarpress.com` configurado
- [ ] SSL activo (https)
- [ ] Variables de entorno configuradas
- [ ] Probar registro de usuario
- [ ] Probar login

### Backend
- [ ] Compilación exitosa (IntelliJ + Lombok)
- [ ] JAR generado (`target/*.jar`)
- [ ] Tests pasan (opcional)
- [ ] Deploy en producción (DigitalOcean/AWS)
- [ ] Dominio `api.drakkarpress.com` configurado
- [ ] SSL activo
- [ ] `/actuator/health` responde `UP`
- [ ] Base de datos conectada

### Servicios Externos
- [ ] Stripe: Cuenta activada, productos creados, webhooks configurados
- [ ] SendGrid: API key obtenida, sender verificado
- [ ] AWS S3: 3 buckets creados, IAM user configurado
- [ ] PostgreSQL: Database provisionada, script ejecutado
- [ ] Lulu.com: Credenciales ya configuradas ✅
- [ ] Shopify: App ya inicializada ✅

### Variables de Entorno
- [ ] `backend/.env.production` completado con todos los valores reales
- [ ] Variables subidas al servicio de hosting (DigitalOcean/AWS)
- [ ] Secrets seguros (no committed a Git)

---

## 🆘 Soporte y Troubleshooting

### Error: Backend no compila en IntelliJ

**Síntoma**: "Cannot find symbol" en getters/setters

**Solución**:
```
1. File → Invalidate Caches → Invalidate and Restart
2. Build → Rebuild Project
3. Verificar: Settings → Plugins → Lombok instalado
4. Verificar: Settings → Annotation Processors → Enabled
```

### Error: Frontend no se conecta al backend

**Síntoma**: `ERR_CONNECTION_REFUSED` en consola del navegador

**Solución**:
```
1. Verificar variable VITE_API_URL en Vercel
2. Verificar CORS en backend (permitir www.drakkarpress.com)
3. Verificar DNS: api.drakkarpress.com apunta a backend
4. Verificar SSL: https (no http)
```

### Error: Stripe webhook falla

**Síntoma**: Pagos no se confirman automáticamente

**Solución**:
```
1. Stripe Dashboard → Webhooks → Ver intentos fallidos
2. Verificar URL: https://api.drakkarpress.com/api/payments/webhook
3. Verificar STRIPE_WEBHOOK_SECRET en .env.production
4. Probar con Stripe CLI:
   stripe listen --forward-to localhost:8080/api/payments/webhook
```

### Error: S3 uploads fallan

**Síntoma**: `AccessDenied` al subir archivos

**Solución**:
```
1. Verificar AWS_ACCESS_KEY_ID y AWS_SECRET_ACCESS_KEY
2. IAM → Users → drakkarpress-s3-user → Permissions
   → Debe tener AmazonS3FullAccess
3. S3 → Bucket → Permissions → CORS configuration
   → Debe permitir tu dominio
```

### Base de datos: Connection timeout

**Síntoma**: `Connection refused` o `timeout`

**Solución**:
```
1. Verificar DATABASE_URL correcto
2. Verificar firewall/security group permite conexiones desde backend
3. DigitalOcean: Settings → Trusted Sources → Add backend IP
4. AWS RDS: Security Group → Inbound rules → Allow 5432 desde backend
```

---

## 📞 Contactos Útiles

- **Documentación Técnica**: Ver carpeta `/docs` y `/backend/docs`
- **Guía Rápida**: `QUICK_START.md`
- **Roadmap Completo**: `ROADMAP.md`
- **Checklist Interactivo**: `DEPLOYMENT_CHECKLIST.md`

---

## 🎉 ¡Éxito!

Una vez completados todos los pasos:

✅ **Frontend**: https://www.drakkarpress.com  
✅ **Backend**: https://api.drakkarpress.com  
✅ **Base de Datos**: Conectada y poblada  
✅ **Pagos**: Stripe funcionando  
✅ **Emails**: SendGrid enviando  
✅ **Almacenamiento**: S3 operativo  
✅ **Print-on-Demand**: Lulu.com integrado  
✅ **Marketplace**: Shopify sincronizado  

**🚀 ¡DrakkarPress LIVE EN PRODUCCIÓN!**

---

**Última actualización**: Enero 2025  
**Versión**: 1.0.0  
**Tiempo total estimado**: 2 horas 45 minutos
