-- Create payment_transactions table
CREATE TABLE payment_transactions (
    transaction_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    payment_id BIGINT NOT NULL,
    transaction_reference VARCHAR(100) UNIQUE NOT NULL,
    type ENUM('PAYMENT', 'REFUND', 'PARTIAL_REFUND', 'CHARGEBACK', 'ADJUSTMENT', 'FEE', 'REVERSAL') NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED', 'EXPIRED', 'DISPUTED') NOT NULL DEFAULT 'PENDING',
    
    -- Gateway Information
    gateway_transaction_id VARCHAR(255),
    gateway_response JSON,
    gateway_fee DECIMAL(12,2),
    
    -- Timing
    processed_at TIMESTAMP,
    settled_at TIMESTAMP,
    
    -- Additional Information
    description VARCHAR(500),
    reason VARCHAR(500),
    metadata JSON,
    notes TEXT,
    
    -- Parent Transaction (for refunds)
    parent_transaction_id BIGINT,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE,
    FOREIGN KEY (parent_transaction_id) REFERENCES payment_transactions(transaction_id) ON DELETE SET NULL,
    
    -- Indexes
    INDEX idx_payment_transactions_payment_id (payment_id),
    INDEX idx_payment_transactions_type (type),
    INDEX idx_payment_transactions_status (status),
    INDEX idx_payment_transactions_gateway_transaction_id (gateway_transaction_id),
    INDEX idx_payment_transactions_processed_at (processed_at),
    INDEX idx_payment_transactions_parent_id (parent_transaction_id),
    INDEX idx_payment_transactions_reference (transaction_reference)
);
