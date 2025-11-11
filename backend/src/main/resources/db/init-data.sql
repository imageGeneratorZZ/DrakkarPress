-- ============================================================================
-- DRAKKARPRESS - DATOS INICIALES
-- ============================================================================
-- Versión: 2.0
-- Fecha: 11 de Noviembre, 2025
-- Descripción: Seeds para runas, badges y configuración inicial
-- ============================================================================

-- ============================================================================
-- SEED: 24 RUNAS DEL ELDER FUTHARK
-- ============================================================================

-- Categoría: CREATIVIDAD & CONOCIMIENTO
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᚲ', 'Kenaz', 'Creatividad, inspiración, luz interior', 'Creativity, inspiration, inner light', 'CREATIVITY_KNOWLEDGE', 
 'La antorcha que ilumina el camino del escritor. Representa la chispa creativa y la inspiración divina que guía las palabras.', 
 'The torch that illuminates the writer''s path. Represents the creative spark and divine inspiration that guides words.', 1),
 
('ᚨ', 'Ansuz', 'Sabiduría, comunicación, palabra divina', 'Wisdom, communication, divine word', 'CREATIVITY_KNOWLEDGE',
 'La runa de Odín, dios de la sabiduría y la poesía. Simboliza el poder de la palabra escrita y la comunicación profunda.',
 'The rune of Odin, god of wisdom and poetry. Symbolizes the power of the written word and deep communication.', 2),
 
('ᛗ', 'Mannaz', 'Intelecto, el yo creador, humanidad', 'Intellect, the creator self, humanity', 'CREATIVITY_KNOWLEDGE',
 'Representa al ser humano como creador. La conexión entre el escritor y sus lectores, la humanidad en las historias.',
 'Represents the human being as creator. The connection between writer and readers, humanity in stories.', 3);

-- Categoría: ÉXITO & LOGRO
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᛊ', 'Sowilo', 'Éxito, victoria, poder solar', 'Success, victory, solar power', 'SUCCESS_ACHIEVEMENT',
 'El sol que no se oculta. Representa el éxito alcanzado, la victoria del escritor que logra sus metas.',
 'The sun that never sets. Represents success achieved, the victory of the writer who reaches their goals.', 4),
 
('ᛃ', 'Jera', 'Cosecha, recompensa del esfuerzo, ciclos', 'Harvest, reward of effort, cycles', 'SUCCESS_ACHIEVEMENT',
 'La cosecha tras la siembra. El fruto del trabajo constante del escritor, la recompensa merecida.',
 'The harvest after sowing. The fruit of the writer''s constant work, the deserved reward.', 5),
 
('ᚹ', 'Wunjo', 'Alegría, perfección, éxito alcanzado', 'Joy, perfection, success achieved', 'SUCCESS_ACHIEVEMENT',
 'La alegría del logro. Cuando la obra está completa y el escritor puede disfrutar de su creación.',
 'The joy of achievement. When the work is complete and the writer can enjoy their creation.', 6),
 
('ᛏ', 'Tiwaz', 'Honor, victoria justa, liderazgo', 'Honor, just victory, leadership', 'SUCCESS_ACHIEVEMENT',
 'El guerrero honorable. Representa al escritor que lidera con integridad y alcanza victorias justas.',
 'The honorable warrior. Represents the writer who leads with integrity and achieves just victories.', 7);

-- Categoría: CRECIMIENTO & TRANSFORMACIÓN
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᛒ', 'Berkano', 'Nuevo comienzo, crecimiento, renacimiento', 'New beginning, growth, rebirth', 'GROWTH_TRANSFORMATION',
 'El abedul en primavera. Nuevos comienzos, el primer libro, el renacer del escritor en cada proyecto.',
 'The birch in spring. New beginnings, the first book, the writer''s rebirth in each project.', 8),
 
('ᛞ', 'Dagaz', 'Despertar, transformación, iluminación', 'Awakening, transformation, enlightenment', 'GROWTH_TRANSFORMATION',
 'El amanecer. La transformación del escritor, el momento de claridad donde todo cobra sentido.',
 'The dawn. The writer''s transformation, the moment of clarity where everything makes sense.', 9),
 
('ᛁ', 'Isa', 'Concentración, enfoque, cristalización', 'Concentration, focus, crystallization', 'GROWTH_TRANSFORMATION',
 'El hielo que preserva. La concentración necesaria para escribir, las ideas que se cristalizan en palabras.',
 'The ice that preserves. The concentration needed to write, ideas that crystallize into words.', 10);

-- Categoría: PROTECCIÓN & FUERZA
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᚦ', 'Thurisaz', 'Protección del trabajo, defensa', 'Protection of work, defense', 'PROTECTION_STRENGTH',
 'El martillo de Thor. Protección contra el bloqueo del escritor, defensa de la obra creada.',
 'Thor''s hammer. Protection against writer''s block, defense of the created work.', 11),
 
('ᚢ', 'Uruz', 'Fuerza vital, resistencia, poder primitivo', 'Vital force, endurance, primal power', 'PROTECTION_STRENGTH',
 'El uro salvaje. La fuerza bruta necesaria para perseverar, la resistencia del escritor comprometido.',
 'The wild aurochs. The brute force needed to persevere, the endurance of the committed writer.', 12),
 
('ᛉ', 'Algiz', 'Protección espiritual, conexión divina', 'Spiritual protection, divine connection', 'PROTECTION_STRENGTH',
 'El alce con cornamenta. Protección espiritual durante el proceso creativo, conexión con lo divino.',
 'The elk with antlers. Spiritual protection during the creative process, connection with the divine.', 13);

-- Categoría: INTUICIÓN & MISTERIO
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᛚ', 'Laguz', 'Intuición, flujo creativo, lo oculto', 'Intuition, creative flow, the hidden', 'INTUITION_MYSTERY',
 'El agua que fluye. La intuición del escritor, el flujo de palabras que emerge del subconsciente.',
 'The flowing water. The writer''s intuition, the flow of words that emerges from the subconscious.', 14),
 
('ᛈ', 'Perthro', 'Misterio, destino, secretos revelados', 'Mystery, destiny, secrets revealed', 'INTUITION_MYSTERY',
 'El cubilete de dados. Los misterios que el escritor revela, los giros del destino en las historias.',
 'The dice cup. The mysteries the writer reveals, the twists of fate in stories.', 15);

-- Categoría: LEGADO & ABUNDANCIA
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᛟ', 'Othala', 'Herencia, legado, patrimonio ancestral', 'Heritage, legacy, ancestral heritage', 'LEGACY_ABUNDANCE',
 'La tierra ancestral. El legado que el escritor deja, las historias que perduran generaciones.',
 'The ancestral land. The legacy the writer leaves, stories that endure for generations.', 16),
 
('ᚠ', 'Fehu', 'Abundancia, riqueza, prosperidad', 'Abundance, wealth, prosperity', 'LEGACY_ABUNDANCE',
 'El ganado, símbolo de riqueza. La abundancia que llega al escritor, tanto material como espiritual.',
 'The cattle, symbol of wealth. The abundance that comes to the writer, both material and spiritual.', 17);

-- Categoría: COLABORACIÓN & PROGRESO
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᛖ', 'Ehwaz', 'Progreso, colaboración, asociación', 'Progress, collaboration, partnership', 'COLLABORATION_PROGRESS',
 'El caballo y su jinete. La colaboración entre escritor y editor, el progreso conjunto.',
 'The horse and its rider. The collaboration between writer and editor, joint progress.', 18),
 
('ᚷ', 'Gebo', 'Intercambio, generosidad, reciprocidad', 'Exchange, generosity, reciprocity', 'COLLABORATION_PROGRESS',
 'El regalo. El intercambio generoso entre escritores, la reciprocidad en la comunidad.',
 'The gift. The generous exchange between writers, reciprocity in the community.', 19),
 
('ᚱ', 'Raidho', 'Viaje, movimiento, camino del héroe', 'Journey, movement, hero''s path', 'COLLABORATION_PROGRESS',
 'El carro en movimiento. El viaje del escritor, el camino del héroe en cada historia.',
 'The moving cart. The writer''s journey, the hero''s path in each story.', 20);

-- Categoría: NECESIDAD & RESISTENCIA (runas adicionales)
INSERT INTO runes (symbol, name, meaning_es, meaning_en, category, description_es, description_en, display_order) VALUES
('ᚺ', 'Hagalaz', 'Transformación, destrucción creativa', 'Transformation, creative destruction', 'GROWTH_TRANSFORMATION',
 'El granizo. La transformación a través de la adversidad, la destrucción que permite la reconstrucción.',
 'The hail. Transformation through adversity, destruction that allows reconstruction.', 21),
 
('ᚾ', 'Nauthiz', 'Necesidad, destino, disciplina', 'Need, destiny, discipline', 'PROTECTION_STRENGTH',
 'El fuego de la necesidad. La disciplina requerida para escribir, el destino que impulsa al escritor.',
 'The fire of need. The discipline required to write, the destiny that drives the writer.', 22),
 
('ᛇ', 'Eihwaz', 'Defensa, resistencia, perseverancia', 'Defense, resistance, perseverance', 'PROTECTION_STRENGTH',
 'El tejo milenario. La resistencia del escritor ante las dificultades, la perseverancia inquebrantable.',
 'The millennial yew. The writer''s resistance to difficulties, unbreakable perseverance.', 23),
 
('ᛜ', 'Ingwaz', 'Potencial, fertilidad, gestación', 'Potential, fertility, gestation', 'GROWTH_TRANSFORMATION',
 'La semilla. El potencial latente, las ideas en gestación que pronto florecerán.',
 'The seed. The latent potential, ideas in gestation that will soon flourish.', 24);

-- ============================================================================
-- SEED: BADGES DEL SISTEMA
-- ============================================================================

-- Badge: Fundador (Primeros 1000 usuarios Premium)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order) 
VALUES (
    'FOUNDER',
    'Fundador',
    'Founder',
    '🏆',
    (SELECT id FROM runes WHERE name = 'Othala'), -- Runa Othala (legado)
    '#FFD700',
    'Miembro fundador de DrakkarPress. Uno de los primeros 1,000 usuarios Premium que forjaron el inicio de nuestra comunidad.',
    'Founding member of DrakkarPress. One of the first 1,000 Premium users who forged the beginning of our community.',
    TRUE,
    TRUE,
    1
);

-- Badge: Early Adopter (Usuarios 1001-10000 Premium)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'EARLY_ADOPTER',
    'Early Adopter',
    'Early Adopter',
    '⭐',
    (SELECT id FROM runes WHERE name = 'Sowilo'), -- Runa Sowilo (éxito)
    '#C0C0C0',
    'Usuario temprano de DrakkarPress. Parte de los primeros 10,000 miembros Premium que creyeron en nuestra visión.',
    'Early user of DrakkarPress. Part of the first 10,000 Premium members who believed in our vision.',
    TRUE,
    TRUE,
    2
);

-- Badge: Premium (Usuario Premium regular)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'PREMIUM',
    'Premium',
    'Premium',
    '✨',
    NULL, -- Sin runa específica (usa la runa personal del usuario)
    '#9B59B6',
    'Miembro Premium de DrakkarPress. Acceso completo a todas las herramientas de IA y beneficios exclusivos.',
    'Premium member of DrakkarPress. Full access to all AI tools and exclusive benefits.',
    TRUE,
    TRUE,
    3
);

-- Badge: Invitado Especial (Cortesía de admin)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'SPECIAL_GUEST',
    'Invitado Especial',
    'Special Guest',
    '👑',
    (SELECT id FROM runes WHERE name = 'Ansuz'), -- Runa Ansuz (sabiduría divina)
    '#E74C3C',
    'Invitado especial de DrakkarPress. Miembro destacado con acceso Premium cortesía de la plataforma.',
    'Special guest of DrakkarPress. Distinguished member with courtesy Premium access from the platform.',
    FALSE,
    TRUE,
    4
);

-- Badge: Verificado (Datos de pago completos)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'VERIFIED',
    'Verificado',
    'Verified',
    '✓',
    NULL,
    '#3498DB',
    'Usuario verificado. Ha completado su información de pago y datos fiscales.',
    'Verified user. Has completed their payment information and tax data.',
    FALSE,
    FALSE,
    5
);

-- Badge: Certificado (Para imprentas)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'CERTIFIED',
    'Certificado',
    'Certified',
    '⚡',
    NULL,
    '#F39C12',
    'Imprenta certificada. Ha completado el proceso de verificación con documentación legal aprobada.',
    'Certified print shop. Has completed the verification process with approved legal documentation.',
    FALSE,
    FALSE,
    6
);

-- Badge: Bestseller (Autor con ventas destacadas - futuro)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'BESTSELLER',
    'Bestseller',
    'Bestseller',
    '📚',
    (SELECT id FROM runes WHERE name = 'Jera'), -- Runa Jera (cosecha)
    '#2ECC71',
    'Autor bestseller. Ha alcanzado ventas excepcionales en la plataforma.',
    'Bestseller author. Has achieved exceptional sales on the platform.',
    FALSE,
    FALSE,
    7
);

-- Badge: Prolífico (Autor con muchas obras - futuro)
INSERT INTO badges (code, name_es, name_en, icon, rune_id, color_hex, description_es, description_en, auto_assign, requires_premium, display_order)
VALUES (
    'PROLIFIC',
    'Prolífico',
    'Prolific',
    '✍️',
    (SELECT id FROM runes WHERE name = 'Kenaz'), -- Runa Kenaz (creatividad)
    '#E67E22',
    'Escritor prolífico. Ha publicado múltiples obras en la plataforma.',
    'Prolific writer. Has published multiple works on the platform.',
    FALSE,
    FALSE,
    8
);

-- ============================================================================
-- SEED: LÍMITES DE USO DE IA POR PLAN
-- ============================================================================

-- Plan: FREE
INSERT INTO ai_usage_limits (
    plan, 
    full_book_generation_limit, 
    cover_generation_limit, 
    writing_assistant_limit, 
    text_correction_limit,
    series_generation_limit,
    translation_limit
) VALUES (
    'FREE',
    0,      -- Generación completa: BLOQUEADA
    3,      -- Portadas: 3/mes
    10,     -- Asistente: 10 consultas/mes
    5,      -- Corrección: 5 capítulos/mes
    0,      -- Series: BLOQUEADA
    0       -- Traducción: BLOQUEADA
);

-- Plan: PREMIUM_PHASE_1 ($5/mes - Fundadores)
INSERT INTO ai_usage_limits (
    plan,
    full_book_generation_limit,
    cover_generation_limit,
    writing_assistant_limit,
    text_correction_limit,
    series_generation_limit,
    translation_limit
) VALUES (
    'PREMIUM_PHASE_1',
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL    -- Ilimitado
);

-- Plan: PREMIUM_PHASE_2 ($10/mes - Early Adopters)
INSERT INTO ai_usage_limits (
    plan,
    full_book_generation_limit,
    cover_generation_limit,
    writing_assistant_limit,
    text_correction_limit,
    series_generation_limit,
    translation_limit
) VALUES (
    'PREMIUM_PHASE_2',
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL    -- Ilimitado
);

-- Plan: PREMIUM_PHASE_3 ($19.99/mes - Regular)
INSERT INTO ai_usage_limits (
    plan,
    full_book_generation_limit,
    cover_generation_limit,
    writing_assistant_limit,
    text_correction_limit,
    series_generation_limit,
    translation_limit
) VALUES (
    'PREMIUM_PHASE_3',
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL    -- Ilimitado
);

-- Plan: PREMIUM_COURTESY (Cortesía)
INSERT INTO ai_usage_limits (
    plan,
    full_book_generation_limit,
    cover_generation_limit,
    writing_assistant_limit,
    text_correction_limit,
    series_generation_limit,
    translation_limit
) VALUES (
    'PREMIUM_COURTESY',
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL,   -- Ilimitado
    NULL    -- Ilimitado
);

-- ============================================================================
-- SEED: USUARIO ADMIN DE PRUEBA
-- ============================================================================
-- NOTA: Cambiar la contraseña en producción
-- Password: Admin123!@# (hasheado con bcrypt)

INSERT INTO users (
    email,
    username,
    password_hash,
    first_name,
    last_name,
    display_name,
    bio,
    email_verified,
    is_active,
    is_admin
) VALUES (
    'admin@drakkarpress.com',
    'admin',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Z9Z3pTRm0B4UXKP2CqJMu', -- Cambiar en producción
    'Admin',
    'DrakkarPress',
    'Administrador',
    'Cuenta administrativa de DrakkarPress',
    TRUE,
    TRUE,
    TRUE
);

-- Asignar membresía Premium Cortesía al admin
INSERT INTO memberships (
    user_id,
    plan,
    status,
    is_courtesy,
    courtesy_reason,
    started_at
) VALUES (
    (SELECT id FROM users WHERE email = 'admin@drakkarpress.com'),
    'PREMIUM_COURTESY',
    'ACTIVE',
    TRUE,
    'Cuenta administrativa del sistema',
    CURRENT_TIMESTAMP
);

-- Asignar badge "Invitado Especial" al admin
INSERT INTO user_badges (
    user_id,
    badge_id,
    status,
    granted_reason
) VALUES (
    (SELECT id FROM users WHERE email = 'admin@drakkarpress.com'),
    (SELECT id FROM badges WHERE code = 'SPECIAL_GUEST'),
    'ACTIVE',
    'Cuenta administrativa'
);

-- Asignar runa Ansuz al admin
INSERT INTO user_runes (
    user_id,
    rune_id,
    can_change_after
) VALUES (
    (SELECT id FROM users WHERE email = 'admin@drakkarpress.com'),
    (SELECT id FROM runes WHERE name = 'Ansuz'),
    CURRENT_TIMESTAMP + INTERVAL '30 days'
);

-- ============================================================================
-- USUARIOS DE PRUEBA (DEVELOPMENT ONLY - Eliminar en producción)
-- ============================================================================

-- Usuario 1: Fundador (user_number <= 1000)
-- Password: Test123!@#
INSERT INTO users (
    email,
    username,
    password_hash,
    first_name,
    last_name,
    display_name,
    bio,
    avatar_url,
    email_verified,
    is_active
) VALUES (
    'founder@test.com',
    'founder_test',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Z9Z3pTRm0B4UXKP2CqJMu',
    'Ana',
    'García',
    'Ana García',
    'Escritora de fantasía épica. Autora bestseller de la saga "Dragones del Norte".',
    'https://i.pravatar.cc/300?img=1',
    TRUE,
    TRUE
);

-- Membresía Fundador
INSERT INTO memberships (
    user_id,
    plan,
    status,
    payment_frequency,
    price_usd,
    is_grandfathered,
    started_at
) VALUES (
    (SELECT id FROM users WHERE email = 'founder@test.com'),
    'PREMIUM_PHASE_1',
    'ACTIVE',
    'MONTHLY',
    5.00,
    TRUE,
    CURRENT_TIMESTAMP
);

-- Rol: Autor Individual
INSERT INTO user_roles (
    user_id,
    role_type,
    is_active,
    entity_type
) VALUES (
    (SELECT id FROM users WHERE email = 'founder@test.com'),
    'AUTHOR_PUBLISHER',
    TRUE,
    'INDIVIDUAL'
);

-- Asignar runa Kenaz (creatividad)
INSERT INTO user_runes (
    user_id,
    rune_id,
    can_change_after
) VALUES (
    (SELECT id FROM users WHERE email = 'founder@test.com'),
    (SELECT id FROM runes WHERE name = 'Kenaz'),
    CURRENT_TIMESTAMP + INTERVAL '30 days'
);

-- Usuario 2: Free (sin Premium)
-- Password: Test123!@#
INSERT INTO users (
    email,
    username,
    password_hash,
    first_name,
    last_name,
    display_name,
    bio,
    email_verified,
    is_active
) VALUES (
    'free@test.com',
    'free_user',
    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36Z9Z3pTRm0B4UXKP2CqJMu',
    'Juan',
    'Pérez',
    'Juan Pérez',
    'Escritor principiante explorando mis primeras historias.',
    'https://i.pravatar.cc/300?img=2',
    TRUE,
    TRUE
);

-- Membresía Free
INSERT INTO memberships (
    user_id,
    plan,
    status,
    started_at
) VALUES (
    (SELECT id FROM users WHERE email = 'free@test.com'),
    'FREE',
    'ACTIVE',
    CURRENT_TIMESTAMP
);

-- ============================================================================
-- ÍNDICES ADICIONALES PARA PERFORMANCE
-- ============================================================================

-- Índice para búsqueda de usuarios por display_name
CREATE INDEX idx_users_display_name_search ON users 
    USING gin(to_tsvector('spanish', display_name));

-- Índice para búsqueda de runas por nombre
CREATE INDEX idx_runes_name_search ON runes 
    USING gin(to_tsvector('spanish', name));

-- ============================================================================
-- FUNCIONES ÚTILES PARA QUERIES
-- ============================================================================

-- Función: Obtener límites de IA para un usuario
CREATE OR REPLACE FUNCTION get_user_ai_limits(p_user_id UUID)
RETURNS TABLE (
    usage_type ai_usage_type,
    limit_value INTEGER,
    used_this_month INTEGER,
    remaining INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.usage_type,
        CASE u.usage_type
            WHEN 'FULL_BOOK_GENERATION' THEN l.full_book_generation_limit
            WHEN 'COVER_GENERATION' THEN l.cover_generation_limit
            WHEN 'WRITING_ASSISTANT' THEN l.writing_assistant_limit
            WHEN 'TEXT_CORRECTION' THEN l.text_correction_limit
            WHEN 'SERIES_GENERATION' THEN l.series_generation_limit
            WHEN 'TRANSLATION' THEN l.translation_limit
        END AS limit_value,
        COALESCE(CASE u.usage_type
            WHEN 'FULL_BOOK_GENERATION' THEN s.full_book_generation_count
            WHEN 'COVER_GENERATION' THEN s.cover_generation_count
            WHEN 'WRITING_ASSISTANT' THEN s.writing_assistant_count
            WHEN 'TEXT_CORRECTION' THEN s.text_correction_count
            WHEN 'SERIES_GENERATION' THEN s.series_generation_count
            WHEN 'TRANSLATION' THEN s.translation_count
        END, 0) AS used_this_month,
        CASE 
            WHEN CASE u.usage_type
                WHEN 'FULL_BOOK_GENERATION' THEN l.full_book_generation_limit
                WHEN 'COVER_GENERATION' THEN l.cover_generation_limit
                WHEN 'WRITING_ASSISTANT' THEN l.writing_assistant_limit
                WHEN 'TEXT_CORRECTION' THEN l.text_correction_limit
                WHEN 'SERIES_GENERATION' THEN l.series_generation_limit
                WHEN 'TRANSLATION' THEN l.translation_limit
            END IS NULL THEN -1 -- Ilimitado
            ELSE CASE u.usage_type
                WHEN 'FULL_BOOK_GENERATION' THEN l.full_book_generation_limit
                WHEN 'COVER_GENERATION' THEN l.cover_generation_limit
                WHEN 'WRITING_ASSISTANT' THEN l.writing_assistant_limit
                WHEN 'TEXT_CORRECTION' THEN l.text_correction_limit
                WHEN 'SERIES_GENERATION' THEN l.series_generation_limit
                WHEN 'TRANSLATION' THEN l.translation_limit
            END - COALESCE(CASE u.usage_type
                WHEN 'FULL_BOOK_GENERATION' THEN s.full_book_generation_count
                WHEN 'COVER_GENERATION' THEN s.cover_generation_count
                WHEN 'WRITING_ASSISTANT' THEN s.writing_assistant_count
                WHEN 'TEXT_CORRECTION' THEN s.text_correction_count
                WHEN 'SERIES_GENERATION' THEN s.series_generation_count
                WHEN 'TRANSLATION' THEN s.translation_count
            END, 0)
        END AS remaining
    FROM 
        (SELECT unnest(enum_range(NULL::ai_usage_type)) AS usage_type) u
    CROSS JOIN memberships m
    LEFT JOIN ai_usage_limits l ON m.plan = l.plan
    LEFT JOIN ai_usage_monthly_summary s ON m.user_id = s.user_id
        AND s.year = EXTRACT(YEAR FROM CURRENT_DATE)
        AND s.month = EXTRACT(MONTH FROM CURRENT_DATE)
    WHERE m.user_id = p_user_id
      AND m.status = 'ACTIVE';
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- VERIFICACIÓN DE DATOS
-- ============================================================================

-- Verificar que se insertaron todas las runas
SELECT 'Runas insertadas: ' || COUNT(*) || ' de 24' AS verification
FROM runes;

-- Verificar badges
SELECT 'Badges insertados: ' || COUNT(*) AS verification
FROM badges;

-- Verificar límites de IA
SELECT 'Límites de IA configurados para: ' || COUNT(*) || ' planes' AS verification
FROM ai_usage_limits;

-- Verificar usuarios de prueba
SELECT 'Usuarios de prueba creados: ' || COUNT(*) AS verification
FROM users
WHERE email LIKE '%@test.com' OR email LIKE '%@drakkarpress.com';

-- ============================================================================
-- FIN DEL SCRIPT DE INICIALIZACIÓN
-- ============================================================================

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE '✅ Base de datos inicializada correctamente';
    RAISE NOTICE '📚 24 runas del Elder Futhark';
    RAISE NOTICE '🏆 8 badges configurados';
    RAISE NOTICE '⚙️ Límites de IA por plan';
    RAISE NOTICE '👤 Usuario admin creado (cambiar contraseña en producción)';
    RAISE NOTICE '🧪 Usuarios de prueba creados (eliminar en producción)';
END $$;
