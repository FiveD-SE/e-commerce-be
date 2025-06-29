-- Create auth_tokens table
CREATE TABLE auth_tokens (
    token_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    access_token VARCHAR(1000) NOT NULL,
    refresh_token VARCHAR(1000) NOT NULL,
    access_token_expires_at TIMESTAMP NOT NULL,
    refresh_token_expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    device_info VARCHAR(255),
    ip_address VARCHAR(45),
    last_used_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_auth_tokens_user_id (user_id),
    INDEX idx_auth_tokens_access_token (access_token(255)),
    INDEX idx_auth_tokens_refresh_token (refresh_token(255)),
    INDEX idx_auth_tokens_is_active (is_active),
    INDEX idx_auth_tokens_expires_at (access_token_expires_at, refresh_token_expires_at)
); 