# 📧 Configuración de Email (SMTP) - DrakkarPress

## 🎯 Proveedores Recomendados

### 1. **Gmail (Google Workspace)** ⭐ Recomendado para empezar
- **Costo**: Gratis hasta 500 emails/día
- **Reputación**: Excelente
- **Configuración**: Sencilla con App Password

### 2. **SendGrid**
- **Costo**: 100 emails/día gratis, luego $19.95/mes
- **Reputación**: Muy buena
- **Ventajas**: Mejor deliverability, analytics

### 3. **AWS SES**
- **Costo**: $0.10 por 1000 emails
- **Reputación**: Excelente
- **Ventajas**: Escalable, integrado con AWS

### 4. **Mailgun**
- **Costo**: 5000 emails/mes gratis
- **Reputación**: Muy buena
- **Ventajas**: API flexible, logs detallados

---

## 🔧 Configuración por Proveedor

### Gmail / Google Workspace

#### Paso 1: Crear App Password

1. Ir a [Google Account](https://myaccount.google.com/)
2. Security → 2-Step Verification (activar si no está)
3. Security → App passwords
4. Seleccionar "Mail" y "Other (Custom name)"
5. Nombre: "DrakkarPress Production"
6. Copiar el password generado (16 caracteres)

#### Paso 2: Variables de Entorno

```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=noreply@drakkarpress.com
SMTP_PASSWORD=abcd efgh ijkl mnop  # App Password de 16 caracteres
SMTP_FROM_EMAIL=noreply@drakkarpress.com
SMTP_FROM_NAME=DrakkarPress
SMTP_TLS_ENABLED=true
```

#### Paso 3: Verificar Dominio

Para usar `@drakkarpress.com` necesitas Google Workspace ($6/mes):
- [Google Workspace](https://workspace.google.com/)
- Configurar MX records en tu dominio
- Crear cuenta `noreply@drakkarpress.com`

#### Alternativa Temporal (Gmail personal)

```bash
SMTP_USERNAME=tu.email.personal@gmail.com
SMTP_FROM_EMAIL=tu.email.personal@gmail.com
```

**Nota**: Gmail puede marcar emails como "enviados desde otra cuenta"

---

### SendGrid (Recomendado para producción)

#### Paso 1: Crear Cuenta

1. Ir a [SendGrid](https://sendgrid.com/)
2. Sign Up → Free plan
3. Verificar email

#### Paso 2: Crear API Key

1. Settings → API Keys → Create API Key
2. Nombre: "DrakkarPress Production"
3. Permisos: "Full Access"
4. Copiar API Key (empieza con `SG.`)

#### Paso 3: Variables de Entorno

```bash
SMTP_HOST=smtp.sendgrid.net
SMTP_PORT=587
SMTP_USERNAME=apikey  # Literal "apikey"
SMTP_PASSWORD=SG.abc123def456ghi789jkl  # Tu API Key
SMTP_FROM_EMAIL=noreply@drakkarpress.com
SMTP_FROM_NAME=DrakkarPress
SMTP_TLS_ENABLED=true
```

#### Paso 4: Autenticar Dominio (Single Sender)

1. Settings → Sender Authentication
2. Single Sender Verification
3. Email: `noreply@drakkarpress.com`
4. Verificar email de confirmación

#### Paso 5: Dominio Completo (Opcional pero recomendado)

1. Settings → Sender Authentication → Authenticate Domain
2. Dominio: `drakkarpress.com`
3. Agregar DNS records:

```dns
# CNAME Records
em123.drakkarpress.com → u1234567.wl123.sendgrid.net
s1._domainkey.drakkarpress.com → s1.domainkey.u1234567.wl123.sendgrid.net
s2._domainkey.drakkarpress.com → s2.domainkey.u1234567.wl123.sendgrid.net
```

---

### AWS SES

#### Paso 1: Configurar SES

1. Consola AWS → SES
2. Verify a New Email Address: `noreply@drakkarpress.com`
3. Verificar email

#### Paso 2: Salir del Sandbox (Importante)

Por defecto, SES está en sandbox (solo 200 emails/día):
1. SES → Account dashboard
2. Request production access
3. Explicar caso de uso (membresías, verificación email)
4. Esperar aprobación (1-2 días)

#### Paso 3: Crear SMTP Credentials

1. SES → SMTP Settings → Create My SMTP Credentials
2. Descargar credenciales

#### Paso 4: Variables de Entorno

```bash
SMTP_HOST=email-smtp.us-east-1.amazonaws.com
SMTP_PORT=587
SMTP_USERNAME=AKIAIOSFODNN7EXAMPLE  # De las credenciales descargadas
SMTP_PASSWORD=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
SMTP_FROM_EMAIL=noreply@drakkarpress.com
SMTP_FROM_NAME=DrakkarPress
SMTP_TLS_ENABLED=true
```

#### Paso 5: Configurar SPF y DKIM

```dns
# SPF Record
drakkarpress.com TXT "v=spf1 include:amazonses.com ~all"

# DKIM (generado por SES)
abc123._domainkey.drakkarpress.com CNAME abc123.dkim.amazonses.com
```

---

### Mailgun

#### Paso 1: Crear Cuenta

1. [Mailgun](https://mailgun.com/) → Sign Up
2. Plan Free (5000 emails/mes)

#### Paso 2: Verificar Dominio

1. Sending → Domains → Add New Domain
2. Dominio: `mg.drakkarpress.com` (subdominio recomendado)
3. Agregar DNS records:

```dns
# TXT (SPF)
mg.drakkarpress.com TXT "v=spf1 include:mailgun.org ~all"

# TXT (DKIM)
k1._domainkey.mg.drakkarpress.com TXT "k=rsa; p=MIGfMA0GCS..."

# CNAME (Tracking)
email.mg.drakkarpress.com CNAME mailgun.org
```

#### Paso 3: Obtener Credenciales

1. Sending → Domain settings → SMTP credentials
2. User: `postmaster@mg.drakkarpress.com`
3. Reset password → Copiar

#### Paso 4: Variables de Entorno

```bash
SMTP_HOST=smtp.mailgun.org
SMTP_PORT=587
SMTP_USERNAME=postmaster@mg.drakkarpress.com
SMTP_PASSWORD=abc123def456
SMTP_FROM_EMAIL=noreply@drakkarpress.com
SMTP_FROM_NAME=DrakkarPress
SMTP_TLS_ENABLED=true
```

---

## 📋 Plantillas de Email

### 1. Email de Verificación

**Asunto**: `Verifica tu cuenta - DrakkarPress`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 50px auto; background: white; padding: 40px; border-radius: 10px; }
        .logo { text-align: center; margin-bottom: 30px; }
        h1 { color: #1a1a1a; font-size: 24px; }
        .button { display: inline-block; padding: 15px 30px; background: #0066cc; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
        .footer { margin-top: 30px; padding-top: 20px; border-top: 1px solid #eee; font-size: 12px; color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <div class="logo">
            <h1>🪓 DrakkarPress</h1>
        </div>
        <h1>¡Bienvenido a DrakkarPress!</h1>
        <p>Hola {{username}},</p>
        <p>Gracias por registrarte. Por favor verifica tu cuenta haciendo clic en el botón:</p>
        <a href="{{verifyUrl}}" class="button">Verificar mi cuenta</a>
        <p style="margin-top: 20px; font-size: 14px; color: #666;">
            O copia este enlace en tu navegador:<br>
            <code>{{verifyUrl}}</code>
        </p>
        <p style="margin-top: 30px;">Este enlace expira en 24 horas.</p>
        <div class="footer">
            <p>Este correo fue enviado a {{email}}</p>
            <p>Si no creaste esta cuenta, ignora este mensaje.</p>
            <p>&copy; 2024 DrakkarPress. Todos los derechos reservados.</p>
        </div>
    </div>
</body>
</html>
```

### 2. Email de Bienvenida (después de verificar)

**Asunto**: `¡Tu cuenta está lista! - DrakkarPress`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 50px auto; background: white; padding: 40px; border-radius: 10px; }
        .badge { background: #f0f8ff; padding: 20px; border-radius: 10px; text-align: center; margin: 20px 0; }
        .badge h2 { color: #0066cc; margin: 0; }
        .button { display: inline-block; padding: 15px 30px; background: #0066cc; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
        .feature { margin: 15px 0; padding: 10px; background: #f9f9f9; border-left: 3px solid #0066cc; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🪓 ¡Cuenta verificada!</h1>
        <p>Hola {{username}},</p>
        <p>Tu cuenta ha sido verificada exitosamente. Eres el usuario <strong>#{{userNumber}}</strong>!</p>
        
        <div class="badge">
            <h2>🏅 Badge Desbloqueado</h2>
            <p><strong>{{badgeName}}</strong></p>
            <p>{{badgeDescription}}</p>
        </div>

        <h3>Tu Plan:</h3>
        <div class="feature">
            <strong>{{planName}}</strong> - ${{price}}/mes<br>
            ✨ {{aiLimit}} características de IA al mes<br>
            📚 Biblioteca de libros ilimitada<br>
            🔮 Runas personalizadas<br>
            🎨 Generadores de arte
        </div>

        <a href="https://www.drakkarpress.com/biblioteca" class="button">Explorar mi biblioteca</a>

        <p style="margin-top: 30px;">¡Bienvenido a la comunidad DrakkarPress! 🎉</p>
    </div>
</body>
</html>
```

### 3. Email de Reset Password

**Asunto**: `Restablecer contraseña - DrakkarPress`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 50px auto; background: white; padding: 40px; border-radius: 10px; }
        .button { display: inline-block; padding: 15px 30px; background: #0066cc; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
        .warning { background: #fff3cd; padding: 15px; border-radius: 5px; margin: 20px 0; border-left: 4px solid #ffc107; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🔐 Restablecer contraseña</h1>
        <p>Hola {{username}},</p>
        <p>Recibimos una solicitud para restablecer tu contraseña.</p>
        <a href="{{resetUrl}}" class="button">Restablecer mi contraseña</a>
        <p style="margin-top: 20px; font-size: 14px; color: #666;">
            O copia este enlace:<br>
            <code>{{resetUrl}}</code>
        </p>
        <div class="warning">
            <strong>⚠️ Importante:</strong><br>
            Este enlace expira en 1 hora.<br>
            Si no solicitaste este cambio, ignora este mensaje.
        </div>
    </div>
</body>
</html>
```

### 4. Email de Renovación de Membresía

**Asunto**: `Tu membresía se renovó - DrakkarPress`

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; background: #f4f4f4; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 50px auto; background: white; padding: 40px; border-radius: 10px; }
        .invoice { background: #f9f9f9; padding: 20px; border-radius: 5px; margin: 20px 0; }
        .button { display: inline-block; padding: 15px 30px; background: #0066cc; color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>✅ Pago procesado</h1>
        <p>Hola {{username}},</p>
        <p>Tu membresía se renovó exitosamente.</p>
        
        <div class="invoice">
            <h3>Resumen de pago</h3>
            <p><strong>Plan:</strong> {{planName}}</p>
            <p><strong>Monto:</strong> ${{amount}} USD</p>
            <p><strong>Fecha:</strong> {{date}}</p>
            <p><strong>Método:</strong> •••• {{cardLast4}}</p>
            <p><strong>Próxima renovación:</strong> {{nextBillingDate}}</p>
        </div>

        <a href="https://www.drakkarpress.com/account/invoices" class="button">Ver factura completa</a>
    </div>
</body>
</html>
```

---

## 🧪 Probar Configuración

### Opción 1: Usando Spring Boot Test

```java
// src/test/java/com/drakkarpress/EmailTest.java
@SpringBootTest
public class EmailTest {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Test
    public void testEmailConnection() throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
        helper.setFrom("noreply@drakkarpress.com");
        helper.setTo("tu.email.personal@gmail.com");
        helper.setSubject("Test Email - DrakkarPress");
        helper.setText("<h1>Test exitoso!</h1>", true);
        
        mailSender.send(message);
        System.out.println("✅ Email enviado correctamente");
    }
}
```

### Opción 2: Endpoint de Test (Desarrollo)

```java
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private EmailService emailService;
    
    @PostMapping("/send-email")
    public ResponseEntity<String> testEmail(@RequestParam String to) {
        try {
            emailService.sendTestEmail(to);
            return ResponseEntity.ok("Email enviado a " + to);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
```

### Opción 3: PowerShell Script

```powershell
# test-smtp.ps1
$smtp = New-Object Net.Mail.SmtpClient("smtp.gmail.com", 587)
$smtp.EnableSsl = $true
$smtp.Credentials = New-Object Net.NetworkCredential("noreply@drakkarpress.com", "tu_app_password")

$message = New-Object Net.Mail.MailMessage
$message.From = "noreply@drakkarpress.com"
$message.To.Add("tu.email.personal@gmail.com")
$message.Subject = "Test SMTP - DrakkarPress"
$message.Body = "Email de prueba enviado correctamente!"

try {
    $smtp.Send($message)
    Write-Host "✅ Email enviado correctamente" -ForegroundColor Green
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
```

---

## 🔒 Checklist de Seguridad

- [ ] Usar App Password en lugar de contraseña real (Gmail)
- [ ] Habilitar TLS/SSL (puerto 587 o 465)
- [ ] Verificar dominio con SPF/DKIM
- [ ] No hardcodear credenciales en código
- [ ] Usar variables de entorno
- [ ] Configurar rate limiting (evitar spam)
- [ ] Implementar logs de emails enviados
- [ ] Configurar bounce/complaint handling
- [ ] Agregar unsubscribe link (requerido por ley)
- [ ] Probar deliverability con [Mail Tester](https://www.mail-tester.com/)

---

## 📊 Monitoreo y Logs

### Spring Boot Logging

```properties
# application.properties
logging.level.org.springframework.mail=DEBUG
spring.mail.properties.mail.debug=true
```

### Eventos a Trackear

```java
@Service
public class EmailService {
    
    public void sendEmail(String to, String subject, String body) {
        try {
            mailSender.send(message);
            logEmailEvent("SENT", to, subject);
        } catch (Exception e) {
            logEmailEvent("FAILED", to, subject, e.getMessage());
            throw e;
        }
    }
    
    private void logEmailEvent(String status, String to, String subject) {
        EmailLog log = new EmailLog();
        log.setStatus(status);
        log.setRecipient(to);
        log.setSubject(subject);
        log.setTimestamp(Instant.now());
        emailLogRepository.save(log);
    }
}
```

---

**Creado**: 2025-11-11  
**Última actualización**: 2025-11-11  
**Proveedor recomendado**: SendGrid (producción) / Gmail (desarrollo)
