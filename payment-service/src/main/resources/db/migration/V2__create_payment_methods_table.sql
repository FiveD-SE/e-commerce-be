-- Create payment_methods table
CREATE TABLE payment_methods (
    method_id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- CREDIT_CARD, DEBIT_CARD, BANK_ACCOUNT, WALLET
    gateway ENUM('STRIPE', 'PAYPAL', 'VNPAY', 'MOMO', 'ZALOPAY', 'BANK_TRANSFER', 'CASH_ON_DELIVERY', 'CREDIT_CARD', 'DEBIT_CARD') NOT NULL,
    gateway_method_id VARCHAR(255),
    
    -- Card Information (encrypted)
    card_last_four VARCHAR(4),
    card_brand VARCHAR(20), -- VISA, MASTERCARD, AMEX, etc.
    card_exp_month INT,
    card_exp_year INT,
    card_holder_name VARCHAR(255),
    
    -- Bank Account Information
    bank_name VARCHAR(255),
    account_last_four VARCHAR(4),
    account_type VARCHAR(20), -- CHECKING, SAVINGS
    
    -- Wallet Information
    wallet_email VARCHAR(255),
    wallet_phone VARCHAR(20),
    
    -- Status and Settings
    is_active BOOLEAN DEFAULT TRUE,
    is_default BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT FALSE,
    
    -- Additional Information
    billing_address JSON,
    metadata JSON,
    notes TEXT,
    
    -- Audit fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_payment_methods_user_id (user_id),
    INDEX idx_payment_methods_type (type),
    INDEX idx_payment_methods_gateway (gateway),
    INDEX idx_payment_methods_active (is_active),
    INDEX idx_payment_methods_default (is_default),
    INDEX idx_payment_methods_gateway_method_id (gateway_method_id)
);
