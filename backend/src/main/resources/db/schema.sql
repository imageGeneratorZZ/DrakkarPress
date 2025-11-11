-- ============================================================================
-- DRAKKARPRESS - ESQUEMA DE BASE DE DATOS COMPLETO
-- ============================================================================
-- Versión: 2.0
-- Fecha: 11 de Noviembre, 2025
-- Sistema: PostgreSQL 14+
-- ============================================================================

-- Extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- ENUMS Y TIPOS PERSONALIZADOS
-- ============================================================================

-- Tipo de plan de membresía
CREATE TYPE membership_plan AS ENUM (
    'FREE',
    'PREMIUM_PHASE_1',     -- $5/mes - Fundadores (1-1000)
    'PREMIUM_PHASE_2',     -- $10/mes - Early Adopters (1001-10000)
    'PREMIUM_PHASE_3',     -- $19.99/mes - Regular (10001+)
    'PREMIUM_COURTESY'     -- Gratis - Otorgado por admin
);

-- Estado de membresía
CREATE TYPE membership_status AS ENUM (
    'ACTIVE',
    'EXPIRED',
    'CANCELLED',
    'SUSPENDED'
);

-- Frecuencia de pago
CREATE TYPE payment_frequency AS ENUM (
    'MONTHLY',
    'ANNUAL',
    'LIFETIME'
);

-- Roles de usuario
CREATE TYPE user_role_type AS ENUM (
    'CLIENT',           -- Base (obligatorio para todos)
    'AUTHOR_PUBLISHER', -- Autor individual o Editorial
    'PRINT_SHOP',       -- Imprenta
    'RESELLER'          -- Revendedor
);

-- Estado de verificación
CREATE TYPE verification_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED',
    'EXPIRED'
);

-- Tipo de entidad (para AUTHOR_PUBLISHER)
CREATE TYPE entity_type AS ENUM (
    'INDIVIDUAL',  -- Persona (autor individual)
    'COMPANY'      -- Empresa (editorial)
);

-- Estado de badge
CREATE TYPE badge_status AS ENUM (
    'ACTIVE',
    'INACTIVE',
    'REVOKED'
);

-- Tipo de uso de IA
CREATE TYPE ai_usage_type AS ENUM (
    'FULL_BOOK_GENERATION',
    'COVER_GENERATION',
    'WRITING_ASSISTANT',
    'TEXT_CORRECTION',
    'SERIES_GENERATION',
    'TRANSLATION'
);

-- Tipo de conexión entre usuarios
CREATE TYPE connection_status AS ENUM (
    'PENDING',
    'ACCEPTED',
    'BLOCKED'
);

-- ============================================================================
-- TABLA: users (Tabla base de usuarios)
-- ============================================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Identificación básica
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    
    -- Información personal
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    display_name VARCHAR(150),
    bio TEXT,
    avatar_url VARCHAR(500),
    
    -- Configuración
    email_verified BOOLEAN DEFAULT FALSE,
    phone_number VARCHAR(20),
    phone_verified BOOLEAN DEFAULT FALSE,
    language_preference VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    
    -- Seguridad
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_secret VARCHAR(100),
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_login_ip INET,
    
    -- Control
    is_active BOOLEAN DEFAULT TRUE,
    is_admin BOOLEAN DEFAULT FALSE,
    is_suspended BOOLEAN DEFAULT FALSE,
    suspension_reason TEXT,
    
    -- Número de usuario (para tracking de fases)
    user_number SERIAL UNIQUE NOT NULL,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE,
    
    -- Índices
    CONSTRAINT email_lowercase CHECK (email = LOWER(email)),
    CONSTRAINT username_lowercase CHECK (username = LOWER(username))
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_user_number ON users(user_number);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_is_active ON users(is_active);

-- ============================================================================
-- TABLA: runes (24 runas del Elder Futhark)
-- ============================================================================

CREATE TABLE runes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Identificación
    symbol VARCHAR(10) UNIQUE NOT NULL,  -- ej: 'ᚲ'
    name VARCHAR(50) UNIQUE NOT NULL,     -- ej: 'Kenaz'
    
    -- Información
    meaning_es TEXT NOT NULL,             -- Significado en español
    meaning_en TEXT NOT NULL,             -- Significado en inglés
    category VARCHAR(50) NOT NULL,        -- ej: 'CREATIVITY_KNOWLEDGE'
    description_es TEXT,                  -- Descripción extendida
    description_en TEXT,
    
    -- Popularidad
    times_selected INTEGER DEFAULT 0,
    
    -- Control
    is_active BOOLEAN DEFAULT TRUE,
    display_order INTEGER,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_runes_category ON runes(category);
CREATE INDEX idx_runes_is_active ON runes(is_active);

-- ============================================================================
-- TABLA: badges (Tipos de badges del sistema)
-- ============================================================================

CREATE TABLE badges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Identificación
    code VARCHAR(50) UNIQUE NOT NULL,     -- ej: 'FOUNDER', 'EARLY_ADOPTER'
    name_es VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    
    -- Visualización
    icon VARCHAR(50),                     -- ej: '🏆', '⭐', '✨'
    rune_id UUID REFERENCES runes(id),   -- Runa asociada al badge
    color_hex VARCHAR(7),                 -- Color del badge
    
    -- Descripción
    description_es TEXT,
    description_en TEXT,
    
    -- Criterios de obtención
    auto_assign BOOLEAN DEFAULT FALSE,    -- Se asigna automáticamente
    requires_premium BOOLEAN DEFAULT FALSE,
    
    -- Control
    is_active BOOLEAN DEFAULT TRUE,
    display_order INTEGER,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_badges_code ON badges(code);
CREATE INDEX idx_badges_is_active ON badges(is_active);

-- ============================================================================
-- TABLA: memberships (Membresías de usuario)
-- ============================================================================

CREATE TABLE memberships (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Plan
    plan membership_plan NOT NULL DEFAULT 'FREE',
    status membership_status NOT NULL DEFAULT 'ACTIVE',
    payment_frequency payment_frequency,
    
    -- Pricing
    price_usd DECIMAL(10,2),              -- Precio en USD
    is_grandfathered BOOLEAN DEFAULT FALSE, -- Precio bloqueado de por vida
    
    -- Cortesía (si aplica)
    is_courtesy BOOLEAN DEFAULT FALSE,
    courtesy_reason TEXT,                  -- Razón del premium cortesía
    courtesy_granted_by UUID REFERENCES users(id), -- Admin que lo otorgó
    courtesy_granted_at TIMESTAMP WITH TIME ZONE,
    
    -- Fechas
    started_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE,   -- NULL = permanente
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    
    -- Pagos externos
    stripe_subscription_id VARCHAR(100),
    paypal_subscription_id VARCHAR(100),
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT one_active_membership UNIQUE(user_id, status) WHERE status = 'ACTIVE'
);

CREATE INDEX idx_memberships_user_id ON memberships(user_id);
CREATE INDEX idx_memberships_status ON memberships(status);
CREATE INDEX idx_memberships_plan ON memberships(plan);
CREATE INDEX idx_memberships_expires_at ON memberships(expires_at);

-- ============================================================================
-- TABLA: user_runes (Runa seleccionada por usuario Premium)
-- ============================================================================

CREATE TABLE user_runes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rune_id UUID NOT NULL REFERENCES runes(id),
    
    -- Control de cambios
    selected_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    can_change_after TIMESTAMP WITH TIME ZONE NOT NULL, -- 1 mes después
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint: solo una runa activa por usuario
    CONSTRAINT one_active_rune UNIQUE(user_id)
);

CREATE INDEX idx_user_runes_user_id ON user_runes(user_id);
CREATE INDEX idx_user_runes_rune_id ON user_runes(rune_id);

-- ============================================================================
-- TABLA: user_badges (Badges asignados a usuarios)
-- ============================================================================

CREATE TABLE user_badges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    badge_id UUID NOT NULL REFERENCES badges(id),
    
    -- Estado
    status badge_status NOT NULL DEFAULT 'ACTIVE',
    
    -- Razón (si es otorgado manualmente)
    granted_reason TEXT,
    granted_by UUID REFERENCES users(id), -- Admin que lo otorgó
    
    -- Fechas
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked_at TIMESTAMP WITH TIME ZONE,
    revoked_reason TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint: no duplicar badges activos
    CONSTRAINT unique_active_badge UNIQUE(user_id, badge_id) WHERE status = 'ACTIVE'
);

CREATE INDEX idx_user_badges_user_id ON user_badges(user_id);
CREATE INDEX idx_user_badges_badge_id ON user_badges(badge_id);
CREATE INDEX idx_user_badges_status ON user_badges(status);

-- ============================================================================
-- TABLA: user_roles (Roles activados por usuario)
-- ============================================================================

CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Rol
    role_type user_role_type NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Tipo de entidad (solo para AUTHOR_PUBLISHER)
    entity_type entity_type,              -- INDIVIDUAL o COMPANY
    company_name VARCHAR(200),             -- Si es COMPANY
    company_logo_url VARCHAR(500),
    tax_id VARCHAR(100),                   -- RFC/NIT/Tax ID
    
    -- Información de contacto profesional
    professional_email VARCHAR(255),
    professional_phone VARCHAR(20),
    website_url VARCHAR(500),
    
    -- Dirección fiscal
    billing_address_line1 VARCHAR(255),
    billing_address_line2 VARCHAR(255),
    billing_city VARCHAR(100),
    billing_state VARCHAR(100),
    billing_country VARCHAR(100),
    billing_postal_code VARCHAR(20),
    
    -- Fechas
    activated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deactivated_at TIMESTAMP WITH TIME ZONE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint: un usuario puede tener múltiples roles pero no duplicados
    CONSTRAINT unique_user_role UNIQUE(user_id, role_type)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_type ON user_roles(role_type);
CREATE INDEX idx_user_roles_is_active ON user_roles(is_active);

-- ============================================================================
-- TABLA: role_verification (Verificación de roles que venden/ofrecen servicios)
-- ============================================================================

CREATE TABLE role_verification (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_role_id UUID NOT NULL REFERENCES user_roles(id) ON DELETE CASCADE,
    
    -- Estado de verificación
    status verification_status NOT NULL DEFAULT 'PENDING',
    
    -- Documentos requeridos (URLs a S3/Cloudinary)
    payment_info_document_url VARCHAR(500),
    tax_document_url VARCHAR(500),
    business_license_url VARCHAR(500),      -- Para PRINT_SHOP
    quality_certificates_url VARCHAR(500)[],-- Para PRINT_SHOP (array)
    sample_works_url VARCHAR(500)[],        -- Para PRINT_SHOP (array)
    
    -- Información de pago
    payment_method VARCHAR(50),             -- 'PAYPAL', 'STRIPE', 'BANK_TRANSFER'
    paypal_email VARCHAR(255),
    stripe_account_id VARCHAR(100),
    bank_account_number VARCHAR(100),       -- Encriptado
    bank_name VARCHAR(100),
    
    -- Para PRINT_SHOP
    production_capacity TEXT,
    certifications TEXT[],                  -- Array de certificaciones
    insurance_info TEXT,
    
    -- Para RESELLER
    commission_model VARCHAR(50),           -- Porcentaje o fixed
    preferred_commission_rate DECIMAL(5,2), -- ej: 15.00 (%)
    operating_region VARCHAR(100),
    estimated_monthly_volume INTEGER,
    
    -- Revisión
    reviewed_by UUID REFERENCES users(id),  -- Admin que revisó
    reviewed_at TIMESTAMP WITH TIME ZONE,
    rejection_reason TEXT,
    
    -- Fechas
    submitted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP WITH TIME ZONE,
    expires_at TIMESTAMP WITH TIME ZONE,    -- Verificación puede expirar
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_role_verification_user_role_id ON role_verification(user_role_id);
CREATE INDEX idx_role_verification_status ON role_verification(status);

-- ============================================================================
-- TABLA: ai_usage_limits (Límites de uso de IA por plan)
-- ============================================================================

CREATE TABLE ai_usage_limits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Plan al que aplica
    plan membership_plan NOT NULL UNIQUE,
    
    -- Límites mensuales (NULL = ilimitado)
    full_book_generation_limit INTEGER,    -- FREE: 0, PREMIUM: NULL (ilimitado)
    cover_generation_limit INTEGER,        -- FREE: 3, PREMIUM: NULL
    writing_assistant_limit INTEGER,       -- FREE: 10, PREMIUM: NULL
    text_correction_limit INTEGER,         -- FREE: 5, PREMIUM: NULL
    series_generation_limit INTEGER,       -- FREE: 0, PREMIUM: NULL
    translation_limit INTEGER,             -- FREE: 0, PREMIUM: NULL
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- TABLA: ai_usage_tracking (Tracking de uso de IA por usuario)
-- ============================================================================

CREATE TABLE ai_usage_tracking (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Tipo de uso
    usage_type ai_usage_type NOT NULL,
    
    -- Contexto
    usage_metadata JSONB,                  -- Info adicional (género, idioma, etc)
    
    -- Resultado
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    
    -- Costos (opcional para analytics)
    tokens_used INTEGER,
    cost_usd DECIMAL(10,4),
    
    -- Timestamp
    used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_usage_user_id ON ai_usage_tracking(user_id);
CREATE INDEX idx_ai_usage_type ON ai_usage_tracking(usage_type);
CREATE INDEX idx_ai_usage_used_at ON ai_usage_tracking(used_at);

-- ============================================================================
-- TABLA: ai_usage_monthly_summary (Resumen mensual de uso por usuario)
-- ============================================================================

CREATE TABLE ai_usage_monthly_summary (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Período
    year INTEGER NOT NULL,
    month INTEGER NOT NULL,                -- 1-12
    
    -- Contadores
    full_book_generation_count INTEGER DEFAULT 0,
    cover_generation_count INTEGER DEFAULT 0,
    writing_assistant_count INTEGER DEFAULT 0,
    text_correction_count INTEGER DEFAULT 0,
    series_generation_count INTEGER DEFAULT 0,
    translation_count INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint: una fila por usuario por mes
    CONSTRAINT unique_user_month UNIQUE(user_id, year, month)
);

CREATE INDEX idx_ai_monthly_user_id ON ai_usage_monthly_summary(user_id);
CREATE INDEX idx_ai_monthly_period ON ai_usage_monthly_summary(year, month);

-- ============================================================================
-- TABLA: connections (Conexiones entre usuarios - Red social)
-- ============================================================================

CREATE TABLE connections (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Usuarios
    requester_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    addressee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Estado
    status connection_status NOT NULL DEFAULT 'PENDING',
    
    -- Fechas
    requested_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP WITH TIME ZONE,
    blocked_at TIMESTAMP WITH TIME ZONE,
    block_reason TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT different_users CHECK (requester_id != addressee_id),
    CONSTRAINT unique_connection UNIQUE(requester_id, addressee_id)
);

CREATE INDEX idx_connections_requester ON connections(requester_id);
CREATE INDEX idx_connections_addressee ON connections(addressee_id);
CREATE INDEX idx_connections_status ON connections(status);

-- ============================================================================
-- TABLA: user_activity_feed (Feed de actividad de usuarios)
-- ============================================================================

CREATE TABLE user_activity_feed (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Tipo de actividad
    activity_type VARCHAR(50) NOT NULL,    -- 'PUBLISHED_BOOK', 'NEW_CONNECTION', etc
    
    -- Contenido
    title VARCHAR(255) NOT NULL,
    description TEXT,
    metadata JSONB,                        -- Info adicional en formato JSON
    
    -- URLs relacionadas
    activity_url VARCHAR(500),             -- Link a la actividad
    thumbnail_url VARCHAR(500),
    
    -- Visibilidad
    is_public BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_activity_feed_user_id ON user_activity_feed(user_id);
CREATE INDEX idx_activity_feed_created_at ON user_activity_feed(created_at);
CREATE INDEX idx_activity_feed_activity_type ON user_activity_feed(activity_type);
CREATE INDEX idx_activity_feed_is_public ON user_activity_feed(is_public);

-- ============================================================================
-- TABLA: messages (Mensajería interna entre usuarios)
-- ============================================================================

CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Usuarios
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    recipient_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Contenido
    subject VARCHAR(255),
    body TEXT NOT NULL,
    
    -- Adjuntos (opcional)
    attachments_urls VARCHAR(500)[],
    
    -- Estado
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    is_deleted_by_sender BOOLEAN DEFAULT FALSE,
    is_deleted_by_recipient BOOLEAN DEFAULT FALSE,
    
    -- Reply chain
    parent_message_id UUID REFERENCES messages(id),
    
    -- Timestamps
    sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT different_users_msg CHECK (sender_id != recipient_id)
);

CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_messages_recipient ON messages(recipient_id);
CREATE INDEX idx_messages_is_read ON messages(is_read);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);

-- ============================================================================
-- TABLA: payment_transactions (Transacciones de pago)
-- ============================================================================

CREATE TABLE payment_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    membership_id UUID REFERENCES memberships(id),
    
    -- Monto
    amount_usd DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    
    -- Proveedor de pago
    payment_provider VARCHAR(50) NOT NULL, -- 'STRIPE', 'PAYPAL'
    transaction_id VARCHAR(200) UNIQUE,    -- ID del proveedor externo
    
    -- Estado
    status VARCHAR(50) NOT NULL,           -- 'PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'
    
    -- Metadata
    payment_method VARCHAR(50),            -- 'CARD', 'PAYPAL', etc
    description TEXT,
    metadata JSONB,
    
    -- Timestamps
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_transactions_user_id ON payment_transactions(user_id);
CREATE INDEX idx_transactions_status ON payment_transactions(status);
CREATE INDEX idx_transactions_processed_at ON payment_transactions(processed_at);

-- ============================================================================
-- TABLA: admin_audit_log (Log de acciones administrativas)
-- ============================================================================

CREATE TABLE admin_audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    
    -- Admin que realizó la acción
    admin_id UUID NOT NULL REFERENCES users(id),
    
    -- Acción
    action_type VARCHAR(100) NOT NULL,     -- 'GRANT_PREMIUM', 'CHANGE_PLAN', etc
    target_user_id UUID REFERENCES users(id),
    
    -- Detalles
    description TEXT NOT NULL,
    metadata JSONB,                        -- Info adicional
    
    -- IP y contexto
    ip_address INET,
    user_agent TEXT,
    
    -- Timestamp
    performed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_log_admin_id ON admin_audit_log(admin_id);
CREATE INDEX idx_audit_log_target_user_id ON admin_audit_log(target_user_id);
CREATE INDEX idx_audit_log_action_type ON admin_audit_log(action_type);
CREATE INDEX idx_audit_log_performed_at ON admin_audit_log(performed_at);

-- ============================================================================
-- TABLA: session_tokens (Tokens JWT y refresh tokens)
-- ============================================================================

CREATE TABLE session_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Token
    refresh_token VARCHAR(500) UNIQUE NOT NULL,
    access_token_jti VARCHAR(100) UNIQUE,  -- JTI del JWT
    
    -- Metadata
    device_info TEXT,
    ip_address INET,
    user_agent TEXT,
    
    -- Expiración
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    
    -- Estado
    is_revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP WITH TIME ZONE,
    revoked_reason TEXT,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_session_tokens_user_id ON session_tokens(user_id);
CREATE INDEX idx_session_tokens_refresh_token ON session_tokens(refresh_token);
CREATE INDEX idx_session_tokens_expires_at ON session_tokens(expires_at);
CREATE INDEX idx_session_tokens_is_revoked ON session_tokens(is_revoked);

-- ============================================================================
-- FUNCIONES Y TRIGGERS
-- ============================================================================

-- Función: Actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger a todas las tablas con updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_runes_updated_at BEFORE UPDATE ON runes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_badges_updated_at BEFORE UPDATE ON badges
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_memberships_updated_at BEFORE UPDATE ON memberships
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_badges_updated_at BEFORE UPDATE ON user_badges
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_roles_updated_at BEFORE UPDATE ON user_roles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_role_verification_updated_at BEFORE UPDATE ON role_verification
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ai_usage_limits_updated_at BEFORE UPDATE ON ai_usage_limits
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_ai_monthly_summary_updated_at BEFORE UPDATE ON ai_usage_monthly_summary
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_connections_updated_at BEFORE UPDATE ON connections
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Función: Auto-asignar badge "Fundador" o "Early Adopter" según user_number
CREATE OR REPLACE FUNCTION auto_assign_badge_on_membership()
RETURNS TRIGGER AS $$
DECLARE
    v_user_number INTEGER;
    v_badge_id UUID;
BEGIN
    -- Solo para nuevas membresías Premium
    IF NEW.plan IN ('PREMIUM_PHASE_1', 'PREMIUM_PHASE_2', 'PREMIUM_PHASE_3') 
       AND TG_OP = 'INSERT' THEN
        
        -- Obtener user_number
        SELECT user_number INTO v_user_number
        FROM users WHERE id = NEW.user_id;
        
        -- Asignar badge según fase
        IF v_user_number <= 1000 THEN
            -- Badge Fundador
            SELECT id INTO v_badge_id FROM badges WHERE code = 'FOUNDER';
        ELSIF v_user_number <= 10000 THEN
            -- Badge Early Adopter
            SELECT id INTO v_badge_id FROM badges WHERE code = 'EARLY_ADOPTER';
        ELSE
            -- Badge Premium regular (opcional)
            SELECT id INTO v_badge_id FROM badges WHERE code = 'PREMIUM';
        END IF;
        
        -- Insertar badge si existe y no está ya asignado
        IF v_badge_id IS NOT NULL THEN
            INSERT INTO user_badges (user_id, badge_id, status)
            VALUES (NEW.user_id, v_badge_id, 'ACTIVE')
            ON CONFLICT (user_id, badge_id) WHERE status = 'ACTIVE'
            DO NOTHING;
        END IF;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER assign_badge_on_membership AFTER INSERT ON memberships
    FOR EACH ROW EXECUTE FUNCTION auto_assign_badge_on_membership();

-- Función: Incrementar contador de veces que se selecciona una runa
CREATE OR REPLACE FUNCTION increment_rune_selection_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE runes 
    SET times_selected = times_selected + 1 
    WHERE id = NEW.rune_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER increment_rune_count AFTER INSERT ON user_runes
    FOR EACH ROW EXECUTE FUNCTION increment_rune_selection_count();

-- ============================================================================
-- VISTAS ÚTILES
-- ============================================================================

-- Vista: Perfil completo de usuario con todos los datos
CREATE OR REPLACE VIEW v_user_full_profile AS
SELECT 
    u.id,
    u.email,
    u.username,
    u.first_name,
    u.last_name,
    u.display_name,
    u.bio,
    u.avatar_url,
    u.user_number,
    u.is_active,
    u.created_at,
    
    -- Membresía actual
    m.plan AS current_plan,
    m.status AS membership_status,
    m.is_grandfathered,
    m.price_usd,
    m.is_courtesy,
    
    -- Runa seleccionada
    r.symbol AS rune_symbol,
    r.name AS rune_name,
    
    -- Badges (array)
    ARRAY_AGG(DISTINCT b.code) FILTER (WHERE ub.status = 'ACTIVE') AS active_badges,
    
    -- Roles activos (array)
    ARRAY_AGG(DISTINCT ur.role_type) FILTER (WHERE ur.is_active = TRUE) AS active_roles
    
FROM users u
LEFT JOIN memberships m ON u.id = m.user_id AND m.status = 'ACTIVE'
LEFT JOIN user_runes urune ON u.id = urune.user_id
LEFT JOIN runes r ON urune.rune_id = r.id
LEFT JOIN user_badges ub ON u.id = ub.user_id
LEFT JOIN badges b ON ub.badge_id = b.id
LEFT JOIN user_roles ur ON u.id = ur.user_id
GROUP BY u.id, m.plan, m.status, m.is_grandfathered, m.price_usd, m.is_courtesy,
         r.symbol, r.name;

-- Vista: Estadísticas de uso de IA por usuario (mes actual)
CREATE OR REPLACE VIEW v_user_ai_usage_current_month AS
SELECT 
    user_id,
    full_book_generation_count,
    cover_generation_count,
    writing_assistant_count,
    text_correction_count,
    series_generation_count,
    translation_count
FROM ai_usage_monthly_summary
WHERE year = EXTRACT(YEAR FROM CURRENT_DATE)
  AND month = EXTRACT(MONTH FROM CURRENT_DATE);

-- ============================================================================
-- COMENTARIOS EN TABLAS
-- ============================================================================

COMMENT ON TABLE users IS 'Tabla base de usuarios del sistema';
COMMENT ON TABLE runes IS '24 runas del Elder Futhark para personalización Premium';
COMMENT ON TABLE badges IS 'Tipos de badges disponibles en el sistema';
COMMENT ON TABLE memberships IS 'Historial de membresías de usuarios';
COMMENT ON TABLE user_runes IS 'Runa seleccionada por cada usuario Premium';
COMMENT ON TABLE user_badges IS 'Badges asignados a usuarios';
COMMENT ON TABLE user_roles IS 'Roles activados por cada usuario (multi-rol)';
COMMENT ON TABLE role_verification IS 'Verificación de documentos para roles que venden';
COMMENT ON TABLE ai_usage_limits IS 'Límites de uso de IA por tipo de plan';
COMMENT ON TABLE ai_usage_tracking IS 'Tracking detallado de cada uso de IA';
COMMENT ON TABLE ai_usage_monthly_summary IS 'Resumen mensual de uso de IA por usuario';
COMMENT ON TABLE connections IS 'Conexiones entre usuarios (red social)';
COMMENT ON TABLE user_activity_feed IS 'Feed de actividad pública de usuarios';
COMMENT ON TABLE messages IS 'Mensajería interna entre usuarios';
COMMENT ON TABLE payment_transactions IS 'Historial de transacciones de pago';
COMMENT ON TABLE admin_audit_log IS 'Log de auditoría de acciones administrativas';
COMMENT ON TABLE session_tokens IS 'Tokens de sesión JWT y refresh tokens';

-- ============================================================================
-- FIN DEL ESQUEMA
-- ============================================================================
