CREATE SCHEMA IF NOT EXISTS category_service;

CREATE TABLE categories (
    id CHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id CHAR(36),
    status VARCHAR(20) NOT NULL,
    display_order INTEGER NOT NULL,
    attributes JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_category_name (name)
); 