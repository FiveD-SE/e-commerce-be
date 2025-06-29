-- Create otp_tokens table
CREATE TABLE otp_tokens (
    otp_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    identifier_type ENUM('EMAIL', 'PHONE') NOT NULL,
    otp_code VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    attempt_count INT DEFAULT 0,
    verified_at TIMESTAMP NULL,
    purpose VARCHAR(50) DEFAULT 'LOGIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_otp_tokens_identifier (identifier),
    INDEX idx_otp_tokens_identifier_type (identifier_type),
    INDEX idx_otp_tokens_expires_at (expires_at),
    INDEX idx_otp_tokens_is_verified (is_verified),
    INDEX idx_otp_tokens_identifier_composite (identifier, identifier_type, is_verified, created_at)
); 