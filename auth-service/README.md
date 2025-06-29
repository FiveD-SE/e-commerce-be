# Auth Service

The Authentication and Authorization service for the e-commerce platform.

## Features

- 🔐 JWT Token Management (Access & Refresh tokens)
- 📱 OTP Authentication (Email & SMS)
- 👤 User Session Management
- 🔄 Token Refresh & Validation
- 🚪 Logout from single/all devices
- 📧 Email & SMS notifications
- 🛡️ Security best practices
- 📖 OpenAPI documentation

## API Endpoints

### Authentication
- `POST /api/auth/otp/generate` - Generate and send OTP
- `POST /api/auth/otp/verify` - Verify OTP and authenticate
- `POST /api/auth/refresh` - Refresh access token
- `POST /api/auth/validate` - Validate access token
- `GET /api/auth/me` - Get current user info
- `POST /api/auth/logout` - Logout from current device
- `POST /api/auth/logout-all` - Logout from all devices
- `GET /api/auth/health` - Health check

## Configuration

### Environment Variables

#### Database
- `SPRING_DATASOURCE_URL` - Database URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

#### JWT
- `JWT_SECRET` - JWT signing secret (default: mySecretKey)
- `JWT_EXPIRATION` - Access token expiration in ms (default: 24h)
- `JWT_REFRESH_EXPIRATION` - Refresh token expiration in ms (default: 7d)

#### OTP
- `OTP_EXPIRATION` - OTP expiration in seconds (default: 300)
- `OTP_MAX_ATTEMPTS` - Max OTP attempts (default: 3)
- `OTP_RESEND_COOLDOWN` - Resend cooldown in seconds (default: 60)

#### Redis (Session Management)
- `REDIS_HOST` - Redis host (default: localhost)
- `REDIS_PORT` - Redis port (default: 6379)
- `REDIS_PASSWORD` - Redis password

#### Email (SMTP)
- `SMTP_HOST` - SMTP host (default: smtp.gmail.com)
- `SMTP_PORT` - SMTP port (default: 587)
- `SMTP_USERNAME` - SMTP username
- `SMTP_PASSWORD` - SMTP password

#### SMS
- `SMS_API_KEY` - SMS service API key
- `SMS_API_URL` - SMS service API URL
- `SMS_SENDER` - SMS sender name (default: ECOMMERCE)

#### External Services
- `USER_SERVICE_URL` - User service URL (default: http://localhost:8700/user-service)

### Application Profiles
- `dev` - Development environment
- `prod` - Production environment
- `test` - Testing environment

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- curl (for testing)
- jq (optional, for pretty JSON output)

### Easy Setup with Interactive Script

```bash
# Make the quick-start script executable
chmod +x quick-start.sh

# Run the interactive setup
./quick-start.sh
```

The quick-start script provides an interactive menu to:
- 🐳 Start dependencies (MySQL, Redis, MailHog)
- 🏗️ Build and start the complete service
- 🧪 Run comprehensive automated tests
- 📊 View service logs and monitor health
- 🌐 Access web interfaces (Swagger, MailHog, Redis)
- 🧹 Clean up resources

### Quick Test

```bash
# Start everything with Docker
docker-compose up -d

# Wait for service to be ready, then run tests
./test-scripts/test-otp-flow.sh

# View OTP emails at: http://localhost:8025
# API documentation at: http://localhost:8600/swagger-ui.html
```

## Running the Service

### Prerequisites for Manual Setup
- Java 21+ (if not using Docker)
- MySQL 8.0
- Redis (optional, for session management)

### Local Development

1. **Set environment variables:**
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/auth_service_db
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret_key_here_make_it_long_and_secure
export SMTP_USERNAME=your_email@gmail.com
export SMTP_PASSWORD=your_app_password
```

2. **Run the application:**
```bash
./mvnw spring-boot:run
```

3. **Access the service:**
- Application: http://localhost:8600/auth-service
- Swagger UI: http://localhost:8600/auth-service/swagger-ui.html
- Health Check: http://localhost:8600/auth-service/actuator/health

### Docker

```bash
# Build image
docker build -t auth-service .

# Run container
docker run -p 8600:8600 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/auth_service_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  auth-service
```

## Security Considerations

### JWT Tokens
- Use strong, unique JWT secrets
- Implement proper token rotation
- Short-lived access tokens (15-30 minutes recommended)
- Longer-lived refresh tokens (7-30 days)

### OTP Security
- Rate limiting for OTP generation
- Limited attempts per OTP
- Secure OTP delivery channels
- OTP expiration (5 minutes recommended)

### Database Security
- Use connection pooling
- Encrypt sensitive data at rest
- Regular security updates
- Proper indexing for performance

### Production Deployment
- Remove development OTP exposure
- Enable HTTPS only
- Set secure CORS policies
- Monitor and log security events
- Implement rate limiting
- Use secrets management

## Integration with Other Services

### User Service Integration
The auth service communicates with the user service to:
- Validate user existence
- Retrieve user information
- Create new users (registration flow)

Configure the user service URL:
```yaml
external:
  user-service:
    url: http://localhost:8700/user-service
```

### Frontend Integration
Frontend applications should:
1. Call `/api/auth/otp/generate` to send OTP
2. Call `/api/auth/otp/verify` to authenticate
3. Store access and refresh tokens securely
4. Include `Authorization: Bearer <token>` header
5. Handle token refresh automatically
6. Call logout endpoints when needed

## Monitoring & Observability

### Health Checks
- `/actuator/health` - Application health
- `/actuator/info` - Application info
- `/actuator/metrics` - Application metrics

### Logging
- Authentication events
- OTP generation/verification
- Token operations
- Security violations
- Performance metrics

### Metrics
- Authentication success/failure rates
- OTP delivery success rates
- Token validation performance
- Active sessions count

## Development Notes

### TODO Items
- Complete service implementations (currently stubs)
- Add comprehensive unit tests
- Implement SMS provider integration
- Add rate limiting
- Implement user service client
- Add metrics and monitoring
- Production security hardening

### Known Issues
- Service implementations are incomplete (stubs)
- SMS integration needs provider setup
- User service integration pending
- Rate limiting not implemented

## Contributing

1. Follow the existing code patterns
2. Add proper logging
3. Include unit tests
4. Update documentation
5. Follow security best practices

## Support

For issues and questions, please contact the development team. 