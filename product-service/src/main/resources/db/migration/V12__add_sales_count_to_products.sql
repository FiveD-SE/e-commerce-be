-- Add sales_count column to products table to track total sales amount
ALTER TABLE products 
ADD COLUMN sales_count INT NOT NULL DEFAULT 0 COMMENT 'Total number of units sold';

-- Add index for sales_count for performance when sorting by sales
CREATE INDEX idx_products_sales_count ON products(sales_count);

-- Update existing products to have 0 sales count (already default, but explicit for clarity)
UPDATE products SET sales_count = 0 WHERE sales_count IS NULL; 