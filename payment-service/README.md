# Payment Service

A comprehensive payment processing microservice built with Spring Boot, supporting multiple payment gateways and providing secure payment method management.

## 🚀 Features

### Core Payment Features
- **Multiple Payment Gateways**: Stripe, PayPal, VNPay, MoMo, ZaloPay, Bank Transfer
- **Payment Processing**: Create, confirm, fail, cancel, and expire payments
- **Refund Management**: Full and partial refunds with detailed tracking
- **Payment Methods**: Secure storage and management of customer payment methods
- **Webhook Support**: Real-time payment status updates from gateways

### Security & Compliance
- **PCI DSS Compliance**: Secure handling of payment data
- **Risk Assessment**: Fraud detection and risk scoring
- **Data Encryption**: Sensitive data encryption at rest and in transit
- **Audit Trail**: Comprehensive logging of all payment activities

### Integration & APIs
- **RESTful APIs**: Comprehensive REST API with OpenAPI documentation
- **Service Discovery**: Eureka integration for microservices architecture
- **Database**: MySQL with Flyway migrations
- **Monitoring**: Actuator endpoints for health checks and metrics

## 🏗️ Architecture

```
payment-service/
├── src/main/java/com/pm/paymentservice/
│   ├── config/          # Configuration classes
│   ├── controller/      # REST controllers
│   ├── dto/            # Data Transfer Objects
│   ├── mapper/         # MapStruct mappers
│   ├── model/          # JPA entities
│   ├── repository/     # Data repositories
│   └── service/        # Business logic
├── src/main/resources/
│   ├── db/migration/   # Flyway database migrations
│   └── application.yml # Application configuration
└── src/test/          # Unit and integration tests
```

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **Migration**: Flyway
- **Mapping**: MapStruct
- **Documentation**: OpenAPI 3 (Swagger)
- **Containerization**: Docker & Docker Compose
- **Service Discovery**: Eureka Client

## 🚦 Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose
- MySQL 8.0+ (if running locally)

### Quick Start with Docker

1. **Clone and navigate to the service**
   ```bash
   cd payment-service
   ```

2. **Copy environment configuration**
   ```bash
   cp env.example .env
   ```

3. **Start the services**
   ```bash
   docker-compose up -d
   ```

4. **Verify the service is running**
   ```bash
   curl http://localhost:8800/payment-service/actuator/health
   ```

### Local Development

1. **Start MySQL database**
   ```bash
   docker-compose up payment-mysql -d
   ```

2. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**
   - API: http://localhost:8800/payment-service
   - Swagger UI: http://localhost:8800/payment-service/swagger-ui.html
   - Health Check: http://localhost:8800/payment-service/actuator/health

## 📚 API Documentation

### Main Endpoints

#### Payments
- `POST /api/payments` - Create a new payment
- `GET /api/payments/{id}` - Get payment details
- `PUT /api/payments/{id}/confirm` - Confirm payment
- `POST /api/payments/{id}/refund` - Refund payment
- `GET /api/payments/user/{userId}` - Get user payments

#### Payment Methods
- `POST /api/payment-methods` - Create payment method
- `GET /api/payment-methods/user/{userId}` - Get user payment methods
- `PUT /api/payment-methods/{id}/set-default` - Set default payment method
- `DELETE /api/payment-methods/{id}` - Delete payment method

#### Webhooks
- `POST /api/payments/webhooks/{gateway}` - Process payment webhooks

### Example Requests

#### Create Payment
```bash
curl -X POST http://localhost:8800/payment-service/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "userEmail": "user@example.com",
    "amount": 100.00,
    "currency": "VND",
    "gateway": "STRIPE",
    "description": "Payment for order #1"
  }'
```

#### Create Payment Method
```bash
curl -X POST http://localhost:8800/payment-service/api/payment-methods \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "name": "My Credit Card",
    "type": "CREDIT_CARD",
    "gateway": "STRIPE",
    "cardHolderName": "John Doe",
    "cardExpMonth": 12,
    "cardExpYear": 2025,
    "setAsDefault": true
  }'
```

## 🧪 Testing

### Run Tests
```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Test with Docker
docker-compose exec payment-service mvn test
```

### API Testing
```bash
# Make the test script executable
chmod +x test-apis.sh

# Run API tests
./test-apis.sh
```

## 🔧 Configuration

### Environment Variables
See `env.example` for all available configuration options.

Key configurations:
- `PAYMENT_SERVICE_PORT`: Service port (default: 8800)
- `SPRING_DATASOURCE_*`: Database connection settings
- `STRIPE_*`: Stripe payment gateway configuration
- `PAYPAL_*`: PayPal payment gateway configuration

### Payment Gateway Setup

#### Stripe
```yaml
stripe:
  secret-key: ${STRIPE_SECRET_KEY}
  publishable-key: ${STRIPE_PUBLISHABLE_KEY}
  webhook-secret: ${STRIPE_WEBHOOK_SECRET}
```

#### PayPal
```yaml
paypal:
  client-id: ${PAYPAL_CLIENT_ID}
  client-secret: ${PAYPAL_CLIENT_SECRET}
  mode: ${PAYPAL_MODE:sandbox}
```

## 🐳 Docker

### Development
```bash
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### Production
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

See [DOCKER_README.md](DOCKER_README.md) for detailed Docker instructions.

## 📊 Monitoring

### Health Checks
- **Service Health**: `/actuator/health`
- **Database Health**: `/actuator/health/db`
- **Disk Space**: `/actuator/health/diskSpace`

### Metrics
- **Application Metrics**: `/actuator/metrics`
- **Prometheus Metrics**: `/actuator/prometheus`
- **Custom Metrics**: Payment processing rates, success rates, etc.

## 🔒 Security

### Data Protection
- Sensitive payment data is encrypted
- PCI DSS compliance measures implemented
- Secure API endpoints with proper validation

### Authentication & Authorization
- JWT token validation
- Role-based access control
- API rate limiting

## 🤝 Integration

### With Order Service
The Payment Service integrates with the Order Service to:
- Automatically create payments when orders are placed
- Update order status based on payment status
- Handle payment confirmations and failures

### Webhook Integration
```bash
# Example webhook payload
{
  "orderId": 1,
  "paymentId": 123,
  "status": "COMPLETED",
  "gatewayTransactionId": "txn_abc123",
  "amount": 100.00,
  "currency": "VND"
}
```

## 📝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Check the [DOCKER_README.md](DOCKER_README.md) for Docker-related issues
- Review the API documentation at `/swagger-ui.html`
- Check application logs for error details
- Ensure all required environment variables are set
