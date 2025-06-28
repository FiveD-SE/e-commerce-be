-- Add brand_id column to products table
ALTER TABLE products ADD COLUMN brand_id CHAR(36);

-- Add foreign key constraint
ALTER TABLE products ADD CONSTRAINT fk_products_brand_id 
    FOREIGN KEY (brand_id) REFERENCES brands(id);

-- Add index for better performance
CREATE INDEX idx_products_brand_id ON products(brand_id);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_name ON products(name); 