-- DrakkarPress Database Initialization Script
-- PostgreSQL version 12+

-- Note: Database is already created by docker-compose
-- This script runs inside the drakkarpress database

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create custom types
CREATE TYPE user_role AS ENUM ('READER', 'AUTHOR', 'PRINTER', 'RESELLER', 'ADMIN');
CREATE TYPE book_status AS ENUM ('DRAFT', 'PUBLISHED', 'UNPUBLISHED');
CREATE TYPE sale_status AS ENUM ('PENDING', 'COMPLETED', 'REFUNDED', 'CANCELLED');
CREATE TYPE payment_method AS ENUM ('CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'STRIPE', 'BANK_TRANSFER');

-- Users table (will be created by JPA, but we can add indexes)
-- CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
-- CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
-- CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Books table indexes
-- CREATE INDEX IF NOT EXISTS idx_books_author_id ON books(author_id);
-- CREATE INDEX IF NOT EXISTS idx_books_genre ON books(genre);
-- CREATE INDEX IF NOT EXISTS idx_books_published ON books(published);
-- CREATE INDEX IF NOT EXISTS idx_books_created_at ON books(created_at);

-- Sales table indexes
-- CREATE INDEX IF NOT EXISTS idx_sales_user_id ON sales(user_id);
-- CREATE INDEX IF NOT EXISTS idx_sales_book_id ON sales(book_id);
-- CREATE INDEX IF NOT EXISTS idx_sales_reseller_id ON sales(reseller_id);
-- CREATE INDEX IF NOT EXISTS idx_sales_created_at ON sales(created_at);

-- Insert default admin user (password will be 'admin123' - CHANGE IN PRODUCTION)
-- The password hash is for 'admin123' using BCrypt
INSERT INTO users (username, email, password, role, email_verified, active, created_at, updated_at)
VALUES (
    'admin',
    'admin@drakkarpress.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
    'ADMIN',
    true,
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (email) DO NOTHING;

-- Insert sample genres
CREATE TABLE IF NOT EXISTS genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO genres (name, description) VALUES
    ('Ficción', 'Narrativa de ficción'),
    ('No Ficción', 'Libros basados en hechos reales'),
    ('Romance', 'Historias de amor'),
    ('Thriller', 'Novelas de suspense'),
    ('Ciencia Ficción', 'Ficción especulativa sobre ciencia y tecnología'),
    ('Fantasía', 'Mundos imaginarios y magia'),
    ('Misterio', 'Novelas policíacas y de detectives'),
    ('Terror', 'Historias de horror'),
    ('Histórica', 'Novelas ambientadas en el pasado'),
    ('Biografía', 'Historias de vida real'),
    ('Autoayuda', 'Desarrollo personal'),
    ('Negocios', 'Libros sobre emprendimiento y negocios'),
    ('Tecnología', 'Libros sobre tecnología e informática'),
    ('Cocina', 'Recetas y gastronomía'),
    ('Viajes', 'Guías y relatos de viajes')
ON CONFLICT (name) DO NOTHING;

-- Create view for sales statistics
CREATE OR REPLACE VIEW sales_statistics AS
SELECT 
    b.id as book_id,
    b.title,
    COUNT(s.id) as total_sales,
    SUM(s.amount) as total_revenue,
    AVG(s.amount) as average_price,
    MAX(s.created_at) as last_sale_date
FROM books b
LEFT JOIN sales s ON b.id = s.book_id
WHERE s.status = 'COMPLETED'
GROUP BY b.id, b.title;

-- Create view for user statistics
CREATE OR REPLACE VIEW user_statistics AS
SELECT 
    u.id as user_id,
    u.username,
    u.email,
    u.role,
    COUNT(DISTINCT b.id) as books_published,
    COUNT(DISTINCT s.id) as total_sales,
    SUM(s.amount) as total_earnings
FROM users u
LEFT JOIN books b ON u.id = b.author_id AND b.published = true
LEFT JOIN sales s ON b.id = s.book_id AND s.status = 'COMPLETED'
WHERE u.role = 'AUTHOR'
GROUP BY u.id, u.username, u.email, u.role;

-- Create function to update timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Success message
DO $$
BEGIN
    RAISE NOTICE 'DrakkarPress database initialized successfully!';
    RAISE NOTICE 'Default admin user created:';
    RAISE NOTICE '  - Username: admin';
    RAISE NOTICE '  - Email: admin@drakkarpress.com';
    RAISE NOTICE '  - Password: admin123 (CHANGE THIS IN PRODUCTION!)';
END $$;
