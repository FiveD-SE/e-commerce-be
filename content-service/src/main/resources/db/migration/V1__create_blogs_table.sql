-- Create blogs table
CREATE TABLE blogs (
    blog_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    label VARCHAR(100) NOT NULL,
    image_url VARCHAR(500),
    description TEXT,
    content LONGTEXT,
    author VARCHAR(100),
    author_id INT,
    category VARCHAR(100),
    tags VARCHAR(500),
    slug VARCHAR(255) UNIQUE,
    meta_title VARCHAR(255),
    meta_description VARCHAR(500),
    meta_keywords VARCHAR(500),
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    view_count BIGINT NOT NULL DEFAULT 0,
    like_count BIGINT NOT NULL DEFAULT 0,
    comment_count BIGINT NOT NULL DEFAULT 0,
    published_at TIMESTAMP NULL,
    reading_time INT,
    language VARCHAR(10) DEFAULT 'vi',
    status VARCHAR(20) DEFAULT 'DRAFT',
    priority INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create indexes for blogs table
CREATE INDEX idx_blogs_name ON blogs(name);
CREATE INDEX idx_blogs_label ON blogs(label);
CREATE INDEX idx_blogs_author ON blogs(author);
CREATE INDEX idx_blogs_author_id ON blogs(author_id);
CREATE INDEX idx_blogs_category ON blogs(category);
CREATE INDEX idx_blogs_slug ON blogs(slug);
CREATE INDEX idx_blogs_is_published ON blogs(is_published);
CREATE INDEX idx_blogs_is_featured ON blogs(is_featured);
CREATE INDEX idx_blogs_status ON blogs(status);
CREATE INDEX idx_blogs_published_at ON blogs(published_at);
CREATE INDEX idx_blogs_created_at ON blogs(created_at);
CREATE INDEX idx_blogs_priority ON blogs(priority);
CREATE INDEX idx_blogs_view_count ON blogs(view_count);

-- Create full-text search index
CREATE FULLTEXT INDEX idx_blogs_search ON blogs(name, description, content, tags);
