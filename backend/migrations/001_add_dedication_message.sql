-- Migration: Add dedication_message to book_purchases
-- Date: 2024-11-18
-- Feature: Personalized dedications for ebook purchases

-- Add dedication message column (max 500 chars, nullable)
ALTER TABLE book_purchases 
ADD COLUMN IF NOT EXISTS dedication_message VARCHAR(500);

-- Create index for fast searches of purchases with dedication
CREATE INDEX IF NOT EXISTS idx_book_purchases_dedication 
ON book_purchases(dedication_message) 
WHERE dedication_message IS NOT NULL;

-- Verification query
SELECT 
    COUNT(*) as total_purchases,
    COUNT(dedication_message) as with_dedication,
    COUNT(*) - COUNT(dedication_message) as without_dedication
FROM book_purchases;
