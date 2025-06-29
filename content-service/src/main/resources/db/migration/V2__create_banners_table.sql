-- Create banners table
CREATE TABLE banners (
    banner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    label VARCHAR(100) NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    title VARCHAR(255),
    subtitle VARCHAR(255),
    description TEXT,
    link_url VARCHAR(500),
    link_text VARCHAR(100),
    position VARCHAR(50),
    type VARCHAR(50) DEFAULT 'IMAGE',
    target VARCHAR(20) DEFAULT '_self',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    click_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    priority INT DEFAULT 0,
    width INT,
    height INT,
    alt_text VARCHAR(255),
    css_class VARCHAR(100),
    css_style VARCHAR(500),
    html_content TEXT,
    mobile_image_url VARCHAR(500),
    tablet_image_url VARCHAR(500),
    created_by INT,
    updated_by INT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for banners table
CREATE INDEX idx_banners_name ON banners(name);
CREATE INDEX idx_banners_label ON banners(label);
CREATE INDEX idx_banners_position ON banners(position);
CREATE INDEX idx_banners_type ON banners(type);
CREATE INDEX idx_banners_is_active ON banners(is_active);
CREATE INDEX idx_banners_is_featured ON banners(is_featured);
CREATE INDEX idx_banners_start_date ON banners(start_date);
CREATE INDEX idx_banners_end_date ON banners(end_date);
CREATE INDEX idx_banners_created_at ON banners(created_at);
CREATE INDEX idx_banners_priority ON banners(priority);
CREATE INDEX idx_banners_click_count ON banners(click_count);
CREATE INDEX idx_banners_view_count ON banners(view_count);
CREATE INDEX idx_banners_created_by ON banners(created_by);

-- Create composite indexes
CREATE INDEX idx_banners_active_position ON banners(is_active, position);
CREATE INDEX idx_banners_active_featured ON banners(is_active, is_featured);
CREATE INDEX idx_banners_active_dates ON banners(is_active, start_date, end_date);
