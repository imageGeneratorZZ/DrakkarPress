# Sistema de Entrega Digital Automática - DrakkarPress

## Visión General

DrakkarPress implementa un **sistema automático de entrega de archivos PDF** que envía una copia digital del libro al email del cliente cuando compra la versión física. Esto proporciona:

- ✅ **Gratificación instantánea**: Cliente puede leer mientras espera el físico
- ✅ **Valor agregado**: Compra física incluye digital gratis
- ✅ **Reducción de ansiedad**: El cliente no tiene que esperar días para empezar a leer
- ✅ **Ventaja competitiva**: Diferenciador vs Amazon/otras plataformas

---

## 🎯 Modelo de Negocio: "Físico + Digital Incluido"

### Concepto

```
CLIENTE COMPRA LIBRO FÍSICO
         ↓
┌────────────────────────────────────────┐
│ RECIBE INMEDIATAMENTE:                 │
│ ✉️  Email con PDF del libro           │
│ 📱 Acceso a biblioteca digital         │
│                                        │
│ RECIBE EN 3-7 DÍAS:                    │
│ 📦 Libro físico impreso               │
│ 🚚 Envío con tracking                 │
└────────────────────────────────────────┘
```

### Ventajas

**Para el Cliente:**
- Empieza a leer inmediatamente
- Tiene ambas versiones (física + digital)
- No paga extra por el PDF
- Puede leer en dispositivos mientras espera

**Para el Escritor:**
- Mayor conversión de ventas
- Satisfacción del cliente más alta
- Diferenciación vs competencia
- No afecta margen (PDF ya existe)

**Para DrakkarPress:**
- Valor agregado sin costo adicional
- Retención de clientes
- Experiencia superior
- Ventaja competitiva

---

## 🔄 Flujo Completo: Compra Física + PDF Automático

### Diagrama de Flujo

```
┌─────────────────────────────────────────────────────────────────┐
│  1. CLIENTE COMPRA LIBRO FÍSICO                                 │
│     (Shopify / MercadoLibre / Tienda Directa)                   │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────────────┐
│  2. WEBHOOK RECIBIDO                                            │
│     Order Service (Puerto 8083) recibe notificación             │
│     Valida: order_type = "PRINT"                                │
└────────────────────┬────────────────────────────────────────────┘
                     │
                     ├─────────────────────┬───────────────────────┐
                     ▼                     ▼                       ▼
         ┌────────────────────┐  ┌──────────────────┐  ┌─────────────────┐
         │  3A. ENVIAR PDF    │  │ 3B. ORDEN LULU   │  │ 3C. REGISTRO    │
         │  AL EMAIL          │  │ Impresión física │  │ EN BIBLIOTECA   │
         │  ⚡ INMEDIATO      │  │                  │  │ DIGITAL         │
         └────────┬───────────┘  └────────┬─────────┘  └────────┬────────┘
                  │                       │                      │
                  ▼                       ▼                      ▼
    ┌──────────────────────┐   ┌──────────────────┐   ┌──────────────────┐
    │ Email con:           │   │ Lulu.com API:    │   │ Digital Library: │
    │ • Link descarga PDF  │   │ • Crear proyecto │   │ • Agregar libro  │
    │ • Instrucciones      │   │ • Submit orden   │   │ • Usuario owns   │
    │ • Acceso biblioteca  │   │ • Tracking       │   │ • Descarga ∞     │
    └──────────────────────┘   └──────────────────┘   └──────────────────┘
                  │                       │                      │
                  └───────────────────────┴──────────────────────┘
                                          │
                                          ▼
                  ┌─────────────────────────────────────────────┐
                  │  4. CLIENTE RECIBE                          │
                  │  • Email en segundos con PDF                │
                  │  • Puede leer inmediatamente                │
                  │  • Físico llega en 3-7 días                 │
                  └─────────────────────────────────────────────┘
```

---

## 💻 Implementación Técnica

### 1. Order Service - Handler Principal

```java
@Service
@Slf4j
public class PhysicalOrderService {
    
    @Autowired
    private BookFileRepository bookFileRepository;
    
    @Autowired
    private EmailDeliveryService emailDeliveryService;
    
    @Autowired
    private LuluIntegrationService luluService;
    
    @Autowired
    private DigitalLibraryService digitalLibraryService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Transactional
    public void processPhysicalBookOrder(Order order) {
        log.info("Processing physical book order: {}", order.getId());
        
        Book book = order.getBook();
        User customer = order.getCustomer();
        
        try {
            // 1. INMEDIATO: Enviar PDF por email
            sendDigitalCopyToEmail(book, customer, order);
            
            // 2. Agregar a biblioteca digital del usuario
            addToDigitalLibrary(book, customer, order);
            
            // 3. Crear orden de impresión en Lulu.com
            String luluOrderId = createLuluPrintOrder(book, order);
            
            // 4. Actualizar orden con información de impresión
            order.setLuluOrderId(luluOrderId);
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);
            
            // 5. Notificar al escritor
            notificationService.notifyWriterOfSale(
                book.getWriter(), 
                order, 
                "PHYSICAL"
            );
            
            log.info("Physical order processed successfully: {}", order.getId());
            
        } catch (Exception e) {
            log.error("Error processing physical order: {}", order.getId(), e);
            
            // Marcar orden como fallida pero NO revertir pago
            order.setStatus(OrderStatus.FAILED);
            order.setErrorMessage(e.getMessage());
            orderRepository.save(order);
            
            // Notificar soporte para intervención manual
            notificationService.alertSupport(order, e);
            
            throw new OrderProcessingException("Failed to process physical order", e);
        }
    }
    
    private void sendDigitalCopyToEmail(Book book, User customer, Order order) {
        log.info("Sending digital copy of book {} to {}", book.getId(), customer.getEmail());
        
        // Obtener archivo PDF del libro
        BookFile pdfFile = bookFileRepository
            .findByBookIdAndFileType(book.getId(), FileType.PDF_DIGITAL)
            .orElseThrow(() -> new FileNotFoundException("PDF not found for book"));
        
        // Generar link de descarga temporal (válido 7 días)
        String downloadLink = generateSecureDownloadLink(
            pdfFile, 
            customer, 
            order,
            Duration.ofDays(7)
        );
        
        // Enviar email con el PDF
        EmailTemplate template = EmailTemplate.builder()
            .to(customer.getEmail())
            .subject("🎁 Tu libro digital incluido: " + book.getTitle())
            .template("physical-order-with-pdf")
            .variable("customerName", customer.getFirstName())
            .variable("bookTitle", book.getTitle())
            .variable("bookAuthor", book.getWriter().getFullName())
            .variable("downloadLink", downloadLink)
            .variable("coverImageUrl", book.getCoverImageUrl())
            .variable("orderNumber", order.getOrderNumber())
            .variable("estimatedDelivery", calculateDeliveryDate(order))
            .build();
        
        emailDeliveryService.sendEmail(template);
        
        // Registrar envío
        DigitalDelivery delivery = DigitalDelivery.builder()
            .orderId(order.getId())
            .userId(customer.getId())
            .bookId(book.getId())
            .deliveryMethod("EMAIL")
            .deliveryStatus("SENT")
            .downloadLink(downloadLink)
            .sentAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plusDays(7))
            .build();
        
        digitalDeliveryRepository.save(delivery);
        
        log.info("Digital copy sent successfully to {}", customer.getEmail());
    }
    
    private String generateSecureDownloadLink(BookFile pdfFile, User customer, 
                                             Order order, Duration validity) {
        // Generar token único y seguro
        String token = UUID.randomUUID().toString();
        
        // Guardar token en base de datos
        DownloadToken downloadToken = DownloadToken.builder()
            .token(token)
            .bookFileId(pdfFile.getId())
            .userId(customer.getId())
            .orderId(order.getId())
            .maxDownloads(10) // Permitir 10 descargas
            .downloadCount(0)
            .expiresAt(LocalDateTime.now().plus(validity))
            .createdAt(LocalDateTime.now())
            .build();
        
        downloadTokenRepository.save(downloadToken);
        
        // Generar URL: https://drakkarpress.com/download/{token}
        return String.format("%s/download/%s", 
            configService.getBaseUrl(), 
            token
        );
    }
    
    private void addToDigitalLibrary(Book book, User customer, Order order) {
        // Verificar si ya tiene el libro
        boolean alreadyOwns = digitalLibraryRepository
            .existsByUserIdAndBookId(customer.getId(), book.getId());
        
        if (!alreadyOwns) {
            DigitalLibraryEntry entry = DigitalLibraryEntry.builder()
                .userId(customer.getId())
                .bookId(book.getId())
                .orderId(order.getId())
                .acquisitionType("PHYSICAL_PURCHASE_BONUS")
                .purchaseDate(LocalDateTime.now())
                .build();
            
            digitalLibraryRepository.save(entry);
            
            log.info("Book {} added to digital library of user {}", 
                book.getId(), customer.getId());
        }
    }
    
    private String createLuluPrintOrder(Book book, Order order) {
        // Validar que el libro tiene configuración de impresión
        if (!book.hasPrintConfiguration()) {
            throw new IllegalStateException("Book does not have print configuration");
        }
        
        // Crear orden en Lulu.com
        LuluOrderRequest request = LuluOrderRequest.builder()
            .projectId(book.getLuluProjectId())
            .quantity(order.getQuantity())
            .shippingLevel(order.getShippingLevel())
            .shippingAddress(mapToLuluAddress(order.getShippingAddress()))
            .contactEmail(order.getCustomer().getEmail())
            .build();
        
        LuluOrderResponse response = luluService.createOrder(request);
        
        // Guardar información de impresión
        PrintOrder printOrder = PrintOrder.builder()
            .orderId(order.getId())
            .luluOrderId(response.getOrderId())
            .status("SUBMITTED")
            .trackingNumber(response.getTrackingNumber())
            .printCost(response.getTotalCost())
            .submittedAt(LocalDateTime.now())
            .build();
        
        printOrderRepository.save(printOrder);
        
        return response.getOrderId();
    }
}
```

---

## 📧 Servicio de Entrega por Email

```java
@Service
public class EmailDeliveryService {
    
    @Autowired
    private EmailClient emailClient; // SendGrid, AWS SES, etc.
    
    @Autowired
    private TemplateEngine templateEngine;
    
    public void sendEmail(EmailTemplate template) {
        try {
            // Renderizar template HTML
            String htmlContent = templateEngine.render(
                template.getTemplate(), 
                template.getVariables()
            );
            
            // Crear email
            Email email = Email.builder()
                .from("libros@drakkarpress.com", "DrakkarPress")
                .to(template.getTo())
                .subject(template.getSubject())
                .htmlContent(htmlContent)
                .textContent(generateTextVersion(htmlContent))
                .replyTo("soporte@drakkarpress.com")
                .build();
            
            // Enviar
            emailClient.send(email);
            
            log.info("Email sent to {}", template.getTo());
            
        } catch (Exception e) {
            log.error("Failed to send email to {}", template.getTo(), e);
            throw new EmailDeliveryException("Failed to send email", e);
        }
    }
}
```

---

## 📄 Template de Email: physical-order-with-pdf.html

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            text-align: center;
            padding: 20px;
            background: linear-gradient(135deg, #1A4D7A 0%, #2E6BA0 100%);
            color: white;
            border-radius: 10px 10px 0 0;
        }
        .content {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 0 0 10px 10px;
        }
        .book-card {
            background: white;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            display: flex;
            gap: 20px;
        }
        .book-cover {
            width: 120px;
            height: auto;
            border-radius: 5px;
        }
        .download-button {
            display: inline-block;
            padding: 15px 30px;
            background: #D4AF37;
            color: #1A4D7A;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            margin: 20px 0;
            text-align: center;
        }
        .info-box {
            background: #e3f2fd;
            border-left: 4px solid #2196F3;
            padding: 15px;
            margin: 20px 0;
            border-radius: 5px;
        }
        .footer {
            text-align: center;
            padding: 20px;
            color: #666;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🎁 ¡Sorpresa! Tu libro digital incluido</h1>
    </div>
    
    <div class="content">
        <p>Hola <strong>{{customerName}}</strong>,</p>
        
        <p>¡Gracias por tu compra! Mientras tu libro físico está en camino, queremos que empieces a disfrutarlo <strong>ahora mismo</strong>.</p>
        
        <div class="book-card">
            <img src="{{coverImageUrl}}" alt="{{bookTitle}}" class="book-cover">
            <div>
                <h2 style="margin-top: 0;">{{bookTitle}}</h2>
                <p style="color: #666;">por {{bookAuthor}}</p>
                <p><strong>Pedido:</strong> #{{orderNumber}}</p>
            </div>
        </div>
        
        <div class="info-box">
            <strong>📚 Al comprar la versión física, la versión digital es GRATIS</strong>
            <p>No tienes que esperar días para empezar a leer. Descarga el PDF ahora y comienza tu lectura en cualquier dispositivo.</p>
        </div>
        
        <center>
            <a href="{{downloadLink}}" class="download-button">
                ⬇️ DESCARGAR PDF AHORA
            </a>
        </center>
        
        <p style="font-size: 14px; color: #666;">
            <strong>Nota:</strong> Este enlace es válido por 7 días y permite hasta 10 descargas. 
            También puedes acceder a este libro en cualquier momento desde tu 
            <a href="https://drakkarpress.com/biblioteca">biblioteca digital</a>.
        </p>
        
        <hr style="border: 1px solid #ddd; margin: 30px 0;">
        
        <h3>📦 Estado de tu libro físico</h3>
        <p>Tu libro impreso será enviado en las próximas 24-48 horas.</p>
        <p><strong>Fecha estimada de entrega:</strong> {{estimatedDelivery}}</p>
        <p>Recibirás un email con el número de tracking cuando tu pedido sea enviado.</p>
        
        <hr style="border: 1px solid #ddd; margin: 30px 0;">
        
        <h3>💡 ¿Sabías que...?</h3>
        <ul>
            <li>El PDF puedes leerlo en computadora, tablet o celular</li>
            <li>Puedes imprimir páginas si lo necesitas</li>
            <li>Está optimizado para lectura digital</li>
            <li>Acceso ilimitado desde tu biblioteca</li>
        </ul>
        
        <p>¿Tienes alguna pregunta? Responde este email o visita nuestro 
        <a href="https://drakkarpress.com/soporte">centro de ayuda</a>.</p>
        
        <p>¡Disfruta tu lectura! 📖</p>
        
        <p>
            El equipo de DrakkarPress<br>
            <em>Donde tus libros navegan al mundo 🚢</em>
        </p>
    </div>
    
    <div class="footer">
        <p>© 2025 DrakkarPress. Todos los derechos reservados.</p>
        <p>
            <a href="https://drakkarpress.com">Inicio</a> |
            <a href="https://drakkarpress.com/catalogo">Catálogo</a> |
            <a href="https://drakkarpress.com/soporte">Soporte</a>
        </p>
    </div>
</body>
</html>
```

---

## 🔐 Sistema de Descarga Segura

### Controlador de Descarga

```java
@RestController
@RequestMapping("/download")
public class DownloadController {
    
    @Autowired
    private DownloadTokenRepository tokenRepository;
    
    @Autowired
    private BookFileRepository bookFileRepository;
    
    @Autowired
    private S3Service s3Service;
    
    @GetMapping("/{token}")
    public ResponseEntity<Resource> downloadBook(@PathVariable String token,
                                                HttpServletRequest request) {
        
        // 1. Validar token
        DownloadToken downloadToken = tokenRepository
            .findByToken(token)
            .orElseThrow(() -> new TokenNotFoundException("Invalid download token"));
        
        // 2. Verificar si está expirado
        if (downloadToken.isExpired()) {
            throw new TokenExpiredException("Download link has expired");
        }
        
        // 3. Verificar límite de descargas
        if (downloadToken.getDownloadCount() >= downloadToken.getMaxDownloads()) {
            throw new DownloadLimitException("Download limit reached");
        }
        
        // 4. Obtener archivo
        BookFile bookFile = bookFileRepository
            .findById(downloadToken.getBookFileId())
            .orElseThrow(() -> new FileNotFoundException("Book file not found"));
        
        // 5. Generar URL pre-firmada de S3 (válida 15 minutos)
        String s3Url = s3Service.generatePresignedUrl(
            bookFile.getS3Key(),
            Duration.ofMinutes(15)
        );
        
        // 6. Incrementar contador de descargas
        downloadToken.incrementDownloadCount();
        downloadToken.setLastDownloadAt(LocalDateTime.now());
        downloadToken.setLastDownloadIp(getClientIP(request));
        tokenRepository.save(downloadToken);
        
        // 7. Registrar descarga
        logDownload(downloadToken, bookFile, request);
        
        // 8. Redirigir a S3 (o stream directo)
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(s3Url))
            .build();
        
        // Alternativa: Stream directo
        // return streamFileDirectly(bookFile);
    }
    
    private void logDownload(DownloadToken token, BookFile file, 
                            HttpServletRequest request) {
        DownloadLog log = DownloadLog.builder()
            .tokenId(token.getId())
            .bookFileId(file.getId())
            .userId(token.getUserId())
            .ipAddress(getClientIP(request))
            .userAgent(request.getHeader("User-Agent"))
            .downloadedAt(LocalDateTime.now())
            .build();
        
        downloadLogRepository.save(log);
    }
    
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
```

---

## 🗄️ Modelos de Base de Datos

```sql
-- Tabla: digital_deliveries
CREATE TABLE digital_deliveries (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    delivery_method VARCHAR(50), -- EMAIL, DOWNLOAD_PAGE, LIBRARY
    delivery_status VARCHAR(50), -- PENDING, SENT, FAILED
    download_link VARCHAR(500),
    sent_at TIMESTAMP,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla: download_tokens
CREATE TABLE download_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    book_file_id BIGINT NOT NULL REFERENCES book_files(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    order_id BIGINT REFERENCES orders(id),
    max_downloads INTEGER DEFAULT 10,
    download_count INTEGER DEFAULT 0,
    expires_at TIMESTAMP NOT NULL,
    last_download_at TIMESTAMP,
    last_download_ip VARCHAR(50),
    created_at TIMESTAMP DEFAULT NOW(),
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires (expires_at)
);

-- Tabla: download_logs
CREATE TABLE download_logs (
    id BIGSERIAL PRIMARY KEY,
    token_id BIGINT REFERENCES download_tokens(id),
    book_file_id BIGINT NOT NULL REFERENCES book_files(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    ip_address VARCHAR(50),
    user_agent TEXT,
    downloaded_at TIMESTAMP DEFAULT NOW(),
    INDEX idx_user_id (user_id),
    INDEX idx_downloaded_at (downloaded_at)
);

-- Tabla: digital_library (usuarios que poseen libros digitales)
CREATE TABLE digital_library (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    order_id BIGINT REFERENCES orders(id),
    acquisition_type VARCHAR(50), -- PURCHASE, PHYSICAL_PURCHASE_BONUS, GIFT
    purchase_date TIMESTAMP,
    last_accessed TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(user_id, book_id),
    INDEX idx_user_books (user_id, book_id)
);
```

---

## 🎨 Página de Biblioteca Digital

### Frontend: /biblioteca

```javascript
// React Component
const DigitalLibrary = () => {
    const [books, setBooks] = useState([]);
    
    useEffect(() => {
        fetchMyLibrary();
    }, []);
    
    const fetchMyLibrary = async () => {
        const response = await axios.get('/api/library/my-books');
        setBooks(response.data);
    };
    
    const downloadBook = async (bookId) => {
        try {
            // Generar link de descarga
            const response = await axios.post(`/api/library/generate-download/${bookId}`);
            
            // Abrir en nueva pestaña
            window.open(response.data.downloadUrl, '_blank');
            
        } catch (error) {
            toast.error('Error al descargar el libro');
        }
    };
    
    return (
        <div className="digital-library">
            <h1>📚 Mi Biblioteca Digital</h1>
            <p>Tienes {books.length} libros en tu biblioteca</p>
            
            <div className="books-grid">
                {books.map(book => (
                    <div key={book.id} className="book-card">
                        <img src={book.coverUrl} alt={book.title} />
                        <h3>{book.title}</h3>
                        <p>{book.author}</p>
                        <p className="acquisition">
                            {book.acquisitionType === 'PHYSICAL_PURCHASE_BONUS' 
                                ? '🎁 Incluido con compra física'
                                : '📥 Comprado'}
                        </p>
                        <button onClick={() => downloadBook(book.id)}>
                            ⬇️ Descargar PDF
                        </button>
                        <button onClick={() => readOnline(book.id)}>
                            📖 Leer online
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
};
```

### Backend API

```java
@RestController
@RequestMapping("/api/library")
public class DigitalLibraryController {
    
    @GetMapping("/my-books")
    public List<DigitalLibraryDTO> getMyBooks(@AuthenticatedUser User user) {
        return digitalLibraryService.getUserBooks(user.getId());
    }
    
    @PostMapping("/generate-download/{bookId}")
    public DownloadLinkDTO generateDownloadLink(
            @PathVariable Long bookId,
            @AuthenticatedUser User user) {
        
        // Verificar que el usuario posee el libro
        boolean owns = digitalLibraryService.userOwnsBook(user.getId(), bookId);
        if (!owns) {
            throw new UnauthorizedException("You don't own this book");
        }
        
        // Generar nuevo token de descarga
        BookFile pdfFile = bookFileRepository
            .findByBookIdAndFileType(bookId, FileType.PDF_DIGITAL)
            .orElseThrow(() -> new FileNotFoundException("PDF not found"));
        
        String token = UUID.randomUUID().toString();
        
        DownloadToken downloadToken = DownloadToken.builder()
            .token(token)
            .bookFileId(pdfFile.getId())
            .userId(user.getId())
            .maxDownloads(1) // Solo 1 descarga por token generado
            .downloadCount(0)
            .expiresAt(LocalDateTime.now().plusHours(2)) // Válido 2 horas
            .build();
        
        downloadTokenRepository.save(downloadToken);
        
        String downloadUrl = String.format("%s/download/%s", 
            configService.getBaseUrl(), token);
        
        return DownloadLinkDTO.builder()
            .downloadUrl(downloadUrl)
            .expiresIn(7200) // segundos
            .build();
    }
}
```

---

## 📊 Analytics y Métricas

### Dashboard para Escritores

```
┌──────────────────────────────────────────────────────┐
│ 📊 DISTRIBUCIÓN DIGITAL                              │
├──────────────────────────────────────────────────────┤
│                                                      │
│  Este mes:                                           │
│  • 45 PDFs entregados (con compra física)            │
│  • 32 PDFs descargados (compra digital)              │
│  • Tasa de descarga: 71% (32/45)                     │
│                                                      │
│  Estadísticas:                                       │
│  • Tiempo promedio hasta primer descarga: 2.3h       │
│  • Descargas por usuario: 1.8 promedio               │
│  • Dispositivos: 60% móvil, 30% PC, 10% tablet       │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### Queries para Métricas

```sql
-- Tasa de descarga de PDFs enviados
SELECT 
    COUNT(DISTINCT dd.id) as pdfs_sent,
    COUNT(DISTINCT dl.id) as pdfs_downloaded,
    ROUND(COUNT(DISTINCT dl.id)::numeric / COUNT(DISTINCT dd.id) * 100, 2) as download_rate
FROM digital_deliveries dd
LEFT JOIN download_logs dl ON dl.user_id = dd.user_id 
    AND dl.book_file_id IN (
        SELECT id FROM book_files WHERE book_id = dd.book_id
    )
WHERE dd.sent_at >= NOW() - INTERVAL '30 days';

-- Tiempo promedio hasta primera descarga
SELECT 
    AVG(EXTRACT(EPOCH FROM (dl.downloaded_at - dd.sent_at))/3600) as avg_hours
FROM digital_deliveries dd
JOIN download_logs dl ON dl.user_id = dd.user_id
WHERE dl.downloaded_at = (
    SELECT MIN(downloaded_at) 
    FROM download_logs 
    WHERE user_id = dd.user_id
);
```

---

## 🔔 Notificaciones Automáticas

### Email 2: Recordatorio de Descarga (24h después)

Si el usuario no ha descargado el PDF en 24 horas:

```java
@Scheduled(fixedRate = 3600000) // Cada hora
public void sendDownloadReminders() {
    LocalDateTime threshold = LocalDateTime.now().minusHours(24);
    
    List<DigitalDelivery> undownloaded = digitalDeliveryRepository
        .findUndownloadedSince(threshold);
    
    for (DigitalDelivery delivery : undownloaded) {
        emailService.sendDownloadReminder(
            delivery.getUser(),
            delivery.getBook(),
            delivery.getDownloadLink()
        );
        
        delivery.setReminderSent(true);
        digitalDeliveryRepository.save(delivery);
    }
}
```

### Email 3: Confirmación de Envío Físico

Cuando Lulu envía el libro:

```java
public void notifyPhysicalBookShipped(Order order, String trackingNumber) {
    EmailTemplate template = EmailTemplate.builder()
        .to(order.getCustomer().getEmail())
        .subject("📦 Tu libro está en camino - " + order.getBook().getTitle())
        .template("physical-book-shipped")
        .variable("trackingNumber", trackingNumber)
        .variable("trackingUrl", generateTrackingUrl(trackingNumber))
        .variable("estimatedDelivery", calculateDeliveryDate(order))
        .build();
    
    emailService.sendEmail(template);
}
```

---

## ✅ Checklist de Implementación

### Fase 1: Core (Semana 1-2)
- [ ] Tabla `digital_deliveries` y `download_tokens`
- [ ] Endpoint `/download/{token}` con validaciones
- [ ] Servicio de generación de links seguros
- [ ] Template de email básico

### Fase 2: Integración (Semana 3)
- [ ] Integrar con Order Service
- [ ] Trigger automático al comprar físico
- [ ] Agregar a biblioteca digital
- [ ] Logs de descargas

### Fase 3: UX (Semana 4)
- [ ] Template de email profesional
- [ ] Página de biblioteca digital
- [ ] Botón "Descargar PDF" en perfil
- [ ] Indicador visual de descarga incluida

### Fase 4: Analytics (Semana 5)
- [ ] Dashboard de distribución digital
- [ ] Métricas de descarga
- [ ] Reportes para escritores
- [ ] A/B testing de emails

---

## 🎯 Mejoras Futuras

1. **Lector Online:** Leer PDF en navegador sin descargar
2. **App Móvil:** Sincronización con apps iOS/Android
3. **Notas y Marcadores:** Guardar progreso de lectura
4. **Conversión a EPUB:** Opción de descargar en múltiples formatos
5. **Audio:** Conversión texto-a-voz del PDF
6. **Compartir:** Regalar acceso digital a amigos

---

**Versión:** 1.0  
**Última actualización:** 9 nov 2025  
**Responsable:** Order Service + Email Delivery Service
