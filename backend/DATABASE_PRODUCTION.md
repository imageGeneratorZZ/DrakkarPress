# 🗄️ Configuración de Base de Datos - Producción

## 📊 PostgreSQL para DrakkarPress

### 🎯 Información de Conexión

**Base de Datos**: `drakkarpress_prod`  
**Usuario**: `drakkarpress_user`  
**Puerto**: `5432` (default PostgreSQL)  
**Encoding**: UTF8  
**Timezone**: UTC  

---

## 🚀 Script de Creación

```sql
-- ==========================================
-- DRAKKARPRESS PRODUCTION DATABASE SETUP
-- PostgreSQL 14+
-- ==========================================

-- 1. CREAR BASE DE DATOS Y USUARIO
CREATE DATABASE drakkarpress_prod
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = 100;

-- Crear usuario de aplicación
CREATE USER drakkarpress_user WITH PASSWORD 'CAMBIAR_EN_PRODUCCION_12345';

-- Otorgar privilegios
GRANT ALL PRIVILEGES ON DATABASE drakkarpress_prod TO drakkarpress_user;

-- Conectar a la base de datos
\c drakkarpress_prod;

-- Otorgar privilegios en schema public
GRANT ALL ON SCHEMA public TO drakkarpress_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO drakkarpress_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO drakkarpress_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO drakkarpress_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO drakkarpress_user;

-- 2. EXTENSIONES
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";       -- Para UUID
CREATE EXTENSION IF NOT EXISTS "pgcrypto";        -- Para encriptación
CREATE EXTENSION IF NOT EXISTS "pg_trgm";         -- Para búsqueda de texto
CREATE EXTENSION IF NOT EXISTS "unaccent";        -- Para búsqueda sin acentos

-- 3. FUNCIONES AUXILIARES

-- Función para actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Función para generar user_number secuencial
CREATE SEQUENCE IF NOT EXISTS user_number_seq START WITH 1;

-- Función para obtener siguiente user_number
CREATE OR REPLACE FUNCTION get_next_user_number()
RETURNS BIGINT AS $$
BEGIN
    RETURN nextval('user_number_seq');
END;
$$ LANGUAGE plpgsql;

-- 4. TRIGGERS PARA updated_at

-- Se crearán después de que JPA cree las tablas
-- Ejemplo:
-- CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
-- FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- 5. ÍNDICES ADICIONALES (complementan los de JPA)

-- Índices para búsqueda full-text
CREATE INDEX IF NOT EXISTS idx_users_email_trgm ON users USING gin(email gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_users_username_trgm ON users USING gin(username gin_trgm_ops);

-- Índices para queries comunes
CREATE INDEX IF NOT EXISTS idx_users_user_number ON users(user_number);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_users_is_active ON users(is_active) WHERE is_active = true;

-- Índices para memberships
CREATE INDEX IF NOT EXISTS idx_memberships_user_id ON memberships(user_id);
CREATE INDEX IF NOT EXISTS idx_memberships_expires_at ON memberships(expires_at);
CREATE INDEX IF NOT EXISTS idx_memberships_is_active ON memberships(is_active) WHERE is_active = true;

-- Índices para session_tokens
CREATE INDEX IF NOT EXISTS idx_session_tokens_refresh_token ON session_tokens(refresh_token);
CREATE INDEX IF NOT EXISTS idx_session_tokens_user_id ON session_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_session_tokens_expires_at ON session_tokens(expires_at);
CREATE INDEX IF NOT EXISTS idx_session_tokens_is_active ON session_tokens(is_active) WHERE is_active = true;

-- Índices para payment_transactions
CREATE INDEX IF NOT EXISTS idx_payment_transactions_user_id ON payment_transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_stripe_payment_intent_id ON payment_transactions(stripe_payment_intent_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_created_at ON payment_transactions(created_at DESC);

-- 6. DATOS INICIALES

-- Insertar runas (24 runas del Futhark Elder)
INSERT INTO runes (name, symbol, meaning, image_url, tier, created_at) VALUES
('Fehu', 'ᚠ', 'Riqueza y prosperidad', '/images/runes/fehu.png', 1, CURRENT_TIMESTAMP),
('Uruz', 'ᚢ', 'Fuerza y vitalidad', '/images/runes/uruz.png', 1, CURRENT_TIMESTAMP),
('Thurisaz', 'ᚦ', 'Protección y defensa', '/images/runes/thurisaz.png', 1, CURRENT_TIMESTAMP),
('Ansuz', 'ᚨ', 'Comunicación y sabiduría', '/images/runes/ansuz.png', 1, CURRENT_TIMESTAMP),
('Raidho', 'ᚱ', 'Viaje y movimiento', '/images/runes/raidho.png', 1, CURRENT_TIMESTAMP),
('Kenaz', 'ᚲ', 'Conocimiento y creatividad', '/images/runes/kenaz.png', 2, CURRENT_TIMESTAMP),
('Gebo', 'ᚷ', 'Generosidad y equilibrio', '/images/runes/gebo.png', 2, CURRENT_TIMESTAMP),
('Wunjo', 'ᚹ', 'Alegría y armonía', '/images/runes/wunjo.png', 2, CURRENT_TIMESTAMP),
('Hagalaz', 'ᚺ', 'Transformación', '/images/runes/hagalaz.png', 2, CURRENT_TIMESTAMP),
('Nauthiz', 'ᚾ', 'Necesidad y resistencia', '/images/runes/nauthiz.png', 2, CURRENT_TIMESTAMP),
('Isa', 'ᛁ', 'Paciencia y concentración', '/images/runes/isa.png', 3, CURRENT_TIMESTAMP),
('Jera', 'ᛃ', 'Cosecha y ciclos', '/images/runes/jera.png', 3, CURRENT_TIMESTAMP),
('Eihwaz', 'ᛇ', 'Defensa y protección', '/images/runes/eihwaz.png', 3, CURRENT_TIMESTAMP),
('Perthro', 'ᛈ', 'Misterio y destino', '/images/runes/perthro.png', 3, CURRENT_TIMESTAMP),
('Algiz', 'ᛉ', 'Protección divina', '/images/runes/algiz.png', 3, CURRENT_TIMESTAMP),
('Sowilo', 'ᛋ', 'Éxito y victoria', '/images/runes/sowilo.png', 4, CURRENT_TIMESTAMP),
('Tiwaz', 'ᛏ', 'Honor y justicia', '/images/runes/tiwaz.png', 4, CURRENT_TIMESTAMP),
('Berkano', 'ᛒ', 'Crecimiento y fertilidad', '/images/runes/berkano.png', 4, CURRENT_TIMESTAMP),
('Ehwaz', 'ᛖ', 'Colaboración y progreso', '/images/runes/ehwaz.png', 4, CURRENT_TIMESTAMP),
('Mannaz', 'ᛗ', 'Humanidad y comunidad', '/images/runes/mannaz.png', 4, CURRENT_TIMESTAMP),
('Laguz', 'ᛚ', 'Intuición y flujo', '/images/runes/laguz.png', 5, CURRENT_TIMESTAMP),
('Ingwaz', 'ᛜ', 'Potencial y fertilidad', '/images/runes/ingwaz.png', 5, CURRENT_TIMESTAMP),
('Dagaz', 'ᛞ', 'Despertar y claridad', '/images/runes/dagaz.png', 5, CURRENT_TIMESTAMP),
('Othala', 'ᛟ', 'Herencia y hogar', '/images/runes/othala.png', 5, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Insertar badges (8 badges por fase de pricing)
INSERT INTO badges (name, description, icon_url, user_number_min, user_number_max, tier, created_at) VALUES
-- PHASE 1: $5/mes (usuarios 1-1000)
('Pionero', 'Primer millar de DrakkarPress', '/images/badges/pioneer.png', 1, 1000, 1, CURRENT_TIMESTAMP),
('Fundador', 'Entre los primeros 100', '/images/badges/founder.png', 1, 100, 1, CURRENT_TIMESTAMP),
-- PHASE 2: $10/mes (usuarios 1001-10000)
('Constructor', 'Ayudaste a construir DrakkarPress', '/images/badges/builder.png', 1001, 10000, 2, CURRENT_TIMESTAMP),
('Visionario', 'Entre los primeros 5000', '/images/badges/visionary.png', 1001, 5000, 2, CURRENT_TIMESTAMP),
-- PHASE 3: $19.99/mes (usuarios 10001+)
('Miembro', 'Parte de la comunidad DrakkarPress', '/images/badges/member.png', 10001, 999999999, 3, CURRENT_TIMESTAMP),
('Veterano', 'Más de 1 año en la plataforma', '/images/badges/veteran.png', 1, 999999999, 3, CURRENT_TIMESTAMP),
-- Badges especiales
('Beta Tester', 'Participaste en la beta', '/images/badges/beta.png', 1, 500, 1, CURRENT_TIMESTAMP),
('VIP', 'Membresía de cortesía', '/images/badges/vip.png', 1, 999999999, 5, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Insertar límites de IA por plan
INSERT INTO ai_usage_limits (plan, features_per_month, created_at) VALUES
('PHASE_1', 1000, CURRENT_TIMESTAMP),
('PHASE_2', 500, CURRENT_TIMESTAMP),
('PHASE_3', 200, CURRENT_TIMESTAMP),
('GRANDFATHERED', 999999, CURRENT_TIMESTAMP),
('COURTESY', 999999, CURRENT_TIMESTAMP)
ON CONFLICT (plan) DO NOTHING;

-- 7. VISTAS

-- Vista de estadísticas de usuarios
CREATE OR REPLACE VIEW v_user_statistics AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.user_number,
    u.full_name,
    m.plan,
    m.price_paid,
    m.is_active as membership_active,
    m.is_grandfathered,
    m.is_courtesy,
    COUNT(DISTINCT ur.id) as total_runes,
    COUNT(DISTINCT ub.id) as total_badges,
    COUNT(DISTINCT c1.id) as total_connections_made,
    COUNT(DISTINCT c2.id) as total_connections_received,
    u.created_at as member_since
FROM users u
LEFT JOIN memberships m ON u.id = m.user_id AND m.is_active = true
LEFT JOIN user_runes ur ON u.id = ur.user_id
LEFT JOIN user_badges ub ON u.id = ub.user_id
LEFT JOIN connections c1 ON u.id = c1.user_id
LEFT JOIN connections c2 ON u.id = c2.connected_user_id
GROUP BY u.id, u.username, u.email, u.user_number, u.full_name, m.plan, m.price_paid, m.is_active, m.is_grandfathered, m.is_courtesy, u.created_at;

-- Vista de transacciones de pago
CREATE OR REPLACE VIEW v_payment_summary AS
SELECT 
    DATE_TRUNC('day', created_at) as payment_date,
    COUNT(*) as total_transactions,
    SUM(amount) as total_revenue,
    AVG(amount) as average_transaction,
    COUNT(DISTINCT user_id) as unique_users
FROM payment_transactions
WHERE status = 'succeeded'
GROUP BY DATE_TRUNC('day', created_at)
ORDER BY payment_date DESC;

-- Vista de actividad de IA
CREATE OR REPLACE VIEW v_ai_usage_summary AS
SELECT 
    DATE_TRUNC('month', created_at) as usage_month,
    u.user_number,
    u.username,
    m.plan,
    COUNT(*) as total_features_used,
    l.features_per_month as monthly_limit,
    ROUND((COUNT(*)::decimal / l.features_per_month) * 100, 2) as usage_percentage
FROM ai_usage_tracking aut
JOIN users u ON aut.user_id = u.id
JOIN memberships m ON u.id = m.user_id AND m.is_active = true
JOIN ai_usage_limits l ON m.plan = l.plan
GROUP BY DATE_TRUNC('month', aut.created_at), u.user_number, u.username, m.plan, l.features_per_month
ORDER BY usage_month DESC, usage_percentage DESC;

-- 8. SEGURIDAD

-- Revocar acceso público innecesario
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT USAGE ON SCHEMA public TO drakkarpress_user;

-- 9. CONFIGURACIÓN DE MANTENIMIENTO

-- Configurar autovacuum agresivo para tablas de alta actividad
ALTER TABLE users SET (autovacuum_vacuum_scale_factor = 0.01);
ALTER TABLE session_tokens SET (autovacuum_vacuum_scale_factor = 0.01);
ALTER TABLE payment_transactions SET (autovacuum_vacuum_scale_factor = 0.02);
ALTER TABLE ai_usage_tracking SET (autovacuum_vacuum_scale_factor = 0.02);

-- 10. BACKUP Y RECOVERY

-- Crear rol de backup
CREATE ROLE backup_role WITH
    NOLOGIN
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION;

GRANT SELECT ON ALL TABLES IN SCHEMA public TO backup_role;
GRANT SELECT ON ALL SEQUENCES IN SCHEMA public TO backup_role;

-- ==========================================
-- COMPLETADO
-- ==========================================

DO $$
BEGIN
    RAISE NOTICE '✅ Base de datos DrakkarPress configurada exitosamente!';
    RAISE NOTICE '';
    RAISE NOTICE '📊 Estadísticas:';
    RAISE NOTICE '  - 24 Runas insertadas';
    RAISE NOTICE '  - 8 Badges configurados';
    RAISE NOTICE '  - 5 Planes de IA definidos';
    RAISE NOTICE '  - 3 Vistas creadas';
    RAISE NOTICE '  - Índices optimizados';
    RAISE NOTICE '';
    RAISE NOTICE '🔐 IMPORTANTE:';
    RAISE NOTICE '  - Cambiar password de drakkarpress_user';
    RAISE NOTICE '  - Configurar backups automáticos';
    RAISE NOTICE '  - Revisar políticas de retención';
    RAISE NOTICE '';
    RAISE NOTICE '🚀 Próximos pasos:';
    RAISE NOTICE '  1. Ejecutar aplicación Spring Boot';
    RAISE NOTICE '  2. JPA creará las tablas automáticamente';
    RAISE NOTICE '  3. Aplicar triggers de updated_at después';
    RAISE NOTICE '  4. Verificar integridad de datos';
END $$;
```

---

## 🔧 Variables de Entorno para Aplicación

```bash
# Database
DATABASE_URL=jdbc:postgresql://tu-servidor.com:5432/drakkarpress_prod
DATABASE_USERNAME=drakkarpress_user
DATABASE_PASSWORD=PASSWORD_SEGURO_AQUI

# Pool de Conexiones
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=30000
```

---

## 📋 Comandos Útiles

### Backup
```bash
# Backup completo
pg_dump -h localhost -U drakkarpress_user -d drakkarpress_prod -F c -b -v -f backup_$(date +%Y%m%d).dump

# Backup solo datos
pg_dump -h localhost -U drakkarpress_user -d drakkarpress_prod -a -v -f data_backup_$(date +%Y%m%d).sql
```

### Restore
```bash
# Restore desde backup
pg_restore -h localhost -U drakkarpress_user -d drakkarpress_prod -v backup_20251111.dump

# Restore desde SQL
psql -h localhost -U drakkarpress_user -d drakkarpress_prod < data_backup_20251111.sql
```

### Monitoreo
```sql
-- Ver conexiones activas
SELECT pid, usename, application_name, client_addr, state, query 
FROM pg_stat_activity 
WHERE datname = 'drakkarpress_prod';

-- Ver tamaño de tablas
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Ver índices sin usar
SELECT schemaname, tablename, indexname, idx_scan
FROM pg_stat_user_indexes
WHERE idx_scan = 0 AND indexname NOT LIKE 'pg_toast%';
```

---

## 🔐 Checklist de Seguridad

- [ ] Cambiar password de drakkarpress_user
- [ ] Configurar SSL/TLS para conexiones
- [ ] Habilitar pg_hba.conf solo para IPs permitidas
- [ ] Configurar firewall (solo puerto 5432 desde app server)
- [ ] Habilitar logging de conexiones
- [ ] Configurar rotación de logs
- [ ] Implementar backups automáticos diarios
- [ ] Probar restore de backups
- [ ] Configurar alertas de monitoreo
- [ ] Revisar permisos de usuarios
- [ ] Encriptar backups
- [ ] Configurar replicación (opcional)

---

**Creado**: 2025-11-11  
**PostgreSQL Version**: 14+  
**Encoding**: UTF8  
**Timezone**: UTC
