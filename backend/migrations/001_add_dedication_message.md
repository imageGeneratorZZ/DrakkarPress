# 📝 Migración SQL - Dedicatorias Personalizadas

## Fecha: Noviembre 2024
## Feature: Dedicatorias personalizadas en compras de ebooks

### Cambios en Base de Datos

#### 1. Nueva columna en `book_purchases`

```sql
-- Añadir campo para dedicatorias personalizadas (máximo 500 caracteres)
ALTER TABLE book_purchases 
ADD COLUMN dedication_message VARCHAR(500);

-- Crear índice para búsquedas rápidas de compras con dedicatoria
CREATE INDEX idx_book_purchases_dedication 
ON book_purchases(dedication_message) 
WHERE dedication_message IS NOT NULL;
```

### Verificación Post-Migración

```sql
-- Verificar que la columna existe
SELECT column_name, data_type, character_maximum_length
FROM information_schema.columns
WHERE table_name = 'book_purchases' 
AND column_name = 'dedication_message';

-- Verificar índice
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'book_purchases'
AND indexname = 'idx_book_purchases_dedication';

-- Ejemplo de query con dedicatorias
SELECT 
    bp.id,
    u.email as buyer_email,
    b.title as book_title,
    bp.dedication_message,
    bp.created_at
FROM book_purchases bp
JOIN users u ON bp.user_id = u.id
JOIN books b ON bp.book_id = b.id
WHERE bp.dedication_message IS NOT NULL
ORDER BY bp.created_at DESC
LIMIT 10;
```

### Rollback (si es necesario)

```sql
-- Eliminar índice
DROP INDEX IF EXISTS idx_book_purchases_dedication;

-- Eliminar columna
ALTER TABLE book_purchases 
DROP COLUMN IF EXISTS dedication_message;
```

### Notas de Implementación

1. **Límite de caracteres**: 500 caracteres (suficiente para dedicatoria corta)
2. **Nullable**: Sí (la dedicatoria es opcional)
3. **Encoding**: UTF-8 (soporta emojis y caracteres especiales)
4. **Validación backend**: Trim y substring(0,500) en `BookPurchase.createEbookPurchase()`
5. **Frontend**: Prompt con límite visual en `shop.html`
6. **Admin UI**: Filtro "Con/Sin dedicatoria" + visualización destacada
7. **Email**: Bloque HTML amarillo con emoji ✒️ si existe dedicatoria

### Casos de Uso

- Compras de regalo con mensaje personalizado
- Autógrafos virtuales del autor
- Mensajes inspiracionales para el lector
- Futuro: dedicatoria impresa en libros físicos POD

### Compatibilidad

- ✅ PostgreSQL 12+
- ✅ Railway managed PostgreSQL
- ⚠️ Ejecutar ANTES de deploy del backend actualizado
- ⚠️ No afecta compras existentes (NULL permitido)

### Comando Railway

```bash
# Conectar a PostgreSQL de Railway
railway connect postgres

# Ejecutar migración
\i migration_dedication.sql

# Verificar
SELECT COUNT(*) FROM book_purchases WHERE dedication_message IS NOT NULL;
```

### Testing Post-Deploy

1. Comprar ebook con dedicatoria → verificar email con bloque destacado
2. Comprar ebook sin dedicatoria → verificar email normal
3. Admin panel → filtrar "Con dedicatoria" → verificar tabla
4. Admin panel → buscar por contenido de dedicatoria
