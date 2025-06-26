CREATE TABLE order_items (
    order_item_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BINARY(16) NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_sku VARCHAR(50) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    product_image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
); 