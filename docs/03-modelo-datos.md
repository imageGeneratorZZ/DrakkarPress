# Modelo de Datos - DrakkarPress

## Diagrama ER (Entidades y Relaciones)

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│    USER     │◄────────│    BOOK     │────────►│  CATEGORY   │
│             │  author │             │category │             │
│ • id        │         │ • id        │         │ • id        │
│ • email     │         │ • isbn      │         │ • slug      │
│ • role      │         │ • title     │         │ • name      │
│ • plan      │         │ • synopsis  │         │ • desc      │
└──────┬──────┘         │ • pages     │         └─────────────┘
       │                │ • language  │
       │                │ • status    │         ┌─────────────┐
       │                │ • created   │────────►│   REVIEW    │
       │                └──────┬──────┘         │             │
       │                       │                │ • id        │
       │                       │                │ • rating    │
       │                       │                │ • comment   │
       │                ┌──────▼──────┐         └─────────────┘
       │                │  BOOK_FILE  │
       │                │             │
       │                │ • id        │
       │                │ • type      │
       │                │ • url       │
       │                │ • format    │
       │                └─────────────┘
       │
       ├───────────────┐
       │               │
┌──────▼──────┐ ┌──────▼──────┐         ┌─────────────┐
│    ORDER    │ │  AFFILIATE  │────────►│AFFILIATE_LINK│
│             │ │             │         │             │
│ • id        │ │ • catalog   │         │ • id        │
│ • status    │ │ • commission│         │ • code      │
│ • total     │ └─────────────┘         │ • clicks    │
│ • source    │                         │ • sales     │
└──────┬──────┘                         └─────────────┘
       │
       │
┌──────▼──────┐         ┌─────────────┐
│ ORDER_ITEM  │────────►│ PRINT_ORDER │
│             │         │             │
│ • quantity  │         │ • printer_id│
│ • price     │         │ • status    │
│ • format    │         │ • tracking  │
└─────────────┘         └─────────────┘
```

---

## Entidades Principales

### 1. User (usuarios)

Tabla base para todos los tipos de usuario.

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- WRITER, RESELLER, PRINTER, READER, ADMIN
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_url VARCHAR(500),
    bio TEXT,
    plan VARCHAR(50) DEFAULT 'FREE', -- FREE, WRITER_PRO, RESELLER_PRO, PRINTER_PRO
    email_verified BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMP,
    status VARCHAR(50) DEFAULT 'ACTIVE', -- ACTIVE, SUSPENDED, BANNED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    
    -- Integraciones externas
    shopify_access_token VARCHAR(255),
    mercadolibre_access_token VARCHAR(255),
    mercadolibre_refresh_token VARCHAR(255),
    mercadolibre_user_id VARCHAR(100),
    
    -- Preferencias
    language VARCHAR(10) DEFAULT 'es',
    currency VARCHAR(10) DEFAULT 'USD',
    timezone VARCHAR(50) DEFAULT 'America/Santiago',
    
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
);
```

**Relaciones:**
- Un User puede ser Writer → tiene muchos Books
- Un User puede ser Reseller → tiene un Affiliate profile
- Un User puede ser Printer → tiene un Printer profile
- Un User puede ser Reader → tiene muchos Orders

---

### 2. Writer Profile (writer_profiles)

Información adicional para escritores.

```sql
CREATE TABLE writer_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    pen_name VARCHAR(255), -- Nombre de pluma (opcional)
    biography TEXT,
    website VARCHAR(500),
    social_twitter VARCHAR(100),
    social_instagram VARCHAR(100),
    social_facebook VARCHAR(100),
    
    -- Configuración de pagos
    payment_method VARCHAR(50), -- BANK_TRANSFER, PAYPAL, STRIPE
    payment_email VARCHAR(255),
    bank_account_number VARCHAR(100),
    bank_name VARCHAR(255),
    tax_id VARCHAR(100), -- RFC, SSN, etc.
    
    -- Estadísticas
    total_books INT DEFAULT 0,
    total_sales INT DEFAULT 0,
    total_earnings DECIMAL(10, 2) DEFAULT 0.00,
    
    -- Configuración
    auto_publish_shopify BOOLEAN DEFAULT TRUE,
    auto_publish_mercadolibre BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id)
);
```

---

### 3. Affiliate Profile (affiliate_profiles)

Información de revendedores/afiliados.

```sql
CREATE TABLE affiliate_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    affiliate_code VARCHAR(50) UNIQUE NOT NULL, -- Código único ej: "mariasanchez"
    
    -- Configuración de comisiones
    commission_rate DECIMAL(5, 2) DEFAULT 15.00, -- Porcentaje
    
    -- Configuración de pagos
    payment_method VARCHAR(50),
    payment_email VARCHAR(255),
    bank_account_number VARCHAR(100),
    
    -- Estadísticas
    total_clicks INT DEFAULT 0,
    total_sales INT DEFAULT 0,
    total_commissions DECIMAL(10, 2) DEFAULT 0.00,
    conversion_rate DECIMAL(5, 2) DEFAULT 0.00,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_affiliate_code (affiliate_code)
);
```

---

### 4. Printer Profile (printer_profiles)

Información de imprentas.

```sql
CREATE TABLE printer_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_name VARCHAR(255) NOT NULL,
    company_tax_id VARCHAR(100),
    
    -- Ubicación
    country VARCHAR(2), -- Código ISO país
    state VARCHAR(100),
    city VARCHAR(100),
    address TEXT,
    postal_code VARCHAR(20),
    phone VARCHAR(50),
    
    -- Coordenadas para mapa
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    
    -- Capacidades
    supports_softcover BOOLEAN DEFAULT TRUE,
    supports_hardcover BOOLEAN DEFAULT FALSE,
    supports_color BOOLEAN DEFAULT TRUE,
    supports_bw BOOLEAN DEFAULT TRUE,
    min_pages INT DEFAULT 24,
    max_pages INT DEFAULT 500,
    
    -- Tiempos
    avg_production_days INT DEFAULT 3,
    avg_shipping_days INT DEFAULT 5,
    
    -- Costos (por página aproximado)
    cost_per_page_bw DECIMAL(5, 4),
    cost_per_page_color DECIMAL(5, 4),
    
    -- Estadísticas
    total_orders INT DEFAULT 0,
    completed_orders INT DEFAULT 0,
    rating DECIMAL(3, 2) DEFAULT 5.00,
    
    -- Estado
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_country (country),
    INDEX idx_is_active (is_active)
);
```

---

### 5. Category (categories)

Categorías editoriales.

```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon VARCHAR(50), -- Emoji o clase de ícono
    color VARCHAR(7), -- Hex color
    parent_id BIGINT REFERENCES categories(id), -- Para subcategorías
    
    -- SEO
    meta_title VARCHAR(255),
    meta_description TEXT,
    
    -- Orden
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Estadísticas
    book_count INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_slug (slug),
    INDEX idx_parent_id (parent_id),
    INDEX idx_is_active (is_active)
);

-- Datos iniciales
INSERT INTO categories (slug, name, icon, color) VALUES
('scryptorium', 'Scryptorium (Infantil)', '🧒', '#FF6B6B'),
('erotica', 'Erótica', '🔥', '#E91E63'),
('thriller', 'Thriller / Suspenso', '🔪', '#9C27B0'),
('romance', 'Romance', '💕', '#F48FB1'),
('fantasy-scifi', 'Fantasía / Sci-Fi', '🚀', '#3F51B5'),
('cooking', 'Cocina y Recetas', '🍳', '#FF9800'),
('nonfiction', 'No Ficción', '📈', '#4CAF50');
```

---

### 6. Book (books)

Libros del catálogo.

```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category_id BIGINT REFERENCES categories(id),
    
    -- Identificación
    isbn VARCHAR(17) UNIQUE, -- ISBN-13
    title VARCHAR(500) NOT NULL,
    subtitle VARCHAR(500),
    slug VARCHAR(600) UNIQUE NOT NULL,
    
    -- Contenido
    synopsis TEXT,
    description TEXT, -- Descripción larga
    language VARCHAR(10) DEFAULT 'es', -- ISO 639-1
    pages INT,
    
    -- Imágenes
    cover_url VARCHAR(500),
    cover_thumbnail_url VARCHAR(500),
    
    -- Pricing
    digital_price DECIMAL(10, 2),
    print_price DECIMAL(10, 2),
    print_cost DECIMAL(10, 2), -- Costo de impresión (Lulu)
    
    -- Formatos disponibles
    has_digital BOOLEAN DEFAULT TRUE,
    has_print BOOLEAN DEFAULT FALSE,
    has_audio BOOLEAN DEFAULT FALSE,
    
    -- Especificaciones de impresión
    print_size VARCHAR(50), -- US_TRADE, A4, etc.
    print_binding VARCHAR(50), -- PERFECT_BIND, HARDCOVER
    print_color VARCHAR(50), -- COLOR, BW
    print_paper VARCHAR(50), -- WHITE, CREAM
    
    -- Estado
    status VARCHAR(50) DEFAULT 'DRAFT', -- DRAFT, PENDING_REVIEW, PUBLISHED, ARCHIVED
    published_at TIMESTAMP,
    
    -- Integraciones
    shopify_product_id VARCHAR(100),
    mercadolibre_item_id VARCHAR(100),
    lulu_project_id VARCHAR(100),
    
    -- SEO
    meta_title VARCHAR(255),
    meta_description TEXT,
    keywords TEXT, -- Separados por comas
    
    -- Estadísticas
    views INT DEFAULT 0,
    sales_count INT DEFAULT 0,
    rating_avg DECIMAL(3, 2) DEFAULT 0.00,
    rating_count INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_author_id (author_id),
    INDEX idx_category_id (category_id),
    INDEX idx_isbn (isbn),
    INDEX idx_slug (slug),
    INDEX idx_status (status),
    INDEX idx_published_at (published_at),
    
    FULLTEXT idx_search (title, synopsis, description)
);
```

---

### 7. Book Files (book_files)

Archivos asociados a libros.

```sql
CREATE TABLE book_files (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    file_type VARCHAR(50) NOT NULL, -- COVER, INTERIOR, DIGITAL, AUDIO
    format VARCHAR(50), -- PDF, EPUB, MOBI, MP3, JPG
    url VARCHAR(500) NOT NULL,
    file_size BIGINT, -- En bytes
    version INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_book_id (book_id),
    INDEX idx_file_type (file_type)
);
```

---

### 8. Review (reviews)

Reseñas de libros.

```sql
CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    
    -- Moderación
    is_verified_purchase BOOLEAN DEFAULT FALSE,
    is_approved BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    
    -- Utilidad
    helpful_count INT DEFAULT 0,
    unhelpful_count INT DEFAULT 0,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (book_id, user_id), -- Un usuario solo puede dejar una reseña por libro
    INDEX idx_book_id (book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating)
);
```

---

### 9. Order (orders)

Pedidos/Órdenes.

```sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL, -- DP-2025-001234
    user_id BIGINT REFERENCES users(id),
    
    -- Fuente de la orden
    source VARCHAR(50) NOT NULL, -- DRAKKARPRESS, SHOPIFY, MERCADOLIBRE
    external_order_id VARCHAR(100), -- ID en plataforma externa
    
    -- Afiliado (si aplica)
    affiliate_id BIGINT REFERENCES affiliate_profiles(id),
    affiliate_commission DECIMAL(10, 2) DEFAULT 0.00,
    
    -- Estado
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    payment_status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, PAID, FAILED, REFUNDED
    
    -- Montos
    subtotal DECIMAL(10, 2) NOT NULL,
    shipping_cost DECIMAL(10, 2) DEFAULT 0.00,
    tax DECIMAL(10, 2) DEFAULT 0.00,
    discount DECIMAL(10, 2) DEFAULT 0.00,
    total DECIMAL(10, 2) NOT NULL,
    
    currency VARCHAR(10) DEFAULT 'USD',
    
    -- Cliente
    customer_email VARCHAR(255) NOT NULL,
    customer_name VARCHAR(255),
    customer_phone VARCHAR(50),
    
    -- Dirección de envío
    shipping_address_line1 VARCHAR(255),
    shipping_address_line2 VARCHAR(255),
    shipping_city VARCHAR(100),
    shipping_state VARCHAR(100),
    shipping_postal_code VARCHAR(20),
    shipping_country VARCHAR(2),
    
    -- Notas
    customer_notes TEXT,
    internal_notes TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    
    INDEX idx_order_number (order_number),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_source (source),
    INDEX idx_affiliate_id (affiliate_id),
    INDEX idx_created_at (created_at)
);
```

---

### 10. Order Item (order_items)

Items individuales en una orden.

```sql
CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    book_id BIGINT NOT NULL REFERENCES books(id),
    
    format VARCHAR(50) NOT NULL, -- DIGITAL, PRINT
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    
    -- Para impreso
    print_order_id BIGINT, -- Se asigna después
    
    -- Regalías
    author_royalty DECIMAL(10, 2),
    author_royalty_percent DECIMAL(5, 2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id)
);
```

---

### 11. Print Order (print_orders)

Órdenes de impresión.

```sql
CREATE TABLE print_orders (
    id BIGSERIAL PRIMARY KEY,
    order_item_id BIGINT NOT NULL REFERENCES order_items(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    
    -- Imprenta asignada
    printer_id BIGINT REFERENCES printer_profiles(id),
    assignment_method VARCHAR(50), -- AUTO, MANUAL, LULU
    
    -- Si es Lulu
    lulu_order_id VARCHAR(100),
    
    -- Especificaciones
    quantity INT NOT NULL,
    specifications JSONB, -- Detalles de impresión
    
    -- Estado
    status VARCHAR(50) DEFAULT 'PENDING', 
    -- PENDING, ASSIGNED, ACCEPTED, IN_PRODUCTION, PRINTED, SHIPPED, DELIVERED, CANCELLED
    
    -- Tracking
    tracking_number VARCHAR(100),
    carrier VARCHAR(50), -- DHL, FEDEX, UPS, etc.
    
    -- Costos
    production_cost DECIMAL(10, 2),
    shipping_cost DECIMAL(10, 2),
    
    -- Fechas
    assigned_at TIMESTAMP,
    accepted_at TIMESTAMP,
    production_started_at TIMESTAMP,
    printed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_order_item_id (order_item_id),
    INDEX idx_printer_id (printer_id),
    INDEX idx_status (status),
    INDEX idx_tracking_number (tracking_number)
);
```

---

### 12. Affiliate Link (affiliate_links)

Enlaces de tracking de afiliados.

```sql
CREATE TABLE affiliate_links (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL REFERENCES affiliate_profiles(id) ON DELETE CASCADE,
    book_id BIGINT REFERENCES books(id), -- NULL = link general
    
    code VARCHAR(100) UNIQUE NOT NULL, -- Código corto único
    full_url VARCHAR(500) NOT NULL,
    
    -- Estadísticas
    clicks INT DEFAULT 0,
    unique_clicks INT DEFAULT 0,
    sales INT DEFAULT 0,
    revenue DECIMAL(10, 2) DEFAULT 0.00,
    
    -- Metadata
    source VARCHAR(50), -- INSTAGRAM, FACEBOOK, EMAIL, QR, etc.
    campaign VARCHAR(100),
    
    is_active BOOLEAN DEFAULT TRUE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    
    INDEX idx_affiliate_id (affiliate_id),
    INDEX idx_book_id (book_id),
    INDEX idx_code (code)
);
```

---

### 13. Affiliate Click (affiliate_clicks)

Registro de clics en enlaces de afiliado.

```sql
CREATE TABLE affiliate_clicks (
    id BIGSERIAL PRIMARY KEY,
    affiliate_link_id BIGINT NOT NULL REFERENCES affiliate_links(id) ON DELETE CASCADE,
    
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer VARCHAR(500),
    
    -- Geolocalización
    country VARCHAR(2),
    city VARCHAR(100),
    
    -- Conversión
    converted BOOLEAN DEFAULT FALSE,
    order_id BIGINT REFERENCES orders(id),
    
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_affiliate_link_id (affiliate_link_id),
    INDEX idx_clicked_at (clicked_at),
    INDEX idx_converted (converted)
);
```

---

### 14. Payment (payments)

Pagos realizados.

```sql
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(id),
    
    payment_method VARCHAR(50), -- CREDIT_CARD, PAYPAL, MERCADOPAGO, etc.
    payment_provider VARCHAR(50), -- SHOPIFY, STRIPE, MERCADOLIBRE
    
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(10) DEFAULT 'USD',
    
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, COMPLETED, FAILED, REFUNDED
    
    -- IDs externos
    transaction_id VARCHAR(255),
    provider_payment_id VARCHAR(255),
    
    -- Metadata
    metadata JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    
    INDEX idx_order_id (order_id),
    INDEX idx_status (status),
    INDEX idx_transaction_id (transaction_id)
);
```

---

### 15. Royalty (royalties)

Regalías para autores.

```sql
CREATE TABLE royalties (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id),
    order_item_id BIGINT NOT NULL REFERENCES order_items(id),
    book_id BIGINT NOT NULL REFERENCES books(id),
    
    sale_amount DECIMAL(10, 2) NOT NULL,
    royalty_percent DECIMAL(5, 2) NOT NULL,
    royalty_amount DECIMAL(10, 2) NOT NULL,
    
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, APPROVED, PAID
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    paid_at TIMESTAMP,
    
    INDEX idx_author_id (author_id),
    INDEX idx_book_id (book_id),
    INDEX idx_status (status)
);
```

---

### 16. Commission (commissions)

Comisiones para afiliados.

```sql
CREATE TABLE commissions (
    id BIGSERIAL PRIMARY KEY,
    affiliate_id BIGINT NOT NULL REFERENCES affiliate_profiles(id),
    order_id BIGINT NOT NULL REFERENCES orders(id),
    
    sale_amount DECIMAL(10, 2) NOT NULL,
    commission_percent DECIMAL(5, 2) NOT NULL,
    commission_amount DECIMAL(10, 2) NOT NULL,
    
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, APPROVED, PAID
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    paid_at TIMESTAMP,
    
    INDEX idx_affiliate_id (affiliate_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
);
```

---

### 17. AI Usage (ai_usage)

Registro de uso de IA.

```sql
CREATE TABLE ai_usage (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    
    operation VARCHAR(100) NOT NULL, -- GENERATE_IDEAS, EXTEND_TEXT, etc.
    tokens_used INT,
    
    -- Request/Response (opcional, para auditoría)
    request_data JSONB,
    response_data JSONB,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_operation (operation),
    INDEX idx_created_at (created_at)
);
```

---

### 18. Notification (notifications)

Notificaciones para usuarios.

```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    type VARCHAR(50) NOT NULL, -- NEW_SALE, NEW_REVIEW, PAYMENT_RECEIVED, etc.
    title VARCHAR(255) NOT NULL,
    message TEXT,
    
    link VARCHAR(500), -- URL de acción
    
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
);
```

---

### 19. Favorite (favorites)

Libros favoritos de lectores.

```sql
CREATE TABLE favorites (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    book_id BIGINT NOT NULL REFERENCES books(id) ON DELETE CASCADE,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id)
);
```

---

### 20. Digital Library (digital_library)

Biblioteca digital de lectores.

```sql
CREATE TABLE digital_library (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    book_id BIGINT NOT NULL REFERENCES books(id),
    order_item_id BIGINT REFERENCES order_items(id),
    
    download_count INT DEFAULT 0,
    last_downloaded_at TIMESTAMP,
    
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id)
);
```

---

## Índices Adicionales para Performance

```sql
-- Búsqueda de libros
CREATE INDEX idx_books_fulltext ON books USING GIN(to_tsvector('spanish', title || ' ' || COALESCE(synopsis, '')));

-- Estadísticas de ventas
CREATE INDEX idx_order_items_stats ON order_items(book_id, created_at);

-- Tracking de órdenes
CREATE INDEX idx_print_orders_tracking ON print_orders(tracking_number) WHERE tracking_number IS NOT NULL;

-- Performance de afiliados
CREATE INDEX idx_affiliate_performance ON affiliate_clicks(affiliate_link_id, clicked_at, converted);
```

---

## Vistas Útiles

### Vista: Sales Dashboard

```sql
CREATE VIEW v_sales_dashboard AS
SELECT 
    b.id as book_id,
    b.title,
    u.first_name || ' ' || u.last_name as author_name,
    COUNT(oi.id) as total_sales,
    SUM(oi.quantity) as total_copies,
    SUM(oi.subtotal) as total_revenue,
    SUM(r.royalty_amount) as total_royalties
FROM books b
JOIN users u ON b.author_id = u.id
LEFT JOIN order_items oi ON b.id = oi.book_id
LEFT JOIN royalties r ON b.id = r.book_id
WHERE b.status = 'PUBLISHED'
GROUP BY b.id, b.title, u.first_name, u.last_name;
```

### Vista: Affiliate Performance

```sql
CREATE VIEW v_affiliate_performance AS
SELECT 
    ap.id as affiliate_id,
    u.first_name || ' ' || u.last_name as affiliate_name,
    ap.affiliate_code,
    COUNT(DISTINCT al.id) as total_links,
    SUM(al.clicks) as total_clicks,
    SUM(al.sales) as total_sales,
    SUM(c.commission_amount) as total_commissions,
    CASE 
        WHEN SUM(al.clicks) > 0 
        THEN (SUM(al.sales)::DECIMAL / SUM(al.clicks) * 100)
        ELSE 0 
    END as conversion_rate
FROM affiliate_profiles ap
JOIN users u ON ap.user_id = u.id
LEFT JOIN affiliate_links al ON ap.id = al.affiliate_id
LEFT JOIN commissions c ON ap.id = c.affiliate_id
GROUP BY ap.id, u.first_name, u.last_name, ap.affiliate_code;
```

---

## Triggers

### Actualizar contador de reseñas

```sql
CREATE OR REPLACE FUNCTION update_book_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE books
    SET 
        rating_avg = (SELECT AVG(rating) FROM reviews WHERE book_id = NEW.book_id),
        rating_count = (SELECT COUNT(*) FROM reviews WHERE book_id = NEW.book_id)
    WHERE id = NEW.book_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_book_rating
AFTER INSERT OR UPDATE ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_book_rating();
```

### Generar número de orden

```sql
CREATE OR REPLACE FUNCTION generate_order_number()
RETURNS TRIGGER AS $$
BEGIN
    NEW.order_number = 'DP-' || TO_CHAR(NOW(), 'YYYY') || '-' || LPAD(nextval('order_number_seq')::TEXT, 6, '0');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE SEQUENCE order_number_seq;

CREATE TRIGGER trg_generate_order_number
BEFORE INSERT ON orders
FOR EACH ROW
EXECUTE FUNCTION generate_order_number();
```

---

## Relaciones Resumen

```
USER
├── has many BOOKS (as author)
├── has one WRITER_PROFILE
├── has one AFFILIATE_PROFILE
├── has one PRINTER_PROFILE
├── has many ORDERS
├── has many REVIEWS
├── has many FAVORITES
└── has many AI_USAGE

BOOK
├── belongs to USER (author)
├── belongs to CATEGORY
├── has many BOOK_FILES
├── has many REVIEWS
├── has many ORDER_ITEMS
└── has many FAVORITES

ORDER
├── belongs to USER
├── has many ORDER_ITEMS
├── has one PAYMENT
└── may belong to AFFILIATE_PROFILE

AFFILIATE_PROFILE
├── has many AFFILIATE_LINKS
└── has many COMMISSIONS
```

---

## Próximos Pasos

1. Implementar esquema en PostgreSQL
2. Crear seeders con datos de prueba
3. Implementar JPA entities en Java
4. Crear repositorios Spring Data
5. Implementar servicios de negocio
6. Testing de integridad referencial
7. Optimización de queries
8. Backup y restore procedures
