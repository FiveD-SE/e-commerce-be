-- Add promotion fields to payments table
ALTER TABLE payments 
ADD COLUMN promotion_code VARCHAR(50),
ADD COLUMN promotion_id BIGINT,
ADD COLUMN promotion_name VARCHAR(255),
ADD COLUMN promotion_type VARCHAR(20),
ADD COLUMN original_amount DECIMAL(12,2),
ADD COLUMN discount_amount DECIMAL(12,2) DEFAULT 0.00,
ADD COLUMN promotion_usage_id BIGINT,
ADD COLUMN has_promotion BOOLEAN DEFAULT FALSE;

-- Create indexes for promotion fields
CREATE INDEX idx_payments_promotion_code ON payments(promotion_code);
CREATE INDEX idx_payments_promotion_id ON payments(promotion_id);
CREATE INDEX idx_payments_has_promotion ON payments(has_promotion);
CREATE INDEX idx_payments_promotion_usage_id ON payments(promotion_usage_id);

-- Create composite indexes
CREATE INDEX idx_payments_promotion_status ON payments(has_promotion, status);
CREATE INDEX idx_payments_promotion_amount ON payments(has_promotion, discount_amount);
