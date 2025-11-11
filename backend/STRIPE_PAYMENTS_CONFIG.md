# 💳 Configuración de Stripe Payments - DrakkarPress

## 🎯 Sistema de Pricing Dinámico

DrakkarPress usa precios escalonados según el número de usuario:

| Fase | User Number | Precio Mensual | Precio Anual |
|------|-------------|----------------|--------------|
| **Phase 1** | 1 - 1,000 | $5.00/mes | $50/año (2 meses gratis) |
| **Phase 2** | 1,001 - 10,000 | $10.00/mes | $100/año |
| **Phase 3** | 10,001+ | $19.99/mes | $199.90/año |

**Grandfathered**: Usuarios mantienen su precio original de por vida.

---

## 🔧 Configuración Inicial de Stripe

### 1. Crear Cuenta

1. Ir a [Stripe.com](https://stripe.com/)
2. Sign Up → País: United States (o tu país)
3. Completar información de negocio:
   - **Business Name**: DrakkarPress
   - **Industry**: Publishing / Software
   - **Website**: https://www.drakkarpress.com

### 2. Activar Account

**Modo Test** (para desarrollo):
- No requiere activación
- Usar test keys inmediatamente

**Modo Live** (para producción):
1. Dashboard → Activate your account
2. Completar información:
   - Business details (dirección, tax ID)
   - Bank account (para recibir pagos)
   - Identity verification
3. Esperar aprobación (1-3 días)

---

## 🔑 Obtener API Keys

### Test Mode (Desarrollo)

1. Dashboard → Developers → API keys
2. Copiar:
   - **Publishable key**: `pk_test_51ABC...`
   - **Secret key**: `sk_test_51ABC...`

### Live Mode (Producción)

1. Toggle "View test data" → OFF
2. Copiar:
   - **Publishable key**: `pk_live_51ABC...`
   - **Secret key**: `sk_live_51ABC...`

---

## 💰 Crear Productos y Precios

### Opción A: Vía Dashboard (Manual)

#### Paso 1: Crear Productos

1. Dashboard → Products → Add product

**Product 1: Phase 1**
- **Name**: DrakkarPress Membership - Early Adopter
- **Description**: Para usuarios 1-1000. Precio grandfathered de por vida.
- **Pricing**:
  - Monthly: $5.00 USD (recurring)
  - Yearly: $50.00 USD (recurring)

**Product 2: Phase 2**
- **Name**: DrakkarPress Membership - Builder
- **Description**: Para usuarios 1001-10000
- **Pricing**:
  - Monthly: $10.00 USD (recurring)
  - Yearly: $100.00 USD (recurring)

**Product 3: Phase 3**
- **Name**: DrakkarPress Membership - Standard
- **Description**: Para usuarios 10001+
- **Pricing**:
  - Monthly: $19.99 USD (recurring)
  - Yearly: $199.90 USD (recurring)

#### Paso 2: Obtener Price IDs

Después de crear, copiar los **Price IDs**:
```
price_1ABC123monthly (Phase 1 monthly)
price_1ABC123yearly (Phase 1 yearly)
price_1DEF456monthly (Phase 2 monthly)
price_1DEF456yearly (Phase 2 yearly)
price_1GHI789monthly (Phase 3 monthly)
price_1GHI789yearly (Phase 3 yearly)
```

### Opción B: Vía API (Automático)

```bash
# Crear productos y precios con curl
curl https://api.stripe.com/v1/products \
  -u sk_test_51ABC...: \
  -d name="DrakkarPress Membership - Early Adopter" \
  -d description="Para usuarios 1-1000" \
  -d metadata[phase]=1

# Crear precio mensual
curl https://api.stripe.com/v1/prices \
  -u sk_test_51ABC...: \
  -d unit_amount=500 \
  -d currency=usd \
  -d recurring[interval]=month \
  -d product=prod_ABC123 \
  -d metadata[phase]=1

# Crear precio anual
curl https://api.stripe.com/v1/prices \
  -u sk_test_51ABC...: \
  -d unit_amount=5000 \
  -d currency=usd \
  -d recurring[interval]=year \
  -d product=prod_ABC123 \
  -d metadata[phase]=1
```

---

## 🪝 Configurar Webhooks

### Paso 1: Crear Endpoint

1. Dashboard → Developers → Webhooks → Add endpoint
2. **Endpoint URL**: `https://api.drakkarpress.com/api/webhooks/stripe`
3. **Events to send**:
   - `checkout.session.completed` (pago exitoso)
   - `customer.subscription.created` (nueva suscripción)
   - `customer.subscription.updated` (cambio de plan)
   - `customer.subscription.deleted` (cancelación)
   - `invoice.payment_succeeded` (renovación exitosa)
   - `invoice.payment_failed` (pago fallido)
   - `payment_intent.succeeded` (pago one-time exitoso)
   - `payment_intent.payment_failed` (pago one-time fallido)

### Paso 2: Obtener Signing Secret

Después de crear el webhook, copiar el **Signing secret**:
```
whsec_ABC123...
```

### Paso 3: Variables de Entorno

```bash
# Test Mode
STRIPE_PUBLIC_KEY=pk_test_51ABC...
STRIPE_SECRET_KEY=sk_test_51ABC...
STRIPE_WEBHOOK_SECRET=whsec_ABC123...

# Live Mode
STRIPE_PUBLIC_KEY=pk_live_51ABC...
STRIPE_SECRET_KEY=sk_live_51ABC...
STRIPE_WEBHOOK_SECRET=whsec_DEF456...

# Price IDs (Test Mode)
STRIPE_PRICE_PHASE_1_MONTHLY=price_1ABC123monthly
STRIPE_PRICE_PHASE_1_YEARLY=price_1ABC123yearly
STRIPE_PRICE_PHASE_2_MONTHLY=price_1DEF456monthly
STRIPE_PRICE_PHASE_2_YEARLY=price_1DEF456yearly
STRIPE_PRICE_PHASE_3_MONTHLY=price_1GHI789monthly
STRIPE_PRICE_PHASE_3_YEARLY=price_1GHI789yearly

# Configuración
STRIPE_CURRENCY=usd
STRIPE_SUCCESS_URL=https://www.drakkarpress.com/success?session_id={CHECKOUT_SESSION_ID}
STRIPE_CANCEL_URL=https://www.drakkarpress.com/pricing
```

---

## 💻 Implementación Backend (Spring Boot)

### 1. Dependencias Maven

```xml
<dependency>
    <groupId>com.stripe</groupId>
    <artifactId>stripe-java</artifactId>
    <version>24.16.0</version>
</dependency>
```

### 2. Configuración

```java
@Configuration
public class StripeConfig {
    
    @Value("${stripe.secret.key}")
    private String secretKey;
    
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}
```

### 3. Service de Pricing Dinámico

```java
@Service
public class PricingService {
    
    public String getPriceIdForUser(Long userNumber, String interval) {
        // Phase 1: 1-1000
        if (userNumber <= 1000) {
            return interval.equals("monthly") 
                ? env.getProperty("stripe.price.phase1.monthly")
                : env.getProperty("stripe.price.phase1.yearly");
        }
        
        // Phase 2: 1001-10000
        if (userNumber <= 10000) {
            return interval.equals("monthly")
                ? env.getProperty("stripe.price.phase2.monthly")
                : env.getProperty("stripe.price.phase2.yearly");
        }
        
        // Phase 3: 10001+
        return interval.equals("monthly")
            ? env.getProperty("stripe.price.phase3.monthly")
            : env.getProperty("stripe.price.phase3.yearly");
    }
    
    public BigDecimal getPriceAmountForUser(Long userNumber, String interval) {
        if (userNumber <= 1000) {
            return interval.equals("monthly") ? new BigDecimal("5.00") : new BigDecimal("50.00");
        }
        if (userNumber <= 10000) {
            return interval.equals("monthly") ? new BigDecimal("10.00") : new BigDecimal("100.00");
        }
        return interval.equals("monthly") ? new BigDecimal("19.99") : new BigDecimal("199.90");
    }
    
    public String getPlanNameForUser(Long userNumber) {
        if (userNumber <= 1000) return "PHASE_1";
        if (userNumber <= 10000) return "PHASE_2";
        return "PHASE_3";
    }
}
```

### 4. Crear Checkout Session

```java
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    
    @Autowired
    private PricingService pricingService;
    
    @PostMapping("/create-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(
            @RequestBody CheckoutRequest request,
            Authentication authentication) {
        
        User user = (User) authentication.getPrincipal();
        
        // Obtener precio según user_number
        String priceId = pricingService.getPriceIdForUser(
            user.getUserNumber(), 
            request.getInterval()
        );
        
        // Crear Stripe Customer
        CustomerCreateParams customerParams = CustomerCreateParams.builder()
            .setEmail(user.getEmail())
            .setName(user.getFullName())
            .putMetadata("user_id", user.getId().toString())
            .putMetadata("user_number", user.getUserNumber().toString())
            .build();
        
        Customer customer = Customer.create(customerParams);
        
        // Crear Checkout Session
        SessionCreateParams params = SessionCreateParams.builder()
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setCustomer(customer.getId())
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setPrice(priceId)
                    .setQuantity(1L)
                    .build()
            )
            .setSuccessUrl(env.getProperty("stripe.success.url"))
            .setCancelUrl(env.getProperty("stripe.cancel.url"))
            .putMetadata("user_id", user.getId().toString())
            .putMetadata("user_number", user.getUserNumber().toString())
            .putMetadata("plan", pricingService.getPlanNameForUser(user.getUserNumber()))
            .putMetadata("is_grandfathered", "true")
            .build();
        
        Session session = Session.create(params);
        
        return ResponseEntity.ok(Map.of(
            "sessionId", session.getId(),
            "url", session.getUrl()
        ));
    }
}
```

### 5. Webhook Handler

```java
@RestController
@RequestMapping("/api/webhooks")
public class StripeWebhookController {
    
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    
    @Autowired
    private MembershipService membershipService;
    
    @PostMapping("/stripe")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(400).body("Invalid signature");
        }
        
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutCompleted(event);
                break;
            case "customer.subscription.created":
                handleSubscriptionCreated(event);
                break;
            case "customer.subscription.deleted":
                handleSubscriptionCanceled(event);
                break;
            case "invoice.payment_succeeded":
                handlePaymentSucceeded(event);
                break;
            case "invoice.payment_failed":
                handlePaymentFailed(event);
                break;
        }
        
        return ResponseEntity.ok("Received");
    }
    
    private void handleCheckoutCompleted(Event event) {
        Session session = (Session) event.getDataObjectDeserializer()
            .getObject().orElse(null);
        
        if (session != null) {
            Long userId = Long.parseLong(session.getMetadata().get("user_id"));
            Long userNumber = Long.parseLong(session.getMetadata().get("user_number"));
            String plan = session.getMetadata().get("plan");
            boolean isGrandfathered = Boolean.parseBoolean(
                session.getMetadata().get("is_grandfathered")
            );
            
            // Crear membresía
            membershipService.activateMembership(
                userId, 
                plan, 
                session.getAmountTotal() / 100.0,
                isGrandfathered,
                session.getSubscription()
            );
        }
    }
    
    private void handlePaymentSucceeded(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
            .getObject().orElse(null);
        
        if (invoice != null) {
            // Renovar membresía
            membershipService.renewMembership(
                invoice.getCustomer(),
                invoice.getAmountPaid() / 100.0
            );
        }
    }
    
    private void handlePaymentFailed(Event event) {
        Invoice invoice = (Invoice) event.getDataObjectDeserializer()
            .getObject().orElse(null);
        
        if (invoice != null) {
            // Notificar al usuario
            membershipService.handlePaymentFailure(invoice.getCustomer());
        }
    }
}
```

---

## 🌐 Implementación Frontend

### 1. Incluir Stripe.js

```html
<!-- En <head> de todas las páginas de checkout -->
<script src="https://js.stripe.com/v3/"></script>
```

### 2. Código JavaScript

```javascript
// js/checkout.js
const stripe = Stripe('pk_test_51ABC...'); // Reemplazar con tu public key

async function createCheckoutSession(interval) {
    try {
        // Llamar al backend
        const response = await fetch('/api/checkout/create-session', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${getAuthToken()}`
            },
            body: JSON.stringify({ interval: interval })
        });
        
        const session = await response.json();
        
        // Redirigir a Stripe Checkout
        const result = await stripe.redirectToCheckout({
            sessionId: session.sessionId
        });
        
        if (result.error) {
            alert(result.error.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al crear sesión de pago');
    }
}

// Event listeners para botones
document.getElementById('btn-monthly').addEventListener('click', () => {
    createCheckoutSession('monthly');
});

document.getElementById('btn-yearly').addEventListener('click', () => {
    createCheckoutSession('yearly');
});
```

### 3. Página de Pricing (ejemplo)

```html
<!-- pricing.html -->
<div class="pricing-card">
    <h3>Tu Precio Personal</h3>
    <p class="price">
        <span class="amount">$<span id="monthly-price">5.00</span></span>
        <span class="period">/mes</span>
    </p>
    <p class="user-number">Eres el usuario #<span id="user-number">42</span></p>
    <button id="btn-monthly" class="btn-primary">Suscribirse Mensual</button>
    <button id="btn-yearly" class="btn-secondary">
        Suscribirse Anual 
        <small>(Ahorra 2 meses)</small>
    </button>
    <ul class="features">
        <li>✨ 1000 características de IA/mes</li>
        <li>📚 Biblioteca ilimitada</li>
        <li>🔮 Runas personalizadas</li>
        <li>🏅 Badge exclusivo de Early Adopter</li>
        <li>🔒 Precio grandfathered de por vida</li>
    </ul>
</div>

<script>
// Obtener pricing dinámico
async function loadUserPricing() {
    const response = await fetch('/api/users/me', {
        headers: { 'Authorization': `Bearer ${getAuthToken()}` }
    });
    const user = await response.json();
    
    document.getElementById('user-number').textContent = user.userNumber;
    
    // Calcular precio según user_number
    let monthlyPrice, yearlyPrice;
    if (user.userNumber <= 1000) {
        monthlyPrice = 5.00;
        yearlyPrice = 50.00;
    } else if (user.userNumber <= 10000) {
        monthlyPrice = 10.00;
        yearlyPrice = 100.00;
    } else {
        monthlyPrice = 19.99;
        yearlyPrice = 199.90;
    }
    
    document.getElementById('monthly-price').textContent = monthlyPrice.toFixed(2);
}

loadUserPricing();
</script>
```

---

## 🧪 Testing con Tarjetas de Prueba

### Tarjetas de Test

**Pago exitoso**:
```
Número: 4242 4242 4242 4242
Fecha: Cualquier fecha futura (ej: 12/25)
CVV: Cualquier 3 dígitos (ej: 123)
ZIP: Cualquier código postal
```

**Pago fallido**:
```
Número: 4000 0000 0000 0002
```

**Requiere autenticación 3D Secure**:
```
Número: 4000 0027 6000 3184
```

### Script de Test

```bash
# test-stripe-checkout.sh
curl -X POST https://api.drakkarpress.com/api/checkout/create-session \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"interval": "monthly"}'
```

---

## 📊 Dashboard y Reporting

### Métricas Importantes

1. **MRR (Monthly Recurring Revenue)**
   - Dashboard → Home → Monthly Recurring Revenue

2. **Churn Rate**
   - Dashboard → Analytics → Subscriptions → Churn

3. **Failed Payments**
   - Dashboard → Payments → Failed

4. **Customer Lifetime Value (LTV)**
   - Dashboard → Analytics → Customers

### Exportar Datos

1. Dashboard → Reports → Create report
2. Seleccionar métricas:
   - Subscriptions created
   - Revenue
   - Customer count
   - Failed charges
3. Schedule → Daily/Weekly email

---

## 🔐 Checklist de Seguridad

- [ ] Nunca exponer Secret Key en frontend
- [ ] Validar webhook signature (Stripe-Signature header)
- [ ] Usar HTTPS en producción
- [ ] Guardar Secret Key en variables de entorno
- [ ] Habilitar Stripe Radar (fraud detection)
- [ ] Configurar límites de rate limiting
- [ ] Implementar retry logic para webhooks
- [ ] Logs de todas las transacciones
- [ ] Compliance con PCI DSS (Stripe lo maneja)
- [ ] Implementar idempotency keys para requests

---

## 🚨 Manejo de Errores Comunes

### Error: Invalid API Key
```
Verificar que STRIPE_SECRET_KEY esté correctamente configurado
y que coincida con el modo (test vs live)
```

### Error: No such price
```
Verificar que los Price IDs existan en tu cuenta
y coincidan con el modo (test vs live)
```

### Error: Webhook signature verification failed
```
Verificar que STRIPE_WEBHOOK_SECRET sea correcto
y que esté recibiendo el header Stripe-Signature
```

### Error: Amount too small
```
Stripe requiere mínimo $0.50 USD
Verificar que los precios sean >= 50 centavos
```

---

## 📚 Recursos Adicionales

- [Stripe Dashboard](https://dashboard.stripe.com/)
- [Stripe Docs](https://stripe.com/docs)
- [Stripe Java SDK](https://github.com/stripe/stripe-java)
- [Testing Webhooks](https://stripe.com/docs/webhooks/test)
- [Price IDs](https://dashboard.stripe.com/prices)

---

**Creado**: 2025-11-11  
**Última actualización**: 2025-11-11  
**Stripe API Version**: 2024-11-20
