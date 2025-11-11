# 💰 SISTEMA DE PRECIOS DRAKKARPRESS v2.0

## 🎯 ESTRATEGIA DE GRANDFATHERING

### 📊 Fases de Lanzamiento (Precio de por vida)

#### **FASE 1: FUNDADORES** 👑
- **Usuarios:** 1 - 1,000
- **Precio Mensual:** $5.00 USD
- **Precio Anual:** $50.00 USD (ahorro 17%)
- **Status:** GRANDFATHERED (precio de por vida)
- **Revenue Año 1:** $50,000 USD
- **Beneficios:**
  - ✅ Badge "Founder"
  - ✅ Precio bloqueado para siempre
  - ✅ Acceso prioritario a nuevas funciones
  - ✅ Mención en "Hall of Fame"

#### **FASE 2: EARLY ADOPTERS** 🚀
- **Usuarios:** 1,001 - 10,000
- **Precio Mensual:** $10.00 USD
- **Precio Anual:** $100.00 USD (ahorro 17%)
- **Status:** GRANDFATHERED (precio de por vida)
- **Revenue Año 1:** $900,000 USD
- **Beneficios:**
  - ✅ Badge "Early Adopter"
  - ✅ Precio bloqueado para siempre
  - ✅ Acceso temprano a nuevas funciones

#### **FASE 3: LAUNCH PROMO** 🎉
- **Usuarios:** 10,001 - 15,000
- **Precio Mensual:** $15.00 USD
- **Precio Anual:** $150.00 USD (ahorro 17%)
- **Status:** GRANDFATHERED (precio de por vida)
- **Revenue Año 1:** $750,000 USD
- **Beneficios:**
  - ✅ Badge "Launch Member"
  - ✅ Precio bloqueado para siempre

#### **FASE 4: PRECIO REGULAR** 💎
- **Usuarios:** 15,001+
- **Precio Mensual:** $19.90 USD
- **Precio Anual:** $170.00 USD (ahorro 15%)
- **Status:** Regular (sin grandfathering)
- **Revenue Proyectado:** Escalable

---

## 📈 PROYECCIÓN DE REVENUE

### Año 1 (Alcanzar 15,000 usuarios)
```
Fase 1 (1-1,000):        1,000 x $50  = $50,000
Fase 2 (1,001-10,000):   9,000 x $100 = $900,000
Fase 3 (10,001-15,000):  5,000 x $150 = $750,000
──────────────────────────────────────────────────
TOTAL AÑO 1:                          $1,700,000
```

### Año 2+ (Usuarios grandfathered + nuevos regulares)
```
15,000 usuarios grandfathered:  $1,700,000/año (recurring)
+ Nuevos usuarios regulares:     $19.90/mes cada uno

Ejemplo con 5,000 nuevos usuarios regulares:
5,000 x $170/año = $850,000

TOTAL AÑO 2:  $1,700,000 + $850,000 = $2,550,000
```

---

## 🎁 COMPARACIÓN DE PLANES

| Fase | Usuarios | Mensual | Anual | Ahorro | Grandfathered | Badge |
|------|----------|---------|-------|--------|---------------|-------|
| **FREE** | Todos | $0 | $0 | - | N/A | - |
| **Fase 1** | 1-1K | $5 | $50 | 17% | ✅ SÍ | Founder |
| **Fase 2** | 1K-10K | $10 | $100 | 17% | ✅ SÍ | Early Adopter |
| **Fase 3** | 10K-15K | $15 | $150 | 17% | ✅ SÍ | Launch Member |
| **Regular** | 15K+ | $19.90 | $170 | 15% | ❌ NO | Premium |

---

## 💡 BENEFICIOS POR PLAN

### FREE
- ✅ Acceso básico a la plataforma
- ✅ Generación limitada de PDFs (10/mes)
- ❌ Sin selección de runas
- ❌ Sin badges especiales

### PREMIUM (Todos los tiers)
- ✅ Generación ilimitada de PDFs
- ✅ Selección de runa personalizada (24 Elder Futhark)
- ✅ Acceso a todos los generadores de IA
- ✅ Red social completa
- ✅ Mensajería privada
- ✅ Badges especiales
- ✅ Soporte prioritario

### GRANDFATHERED (Fases 1-3)
- ✅ TODO lo de Premium
- ✅ Precio bloqueado de por vida
- ✅ Badge exclusivo de fase
- ✅ Acceso prioritario a nuevas funciones

---

## 🔢 CÁLCULO DE PRECIO AUTOMÁTICO

### Lógica de Asignación
```javascript
function calculatePrice(userNumber) {
  if (userNumber >= 1 && userNumber <= 1000) {
    return {
      plan: 'PREMIUM_PHASE_1',
      monthly: 5.00,
      annual: 50.00,
      isGrandfathered: true,
      badge: 'FOUNDER'
    };
  }
  
  if (userNumber >= 1001 && userNumber <= 10000) {
    return {
      plan: 'PREMIUM_PHASE_2',
      monthly: 10.00,
      annual: 100.00,
      isGrandfathered: true,
      badge: 'EARLY_ADOPTER'
    };
  }
  
  if (userNumber >= 10001 && userNumber <= 15000) {
    return {
      plan: 'PREMIUM_PHASE_3',
      monthly: 15.00,
      annual: 150.00,
      isGrandfathered: true,
      badge: 'LAUNCH_MEMBER'
    };
  }
  
  // Regular (15,001+)
  return {
    plan: 'PREMIUM_REGULAR',
    monthly: 19.90,
    annual: 170.00,
    isGrandfathered: false,
    badge: 'PREMIUM_MEMBER'
  };
}
```

---

## 🎯 ESTRATEGIA DE MARKETING

### Urgencia por Fase
1. **Fase 1 (1-1,000):** "¡Solo quedan X cupos de Fundador!"
2. **Fase 2 (1,001-10,000):** "Early Adopter - 50% OFF del precio final"
3. **Fase 3 (10,001-15,000):** "Última oportunidad - 25% OFF"
4. **Regular (15,001+):** Precio estándar

### Mensajes Clave
- ✅ "Precio bloqueado de por vida"
- ✅ "Nunca pagarás más, incluso si subimos los precios"
- ✅ "Badge exclusivo que nadie más podrá obtener"
- ✅ "Solo X cupos disponibles en esta fase"

---

## 📊 DASHBOARD DE ADMIN

### Métricas a Mostrar
- Total usuarios por fase
- Revenue mensual/anual
- Cupos restantes en fase actual
- Proyección de revenue
- Tasa de conversión FREE → PREMIUM

---

## 🔐 IMPLEMENTACIÓN TÉCNICA

### Base de Datos
```sql
-- Campo user_number en tabla users
user_number SERIAL UNIQUE -- Asignado automáticamente al registrarse

-- Trigger automático para calcular precio
CREATE OR REPLACE FUNCTION assign_membership_price()
RETURNS TRIGGER AS $$
BEGIN
  -- Lógica de cálculo según user_number
  IF NEW.user_number <= 1000 THEN
    NEW.price_usd := 50.00; -- Anual
    NEW.is_grandfathered := TRUE;
  ELSIF NEW.user_number <= 10000 THEN
    NEW.price_usd := 100.00;
    NEW.is_grandfathered := TRUE;
  ELSIF NEW.user_number <= 15000 THEN
    NEW.price_usd := 150.00;
    NEW.is_grandfathered := TRUE;
  ELSE
    NEW.price_usd := 170.00;
    NEW.is_grandfathered := FALSE;
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

---

## 🎉 EJEMPLO DE USUARIO

### Usuario #500 (Fundador)
```json
{
  "userId": "uuid",
  "userNumber": 500,
  "email": "user@example.com",
  "membership": {
    "plan": "PREMIUM_PHASE_1",
    "priceMonthly": 5.00,
    "priceAnnual": 50.00,
    "isGrandfathered": true,
    "paymentFrequency": "ANNUAL"
  },
  "badges": ["FOUNDER", "PREMIUM_MEMBER"],
  "message": "¡Felicidades! Eres FUNDADOR #500. Tu precio de $50/año está bloqueado para siempre."
}
```

---

**DrakkarPress Platform v2.0**  
*Sistema de Grandfathering Inteligente*  
🛡️ Precios justos y transparentes
