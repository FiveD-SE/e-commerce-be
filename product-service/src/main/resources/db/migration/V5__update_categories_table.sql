ALTER TABLE categories
    CHANGE COLUMN id category_id INT AUTO_INCREMENT,
    CHANGE COLUMN name category_title VARCHAR(100) NOT NULL,
    ADD COLUMN image_url VARCHAR(255),
    ADD COLUMN parent_category_id INT,
    ADD CONSTRAINT fk_parent_category FOREIGN KEY (parent_category_id) REFERENCES categories(category_id),
    DROP INDEX uk_category_name,
    ADD UNIQUE KEY uk_category_title (category_title); 