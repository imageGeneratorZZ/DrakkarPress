# Integraciones Externas - DrakkarPress

## Visión General

DrakkarPress funciona como **HUB CENTRAL** que se integra con múltiples plataformas de venta e impresión:

```
                    ┌─────────────────┐
                    │  DRAKKARPRESS   │
                    │   (Motor Java)  │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│   SHOPIFY     │   │ MERCADOLIBRE  │   │   LULU.COM    │
│   (E-commerce)│   │  (Marketplace)│   │  (Impresión)  │
└───────────────┘   └───────────────┘   └───────────────┘
```

**Flujo de trabajo:**
1. Escritor crea libro en DrakkarPress
2. DrakkarPress publica automáticamente en Shopify + MercadoLibre
3. Cliente compra en cualquier plataforma
4. DrakkarPress recibe webhook/notificación
5. Si es impreso: envía orden a Lulu.com o imprenta local
6. Sistema actualiza estados en todas las plataformas
7. Reparte comisiones/regalías

---

## 1. Integración con Shopify

### Propósito
Shopify actúa como la **tienda oficial** de DrakkarPress con carrito de compras, checkout y procesamiento de pagos.

### Arquitectura

```
┌─────────────────────────────────────────────────┐
│          DrakkarPress (Java Backend)            │
│                                                 │
│  ┌─────────────────────────────────┐           │
│  │  Shopify Integration Service    │           │
│  │  (Puerto 8089)                  │           │
│  └──────────────┬──────────────────┘           │
│                 │                               │
└─────────────────┼───────────────────────────────┘
                  │
                  │ REST API
                  │ (Shopify Admin API)
                  ▼
         ┌────────────────────┐
         │   SHOPIFY STORE    │
         │ drakkarpress.myshopify.com
         └────────────────────┘
                  │
                  │ Webhooks
                  ▼
         ┌────────────────────┐
         │ DrakkarPress       │
         │ Webhook Handler    │
         └────────────────────┘
```

### Funcionalidades

#### A) Sincronización de Libros

**Cuando un autor publica un libro en DrakkarPress:**

```java
// Pseudo-código
@Service
public class ShopifyIntegrationService {
    
    public void publishBookToShopify(Book book) {
        // 1. Crear producto en Shopify
        ShopifyProduct product = new ShopifyProduct();
        product.setTitle(book.getTitle());
        product.setDescription(book.getSynopsis());
        product.setVendor("DrakkarPress");
        product.setProductType(book.getCategory());
        
        // 2. Crear variantes (Digital y/o Impreso)
        if (book.hasDigitalVersion()) {
            ShopifyVariant digital = new ShopifyVariant();
            digital.setSku(book.getIsbn() + "-DIGITAL");
            digital.setPrice(book.getDigitalPrice());
            digital.setInventoryManagement("shopify");
            digital.setInventoryQuantity(999999); // Ilimitado
            product.addVariant(digital);
        }
        
        if (book.hasPrintVersion()) {
            ShopifyVariant print = new ShopifyVariant();
            print.setSku(book.getIsbn() + "-PRINT");
            print.setPrice(book.getPrintPrice());
            print.setInventoryManagement("manual"); // POD
            product.addVariant(print);
        }
        
        // 3. Subir imágenes
        ShopifyImage coverImage = new ShopifyImage();
        coverImage.setSrc(book.getCoverUrl());
        product.addImage(coverImage);
        
        // 4. Tags y metadatos
        product.setTags(Arrays.asList(
            book.getCategory(),
            "autor:" + book.getAuthor().getName(),
            "isbn:" + book.getIsbn()
        ));
        
        // 5. Enviar a Shopify API
        shopifyClient.createProduct(product);
        
        // 6. Guardar mapping
        saveShopifyMapping(book.getId(), product.getId());
    }
}
```

#### B) Recepción de Órdenes (Webhooks)

**Shopify envía webhooks cuando hay una compra:**

```
Webhook: orders/create
Endpoint: https://api.drakkarpress.com/webhooks/shopify/order-created
```

**Handler en DrakkarPress:**

```java
@RestController
@RequestMapping("/webhooks/shopify")
public class ShopifyWebhookController {
    
    @PostMapping("/order-created")
    public ResponseEntity<?> handleOrderCreated(
        @RequestHeader("X-Shopify-Hmac-SHA256") String hmac,
        @RequestBody ShopifyOrder order
    ) {
        // 1. Verificar autenticidad del webhook
        if (!shopifyService.verifyWebhook(hmac, order)) {
            return ResponseEntity.status(403).build();
        }
        
        // 2. Procesar orden
        DrakkarOrder drakkarOrder = new DrakkarOrder();
        drakkarOrder.setSource("SHOPIFY");
        drakkarOrder.setExternalOrderId(order.getId());
        drakkarOrder.setCustomerEmail(order.getEmail());
        
        // 3. Por cada item
        for (ShopifyLineItem item : order.getLineItems()) {
            Book book = findBookBySku(item.getSku());
            
            // Si es libro digital
            if (item.getSku().endsWith("-DIGITAL")) {
                // Enviar link de descarga por email
                emailService.sendDigitalBookLink(
                    order.getEmail(),
                    book.getDigitalFileUrl()
                );
            }
            
            // Si es libro impreso
            if (item.getSku().endsWith("-PRINT")) {
                // Enviar a Lulu.com para impresión
                luluService.createPrintOrder(
                    book,
                    order.getShippingAddress(),
                    item.getQuantity()
                );
            }
            
            // Registrar venta para regalías
            royaltyService.recordSale(
                book.getAuthor(),
                item.getPrice(),
                "SHOPIFY"
            );
        }
        
        // 4. Guardar orden
        orderRepository.save(drakkarOrder);
        
        return ResponseEntity.ok().build();
    }
}
```

#### C) Actualización de Estados

**DrakkarPress notifica a Shopify sobre cambios:**

```java
public void updateShopifyOrderStatus(Long orderId, String status) {
    ShopifyFulfillment fulfillment = new ShopifyFulfillment();
    fulfillment.setOrderId(orderId);
    fulfillment.setStatus(status); // fulfilled, in_transit, delivered
    fulfillment.setTrackingNumber(trackingNumber);
    fulfillment.setTrackingCompany("DHL");
    
    shopifyClient.createFulfillment(fulfillment);
}
```

### Configuración Requerida

**Variables de entorno:**
```properties
# application.properties
shopify.api.key=shpat_xxxxxxxxxxxxxxxx
shopify.api.secret=shpss_xxxxxxxxxxxxxxxx
shopify.store.domain=drakkarpress.myshopify.com
shopify.api.version=2024-01
shopify.webhook.secret=whsec_xxxxxxxxxxxxxxxx
```

**Webhooks a configurar en Shopify:**
- `orders/create` → Nuevas órdenes
- `orders/updated` → Cambios en órdenes
- `orders/cancelled` → Cancelaciones
- `refunds/create` → Reembolsos
- `products/update` → Cambios en productos (sincronización bidireccional)

### API Endpoints del Servicio

```
POST   /api/shopify/sync-book/{bookId}
GET    /api/shopify/products
DELETE /api/shopify/product/{productId}
PUT    /api/shopify/update-inventory/{sku}
POST   /api/shopify/create-discount
GET    /api/shopify/orders
```

---

## 2. Integración con MercadoLibre

### Propósito
MercadoLibre es un **marketplace adicional** para ampliar el alcance de ventas, especialmente en América Latina.

### Arquitectura

```
┌─────────────────────────────────────────────────┐
│          DrakkarPress (Java Backend)            │
│                                                 │
│  ┌─────────────────────────────────┐           │
│  │ MercadoLibre Integration Service│           │
│  │  (Puerto 8090)                  │           │
│  └──────────────┬──────────────────┘           │
│                 │                               │
└─────────────────┼───────────────────────────────┘
                  │
                  │ REST API
                  │ (MercadoLibre API v2)
                  ▼
         ┌────────────────────┐
         │   MERCADOLIBRE     │
         │ (Cuenta vendedor)  │
         └────────────────────┘
                  │
                  │ Notifications
                  ▼
         ┌────────────────────┐
         │ DrakkarPress       │
         │ Notification Handler│
         └────────────────────┘
```

### Flujo de Integración

#### A) Autenticación OAuth 2.0

**1. Obtener autorización del usuario:**

```
URL: https://auth.mercadolibre.com.mx/authorization
Params:
  - response_type=code
  - client_id=APP_ID
  - redirect_uri=https://drakkarpress.com/ml-callback
  - state=RANDOM_STATE
```

**2. Intercambiar código por token:**

```java
@RestController
@RequestMapping("/ml-callback")
public class MercadoLibreCallbackController {
    
    @GetMapping
    public String handleCallback(
        @RequestParam String code,
        @RequestParam String state
    ) {
        // Intercambiar code por access_token
        MercadoLibreToken token = mlClient.getAccessToken(
            code,
            clientId,
            clientSecret,
            redirectUri
        );
        
        // Guardar token
        userService.saveMercadoLibreToken(
            currentUser.getId(),
            token.getAccessToken(),
            token.getRefreshToken()
        );
        
        return "redirect:/dashboard?ml-connected=true";
    }
}
```

#### B) Publicación de Libros

**Crear publicación en MercadoLibre:**

```java
@Service
public class MercadoLibreService {
    
    public String publishBookToMercadoLibre(Book book, User author) {
        // 1. Preparar item
        MLItem item = new MLItem();
        item.setTitle(truncate(book.getTitle(), 60)); // ML limita a 60 chars
        item.setCategoryId("MLA3025"); // Libros (varía por país)
        item.setPrice(book.getPrintPrice());
        item.setCurrencyId("MXN"); // o USD, ARS, etc.
        item.setAvailableQuantity(99); // POD
        item.setListingTypeId("gold_special"); // gold_special, gold_pro, free
        item.setCondition("new");
        item.setBuyingMode("buy_it_now");
        
        // 2. Descripción
        String description = String.format("""
            <h2>%s</h2>
            <p><strong>Autor:</strong> %s</p>
            <p><strong>Categoría:</strong> %s</p>
            <p><strong>Páginas:</strong> %d</p>
            <p><strong>Idioma:</strong> %s</p>
            <br>
            <h3>Sinopsis</h3>
            <p>%s</p>
            <br>
            <p><em>Impresión bajo demanda. Entrega en 5-7 días hábiles.</em></p>
            """,
            book.getTitle(),
            book.getAuthor().getName(),
            book.getCategory(),
            book.getPages(),
            book.getLanguage(),
            book.getSynopsis()
        );
        
        item.setDescription(description);
        
        // 3. Atributos
        List<MLAttribute> attributes = Arrays.asList(
            new MLAttribute("AUTHOR", book.getAuthor().getName()),
            new MLAttribute("BOOK_TITLE", book.getTitle()),
            new MLAttribute("ISBN", book.getIsbn()),
            new MLAttribute("LANGUAGE", book.getLanguage()),
            new MLAttribute("FORMAT", "Papel")
        );
        item.setAttributes(attributes);
        
        // 4. Imágenes
        List<MLPicture> pictures = new ArrayList<>();
        MLPicture cover = new MLPicture();
        cover.setSource(book.getCoverUrl()); // URL pública
        pictures.add(cover);
        item.setPictures(pictures);
        
        // 5. Envío
        MLShipping shipping = new MLShipping();
        shipping.setMode("me2"); // Envío a cargo del vendedor
        shipping.setLocalPickUp(false);
        shipping.setFreeShipping(false);
        shipping.setDimensions(new MLDimensions(book.getWidth(), book.getHeight(), book.getDepth()));
        item.setShipping(shipping);
        
        // 6. Publicar
        MLItemResponse response = mlClient.createItem(item, author.getMlAccessToken());
        
        // 7. Guardar mapping
        saveMercadoLibreMapping(book.getId(), response.getId(), response.getPermalink());
        
        return response.getPermalink(); // URL de la publicación
    }
}
```

#### C) Recepción de Ventas (Notifications)

**MercadoLibre envía notificaciones:**

```
POST https://api.drakkarpress.com/webhooks/mercadolibre/notifications

Body:
{
  "user_id": 123456,
  "resource": "/orders/1234567890",
  "topic": "orders_v2",
  "received": "2025-11-09T10:30:00.000Z",
  "sent": "2025-11-09T10:30:00.500Z"
}
```

**Handler:**

```java
@RestController
@RequestMapping("/webhooks/mercadolibre")
public class MLNotificationController {
    
    @PostMapping("/notifications")
    public ResponseEntity<?> handleNotification(@RequestBody MLNotification notification) {
        
        if ("orders_v2".equals(notification.getTopic())) {
            // Extraer order ID del resource
            Long orderId = extractOrderId(notification.getResource());
            
            // Obtener detalles completos de la orden
            MLOrder order = mlClient.getOrder(orderId);
            
            // Procesar según el status
            switch (order.getStatus()) {
                case "paid" -> processPaidOrder(order);
                case "cancelled" -> processCancelledOrder(order);
                case "delivered" -> processDeliveredOrder(order);
            }
        }
        
        return ResponseEntity.ok().build();
    }
    
    private void processPaidOrder(MLOrder order) {
        for (MLOrderItem item : order.getOrderItems()) {
            // Buscar libro por ML item ID
            Book book = findBookByMlItemId(item.getItemId());
            
            // Enviar a impresión
            luluService.createPrintOrder(
                book,
                convertMLAddress(order.getShipping().getReceiverAddress()),
                item.getQuantity()
            );
            
            // Registrar venta
            DrakkarOrder drakkarOrder = new DrakkarOrder();
            drakkarOrder.setSource("MERCADOLIBRE");
            drakkarOrder.setExternalOrderId(order.getId().toString());
            drakkarOrder.setStatus("PROCESSING");
            orderRepository.save(drakkarOrder);
            
            // Calcular regalías (descontando comisión ML)
            double mlFee = order.getTotalAmount() * 0.13; // ML cobra ~13%
            double authorRoyalty = (order.getTotalAmount() - mlFee) * 0.70;
            royaltyService.recordSale(book.getAuthor(), authorRoyalty, "ML");
        }
    }
}
```

#### D) Actualización de Stock

**Sincronizar disponibilidad:**

```java
public void updateMercadoLibreStock(String mlItemId, int quantity) {
    MLItem item = new MLItem();
    item.setAvailableQuantity(quantity);
    
    mlClient.updateItem(mlItemId, item, accessToken);
}

// Pausar publicación si libro se agota o retira
public void pauseMercadoLibreItem(String mlItemId) {
    MLItem item = new MLItem();
    item.setStatus("paused");
    
    mlClient.updateItem(mlItemId, item, accessToken);
}
```

### Configuración Requerida

**Variables de entorno:**
```properties
# application.properties
mercadolibre.client.id=1234567890123456
mercadolibre.client.secret=xxxxxxxxxxxxxxxxxxx
mercadolibre.redirect.uri=https://drakkarpress.com/ml-callback
mercadolibre.api.url=https://api.mercadolibre.com
mercadolibre.site.id=MLM # MLM=México, MLA=Argentina, MLB=Brasil, etc.
```

**Configurar webhook en ML:**
```
URL: https://api.drakkarpress.com/webhooks/mercadolibre/notifications
Topics: orders_v2, items, shipments
```

### API Endpoints del Servicio

```
POST   /api/mercadolibre/connect
POST   /api/mercadolibre/publish-book/{bookId}
GET    /api/mercadolibre/items
PUT    /api/mercadolibre/item/{mlItemId}/pause
PUT    /api/mercadolibre/item/{mlItemId}/resume
DELETE /api/mercadolibre/item/{mlItemId}
GET    /api/mercadolibre/orders
GET    /api/mercadolibre/questions
POST   /api/mercadolibre/answer-question
```

### Consideraciones MercadoLibre

**Comisiones por país:**
| País | Comisión ML | IVA |
|------|-------------|-----|
| México | 13% | 16% |
| Argentina | 13% | 21% |
| Brasil | 16% | Varía |
| Chile | 13% | 19% |

**Límites:**
- Título: 60 caracteres
- Descripción: HTML permitido, 50,000 caracteres
- Imágenes: Mínimo 1, máximo 12 (mínimo 500x500px)
- Categorías: Específicas por país

---

## 3. Integración con Lulu.com

### Propósito
Lulu.com es el proveedor de **impresión bajo demanda (POD)** para libros físicos.

### Arquitectura

```
┌─────────────────────────────────────────────────┐
│          DrakkarPress (Java Backend)            │
│                                                 │
│  ┌─────────────────────────────────┐           │
│  │  Lulu Integration Service       │           │
│  │  (Puerto 8091)                  │           │
│  └──────────────┬──────────────────┘           │
│                 │                               │
└─────────────────┼───────────────────────────────┘
                  │
                  │ REST API
                  │ (Lulu Print API)
                  ▼
         ┌────────────────────┐
         │     LULU.COM       │
         │  (Print Provider)  │
         └────────────────────┘
                  │
                  │ Status Callbacks
                  ▼
         ┌────────────────────┐
         │ DrakkarPress       │
         │ Status Handler     │
         └────────────────────┘
```

### Flujo de Integración

#### A) Configurar Libro en Lulu

**Cuando un autor publica un libro:**

```java
@Service
public class LuluService {
    
    public String createPrintableBook(Book book) {
        // 1. Crear proyecto en Lulu
        LuluProject project = new LuluProject();
        project.setTitle(book.getTitle());
        project.setAuthor(book.getAuthor().getName());
        
        // 2. Especificaciones de impresión
        LuluPrintSpec spec = new LuluPrintSpec();
        spec.setSize("US_TRADE"); // 6x9 pulgadas
        spec.setBinding("PERFECT_BIND"); // Tapa blanda
        spec.setColorOption("STANDARD_COLOR"); // Color o B&W
        spec.setPaperType("WHITE"); // Blanco o crema
        spec.setPageCount(book.getPages());
        project.setPrintSpec(spec);
        
        // 3. Subir archivos
        // Interior del libro (PDF)
        LuluFile interiorFile = luluClient.uploadFile(
            book.getInteriorPdfUrl(),
            "INTERIOR"
        );
        project.setInteriorFile(interiorFile);
        
        // Portada (PDF con back cover y spine)
        LuluFile coverFile = luluClient.uploadFile(
            book.getCoverPdfUrl(),
            "COVER"
        );
        project.setCoverFile(coverFile);
        
        // 4. Enviar a Lulu
        LuluProjectResponse response = luluClient.createProject(project);
        
        // 5. Obtener precio de costo
        LuluPricing pricing = luluClient.getPricing(response.getProjectId());
        book.setPrintCost(pricing.getUnitCost());
        
        // 6. Calcular precio de venta
        double markup = 2.5; // 150% markup
        book.setPrintPrice(pricing.getUnitCost() * markup);
        
        // 7. Guardar mapping
        saveLuluMapping(book.getId(), response.getProjectId());
        
        return response.getProjectId();
    }
}
```

#### B) Crear Orden de Impresión

**Cuando hay una venta de libro físico:**

```java
public String createPrintOrder(Book book, Address shippingAddress, int quantity) {
    // 1. Crear orden en Lulu
    LuluOrder order = new LuluOrder();
    order.setProjectId(book.getLuluProjectId());
    order.setQuantity(quantity);
    
    // 2. Información de envío
    LuluShippingAddress address = new LuluShippingAddress();
    address.setName(shippingAddress.getFullName());
    address.setStreet1(shippingAddress.getStreet());
    address.setCity(shippingAddress.getCity());
    address.setState(shippingAddress.getState());
    address.setPostalCode(shippingAddress.getZipCode());
    address.setCountryCode(shippingAddress.getCountryCode());
    address.setPhoneNumber(shippingAddress.getPhone());
    order.setShippingAddress(address);
    
    // 3. Nivel de servío de envío
    order.setShippingLevel("EXPEDITED"); // GROUND, EXPEDITED, EXPRESS
    
    // 4. Información de línea de pedido
    LuluLineItem lineItem = new LuluLineItem();
    lineItem.setProjectId(book.getLuluProjectId());
    lineItem.setQuantity(quantity);
    order.addLineItem(lineItem);
    
    // 5. Enviar orden
    LuluOrderResponse response = luluClient.createOrder(order);
    
    // 6. Guardar orden
    PrintOrder printOrder = new PrintOrder();
    printOrder.setBookId(book.getId());
    printOrder.setLuluOrderId(response.getOrderId());
    printOrder.setStatus("SUBMITTED");
    printOrder.setTrackingNumber(response.getTrackingNumber());
    printOrderRepository.save(printOrder);
    
    // 7. Programar polling de estado
    scheduleStatusCheck(response.getOrderId());
    
    return response.getOrderId();
}
```

#### C) Monitoreo de Estado

**Polling periódico o callbacks:**

```java
@Scheduled(fixedDelay = 3600000) // Cada hora
public void checkPrintOrderStatuses() {
    List<PrintOrder> pendingOrders = printOrderRepository
        .findByStatusIn(Arrays.asList("SUBMITTED", "PROCESSING", "IN_PRODUCTION"));
    
    for (PrintOrder order : pendingOrders) {
        LuluOrderStatus status = luluClient.getOrderStatus(order.getLuluOrderId());
        
        String newStatus = mapLuluStatus(status.getStatus());
        if (!newStatus.equals(order.getStatus())) {
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            
            // Actualizar en Shopify/ML
            if ("SHIPPED".equals(newStatus)) {
                shopifyService.updateFulfillment(
                    order.getShopifyOrderId(),
                    status.getTrackingNumber()
                );
            }
            
            // Notificar al cliente
            emailService.sendOrderUpdate(
                order.getCustomerEmail(),
                newStatus,
                status.getTrackingNumber()
            );
        }
        
        printOrderRepository.save(order);
    }
}

private String mapLuluStatus(String luluStatus) {
    return switch (luluStatus) {
        case "CREATED" -> "SUBMITTED";
        case "PRODUCTION" -> "IN_PRODUCTION";
        case "SHIPPED" -> "SHIPPED";
        case "DELIVERED" -> "DELIVERED";
        case "CANCELLED" -> "CANCELLED";
        case "ERROR" -> "FAILED";
        default -> "UNKNOWN";
    };
}
```

#### D) Cálculo de Costos

```java
public LuluQuote getShippingQuote(String projectId, Address destination, int quantity) {
    LuluQuoteRequest request = new LuluQuoteRequest();
    request.setProjectId(projectId);
    request.setQuantity(quantity);
    request.setDestinationCountry(destination.getCountryCode());
    request.setShippingLevel("GROUND");
    
    LuluQuote quote = luluClient.getQuote(request);
    
    // quote contiene:
    // - printCost: Costo de impresión por unidad
    // - shippingCost: Costo de envío total
    // - totalCost: Costo total
    // - estimatedDelivery: Días estimados
    
    return quote;
}
```

### Configuración Requerida

**Variables de entorno:**
```properties
# application.properties
lulu.api.key=pk_live_xxxxxxxxxxxxxxxx
lulu.api.secret=sk_live_xxxxxxxxxxxxxxxx
lulu.api.url=https://api.lulu.com/v1
lulu.webhook.secret=whsec_xxxxxxxxxxxxxxxx
lulu.callback.url=https://api.drakkarpress.com/webhooks/lulu
```

### API Endpoints del Servicio

```
POST   /api/lulu/create-project/{bookId}
GET    /api/lulu/projects/{projectId}
POST   /api/lulu/print-order
GET    /api/lulu/orders
GET    /api/lulu/order/{orderId}/status
POST   /api/lulu/quote
GET    /api/lulu/shipping-options
```

### Especificaciones de Archivos

**Requisitos de Lulu para PDFs:**

**Interior:**
- Formato: PDF/X-1a:2001 o PDF/X-3:2002
- Resolución: 300 DPI mínimo
- Color: CMYK (no RGB)
- Fonts: Embebidas
- Sangrado: 0.125" (3.175mm)

**Portada:**
- Formato: PDF (full wrap con front, spine, back)
- Resolución: 300 DPI
- Color: CMYK
- Tamaño calculado: (ancho_página * 2) + grosor_lomo + sangrado

**Tamaños disponibles:**
- US Trade: 6" x 9"
- US Letter: 8.5" x 11"
- A4: 210mm x 297mm
- Pocket: 4.25" x 6.87"
- Square: 8" x 8"
- Landscape: 11" x 8.5"

---

## 4. Integración con IA de DrakkarPress

### Propósito
API propia de inteligencia artificial para asistir en la creación de contenido literario.

### Arquitectura

```
┌─────────────────────────────────────────────────┐
│          DrakkarPress (Java Backend)            │
│                                                 │
│  ┌─────────────────────────────────┐           │
│  │  AI Service (Puerto 8085)       │           │
│  └──────────────┬──────────────────┘           │
│                 │                               │
└─────────────────┼───────────────────────────────┘
                  │
                  │ REST/gRPC
                  ▼
         ┌────────────────────┐
         │  IA DRAKKARPRESS   │
         │  (Custom AI API)   │
         │  o OpenAI/Anthropic│
         └────────────────────┘
```

### Endpoints de IA

#### A) Generar Ideas de Libros

```java
@PostMapping("/api/ai/generate-ideas")
public List<BookIdea> generateIdeas(@RequestBody AIIdeaRequest request) {
    // request.category = "SCRYPTORIUM", "THRILLER", etc.
    // request.count = número de ideas (default: 5)
    
    String prompt = String.format("""
        Genera %d ideas originales para libros de la categoría %s.
        Para cada idea proporciona:
        - Título sugerido
        - Sinopsis breve (2-3 líneas)
        - Público objetivo
        - Gancho principal
        
        Formato JSON.
        """,
        request.getCount(),
        request.getCategory()
    );
    
    AIResponse response = aiClient.complete(prompt);
    return parseBookIdeas(response.getText());
}
```

#### B) Extender Texto

```java
@PostMapping("/api/ai/extend-text")
public String extendText(@RequestBody AIExtendRequest request) {
    // request.text = texto a extender
    // request.targetLength = longitud objetivo (palabras)
    // request.style = estilo de escritura
    
    String prompt = String.format("""
        Continúa el siguiente texto de manera natural y coherente.
        Extiende hasta aproximadamente %d palabras adicionales.
        Mantén el estilo: %s
        
        TEXTO:
        %s
        
        CONTINUACIÓN:
        """,
        request.getTargetLength(),
        request.getStyle(),
        request.getText()
    );
    
    AIResponse response = aiClient.complete(prompt);
    return response.getText();
}
```

#### C) Generar Sinopsis

```java
@PostMapping("/api/ai/generate-synopsis")
public BookSynopsis generateSynopsis(@RequestBody AISynopsisRequest request) {
    // request.bookContent = contenido completo o parcial del libro
    // request.length = "short", "medium", "long"
    
    String prompt = String.format("""
        Basándote en el siguiente contenido, crea una sinopsis atractiva.
        Longitud: %s
        
        CONTENIDO:
        %s
        
        SINOPSIS:
        - Engancha desde la primera línea
        - No reveles el final
        - Resalta el conflicto principal
        - Menciona al protagonista
        """,
        request.getLength(),
        truncate(request.getBookContent(), 4000)
    );
    
    AIResponse response = aiClient.complete(prompt);
    
    // También generar keywords SEO
    String keywordsPrompt = "Genera 10 keywords SEO para este libro: " + response.getText();
    AIResponse keywordsResponse = aiClient.complete(keywordsPrompt);
    
    return new BookSynopsis(
        response.getText(),
        parseKeywords(keywordsResponse.getText())
    );
}
```

#### D) Sugerir Títulos

```java
@PostMapping("/api/ai/generate-titles")
public List<String> generateTitles(@RequestBody AITitleRequest request) {
    // request.synopsis = sinopsis del libro
    // request.category = categoría
    // request.count = número de opciones
    
    String prompt = String.format("""
        Genera %d títulos atractivos y comerciales para un libro de %s.
        
        SINOPSIS:
        %s
        
        TÍTULOS:
        - Memorables y pegajosos
        - Máximo 60 caracteres
        - Apropiados para la categoría
        - Que generen curiosidad
        """,
        request.getCount(),
        request.getCategory(),
        request.getSynopsis()
    );
    
    AIResponse response = aiClient.complete(prompt);
    return parseTitles(response.getText());
}
```

#### E) Generar Estructura de Libro

```java
@PostMapping("/api/ai/generate-structure")
public BookStructure generateStructure(@RequestBody AIStructureRequest request) {
    // Para libros infantiles, colorear, recetas, etc.
    
    String prompt = switch (request.getType()) {
        case "COLORING_BOOK" -> """
            Genera una estructura para un libro para colorear de %d páginas sobre: %s
            - Lista de 20-30 ilustraciones temáticas
            - Descripción de cada ilustración
            - Nivel de complejidad (niños 3-5, 6-8, etc.)
            """.formatted(request.getPages(), request.getTheme());
            
        case "RECIPE_BOOK" -> """
            Genera estructura para libro de recetas de cocina %s:
            - Tabla de contenido con secciones
            - 30-50 recetas distribuidas
            - Tips y técnicas especiales
            """.formatted(request.getTheme());
            
        case "NOVEL" -> """
            Genera estructura de novela de %s:
            - Arco narrativo completo
            - 15-25 capítulos
            - Beats principales
            - Desarrollo de personajes
            """.formatted(request.getGenre());
            
        default -> "Estructura genérica";
    };
    
    AIResponse response = aiClient.complete(prompt);
    return parseBookStructure(response.getText(), request.getType());
}
```

#### F) Marketing Copy

```java
@PostMapping("/api/ai/marketing-copy")
public MarketingContent generateMarketingCopy(@RequestBody Book book) {
    // Generar contenido para redes sociales
    
    String[] prompts = {
        "Tweet (280 chars) promocionando: " + book.getTitle(),
        "Post de Instagram (150 palabras) para: " + book.getTitle(),
        "Post de Facebook (200 palabras) para: " + book.getTitle(),
        "Email subject lines (5 opciones) para lanzamiento de: " + book.getTitle(),
        "Descripción corta para anuncios (50 palabras): " + book.getTitle()
    };
    
    MarketingContent content = new MarketingContent();
    for (String prompt : prompts) {
        AIResponse response = aiClient.complete(prompt + "\n\nSINOPSIS: " + book.getSynopsis());
        content.addCopy(extractPlatform(prompt), response.getText());
    }
    
    return content;
}
```

### Configuración IA

**Opciones de implementación:**

**1. OpenAI (GPT-4):**
```properties
ai.provider=openai
ai.openai.api.key=sk-xxxxxxxxxxxxxxxx
ai.openai.model=gpt-4-turbo-preview
ai.openai.temperature=0.7
ai.openai.max.tokens=2000
```

**2. Anthropic (Claude):**
```properties
ai.provider=anthropic
ai.anthropic.api.key=sk-ant-xxxxxxxxxxxxxxxx
ai.anthropic.model=claude-3-opus-20240229
```

**3. IA Propia:**
```properties
ai.provider=custom
ai.custom.api.url=https://ia.drakkarpress.com/v1
ai.custom.api.key=dp_xxxxxxxxxxxxxxxx
```

### Límites y Cuotas

```java
@Component
public class AIQuotaManager {
    
    // Límites por plan
    private static final Map<UserPlan, Integer> DAILY_LIMITS = Map.of(
        UserPlan.FREE, 10,
        UserPlan.WRITER_PRO, 100,
        UserPlan.RESELLER_PRO, 50
    );
    
    public boolean checkQuota(User user, AIOperation operation) {
        int used = aiUsageRepository.getDailyUsage(user.getId(), LocalDate.now());
        int limit = DAILY_LIMITS.get(user.getPlan());
        
        if (used >= limit) {
            throw new QuotaExceededException(
                "Límite diario de IA alcanzado. Upgrade tu plan para más."
            );
        }
        
        return true;
    }
    
    public void recordUsage(User user, AIOperation operation, int tokens) {
        AIUsage usage = new AIUsage();
        usage.setUserId(user.getId());
        usage.setOperation(operation.name());
        usage.setTokensUsed(tokens);
        usage.setCreatedAt(LocalDateTime.now());
        aiUsageRepository.save(usage);
    }
}
```

---

## 5. Dashboard de Integraciones

### Panel de Control para Usuarios

**Escritores pueden ver:**
```
┌──────────────────────────────────────────────────────────┐
│  📊 INTEGRACIONES                                         │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  SHOPIFY                     [✅ Conectado]              │
│  • 12 libros publicados                                  │
│  • 45 ventas este mes                                    │
│  [Ver en Shopify →]                                      │
│                                                          │
│  MERCADOLIBRE                [❌ No conectado]           │
│  [Conectar cuenta →]                                     │
│                                                          │
│  LULU.COM                    [✅ Activo]                 │
│  • 8 libros configurados                                 │
│  • 23 órdenes en proceso                                 │
│  [Ver órdenes →]                                         │
│                                                          │
│  IA DRAKKARPRESS             [✅ Activo]                 │
│  • 47/100 usos diarios                                   │
│  [Usar IA →]                                             │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Configuración de Auto-Publicación

```java
@Entity
public class IntegrationSettings {
    @Id
    private Long id;
    
    private Long userId;
    
    // Auto-publicar en Shopify
    private Boolean autoPublishShopify = true;
    
    // Auto-publicar en MercadoLibre
    private Boolean autoPublishMercadoLibre = false;
    
    // Margen de precio para ML (por comisiones)
    private Double mercadoLibreMarkup = 1.20; // 20% extra
    
    // Auto-aprobar para impresión
    private Boolean autoApprovePrint = true;
    
    // Notificaciones
    private Boolean notifyOnSale = true;
    private Boolean notifyOnPrintComplete = true;
    private Boolean notifyOnLowStock = true;
}
```

---

## Flujo Completo de Venta

### Ejemplo: Cliente compra libro impreso en MercadoLibre

```
1. CLIENTE compra en MercadoLibre
   ↓
2. ML envía webhook a DrakkarPress
   ↓
3. DrakkarPress valida y registra orden
   ↓
4. DrakkarPress envía orden a Lulu.com
   ↓
5. Lulu imprime y envía
   ↓
6. Lulu actualiza tracking
   ↓
7. DrakkarPress actualiza ML y notifica cliente
   ↓
8. Cliente recibe libro
   ↓
9. DrakkarPress calcula y reparte:
   - Costo impresión (Lulu): $5.00
   - Comisión ML: $3.90 (13%)
   - Regalía autor: $15.77 (70% de $30 - $5 - $3.90)
   - Comisión DrakkarPress: $5.33 (30%)
```

### Diagrama de Secuencia

```
Cliente    ML     DrakkarPress   Lulu    Autor
  |        |           |          |       |
  |--Compra-->         |          |       |
  |        |--Webhook->|          |       |
  |        |           |--Order-->|       |
  |        |           |          |--Print->
  |        |           |<-Status--|       |
  |<---Notif|           |          |       |
  |        |           |--Royalty-------->|
  |        |           |          |       |
  |<---Libro-----------|----------|       |
  |        |           |          |       |
```

---

## Próximos Pasos

1. Implementar servicios de integración
2. Configurar webhooks en cada plataforma
3. Testing en sandbox/staging
4. Monitoreo y alertas de integraciones
5. Dashboard de estadísticas consolidadas
6. Documentación API para desarrolladores externos
