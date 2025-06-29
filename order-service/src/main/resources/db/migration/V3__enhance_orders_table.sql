-- Enhanced Orders Table Migration
-- Adding comprehensive fields for order management

-- Add new columns to orders table
ALTER TABLE orders 
ADD COLUMN order_number VARCHAR(50) UNIQUE NOT NULL DEFAULT '',
ADD COLUMN user_email VARCHAR(255) NOT NULL DEFAULT '',
ADD COLUMN user_phone VARCHAR(20),

-- Shipping Address Details
ADD COLUMN shipping_address VARCHAR(500) NOT NULL DEFAULT '',
ADD COLUMN shipping_city VARCHAR(100) NOT NULL DEFAULT '',
ADD COLUMN shipping_postal_code VARCHAR(20),
ADD COLUMN shipping_country VARCHAR(100) NOT NULL DEFAULT '',

-- Billing Address Details
ADD COLUMN billing_address VARCHAR(500),
ADD COLUMN billing_city VARCHAR(100),
ADD COLUMN billing_postal_code VARCHAR(20),
ADD COLUMN billing_country VARCHAR(100),

-- Financial Information
ADD COLUMN subtotal DECIMAL(12,2) NOT NULL DEFAULT 0.00,
ADD COLUMN tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,
ADD COLUMN shipping_fee DECIMAL(12,2) NOT NULL DEFAULT 0.00,
ADD COLUMN discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00,

-- Discount Information
ADD COLUMN discount_code VARCHAR(50),
ADD COLUMN discount_type VARCHAR(20),

-- Payment Information
ADD COLUMN payment_method VARCHAR(50),
ADD COLUMN payment_status VARCHAR(20),
ADD COLUMN transaction_id VARCHAR(100),

-- Shipping Information
ADD COLUMN shipping_method VARCHAR(50),
ADD COLUMN tracking_number VARCHAR(100),
ADD COLUMN estimated_delivery_date TIMESTAMP,
ADD COLUMN actual_delivery_date TIMESTAMP,

-- Order Dates
ADD COLUMN order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN confirmed_date TIMESTAMP,
ADD COLUMN shipped_date TIMESTAMP,
ADD COLUMN delivered_date TIMESTAMP,
ADD COLUMN cancelled_date TIMESTAMP,

-- Additional Information
ADD COLUMN notes TEXT,
ADD COLUMN admin_notes TEXT,
ADD COLUMN cancellation_reason VARCHAR(500),
ADD COLUMN is_gift BOOLEAN DEFAULT FALSE,
ADD COLUMN gift_message TEXT;

-- Rename existing address column to avoid confusion
ALTER TABLE orders CHANGE COLUMN address old_address VARCHAR(500);

-- Update total_amount column precision
ALTER TABLE orders MODIFY COLUMN total_amount DECIMAL(12,2) NOT NULL DEFAULT 0.00;

-- Add indexes for better performance
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_order_date ON orders(order_date);
CREATE INDEX idx_orders_payment_status ON orders(payment_status);
CREATE INDEX idx_orders_order_number ON orders(order_number);

-- Update OrderStatus enum values
ALTER TABLE orders MODIFY COLUMN status ENUM('PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED', 'REFUNDED') NOT NULL;
