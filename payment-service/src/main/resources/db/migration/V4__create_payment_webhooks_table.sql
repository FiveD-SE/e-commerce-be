-- Create payment_webhooks table
CREATE TABLE payment_webhooks (
    webhook_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT,
    gateway ENUM('STRIPE', 'PAYPAL', 'VNPAY', 'MOMO', 'ZALOPAY', 'BANK_TRANSFER', 'CASH_ON_DELIVERY', 'CREDIT_CARD', 'DEBIT_CARD') NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    gateway_event_id VARCHAR(255),
    payload LONGTEXT,
    headers JSON,
    signature VARCHAR(500),
    
    is_verified BOOLEAN DEFAULT FALSE,
    is_processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    processing_result TEXT,
    
    retry_count INT DEFAULT 0,
    last_retry_at TIMESTAMP,
    error_message TEXT,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_payment_webhooks_payment_id (payment_id),
    INDEX idx_payment_webhooks_gateway (gateway),
    INDEX idx_payment_webhooks_event_type (event_type),
    INDEX idx_payment_webhooks_gateway_event_id (gateway_event_id),
    INDEX idx_payment_webhooks_processed (is_processed),
    INDEX idx_payment_webhooks_verified (is_verified),
    INDEX idx_payment_webhooks_created_at (created_at)
);
