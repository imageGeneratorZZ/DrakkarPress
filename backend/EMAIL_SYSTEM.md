# Sistema de Emails Automáticos - DrakkarPress

## 📧 Descripción General

Sistema completo de envío automático de emails con plantillas HTML profesionales para notificaciones de registro, compras, verificación y recordatorios.

---

## ✅ Emails Implementados

### 1. **Email de Bienvenida** 
**Trigger:** Registro de nuevo usuario  
**Template:** `welcome.html`  
**Método:** `EmailService.sendWelcomeEmail(User, PricingInfo)`

**Contenido:**
- Número de usuario asignado
- Badge de fase (Founder, Early Adopter, Launch Member)
- Precio grandfathered (si aplica)
- Mensaje personalizado según fase
- Banner de "PRECIO BLOQUEADO DE POR VIDA"
- Link al dashboard
- Próximos pasos

**Variables Thymeleaf:**
```java
- userName: String
- userNumber: Long
- phase: String (Fundador/Early Adopter/Launch Member/Premium)
- monthlyPrice: BigDecimal
- annualPrice: BigDecimal
- isGrandfathered: boolean
- badge: String (FOUNDER/EARLY_ADOPTER/LAUNCH_MEMBER)
- welcomeMessage: String (personalizado)
- frontendUrl: String
- appName: String
```

---

### 2. **Confirmación de Compra**
**Trigger:** Pago exitoso de membresía Premium  
**Template:** `purchase-confirmation.html`  
**Método:** `EmailService.sendPurchaseConfirmation(User, PaymentTransaction, PricingInfo)`

**Contenido:**
- ✅ Icono de éxito
- ID de transacción
- Detalles del plan comprado
- Fecha y monto pagado
- Badge recibido
- Precio grandfathered (si aplica)
- Beneficios incluidos
- Mensaje de recibo oficial

**Variables Thymeleaf:**
```java
- userName: String
- userNumber: Long
- transactionId: UUID
- plan: String
- amount: BigDecimal
- frequency: String (MONTHLY/ANNUAL)
- date: String (formato dd/MM/yyyy HH:mm)
- isGrandfathered: boolean
- badge: String
- monthlyPrice: BigDecimal
- annualPrice: BigDecimal
- frontendUrl: String
- appName: String
```

---

### 3. **Recordatorio de Renovación**
**Trigger:** Membresía próxima a expirar (7 días antes)  
**Template:** `renewal-reminder.html`  
**Método:** `EmailService.sendRenewalReminder(User, daysUntilExpiration)`

**Contenido:**
- ⚠️ Alerta de expiración
- Días restantes
- Botón de renovación
- Link al dashboard

**Variables Thymeleaf:**
```java
- userName: String
- daysUntilExpiration: int
- frontendUrl: String
- appName: String
```

---

### 4. **Verificación de Email**
**Trigger:** Registro o cambio de email  
**Template:** `verification.html`  
**Método:** `EmailService.sendVerificationEmail(User, verificationToken)`

**Contenido:**
- 🔐 Icono de seguridad
- Botón de verificación
- Token de verificación
- Enlace directo
- Tiempo de expiración (24 horas)

**Variables Thymeleaf:**
```java
- userName: String
- verificationUrl: String (frontendUrl + /verify-email?token=XXX)
- appName: String
```

---

### 5. **Reset de Contraseña**
**Trigger:** Usuario solicita restablecer contraseña  
**Template:** `password-reset.html`  
**Método:** `EmailService.sendPasswordResetEmail(User, resetToken)`

**Contenido:**
- 🔑 Icono de llave
- Botón para resetear
- Token de reset
- Advertencia de seguridad
- Tiempo de expiración (1 hora)

**Variables Thymeleaf:**
```java
- userName: String
- resetUrl: String (frontendUrl + /reset-password?token=XXX)
- appName: String
```

---

## ⚙️ Configuración SMTP

### `application.properties`
```properties
# Mail Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USERNAME:your-email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your-app-password}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com

# Application Configuration
app.name=DrakkarPress
app.frontend-url=${FRONTEND_URL:http://localhost:3000}
```

### Variables de Entorno (Producción)
```bash
MAIL_USERNAME=noreply@drakkarpress.com
MAIL_PASSWORD=your-gmail-app-password
FRONTEND_URL=https://drakkarpress.com
```

### Gmail App Password Setup
1. Ir a https://myaccount.google.com/security
2. Activar "Verificación en 2 pasos"
3. Ir a "Contraseñas de aplicaciones"
4. Generar nueva contraseña para "Mail"
5. Usar esa contraseña en `MAIL_PASSWORD`

---

## 🔄 Flujo de Envío Automático

### **Registro de Usuario**
```java
AuthService.register()
├─ user = userRepository.save(user)
├─ pricing = pricingService.calculatePricing(userNumber)
├─ membership = membershipRepository.save(membership)
└─ emailService.sendWelcomeEmail(user, pricing) ✉️
```

### **Compra de Membresía Premium**
```java
PaymentService.processPayment()
├─ transaction = transactionRepository.save(transaction)
├─ membership.setPlan(pricing.plan)
├─ membership.setIsGrandfathered(pricing.isGrandfathered)
├─ badge = badgeRepository.save(badge)
└─ emailService.sendPurchaseConfirmation(user, transaction, pricing) ✉️
```

### **Verificación Pendiente**
```java
UserService.requestEmailVerification()
├─ token = generateVerificationToken()
├─ user.setVerificationToken(token)
└─ emailService.sendVerificationEmail(user, token) ✉️
```

### **Reset de Contraseña**
```java
AuthService.forgotPassword()
├─ token = generateResetToken()
├─ user.setResetToken(token)
└─ emailService.sendPasswordResetEmail(user, token) ✉️
```

### **Recordatorio Automático (Cron Job)**
```java
@Scheduled(cron = "0 0 9 * * *") // Diario 9:00 AM
public void sendRenewalReminders() {
    List<Membership> expiringSoon = membershipRepository
        .findByExpiringInDays(7);
    
    for (Membership m : expiringSoon) {
        emailService.sendRenewalReminder(m.getUser(), 7);
    }
}
```

---

## 🎨 Diseño de Plantillas

### Paleta de Colores
```css
--bg-dark: #0a0e27
--bg-card: #1a1f3a
--gold: #ffd700
--orange: #ff6b00
--cyan: #00d4ff
--green: #00ff88
--text-white: #ffffff
--text-gray: #888888
```

### Elementos Comunes
- **Header:** Gradiente gold-orange con nombre de app
- **Badge:** Gradiente circular con rol (Founder/Early Adopter)
- **Price Box:** Borde dorado con precio destacado
- **Grandfathered Banner:** Gradiente animado verde-cyan
- **Button:** Gradiente con border-radius 30px
- **Footer:** Fondo oscuro con copyright

### Responsive Design
- Max-width: 600px
- Padding adaptativo
- Fuentes claras y legibles
- Iconos emoji para mejor visualización

---

## 🔧 Integración con AuthService

### Cambios Realizados

**AuthService.java:**
```java
@Autowired
private PricingService pricingService;

@Autowired
private EmailService emailService;

public AuthResponse register(RegisterRequest request) {
    // ...código existente...
    
    // Calcular precio según fase (automático)
    PricingService.PricingInfo pricing = pricingService.calculatePricing(newUserNumber);
    
    // Crear membresía FREE con información de fase
    membership.setIsGrandfathered(pricing.isGrandfathered);
    
    // ...guardar...
    
    // Enviar email de bienvenida con información de fase
    emailService.sendWelcomeEmail(user, pricing);
    
    return authResponse;
}
```

---

## 📊 Tracking y Analytics

### Métricas a Monitorear
- ✉️ Emails enviados por tipo
- ✅ Tasa de apertura (requiere tracking pixels)
- 🔗 Clicks en CTAs
- ❌ Bounces y errores
- ⏱️ Tiempo promedio de verificación
- 🔄 Tasa de conversión (Free → Premium)

### Logs Automáticos
```java
System.err.println("Error sending welcome email: " + e.getMessage());
```

**Mejora Futura:** Usar SLF4J para logs estructurados
```java
log.error("Failed to send email to {}: {}", user.getEmail(), e.getMessage());
```

---

## 🚀 Próximos Pasos

### Fase 1: Testing ✅
- [x] Crear EmailService.java
- [x] Crear 5 plantillas HTML
- [x] Integrar con AuthService
- [x] Configurar SMTP

### Fase 2: Integración (En Progreso)
- [ ] Integrar con PaymentService (compras)
- [ ] Agregar cron job para renovaciones
- [ ] Implementar verificación de email
- [ ] Implementar reset de contraseña

### Fase 3: Mejoras
- [ ] Agregar tracking pixels para analytics
- [ ] Implementar cola de emails (Redis/RabbitMQ)
- [ ] Agregar reintentos automáticos
- [ ] Crear panel admin para ver emails enviados
- [ ] Agregar templates dinámicos (configurables)

### Fase 4: Testing Real
- [ ] Configurar cuenta Gmail de producción
- [ ] Probar envío real con usuarios de prueba
- [ ] Validar renderizado en diferentes clientes
- [ ] Optimizar para móviles

---

## 🔒 Seguridad

### Buenas Prácticas Implementadas
✅ Uso de variables de entorno para credenciales  
✅ Errores capturados (no fallan el registro)  
✅ Tokens de verificación con expiración  
✅ STARTTLS habilitado  
✅ No incluir información sensible en emails  

### Recomendaciones
- Rotar contraseñas de SMTP regularmente
- Usar servicios dedicados en producción (SendGrid, AWS SES)
- Implementar rate limiting (máx X emails por usuario/día)
- Encriptar tokens en base de datos
- Agregar CAPTCHA antes de solicitar reset

---

## 📝 Ejemplo de Uso

### Enviar Email de Bienvenida
```java
@Autowired
private EmailService emailService;

@Autowired
private PricingService pricingService;

public void registerUser(User user) {
    // Guardar usuario
    user = userRepository.save(user);
    
    // Calcular pricing
    PricingInfo pricing = pricingService.calculatePricing(user.getUserNumber());
    
    // Enviar email
    emailService.sendWelcomeEmail(user, pricing);
}
```

### Enviar Confirmación de Compra
```java
public void processPurchase(User user, PaymentTransaction transaction) {
    // Guardar transacción
    transaction = transactionRepository.save(transaction);
    
    // Calcular pricing
    PricingInfo pricing = pricingService.calculatePricing(user.getUserNumber());
    
    // Enviar confirmación
    emailService.sendPurchaseConfirmation(user, transaction, pricing);
}
```

---

## 🎯 KPIs del Sistema

| Métrica | Objetivo | Actual |
|---------|----------|--------|
| Tasa de entrega | >99% | TBD |
| Tasa de apertura | >40% | TBD |
| Tasa de clicks | >15% | TBD |
| Tiempo de envío | <5s | TBD |
| Verificación email | >80% | TBD |

---

## 📞 Soporte

**Errores comunes:**

❌ **"Authentication failed"**  
→ Verificar MAIL_USERNAME y MAIL_PASSWORD  
→ Activar "App Password" en Gmail  

❌ **"Connection timeout"**  
→ Verificar firewall (puerto 587)  
→ Confirmar spring.mail.host correcto  

❌ **"Template not found"**  
→ Verificar que templates estén en `resources/templates/email/`  
→ Confirmar nombre del template sin extensión  

---

**Estado del Sistema:** ✅ Implementado y listo para testing  
**Última actualización:** 11/11/2025
