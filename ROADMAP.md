# 🪓 DrakkarPress - Roadmap de Deployment

## 🎯 Objetivo: Lanzamiento de Producción en www.drakkarpress.com

---

## 📅 Fase 1: Preparación (COMPLETADA ✅)

### ✅ 1.1 Frontend
- [x] Código HTML/CSS/JS completo (30+ páginas)
- [x] Sistema i18n (6 idiomas)
- [x] Commit a Git (136 archivos)
- [x] Push a GitHub
- [x] Configuraciones Vercel/Netlify

### ✅ 1.2 Documentación
- [x] 7 guías de deployment
- [x] 6 guías de configuración de servicios
- [x] Documentación de integraciones (Lulu, Shopify)
- [x] Scripts de base de datos
- [x] Generador de secretos

### ✅ 1.3 Integraciones
- [x] Lulu.com credentials
- [x] Shopify app inicializada
- [x] Stripe documentado
- [x] SMTP documentado
- [x] AWS S3 documentado

**Duración**: 2 días  
**Estado**: ✅ COMPLETADO

---

## 📅 Fase 2: Resolver Bloqueadores (EN PROGRESO 🟡)

### 🚨 2.1 Backend Compilation (CRÍTICO)

**Problema**: Lombok no genera código en Java 21 + Maven  
**Intentos fallidos**: 6 enfoques diferentes vía terminal  
**Solución**: IntelliJ IDEA Community con plugin Lombok  

**Pasos**:
1. [ ] Descargar IntelliJ IDEA Community (free)
2. [ ] Abrir proyecto `DrakkarPress.com/backend/`
3. [ ] Instalar plugin Lombok (detección automática)
4. [ ] Build → Rebuild Project
5. [ ] Verificar 0 errores de compilación
6. [ ] Ejecutar `mvn clean install`
7. [ ] Generar JAR ejecutable

**Tiempo estimado**: 30 minutos  
**Prioridad**: 🔴 CRÍTICA  
**Bloqueador de**: Backend deployment, testing, integrations

---

## 📅 Fase 3: Deploy Frontend (PENDIENTE ⏳)

### 🌐 3.1 Vercel Deployment

**Pasos**:
1. [ ] Ir a https://vercel.com/new
2. [ ] Login con GitHub (OAuth)
3. [ ] Import repository: `imageGeneratorZZ/DrakkarPress`
4. [ ] Configure:
   - Framework Preset: Other
   - Output Directory: `.` (raíz)
   - Install Command: (vacío)
   - Build Command: (vacío)
5. [ ] Click "Deploy"
6. [ ] Esperar deployment (~2 min)
7. [ ] Verificar URL: `https://drakkarpress.vercel.app`

### 🌐 3.2 Configurar Dominio

**Pasos**:
1. [ ] Vercel Dashboard → Settings → Domains
2. [ ] Add Domain: `www.drakkarpress.com`
3. [ ] Copiar CNAME target: `cname.vercel-dns.com`
4. [ ] Ir al proveedor DNS (GoDaddy, Namecheap, etc.)
5. [ ] Agregar CNAME record:
   ```
   Type: CNAME
   Host: www
   Points to: cname.vercel-dns.com
   TTL: 3600
   ```
6. [ ] Esperar propagación DNS (5-30 min)
7. [ ] Verificar: https://www.drakkarpress.com

**Tiempo estimado**: 10 minutos + propagación DNS  
**Prioridad**: 🟡 ALTA  
**Bloqueador de**: Testing frontend, user feedback

---

## 📅 Fase 4: Configurar Servicios (PENDIENTE ⏳)

### 💳 4.1 Stripe (Pagos)

**Pasos**:
1. [ ] Crear cuenta en https://stripe.com/
2. [ ] Completar información de negocio
3. [ ] Dashboard → Developers → API keys
4. [ ] Copiar:
   - `pk_test_...` (Publishable key)
   - `sk_test_...` (Secret key)
5. [ ] Products → Add product (x3):
   - Phase 1: $5/mes, $50/año
   - Phase 2: $10/mes, $100/año
   - Phase 3: $19.99/mes, $199.90/año
6. [ ] Copiar Price IDs (6 total)
7. [ ] Developers → Webhooks → Add endpoint
8. [ ] URL: `https://api.drakkarpress.com/api/webhooks/stripe`
9. [ ] Events: `checkout.session.completed`, `invoice.payment_succeeded`, etc.
10. [ ] Copiar Webhook Secret: `whsec_...`

**Variables de entorno**:
```bash
STRIPE_PUBLIC_KEY=pk_test_...
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_PRICE_PHASE_1_MONTHLY=price_...
STRIPE_PRICE_PHASE_1_YEARLY=price_...
# (total 8 variables)
```

**Tiempo estimado**: 20 minutos  
**Prioridad**: 🟡 ALTA  
**Documentación**: `backend/STRIPE_PAYMENTS_CONFIG.md`

### 📧 4.2 SMTP (Email)

**Opción recomendada**: SendGrid (100 emails/día gratis)

**Pasos**:
1. [ ] Crear cuenta en https://sendgrid.com/
2. [ ] Settings → API Keys → Create API Key
3. [ ] Nombre: "DrakkarPress Production"
4. [ ] Permisos: Full Access
5. [ ] Copiar API Key: `SG.abc123...`
6. [ ] Settings → Sender Authentication
7. [ ] Single Sender Verification
8. [ ] Email: `noreply@drakkarpress.com`
9. [ ] Verificar email de confirmación

**Variables de entorno**:
```bash
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey
SMTP_PASSWORD=SG.abc123...
SMTP_FROM_EMAIL=noreply@drakkarpress.com
```

**Tiempo estimado**: 15 minutos  
**Prioridad**: 🟡 ALTA  
**Documentación**: `backend/SMTP_EMAIL_CONFIG.md`

### ☁️ 4.3 AWS S3 (Storage)

**Pasos**:
1. [ ] Crear cuenta en https://aws.amazon.com/
2. [ ] IAM → Users → Create user
   - Name: `drakkarpress-app`
   - Access: Programmatic only
3. [ ] Attach policy: `AmazonS3FullAccess` (temporal)
4. [ ] Descargar Access Keys:
   - Access Key ID: `AKIA...`
   - Secret Access Key: `wJalr...`
5. [ ] S3 → Create bucket (x3):
   - `drakkarpress-books` (privado)
   - `drakkarpress-covers` (público)
   - `drakkarpress-avatars` (público)
6. [ ] Configurar bucket policies (ver documentación)
7. [ ] IAM → Create custom policy (restrictiva)
8. [ ] Replace `AmazonS3FullAccess` con custom policy

**Variables de entorno**:
```bash
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=wJalr...
AWS_REGION=us-east-1
AWS_S3_BUCKET_BOOKS=drakkarpress-books
AWS_S3_BUCKET_COVERS=drakkarpress-covers
AWS_S3_BUCKET_AVATARS=drakkarpress-avatars
```

**Tiempo estimado**: 30 minutos  
**Prioridad**: 🟢 MEDIA  
**Documentación**: `backend/AWS_S3_CONFIG.md`

---

## 📅 Fase 5: Base de Datos (PENDIENTE ⏳)

### 🗄️ 5.1 Provisionar PostgreSQL

**Opciones**:

| Proveedor | Plan | Costo | RAM | Storage | Backups |
|-----------|------|-------|-----|---------|---------|
| **AWS RDS** | db.t3.micro | $15/mes | 1 GB | 20 GB | Automáticos |
| **DigitalOcean** | Managed DB | $15/mes | 1 GB | 10 GB | Diarios |
| **ElephantSQL** | Tiny Turtle | $5/mes | 20 MB | 20 MB | No |
| **Supabase** | Free | $0 | 500 MB | 500 MB | 7 días |

**Recomendado**: AWS RDS (mejor integración con S3)

**Pasos (AWS RDS)**:
1. [ ] AWS Console → RDS → Create database
2. [ ] Engine: PostgreSQL 14
3. [ ] Template: Free tier (o Production)
4. [ ] DB instance: db.t3.micro
5. [ ] Username: `postgres`
6. [ ] Password: (generar seguro)
7. [ ] Storage: 20 GB
8. [ ] Public access: Yes (temporal)
9. [ ] Security group: Allow port 5432 desde tu IP
10. [ ] Create database (5-10 min)
11. [ ] Copiar endpoint: `xxx.rds.amazonaws.com:5432`

### 🗄️ 5.2 Ejecutar Script de Creación

**Pasos**:
1. [ ] Instalar PostgreSQL client:
   ```powershell
   choco install postgresql
   ```
2. [ ] Conectar a RDS:
   ```bash
   psql -h xxx.rds.amazonaws.com -U postgres -d postgres
   ```
3. [ ] Ejecutar script:
   ```bash
   \i backend/DATABASE_PRODUCTION.md
   ```
4. [ ] Verificar:
   ```sql
   \dt  -- Listar tablas
   SELECT COUNT(*) FROM runes;  -- Debe ser 24
   SELECT COUNT(*) FROM badges; -- Debe ser 8
   ```

**Variables de entorno**:
```bash
DATABASE_URL=jdbc:postgresql://xxx.rds.amazonaws.com:5432/drakkarpress_prod
DATABASE_USERNAME=drakkarpress_user
DATABASE_PASSWORD=(del script)
```

**Tiempo estimado**: 20 minutos  
**Prioridad**: 🟡 ALTA  
**Documentación**: `backend/DATABASE_PRODUCTION.md`

---

## 📅 Fase 6: Deploy Backend (PENDIENTE ⏳)

### 🚀 6.1 Build JAR

**Pasos** (después de resolver Lombok):
1. [ ] Abrir terminal en `backend/`
2. [ ] Verificar Java y Maven:
   ```bash
   java -version  # Debe ser 21.x
   mvn -version   # Debe ser 3.9.x
   ```
3. [ ] Build:
   ```bash
   mvn clean install -DskipTests
   ```
4. [ ] Verificar JAR:
   ```bash
   ls target/drakkarpress-backend-0.0.1-SNAPSHOT.jar
   ```
5. [ ] Test local:
   ```bash
   java -jar target/drakkarpress-backend-0.0.1-SNAPSHOT.jar
   ```
6. [ ] Verificar: http://localhost:8080/actuator/health

**Tiempo estimado**: 10 minutos (post-Lombok)  
**Prioridad**: 🔴 CRÍTICA

### 🚀 6.2 Deploy a Servidor

**Opciones**:

| Proveedor | Plan | Costo | CPU | RAM | Tráfico |
|-----------|------|-------|-----|-----|---------|
| **AWS EC2** | t3.micro | $10/mes | 2 vCPU | 1 GB | Ilimitado |
| **DigitalOcean** | Droplet | $6/mes | 1 vCPU | 1 GB | 1 TB |
| **Heroku** | Hobby | $7/mes | 1 dyno | 512 MB | Ilimitado |
| **Railway** | Hobby | $5/mes | Shared | 512 MB | Ilimitado |

**Recomendado**: DigitalOcean (mejor precio/rendimiento)

**Pasos (DigitalOcean)**:
1. [ ] Crear droplet:
   - Image: Ubuntu 22.04 LTS
   - Plan: Basic $6/mes
   - Datacenter: New York
   - Authentication: SSH key
2. [ ] SSH al servidor:
   ```bash
   ssh root@xxx.xxx.xxx.xxx
   ```
3. [ ] Instalar Java 21:
   ```bash
   apt update
   apt install openjdk-21-jdk -y
   java -version
   ```
4. [ ] Subir JAR (desde local):
   ```bash
   scp target/drakkarpress-backend-0.0.1-SNAPSHOT.jar root@xxx.xxx.xxx.xxx:/app/
   ```
5. [ ] Crear servicio systemd:
   ```bash
   nano /etc/systemd/system/drakkarpress.service
   ```
   ```ini
   [Unit]
   Description=DrakkarPress Backend
   After=network.target

   [Service]
   User=root
   WorkingDirectory=/app
   ExecStart=/usr/bin/java -jar /app/drakkarpress-backend-0.0.1-SNAPSHOT.jar
   Restart=always
   Environment="DATABASE_URL=jdbc:postgresql://..."
   Environment="JWT_SECRET=..."
   # (todas las variables de entorno)

   [Install]
   WantedBy=multi-user.target
   ```
6. [ ] Iniciar servicio:
   ```bash
   systemctl enable drakkarpress
   systemctl start drakkarpress
   systemctl status drakkarpress
   ```
7. [ ] Configurar Nginx como reverse proxy:
   ```bash
   apt install nginx -y
   nano /etc/nginx/sites-available/drakkarpress
   ```
   ```nginx
   server {
       listen 80;
       server_name api.drakkarpress.com;

       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```
8. [ ] Activar sitio:
   ```bash
   ln -s /etc/nginx/sites-available/drakkarpress /etc/nginx/sites-enabled/
   systemctl restart nginx
   ```
9. [ ] Instalar SSL (Let's Encrypt):
   ```bash
   apt install certbot python3-certbot-nginx -y
   certbot --nginx -d api.drakkarpress.com
   ```

**DNS (proveedor de dominio)**:
```
Type: A
Host: api
Points to: xxx.xxx.xxx.xxx (IP del droplet)
TTL: 3600
```

**Tiempo estimado**: 45 minutos  
**Prioridad**: 🔴 CRÍTICA

---

## 📅 Fase 7: Testing End-to-End (PENDIENTE ⏳)

### 🧪 7.1 Testing Checklist

**Frontend**:
- [ ] Página de inicio carga correctamente
- [ ] Menú de navegación funciona
- [ ] Cambio de idioma funciona (6 idiomas)
- [ ] Páginas estáticas accesibles (about, faq, contact)

**Backend - Autenticación**:
- [ ] Registro de usuario (`POST /api/auth/register`)
- [ ] Email de verificación enviado
- [ ] Verificar email (`GET /api/auth/verify?token=...`)
- [ ] Login (`POST /api/auth/login`)
- [ ] Recibir JWT token
- [ ] Refresh token (`POST /api/auth/refresh`)

**Backend - User Profile**:
- [ ] Obtener perfil (`GET /api/users/me`)
- [ ] Verificar `user_number` asignado
- [ ] Verificar runa asignada
- [ ] Verificar badge asignado
- [ ] Upload avatar (`POST /api/upload/avatar`)
- [ ] Avatar visible en S3

**Backend - Pricing**:
- [ ] Obtener pricing dinámico (`GET /api/pricing`)
- [ ] Verificar precio según `user_number`:
   - User 1-1000: $5/mes
   - User 1001-10000: $10/mes
   - User 10001+: $19.99/mes

**Backend - Payments**:
- [ ] Crear checkout session (`POST /api/checkout/create-session`)
- [ ] Redirect a Stripe Checkout
- [ ] Completar pago (tarjeta de test)
- [ ] Webhook recibido (`POST /api/webhooks/stripe`)
- [ ] Membresía activada
- [ ] Usuario redirigido a success page

**Backend - Memberships**:
- [ ] Membership activa (`GET /api/memberships/me`)
- [ ] Precio grandfathered correcto
- [ ] Fecha de expiración correcta
- [ ] Límites de IA correctos

**Tiempo estimado**: 2 horas  
**Prioridad**: 🟡 ALTA

---

## 📅 Fase 8: Monitoreo y Optimización (FUTURO 🔮)

### 📊 8.1 Monitoring

- [ ] Configurar Sentry (error tracking)
- [ ] Configurar New Relic (APM)
- [ ] CloudWatch logs (AWS)
- [ ] Uptime monitoring (UptimeRobot)

### ⚡ 8.2 Optimización

- [ ] Habilitar CloudFront CDN para covers/avatars
- [ ] Configurar Redis cache
- [ ] Database connection pooling
- [ ] Implementar rate limiting
- [ ] Optimizar queries SQL (EXPLAIN ANALYZE)

### 🔐 8.3 Seguridad

- [ ] Audit log de acciones críticas
- [ ] 2FA para admin panel
- [ ] Penetration testing
- [ ] OWASP security checklist
- [ ] Rotar secretos cada 90 días

---

## 📊 Timeline Visual

```
Semana 1: Preparación (COMPLETADO ✅)
├── Frontend code ████████████████████ 100%
├── Documentation ████████████████████ 100%
└── Integrations  ████████████████████ 100%

Semana 2: Bloqueadores (EN PROGRESO 🟡)
├── Backend Lombok ████░░░░░░░░░░░░░░  20% (IntelliJ pending)
├── Frontend deploy ░░░░░░░░░░░░░░░░░░   0% (OAuth pending)
└── Services config ░░░░░░░░░░░░░░░░░░   0% (accounts pending)

Semana 3: Deploy (PENDIENTE ⏳)
├── Database ░░░░░░░░░░░░░░░░░░   0%
├── Backend  ░░░░░░░░░░░░░░░░░░   0%
└── Testing  ░░░░░░░░░░░░░░░░░░   0%

Semana 4: Launch (FUTURO 🔮)
├── Production ░░░░░░░░░░░░░░░░░░   0%
├── Monitoring ░░░░░░░░░░░░░░░░░░   0%
└── Marketing  ░░░░░░░░░░░░░░░░░░   0%
```

---

## 🎯 Métricas de Éxito

**Pre-Launch**:
- ✅ Frontend deployado y accesible
- ✅ Backend compilado y deployado
- ✅ Base de datos poblada
- ✅ 0 errores críticos en logs
- ✅ Testing end-to-end completo

**Post-Launch** (Día 1):
- 🎯 10 usuarios registrados
- 🎯 5 membresías activas
- 🎯 0 downtime
- 🎯 < 2s response time

**Post-Launch** (Semana 1):
- 🎯 100 usuarios registrados
- 🎯 20 membresías activas
- 🎯 99.9% uptime
- 🎯 < 1s response time
- 🎯 5 libros generados

---

## 📞 Contacto y Soporte

**Documentación**: Ver carpeta raíz del proyecto  
**Issues**: GitHub repository  
**Email**: admin@drakkarpress.com  

---

**Creado**: 2025-11-11  
**Última actualización**: 2025-11-11  
**Próxima revisión**: Después de Fase 2 (Lombok resuelto)
