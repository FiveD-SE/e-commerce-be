-- Enhanced Order Items Table Migration
-- Adding comprehensive fields for order item management

-- Add new columns to order_items table
ALTER TABLE order_items 
ADD COLUMN product_description TEXT,
ADD COLUMN product_category VARCHAR(100),
ADD COLUMN product_brand VARCHAR(100),
ADD COLUMN discount_amount DECIMAL(12,2) DEFAULT 0.00,
ADD COLUMN tax_amount DECIMAL(12,2) DEFAULT 0.00,
ADD COLUMN product_weight DECIMAL(8,2),
ADD COLUMN product_dimensions VARCHAR(100),
ADD COLUMN is_digital_product BOOLEAN DEFAULT FALSE,
ADD COLUMN notes TEXT;

-- Update existing columns precision
ALTER TABLE order_items 
MODIFY COLUMN unit_price DECIMAL(12,2) NOT NULL,
MODIFY COLUMN total_price DECIMAL(12,2) NOT NULL;

-- Add indexes for better performance
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_sku ON order_items(product_sku);

-- Add foreign key constraint if not exists
ALTER TABLE order_items 
ADD CONSTRAINT fk_order_items_order_id 
FOREIGN KEY (order_id) REFERENCES orders(order_id) 
ON DELETE CASCADE ON UPDATE CASCADE;
