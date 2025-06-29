-- Create promotions table
CREATE TABLE promotions (
    promotion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    type VARCHAR(20) NOT NULL DEFAULT 'PERCENTAGE',
    discount_percent DECIMAL(5,2),
    discount_amount DECIMAL(10,2),
    max_discount DECIMAL(10,2),
    min_order_amount DECIMAL(10,2),
    stock INT NOT NULL,
    used_count INT NOT NULL DEFAULT 0,
    max_uses_per_user INT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_stackable BOOLEAN NOT NULL DEFAULT FALSE,
    applicable_categories VARCHAR(500),
    applicable_products VARCHAR(500),
    applicable_brands VARCHAR(500),
    excluded_categories VARCHAR(500),
    excluded_products VARCHAR(500),
    user_groups VARCHAR(500),
    first_time_user_only BOOLEAN NOT NULL DEFAULT FALSE,
    auto_apply BOOLEAN NOT NULL DEFAULT FALSE,
    priority INT DEFAULT 0,
    created_by INT,
    updated_by INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for promotions table
CREATE INDEX idx_promotions_code ON promotions(code);
CREATE INDEX idx_promotions_name ON promotions(name);
CREATE INDEX idx_promotions_type ON promotions(type);
CREATE INDEX idx_promotions_is_active ON promotions(is_active);
CREATE INDEX idx_promotions_is_featured ON promotions(is_featured);
CREATE INDEX idx_promotions_start_date ON promotions(start_date);
CREATE INDEX idx_promotions_end_date ON promotions(end_date);
CREATE INDEX idx_promotions_created_at ON promotions(created_at);
CREATE INDEX idx_promotions_priority ON promotions(priority);
CREATE INDEX idx_promotions_stock ON promotions(stock);
CREATE INDEX idx_promotions_created_by ON promotions(created_by);

-- Create composite indexes
CREATE INDEX idx_promotions_active_dates ON promotions(is_active, start_date, end_date);
CREATE INDEX idx_promotions_active_stock ON promotions(is_active, stock);
CREATE INDEX idx_promotions_featured_active ON promotions(is_featured, is_active);
CREATE INDEX idx_promotions_auto_apply ON promotions(auto_apply, is_active, start_date, end_date);

-- Create full-text search index
CREATE FULLTEXT INDEX idx_promotions_search ON promotions(name, description, code);
