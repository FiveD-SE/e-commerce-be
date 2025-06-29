-- Create payments table
CREATE TABLE payments (
    payment_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    payment_reference VARCHAR(100) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL,
    user_id INT NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    
    -- Payment Amount Information
    amount DECIMAL(12,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'VND',
    exchange_rate DECIMAL(10,4) DEFAULT 1.0000,
    amount_in_base_currency DECIMAL(12,2),
    
    -- Payment Gateway Information
    gateway ENUM('STRIPE', 'PAYPAL', 'VNPAY', 'MOMO', 'ZALOPAY', 'BANK_TRANSFER', 'CASH_ON_DELIVERY', 'CREDIT_CARD', 'DEBIT_CARD') NOT NULL,
    gateway_transaction_id VARCHAR(255),
    gateway_payment_url VARCHAR(1000),
    gateway_response JSON,
    
    -- Payment Status
    status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED', 'REFUNDED', 'PARTIALLY_REFUNDED', 'EXPIRED', 'DISPUTED') NOT NULL DEFAULT 'PENDING',
    failure_reason VARCHAR(500),
    gateway_error_code VARCHAR(50),
    
    -- Payment Method Details
    payment_method_type VARCHAR(50),
    payment_method_details JSON,
    
    -- Timing Information
    initiated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    completed_at TIMESTAMP,
    failed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    
    -- Refund Information
    refunded_amount DECIMAL(12,2) DEFAULT 0.00,
    refundable_amount DECIMAL(12,2),
    last_refund_at TIMESTAMP,
    
    -- Additional Information
    description VARCHAR(500),
    customer_ip VARCHAR(45),
    user_agent VARCHAR(500),
    metadata JSON,
    notes TEXT,
    
    -- Risk Assessment
    risk_score DECIMAL(3,2),
    is_high_risk BOOLEAN DEFAULT FALSE,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_payments_order_id (order_id),
    INDEX idx_payments_user_id (user_id),
    INDEX idx_payments_status (status),
    INDEX idx_payments_gateway (gateway),
    INDEX idx_payments_gateway_transaction_id (gateway_transaction_id),
    INDEX idx_payments_initiated_at (initiated_at),
    INDEX idx_payments_completed_at (completed_at),
    INDEX idx_payments_reference (payment_reference)
);
