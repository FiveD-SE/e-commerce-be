-- Create promotion_usages table
CREATE TABLE promotion_usages (
    usage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    promotion_id BIGINT NOT NULL,
    promotion_code VARCHAR(50) NOT NULL,
    user_id INT NOT NULL,
    order_id BIGINT NOT NULL,
    payment_id BIGINT,
    order_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    final_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'APPLIED',
    applied_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP NULL,
    refunded_at TIMESTAMP NULL,
    notes VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500)
);

-- Create indexes for promotion_usages table
CREATE INDEX idx_promotion_usages_promotion_id ON promotion_usages(promotion_id);
CREATE INDEX idx_promotion_usages_promotion_code ON promotion_usages(promotion_code);
CREATE INDEX idx_promotion_usages_user_id ON promotion_usages(user_id);
CREATE INDEX idx_promotion_usages_order_id ON promotion_usages(order_id);
CREATE INDEX idx_promotion_usages_payment_id ON promotion_usages(payment_id);
CREATE INDEX idx_promotion_usages_status ON promotion_usages(status);
CREATE INDEX idx_promotion_usages_applied_at ON promotion_usages(applied_at);

-- Create composite indexes
CREATE INDEX idx_promotion_usages_user_promotion ON promotion_usages(user_id, promotion_id);
CREATE INDEX idx_promotion_usages_user_code ON promotion_usages(user_id, promotion_code);
CREATE INDEX idx_promotion_usages_status_applied ON promotion_usages(status, applied_at);

-- Add foreign key constraint
ALTER TABLE promotion_usages 
ADD CONSTRAINT fk_promotion_usages_promotion_id 
FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) 
ON DELETE CASCADE;
