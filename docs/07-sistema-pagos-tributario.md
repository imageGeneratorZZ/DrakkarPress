# Sistema de Pagos y Gestión Tributaria - DrakkarPress

## Visión General

DrakkarPress implementa un **sistema automatizado de distribución de pagos** que:
- Recibe pagos de múltiples plataformas (Shopify, MercadoLibre, ventas directas)
- Calcula y retiene comisiones según el modelo de negocio
- Distribuye automáticamente a escritores, revendedores e imprentas
- Genera documentación fiscal y retenciones impositivas
- Cumple con regulaciones tributarias internacionales

---

## 🏦 Modelo de Distribución de Pagos

### Estructura de Comisiones

```
VENTA DIRECTA (sin revendedor)
┌─────────────────────────────────────────┐
│ Precio venta: $100.00                   │
├─────────────────────────────────────────┤
│ ✍️ Escritor:        $90.00 (90%)        │
│ 🏢 DrakkarPress:    $10.00 (10%)        │
└─────────────────────────────────────────┘

VENTA CON REVENDEDOR
┌─────────────────────────────────────────┐
│ Precio venta: $100.00                   │
├─────────────────────────────────────────┤
│ ✍️ Escritor:        $60.00 (60%)        │
│ 💼 Revendedor:      $30.00 (30%)        │
│ 🏢 DrakkarPress:    $10.00 (10%)        │
└─────────────────────────────────────────┘

VENTA IMPRESA (se resta costo impresión primero)
┌─────────────────────────────────────────┐
│ Precio venta: $100.00                   │
│ Costo impresión: -$20.00                │
├─────────────────────────────────────────┤
│ Base distribución: $80.00               │
│                                         │
│ ✍️ Escritor:        $72.00 (90%)        │
│ 🏢 DrakkarPress:    $8.00 (10%)         │
│ 🏭 Imprenta:        $20.00 (costo fijo) │
└─────────────────────────────────────────┘
```

---

## 💳 Flujo de Procesamiento de Pagos

### Diagrama de Flujo

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENTE COMPRA                           │
│              (Shopify/MercadoLibre/Directo)                 │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│         PAYMENT SERVICE recibe webhook/notificación         │
│                (Puerto 8084 - Java)                         │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│              VALIDACIÓN DE PAGO                             │
│  • Verificar transacción completada                         │
│  • Validar monto                                            │
│  • Detectar fraude (si aplica)                              │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────┐
│           CÁLCULO DE DISTRIBUCIÓN                           │
│  • Identificar tipo de venta (directa/afiliado)             │
│  • Aplicar porcentajes según modelo                         │
│  • Calcular retenciones fiscales por país                   │
│  • Generar splits de pago                                   │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ├─────────────────┬─────────────────┬──────────────┐
                     ▼                 ▼                 ▼              ▼
            ┌────────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────┐
            │  ESCRITOR      │ │ REVENDEDOR   │ │ IMPRENTA     │ │ DRAKKAR │
            │  Cuenta Stripe │ │ Cuenta       │ │ Cuenta       │ │ Cuenta  │
            │  o Bancaria    │ │ Stripe/Banco │ │ Bancaria     │ │ Master  │
            └────────────────┘ └──────────────┘ └──────────────┘ └─────────┘
                     │                 │                 │              │
                     ▼                 ▼                 ▼              ▼
            ┌────────────────────────────────────────────────────────────┐
            │           REGISTRO CONTABLE Y TRIBUTARIO                   │
            │  • Generar comprobante de pago                             │
            │  • Registrar retención fiscal                              │
            │  • Emitir factura/recibo                                   │
            │  • Actualizar balances                                     │
            └────────────────────────────────────────────────────────────┘
```

---

## 🔧 Arquitectura Técnica

### Microservicio: Payment Service

**Puerto:** 8084  
**Responsabilidades:**
- Procesamiento de webhooks de pago
- Cálculo de splits de pago
- Distribución automática de fondos
- Retenciones fiscales
- Emisión de comprobantes

### Modelo de Datos

```sql
-- Tabla: payments
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    external_payment_id VARCHAR(255), -- ID de Shopify/ML
    payment_method VARCHAR(50), -- credit_card, paypal, etc
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    status VARCHAR(50), -- pending, completed, failed, refunded
    payment_date TIMESTAMP,
    platform VARCHAR(50), -- shopify, mercadolibre, direct
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla: payment_splits (distribución de pagos)
CREATE TABLE payment_splits (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id),
    recipient_type VARCHAR(50), -- writer, reseller, printer, platform
    recipient_id BIGINT NOT NULL,
    gross_amount DECIMAL(10,2) NOT NULL, -- Monto bruto
    tax_withholding DECIMAL(10,2) DEFAULT 0, -- Retención fiscal
    net_amount DECIMAL(10,2) NOT NULL, -- Monto neto a recibir
    percentage DECIMAL(5,2), -- Porcentaje aplicado (90, 60, 30, 10)
    status VARCHAR(50), -- pending, processing, completed, failed
    transfer_id VARCHAR(255), -- ID de transferencia (Stripe, etc)
    transferred_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla: tax_withholdings (retenciones fiscales)
CREATE TABLE tax_withholdings (
    id BIGSERIAL PRIMARY KEY,
    payment_split_id BIGINT NOT NULL REFERENCES payment_splits(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    country_code VARCHAR(3), -- US, MX, AR, ES, etc
    tax_type VARCHAR(50), -- income_tax, vat, iva, etc
    tax_rate DECIMAL(5,2), -- Porcentaje de retención
    tax_amount DECIMAL(10,2) NOT NULL,
    fiscal_year INTEGER,
    fiscal_period VARCHAR(10), -- 2025-01, 2025-02, etc
    certificate_url VARCHAR(500), -- URL del certificado de retención
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla: payout_accounts (cuentas de pago)
CREATE TABLE payout_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    account_type VARCHAR(50), -- stripe, bank_transfer, paypal
    stripe_account_id VARCHAR(255),
    bank_name VARCHAR(255),
    bank_account_number VARCHAR(255) ENCRYPTED,
    bank_routing_number VARCHAR(50),
    bank_swift_code VARCHAR(20),
    paypal_email VARCHAR(255),
    country VARCHAR(3),
    currency VARCHAR(3) DEFAULT 'USD',
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla: invoices (facturas/comprobantes)
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payments(id),
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_type VARCHAR(50), -- sale, commission, service
    issuer_name VARCHAR(255), -- DrakkarPress Inc.
    issuer_tax_id VARCHAR(50), -- RFC/EIN/VAT
    recipient_name VARCHAR(255),
    recipient_tax_id VARCHAR(50),
    subtotal DECIMAL(10,2),
    tax_amount DECIMAL(10,2),
    total DECIMAL(10,2),
    currency VARCHAR(3) DEFAULT 'USD',
    issue_date DATE,
    due_date DATE,
    pdf_url VARCHAR(500),
    xml_url VARCHAR(500), -- Para México (CFDI)
    status VARCHAR(50), -- draft, issued, paid, cancelled
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## 💰 Implementación: Payment Service

### Clase Principal

```java
@Service
@Slf4j
public class PaymentDistributionService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private PaymentSplitRepository splitRepository;
    
    @Autowired
    private TaxService taxService;
    
    @Autowired
    private StripeService stripeService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Transactional
    public void processPaymentDistribution(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        
        Order order = payment.getOrder();
        Book book = order.getBook();
        User writer = book.getWriter();
        
        log.info("Processing payment distribution for order: {}", order.getId());
        
        // 1. Determinar tipo de venta
        boolean hasAffiliate = order.getAffiliateId() != null;
        boolean isPrint = order.getOrderType().equals("PRINT");
        
        BigDecimal totalAmount = payment.getAmount();
        BigDecimal distributionAmount = totalAmount;
        
        // 2. Si es impreso, restar costo de impresión
        BigDecimal printCost = BigDecimal.ZERO;
        if (isPrint) {
            printCost = order.getPrintCost();
            distributionAmount = totalAmount.subtract(printCost);
            
            // Pagar a la imprenta
            createPrintPayment(order.getPrinterId(), printCost, payment);
        }
        
        // 3. Calcular splits según modelo
        if (hasAffiliate) {
            // Venta con revendedor: 60% escritor, 30% revendedor, 10% plataforma
            createPaymentSplit(writer.getId(), "WRITER", 
                distributionAmount, new BigDecimal("0.60"), payment);
            
            createPaymentSplit(order.getAffiliateId(), "RESELLER", 
                distributionAmount, new BigDecimal("0.30"), payment);
            
            createPaymentSplit(null, "PLATFORM", 
                distributionAmount, new BigDecimal("0.10"), payment);
        } else {
            // Venta directa: 90% escritor, 10% plataforma
            createPaymentSplit(writer.getId(), "WRITER", 
                distributionAmount, new BigDecimal("0.90"), payment);
            
            createPaymentSplit(null, "PLATFORM", 
                distributionAmount, new BigDecimal("0.10"), payment);
        }
        
        // 4. Ejecutar transferencias
        executePayoutTransfers(payment);
        
        // 5. Generar facturación
        invoiceService.generateInvoices(payment);
        
        log.info("Payment distribution completed for payment: {}", paymentId);
    }
    
    private void createPaymentSplit(Long recipientId, String recipientType, 
                                   BigDecimal baseAmount, BigDecimal percentage,
                                   Payment payment) {
        
        BigDecimal grossAmount = baseAmount.multiply(percentage);
        
        // Calcular retención fiscal
        BigDecimal taxWithholding = BigDecimal.ZERO;
        if (recipientId != null) {
            User recipient = userRepository.findById(recipientId).orElseThrow();
            taxWithholding = taxService.calculateTaxWithholding(
                recipient, grossAmount, payment.getCurrency()
            );
        }
        
        BigDecimal netAmount = grossAmount.subtract(taxWithholding);
        
        PaymentSplit split = PaymentSplit.builder()
            .paymentId(payment.getId())
            .recipientType(recipientType)
            .recipientId(recipientId)
            .grossAmount(grossAmount)
            .taxWithholding(taxWithholding)
            .netAmount(netAmount)
            .percentage(percentage.multiply(new BigDecimal("100")))
            .status("PENDING")
            .build();
        
        splitRepository.save(split);
        
        log.info("Created payment split: {} - {} - ${}", 
            recipientType, percentage, netAmount);
    }
    
    private void createPrintPayment(Long printerId, BigDecimal amount, 
                                   Payment payment) {
        PaymentSplit split = PaymentSplit.builder()
            .paymentId(payment.getId())
            .recipientType("PRINTER")
            .recipientId(printerId)
            .grossAmount(amount)
            .taxWithholding(BigDecimal.ZERO)
            .netAmount(amount)
            .percentage(BigDecimal.ZERO)
            .status("PENDING")
            .build();
        
        splitRepository.save(split);
    }
    
    @Async
    private void executePayoutTransfers(Payment payment) {
        List<PaymentSplit> splits = splitRepository.findByPaymentId(payment.getId());
        
        for (PaymentSplit split : splits) {
            try {
                if (split.getRecipientId() == null) {
                    // Comisión de plataforma - va a cuenta master
                    split.setStatus("COMPLETED");
                    split.setTransferredAt(LocalDateTime.now());
                    splitRepository.save(split);
                    continue;
                }
                
                // Obtener cuenta de pago del destinatario
                PayoutAccount account = payoutAccountRepository
                    .findActiveByUserId(split.getRecipientId())
                    .orElseThrow(() -> new PayoutAccountNotFoundException());
                
                // Ejecutar transferencia según método
                String transferId = null;
                switch (account.getAccountType()) {
                    case "STRIPE":
                        transferId = stripeService.createTransfer(
                            account.getStripeAccountId(),
                            split.getNetAmount(),
                            payment.getCurrency()
                        );
                        break;
                    case "BANK_TRANSFER":
                        transferId = bankTransferService.createTransfer(
                            account, split.getNetAmount(), payment.getCurrency()
                        );
                        break;
                    case "PAYPAL":
                        transferId = paypalService.createPayout(
                            account.getPaypalEmail(),
                            split.getNetAmount(),
                            payment.getCurrency()
                        );
                        break;
                }
                
                split.setTransferId(transferId);
                split.setStatus("COMPLETED");
                split.setTransferredAt(LocalDateTime.now());
                splitRepository.save(split);
                
                // Notificar al usuario
                notificationService.sendPaymentReceivedNotification(
                    split.getRecipientId(), split.getNetAmount()
                );
                
                log.info("Transfer completed: {} - ${}", transferId, split.getNetAmount());
                
            } catch (Exception e) {
                log.error("Failed to execute transfer for split: {}", split.getId(), e);
                split.setStatus("FAILED");
                splitRepository.save(split);
            }
        }
    }
}
```

---

## 🌍 Gestión Tributaria Internacional

### Servicio de Cálculo Fiscal

```java
@Service
public class TaxService {
    
    // Tasas de retención por país (simplificado)
    private static final Map<String, BigDecimal> TAX_RATES = Map.of(
        "US", new BigDecimal("0.30"),  // Estados Unidos - 30%
        "MX", new BigDecimal("0.10"),  // México - 10%
        "AR", new BigDecimal("0.21"),  // Argentina - 21%
        "ES", new BigDecimal("0.19"),  // España - 19%
        "BR", new BigDecimal("0.15"),  // Brasil - 15%
        "CO", new BigDecimal("0.20"),  // Colombia - 20%
        "CL", new BigDecimal("0.10"),  // Chile - 10%
        "PE", new BigDecimal("0.08")   // Perú - 8%
    );
    
    public BigDecimal calculateTaxWithholding(User user, BigDecimal amount, 
                                             String currency) {
        
        // 1. Verificar si usuario tiene información fiscal completa
        if (!user.hasTaxInfo()) {
            log.warn("User {} missing tax info, no withholding applied", user.getId());
            return BigDecimal.ZERO;
        }
        
        // 2. Obtener país fiscal del usuario
        String countryCode = user.getTaxCountry();
        
        // 3. Verificar si país requiere retención
        if (!TAX_RATES.containsKey(countryCode)) {
            return BigDecimal.ZERO; // Sin retención para países no listados
        }
        
        // 4. Aplicar umbral mínimo (no retener si monto < $100)
        if (amount.compareTo(new BigDecimal("100.00")) < 0) {
            return BigDecimal.ZERO;
        }
        
        // 5. Calcular retención
        BigDecimal taxRate = TAX_RATES.get(countryCode);
        BigDecimal withholding = amount.multiply(taxRate);
        
        // 6. Registrar retención
        TaxWithholding record = TaxWithholding.builder()
            .userId(user.getId())
            .countryCode(countryCode)
            .taxType("INCOME_TAX")
            .taxRate(taxRate.multiply(new BigDecimal("100")))
            .taxAmount(withholding)
            .fiscalYear(LocalDate.now().getYear())
            .fiscalPeriod(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM")))
            .build();
        
        taxWithholdingRepository.save(record);
        
        log.info("Tax withholding: ${} ({}%) for user {} in {}", 
            withholding, taxRate, user.getId(), countryCode);
        
        return withholding;
    }
    
    public TaxCertificate generateTaxCertificate(Long userId, int fiscalYear) {
        // Generar certificado anual de retenciones (Formulario 1099 en US, 
        // Constancia de retenciones en MX, etc.)
        
        List<TaxWithholding> withholdings = taxWithholdingRepository
            .findByUserIdAndFiscalYear(userId, fiscalYear);
        
        BigDecimal totalWithheld = withholdings.stream()
            .map(TaxWithholding::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Generar PDF del certificado
        byte[] pdfBytes = pdfGeneratorService.generateTaxCertificate(
            userId, fiscalYear, totalWithheld, withholdings
        );
        
        // Subir a S3
        String certificateUrl = s3Service.uploadTaxDocument(
            userId, fiscalYear, pdfBytes
        );
        
        return TaxCertificate.builder()
            .userId(userId)
            .fiscalYear(fiscalYear)
            .totalWithheld(totalWithheld)
            .certificateUrl(certificateUrl)
            .generatedAt(LocalDateTime.now())
            .build();
    }
}
```

---

## 📄 Facturación Automática

### Servicio de Facturación

```java
@Service
public class InvoiceService {
    
    public void generateInvoices(Payment payment) {
        Order order = payment.getOrder();
        
        // 1. Factura al cliente (comprador)
        generateCustomerInvoice(payment, order);
        
        // 2. Comprobante al escritor (ingreso por regalías)
        generateWriterReceipt(payment, order);
        
        // 3. Comprobante al revendedor (si aplica)
        if (order.getAffiliateId() != null) {
            generateResellerReceipt(payment, order);
        }
        
        // 4. Comprobante a imprenta (si es impreso)
        if (order.getOrderType().equals("PRINT")) {
            generatePrinterReceipt(payment, order);
        }
    }
    
    private void generateCustomerInvoice(Payment payment, Order order) {
        Invoice invoice = Invoice.builder()
            .paymentId(payment.getId())
            .invoiceNumber(generateInvoiceNumber())
            .invoiceType("SALE")
            .issuerName("DrakkarPress Inc.")
            .issuerTaxId("XX-XXXXXXX") // EIN/RFC de DrakkarPress
            .recipientName(order.getCustomer().getFullName())
            .recipientTaxId(order.getCustomer().getTaxId())
            .subtotal(payment.getAmount())
            .taxAmount(BigDecimal.ZERO) // O calcular IVA según país
            .total(payment.getAmount())
            .currency(payment.getCurrency())
            .issueDate(LocalDate.now())
            .status("ISSUED")
            .build();
        
        invoiceRepository.save(invoice);
        
        // Generar PDF
        byte[] pdfBytes = pdfGeneratorService.generateInvoicePdf(invoice);
        String pdfUrl = s3Service.uploadInvoice(invoice.getId(), pdfBytes);
        
        invoice.setPdfUrl(pdfUrl);
        invoiceRepository.save(invoice);
        
        // Enviar por email
        emailService.sendInvoiceEmail(order.getCustomer().getEmail(), pdfUrl);
    }
    
    private void generateWriterReceipt(Payment payment, Order order) {
        PaymentSplit writerSplit = splitRepository
            .findByPaymentIdAndRecipientType(payment.getId(), "WRITER");
        
        Invoice receipt = Invoice.builder()
            .paymentId(payment.getId())
            .invoiceNumber(generateReceiptNumber())
            .invoiceType("ROYALTY")
            .issuerName("DrakkarPress Inc.")
            .issuerTaxId("XX-XXXXXXX")
            .recipientName(order.getBook().getWriter().getFullName())
            .recipientTaxId(order.getBook().getWriter().getTaxId())
            .subtotal(writerSplit.getGrossAmount())
            .taxAmount(writerSplit.getTaxWithholding())
            .total(writerSplit.getNetAmount())
            .currency(payment.getCurrency())
            .issueDate(LocalDate.now())
            .status("ISSUED")
            .build();
        
        invoiceRepository.save(receipt);
        
        // Generar y enviar
        byte[] pdfBytes = pdfGeneratorService.generateReceiptPdf(receipt);
        String pdfUrl = s3Service.uploadReceipt(receipt.getId(), pdfBytes);
        receipt.setPdfUrl(pdfUrl);
        invoiceRepository.save(receipt);
    }
    
    private String generateInvoiceNumber() {
        // Formato: INV-2025-000001
        int year = LocalDate.now().getYear();
        long count = invoiceRepository.countByYear(year) + 1;
        return String.format("INV-%d-%06d", year, count);
    }
}
```

---

## 🔐 Compliance y Regulaciones

### Requisitos por País

#### Estados Unidos
- **Formulario W-9:** Para residentes/ciudadanos US
- **Formulario W-8BEN:** Para no residentes
- **1099-MISC:** Envío anual si pagos > $600/año
- **Retención:** 30% si no provee W-8BEN válido

#### México
- **RFC:** Registro Federal de Contribuyentes obligatorio
- **Constancia de Situación Fiscal**
- **CFDI (Factura Electrónica):** Para todos los pagos
- **Retención ISR:** 10% sobre honorarios

#### Argentina
- **CUIT/CUIL:** Obligatorio
- **Factura E:** Para monotributistas
- **Retención Ganancias:** 21% según escala

#### España
- **NIF/NIE:** Obligatorio
- **Factura con IVA:** 21% general
- **Retención IRPF:** 15% (7% primer año)

#### Brasil
- **CPF/CNPJ:** Obligatorio
- **Nota Fiscal Eletrônica**
- **IRRF:** 15% retención en la fuente

### Implementación de Validaciones

```java
@Service
public class TaxComplianceService {
    
    public boolean validateUserTaxInfo(User user) {
        String country = user.getTaxCountry();
        
        switch (country) {
            case "US":
                return validateW9OrW8BEN(user);
            case "MX":
                return validateRFC(user);
            case "AR":
                return validateCUIT(user);
            case "ES":
                return validateNIF(user);
            case "BR":
                return validateCPF(user);
            default:
                return user.getTaxId() != null;
        }
    }
    
    private boolean validateRFC(User user) {
        String rfc = user.getTaxId();
        
        // RFC persona física: XXXX000000XXX (13 caracteres)
        // RFC persona moral: XXX000000XXX (12 caracteres)
        
        if (rfc == null) return false;
        
        String regex = "^[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}$";
        return rfc.matches(regex);
    }
    
    private boolean validateCUIT(User user) {
        String cuit = user.getTaxId();
        
        // CUIT: XX-XXXXXXXX-X (11 dígitos con guiones)
        if (cuit == null) return false;
        
        String cleanCuit = cuit.replaceAll("-", "");
        return cleanCuit.matches("^\\d{11}$");
    }
    
    public void ensureCompliance(User user) throws TaxComplianceException {
        if (!validateUserTaxInfo(user)) {
            throw new TaxComplianceException(
                "Información fiscal incompleta o inválida para " + user.getTaxCountry()
            );
        }
        
        // Verificar documentos necesarios
        if (!user.hasTaxDocuments()) {
            throw new TaxComplianceException(
                "Debe subir los documentos fiscales requeridos"
            );
        }
        
        // Verificar cuenta de pago verificada
        PayoutAccount account = payoutAccountRepository
            .findActiveByUserId(user.getId())
            .orElseThrow(() -> new TaxComplianceException(
                "Debe configurar y verificar una cuenta de pago"
            ));
        
        if (!account.isVerified()) {
            throw new TaxComplianceException(
                "Su cuenta de pago debe estar verificada"
            );
        }
    }
}
```

---

## 📊 Dashboard Financiero

### Panel para Escritores

```
┌──────────────────────────────────────────────────────────────┐
│ 💰 TUS INGRESOS                                              │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  Mes actual:  $2,450.00  ↑ 23%                               │
│  Este año:    $18,230.00                                     │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ DESGLOSE DE PAGOS                                      │ │
│  │                                                        │ │
│  │ Fecha       Libro             Monto    Estado         │ │
│  │ ─────────────────────────────────────────────────────  │ │
│  │ 8 Nov 2025  El Mar Eterno     $11.69   ✅ Pagado      │ │
│  │             Venta directa     (90%)                    │ │
│  │             Retención: $0.00                           │ │
│  │             [Ver comprobante]                          │ │
│  │                                                        │ │
│  │ 7 Nov 2025  Recetas Abuela    $7.79    ✅ Pagado      │ │
│  │             Con revendedor    (60%)                    │ │
│  │             Retención: $0.78 (10%)                     │ │
│  │             [Ver comprobante]                          │ │
│  │                                                        │ │
│  │ 6 Nov 2025  El Mar Eterno     $11.69   ⏳ Procesando  │ │
│  │             Venta directa     (90%)                    │ │
│  │                                                        │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ INFORMACIÓN FISCAL                                     │ │
│  │                                                        │ │
│  │ País fiscal: México 🇲🇽                                │ │
│  │ RFC: XXXX000000XXX                                     │ │
│  │ Retenciones 2025: $1,823.00                            │ │
│  │                                                        │ │
│  │ [📄 Descargar constancia anual]                        │ │
│  │ [⚙️ Actualizar información fiscal]                     │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ MÉTODO DE PAGO                                         │ │
│  │                                                        │ │
│  │ ✅ Stripe Connect                                      │ │
│  │    Cuenta verificada                                   │ │
│  │    Transferencias automáticas                          │ │
│  │                                                        │ │
│  │ [➕ Agregar cuenta bancaria]                           │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🔄 Ciclo de Pagos

### Frecuencia de Transferencias

**Escritores y Revendedores:**
- **Automático:** Transferencia inmediata tras cada venta (si >= $10)
- **Acumulativo:** Si < $10, se acumula hasta alcanzar mínimo
- **Mensual:** Pago el día 1 de cada mes del balance acumulado

**Imprentas:**
- **Quincenal:** Días 15 y 30 de cada mes
- **Requiere:** Pedidos completados y confirmados

**Plataforma:**
- **Retención:** 10% va directo a cuenta master de DrakkarPress
- **Sin transferencia:** Ya está en la cuenta corporativa

### Tiempos de Procesamiento

```
STRIPE CONNECT
├─ Transferencia inmediata: 1-2 días hábiles
└─ Disponibilidad en banco: 3-5 días hábiles

TRANSFERENCIA BANCARIA
├─ Nacional: 1-3 días hábiles
└─ Internacional: 5-7 días hábiles

PAYPAL
└─ Inmediato (disponible al instante)
```

---

## 🛡️ Seguridad y Prevención de Fraude

### Validaciones Implementadas

```java
@Service
public class FraudDetectionService {
    
    public void validateTransaction(Payment payment, Order order) {
        // 1. Verificar límites de velocidad
        checkVelocityLimits(order.getUserId());
        
        // 2. Validar monto razonable
        if (payment.getAmount().compareTo(new BigDecimal("10000")) > 0) {
            flagForManualReview(payment, "HIGH_AMOUNT");
        }
        
        // 3. Verificar coincidencia de IPs
        checkIpConsistency(order);
        
        // 4. Validar patrón de compra
        checkPurchasePattern(order.getUserId());
        
        // 5. Verificar cuenta no en lista negra
        checkBlacklist(order.getUserId());
    }
    
    private void checkVelocityLimits(Long userId) {
        // Máximo 10 compras por hora
        long recentPurchases = orderRepository
            .countByUserIdAndCreatedAtAfter(
                userId, 
                LocalDateTime.now().minusHours(1)
            );
        
        if (recentPurchases > 10) {
            throw new FraudException("Velocity limit exceeded");
        }
    }
}
```

---

## 📈 Reportes y Analytics

### Reportes Generados Automáticamente

1. **Reporte Mensual de Ingresos** (para escritores)
2. **Reporte de Comisiones** (para revendedores)
3. **Reporte de Trabajos** (para imprentas)
4. **Constancia Fiscal Anual** (para todos)
5. **Balance Contable** (para plataforma)

### Integración con Sistemas Contables

```java
@Service
public class AccountingIntegrationService {
    
    // Integración con QuickBooks, Xero, Conta.com, etc.
    
    public void syncToAccountingSystem(Payment payment) {
        List<PaymentSplit> splits = splitRepository
            .findByPaymentId(payment.getId());
        
        for (PaymentSplit split : splits) {
            AccountingEntry entry = AccountingEntry.builder()
                .date(payment.getPaymentDate())
                .account(getAccountByRecipientType(split.getRecipientType()))
                .debit(split.getGrossAmount())
                .credit(BigDecimal.ZERO)
                .description("Payment split - " + split.getRecipientType())
                .reference(payment.getExternalPaymentId())
                .build();
            
            quickbooksService.createJournalEntry(entry);
        }
    }
}
```

---

## ✅ Checklist de Implementación

### Fase 1: Core (Mes 1-2)
- [ ] Configurar Stripe Connect para splits automáticos
- [ ] Implementar Payment Service con cálculo de comisiones
- [ ] Crear tablas de base de datos (payments, splits, etc)
- [ ] Webhook handlers para Shopify/MercadoLibre
- [ ] Sistema básico de transferencias

### Fase 2: Tributario (Mes 3)
- [ ] Servicio de cálculo de retenciones fiscales
- [ ] Validación de información fiscal por país
- [ ] Generación de certificados de retención
- [ ] Formularios fiscales (W-9, W-8BEN, etc)

### Fase 3: Facturación (Mes 4)
- [ ] Generación automática de facturas PDF
- [ ] CFDI para México
- [ ] Envío de facturas por email
- [ ] Almacenamiento en S3

### Fase 4: Reportes (Mes 5)
- [ ] Dashboard financiero para cada rol
- [ ] Reportes mensuales automáticos
- [ ] Exportación a Excel/CSV
- [ ] Integración con sistemas contables

### Fase 5: Optimización (Mes 6)
- [ ] Detección de fraude
- [ ] Optimización de costos de transferencias
- [ ] Multi-moneda avanzado
- [ ] Auditoría y compliance

---

## 📞 Soporte y Cumplimiento

### Documentación para Usuarios

**Para Escritores:**
- Guía: "Cómo configurar tu cuenta de pago"
- FAQ: "¿Cuándo recibo mi dinero?"
- Tutorial: "Información fiscal requerida por país"

**Para Revendedores:**
- Guía: "Entender tus comisiones"
- FAQ: "¿Cómo se calculan las comisiones?"

**Para Imprentas:**
- Guía: "Ciclo de pagos quincenales"
- FAQ: "Facturación de servicios"

### Contacto Soporte Fiscal
- Email: fiscal@drakkarpress.com
- Chat: Disponible 9-18h (hora local)
- Teléfono: Líneas por país

---

## 🔮 Futuro: Mejoras Planificadas

1. **Criptomonedas:** Pagos en Bitcoin/USDT
2. **Anticipos:** Adelanto de regalías futuras
3. **Inversión:** Fondo de inversión para escritores
4. **Tokenización:** NFTs de libros
5. **DAO:** Gobernanza descentralizada

---

**Versión:** 1.0  
**Última actualización:** 9 nov 2025  
**Próxima revisión:** Q1 2026 (nuevas regulaciones fiscales)
