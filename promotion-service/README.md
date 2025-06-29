# Promotion & Discount Management Service

Promotion and Discount Management Service for E-commerce Platform - Manages discount codes, promotions, and integrates with Payment Service for seamless discount application.

## 🚀 Features

### Promotion Management
- ✅ **Full CRUD Operations** - Create, read, update, delete promotions
- ✅ **Multiple Discount Types** - Percentage, Fixed Amount, Free Shipping
- ✅ **Smart Scheduling** - Start/end dates with auto activation/deactivation
- ✅ **Stock Management** - Limited usage with real-time stock tracking
- ✅ **User Restrictions** - Max uses per user, first-time user only
- ✅ **Advanced Targeting** - Categories, products, brands, user groups
- ✅ **Auto-Apply Promotions** - Automatic discount application
- ✅ **Stackable Promotions** - Multiple promotions support

### Payment Integration
- ✅ **Seamless Integration** - Direct integration with Payment Service
- ✅ **Real-time Discount** - Instant discount calculation and application
- ✅ **Usage Tracking** - Complete audit trail of promotion usage
- ✅ **Refund Support** - Promotion restoration on payment refunds
- ✅ **Validation** - Comprehensive promotion validation before application

### Analytics & Reporting
- ✅ **Usage Analytics** - Track promotion performance and usage
- ✅ **User Analytics** - Monitor user promotion behavior
- ✅ **Revenue Impact** - Calculate discount impact on revenue
- ✅ **Popular Promotions** - Identify top-performing promotions

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.3.1
- **Language**: Java 21
- **Database**: MySQL 8.0
- **Cache**: Redis
- **ORM**: JPA/Hibernate
- **Migration**: Flyway
- **Mapping**: MapStruct
- **Documentation**: OpenAPI 3 (Swagger)
- **Service Discovery**: Eureka Client
- **Circuit Breaker**: Resilience4j

### Database Schema
- **Promotions Table**: 30+ fields with comprehensive indexing
- **Promotion Usages Table**: Complete usage tracking with analytics
- **Full-text Search**: MySQL FULLTEXT indexes for promotion search

## 📊 API Endpoints

### Promotion Management APIs
```
POST   /api/promotions                    - Create promotion
GET    /api/promotions/{id}               - Get promotion by ID
GET    /api/promotions/code/{code}        - Get promotion by code
PUT    /api/promotions/{id}               - Update promotion
DELETE /api/promotions/{id}               - Delete promotion

GET    /api/promotions                    - List all promotions (paginated)
GET    /api/promotions/active             - List active promotions
GET    /api/promotions/featured           - List featured promotions
GET    /api/promotions/scheduled          - List scheduled promotions
GET    /api/promotions/expired            - List expired promotions
GET    /api/promotions/out-of-stock       - List out of stock promotions

GET    /api/promotions/search             - Search promotions
GET    /api/promotions/search/active      - Search active promotions
GET    /api/promotions/search/fulltext    - Full-text search
```

### Promotion Application APIs (Payment Integration)
```
POST   /api/promotions/apply              - Apply promotion to order
POST   /api/promotions/validate           - Validate promotion code
GET    /api/promotions/auto-apply         - Get auto-apply promotions
POST   /api/promotions/calculate-discount - Calculate discount amount

POST   /api/promotions/usage/{id}/cancel  - Cancel promotion usage
POST   /api/promotions/usage/{id}/refund  - Refund promotion usage
POST   /api/promotions/{id}/restore-stock - Restore promotion stock
```

### Promotion Actions APIs
```
POST   /api/promotions/{id}/activate      - Activate promotion
POST   /api/promotions/{id}/deactivate    - Deactivate promotion
POST   /api/promotions/{id}/feature       - Feature promotion
POST   /api/promotions/{id}/unfeature     - Unfeature promotion

POST   /api/promotions/generate-code      - Generate unique code
GET    /api/promotions/check-code         - Check code uniqueness
```

### Stock & Analytics APIs
```
GET    /api/promotions/low-stock          - Get low stock promotions
POST   /api/promotions/{id}/update-stock  - Update promotion stock

POST   /api/promotions/tasks/activate-scheduled    - Activate scheduled
POST   /api/promotions/tasks/deactivate-expired    - Deactivate expired
POST   /api/promotions/tasks/deactivate-out-of-stock - Deactivate empty
```

## 🔗 Payment Service Integration

### Integration Flow
1. **Order Creation** → Payment Service calls Promotion Service
2. **Promotion Validation** → Check code validity and user eligibility
3. **Discount Calculation** → Calculate final discount amount
4. **Stock Management** → Decrease promotion stock
5. **Usage Tracking** → Record promotion usage
6. **Payment Update** → Update payment with discount information

### Payment Entity Updates
```java
// New fields added to Payment entity
private String promotionCode;
private Long promotionId;
private String promotionName;
private String promotionType;
private BigDecimal originalAmount;
private BigDecimal discountAmount;
private Long promotionUsageId;
private Boolean hasPromotion;
```

### Feign Client Integration
```java
@FeignClient(name = "promotion-service")
public interface PromotionServiceClient {
    @PostMapping("/api/promotions/apply")
    PromotionApplicationResult applyPromotion(@RequestBody ApplyPromotionRequest request);
}
```

## 🐳 Docker Configuration

### Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://promotion-mysql:3306/promotion_service
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret

# Redis Cache
REDIS_HOST=redis
REDIS_PORT=6379

# Promotion Configuration
PROMOTION_VALIDATION_MAX_USES_PER_USER=10
PROMOTION_CACHE_ENABLED=true
PROMOTION_AUTO_APPLY_ENABLED=true
PROMOTION_STOCK_LOW_THRESHOLD=10

# Circuit Breaker
RESILIENCE4J_CIRCUITBREAKER_PAYMENT_SERVICE_FAILURE_RATE_THRESHOLD=50
```

### Docker Compose
```yaml
promotion-service:
  build: ./promotion-service
  ports:
    - "8700:8700"
  depends_on:
    - promotion-mysql
    - redis
  environment:
    - SPRING_PROFILES_ACTIVE=docker
```

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Docker & Docker Compose

### Local Development
```bash
# Clone repository
git clone <repository-url>
cd promotion-service

# Copy environment file
cp .env.example .env

# Build application
mvn clean compile

# Run with Docker
docker-compose up promotion-service promotion-mysql redis
```

### Access Points
- **Application**: http://localhost:8700/promotion-service
- **Swagger UI**: http://localhost:8700/promotion-service/swagger-ui.html
- **Health Check**: http://localhost:8700/promotion-service/actuator/health
- **API Docs**: http://localhost:8700/promotion-service/v3/api-docs

## 📈 Performance Features

### Caching Strategy
- **Redis Caching**: Active promotions cached for fast access
- **Cache TTL**: 5 minutes for promotion data
- **Cache Invalidation**: Automatic cache clearing on updates

### Database Optimization
- **Indexes**: Strategic indexes on frequently queried fields
- **Full-text Search**: MySQL FULLTEXT indexes for promotion search
- **Composite Indexes**: Multi-column indexes for complex queries
- **Connection Pooling**: HikariCP for optimal database connections

### Circuit Breaker
- **Payment Service**: Circuit breaker for payment service calls
- **Resilience**: Automatic fallback and retry mechanisms
- **Monitoring**: Health indicators for circuit breaker status

## 🔧 Configuration

### Promotion Validation
```yaml
promotion:
  validation:
    max-uses-per-user: 10
    max-discount-percentage: 100
    max-discount-amount: 10000
    min-order-amount: 1
    code-pattern: "^[A-Z0-9_-]+$"
```

### Cache Configuration
```yaml
promotion:
  cache:
    enabled: true
    ttl: 300 # 5 minutes
    max-size: 1000
```

### Auto-Apply Configuration
```yaml
promotion:
  auto-apply:
    enabled: true
    max-promotions: 5
```

## 📝 API Documentation

Complete API documentation is available via Swagger UI at:
`http://localhost:8700/promotion-service/swagger-ui.html`

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
