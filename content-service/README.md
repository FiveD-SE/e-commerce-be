# Content Management Service

Content Management Service for E-commerce Platform - Manages blogs and banners with comprehensive features.

## 🚀 Features

### Blog Management
- ✅ **Full CRUD Operations** - Create, read, update, delete blogs
- ✅ **Publishing Workflow** - Draft → Published → Archived states
- ✅ **SEO Optimization** - Auto-generated slugs, meta tags, reading time
- ✅ **Content Organization** - Categories, tags, featured blogs
- ✅ **Analytics & Engagement** - Views, likes, comments tracking
- ✅ **Advanced Search** - Text search + MySQL full-text search
- ✅ **Multi-language Support** - Content in multiple languages
- ✅ **Author Management** - Author attribution and filtering

### Banner Management
- ✅ **Full CRUD Operations** - Create, read, update, delete banners
- ✅ **Position-based Display** - Header, Footer, Sidebar, Main, Popup
- ✅ **Multiple Content Types** - Image, Video, HTML banners
- ✅ **Smart Scheduling** - Start/end dates with auto activation
- ✅ **Analytics & Tracking** - Clicks, views, performance metrics
- ✅ **Responsive Design** - Mobile, tablet, desktop images
- ✅ **Advanced Targeting** - Priority, featured, custom CSS

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.3.1
- **Language**: Java 21
- **Database**: MySQL 8.0
- **ORM**: JPA/Hibernate
- **Migration**: Flyway
- **Mapping**: MapStruct
- **Documentation**: OpenAPI 3 (Swagger)
- **Service Discovery**: Eureka Client

### Database Schema
- **Blogs Table**: 25 fields with indexes and full-text search
- **Banners Table**: 26 fields with composite indexes for performance

## 📊 API Endpoints

### Blog APIs
```
POST   /api/blogs                    - Create blog
GET    /api/blogs/{id}               - Get blog by ID
GET    /api/blogs/slug/{slug}        - Get blog by slug
PUT    /api/blogs/{id}               - Update blog
DELETE /api/blogs/{id}               - Delete blog

GET    /api/blogs                    - List all blogs (paginated)
GET    /api/blogs/published          - List published blogs
GET    /api/blogs/featured           - List featured blogs
GET    /api/blogs/recent             - List recent blogs
GET    /api/blogs/popular            - List popular blogs
GET    /api/blogs/most-liked         - List most liked blogs

GET    /api/blogs/search             - Search blogs
GET    /api/blogs/search/published   - Search published blogs
GET    /api/blogs/search/fulltext    - Full-text search

POST   /api/blogs/{id}/publish       - Publish blog
POST   /api/blogs/{id}/unpublish     - Unpublish blog
POST   /api/blogs/{id}/archive       - Archive blog
POST   /api/blogs/{id}/feature       - Feature blog
POST   /api/blogs/{id}/like          - Like blog
POST   /api/blogs/{id}/view          - Track view

GET    /api/blogs/categories         - Get all categories
GET    /api/blogs/tags               - Get all tags
```

### Banner APIs
```
POST   /api/banners                  - Create banner
GET    /api/banners/{id}             - Get banner by ID
PUT    /api/banners/{id}             - Update banner
DELETE /api/banners/{id}             - Delete banner

GET    /api/banners                  - List all banners (paginated)
GET    /api/banners/active           - List active banners
GET    /api/banners/featured         - List featured banners
GET    /api/banners/scheduled        - List scheduled banners
GET    /api/banners/expired          - List expired banners

GET    /api/banners/position/{pos}   - Get banners by position
GET    /api/banners/active/position/{pos} - Get active banners by position

POST   /api/banners/{id}/activate    - Activate banner
POST   /api/banners/{id}/deactivate  - Deactivate banner
POST   /api/banners/{id}/feature     - Feature banner
POST   /api/banners/{id}/click       - Track click
POST   /api/banners/{id}/view        - Track view

GET    /api/banners/positions        - Get all positions
GET    /api/banners/types            - Get all types
```

## 🐳 Docker Configuration

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://content-mysql:3306/content_service
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret

# Content Configuration
CONTENT_BLOG_DEFAULT_LANGUAGE=vi
CONTENT_BLOG_DEFAULT_STATUS=DRAFT
CONTENT_BANNER_DEFAULT_TYPE=IMAGE
CONTENT_BANNER_MAX_FILE_SIZE=10485760

# File Upload
FILE_STORAGE_PATH=/app/uploads
FILE_BASE_URL=http://localhost:8900/content-service/files
```

### Docker Compose
```yaml
content-service:
  build: ./content-service
  ports:
    - "8900:8900"
  depends_on:
    - content-mysql
  environment:
    - SPRING_PROFILES_ACTIVE=docker
```

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Docker & Docker Compose

### Local Development
```bash
# Clone repository
git clone <repository-url>
cd content-service

# Copy environment file
cp .env.example .env

# Build application
mvn clean compile

# Run with Docker
docker-compose up content-service content-mysql
```

### Access Points
- **Application**: http://localhost:8900/content-service
- **Swagger UI**: http://localhost:8900/content-service/swagger-ui.html
- **Health Check**: http://localhost:8900/content-service/actuator/health
- **API Docs**: http://localhost:8900/content-service/v3/api-docs

## 📈 Performance Features

### Database Optimization
- **Indexes**: Strategic indexes on frequently queried fields
- **Full-text Search**: MySQL FULLTEXT indexes for content search
- **Composite Indexes**: Multi-column indexes for complex queries
- **Connection Pooling**: HikariCP for optimal database connections

### Caching Strategy
- **Entity Caching**: Hibernate second-level cache ready
- **Query Caching**: Optimized repository queries
- **Application Caching**: Spring Cache abstraction ready

### Monitoring
- **Health Checks**: Comprehensive health monitoring
- **Metrics**: Actuator metrics for performance tracking
- **Logging**: Structured logging with file rotation

## 🔧 Configuration

### Blog Configuration
```yaml
content:
  blog:
    default-language: vi
    default-status: DRAFT
    auto-generate-slug: true
    auto-calculate-reading-time: true
    max-content-length: 1000000
```

### Banner Configuration
```yaml
content:
  banner:
    default-type: IMAGE
    default-target: _self
    max-file-size: 10485760
    allowed-positions: [HEADER, FOOTER, SIDEBAR, MAIN, POPUP]
    allowed-types: [IMAGE, VIDEO, HTML]
```

## 📝 API Documentation

Complete API documentation is available via Swagger UI at:
`http://localhost:8900/content-service/swagger-ui.html`

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
