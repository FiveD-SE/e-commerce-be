# 🐳 Auth Service Docker Setup

## 📋 Overview

This document provides comprehensive instructions for running the **Auth Service** using Docker containers. The service handles authentication, JWT token management, OTP verification, and user session management.

## 🏗️ Architecture

### Core Components
- **Auth Service**: Spring Boot application (Port 8600)
- **MySQL**: Database for persistent data (Port 3307)
- **Redis**: Session and JWT token storage (Port 6380)

### Optional Components
- **MailHog**: Email testing (Development)
- **Redis Commander**: Redis management UI
- **Nginx**: Reverse proxy (Production)
- **Zipkin**: Distributed tracing
- **Eureka**: Service discovery

## 🚀 Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- 4GB+ RAM available
- Ports 8600, 3307, 6380 available

### 1. Development Environment (H2 + Redis)
```bash
# Start development environment with H2 database
docker-compose -f docker-compose.dev.yml up --build

# Access points:
# - Auth Service: http://localhost:8600/auth-service
# - H2 Console: http://localhost:8600/auth-service/h2-console
# - MailHog UI: http://localhost:8025
# - Redis Commander: http://localhost:8081
```

### 2. Standard Environment (MySQL + Redis)
```bash
# Start with MySQL database
docker-compose up --build

# Access points:
# - Auth Service: http://localhost:8600/auth-service
# - MySQL: localhost:3307
# - Redis: localhost:6380
```

### 3. Production Environment
```bash
# Create production environment file
cp env.example .env.prod

# Edit .env.prod with production values
vim .env.prod

# Start production environment
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# With Nginx reverse proxy
docker-compose -f docker-compose.prod.yml --profile nginx up -d
```

## 📁 File Structure

```
auth-service/
├── Dockerfile                 # Multi-stage build
├── docker-compose.yml         # Standard environment
├── docker-compose.dev.yml     # Development environment
├── docker-compose.prod.yml    # Production environment
├── .dockerignore              # Build exclusions
├── env.example               # Environment template
├── DOCKER_README.md          # This file
├── logs/                     # Application logs
└── docker/                   # Docker configurations
    ├── nginx/
    ├── mysql/
    └── redis/
```

## 🔧 Configuration

### Environment Variables

#### Core Service Configuration
```bash
AUTH_SERVICE_PORT=8600          # External port
SERVER_PORT=8600                # Internal port
SPRING_PROFILES_ACTIVE=docker   # Spring profile
```

#### Database Configuration
```bash
MYSQL_DATABASE=auth_service
MYSQL_ROOT_PASSWORD=secret
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_FLYWAY_ENABLED=true
```

#### Redis Configuration
```bash
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_PASSWORD=redis_pass
```

#### JWT Configuration
```bash
JWT_SECRET=mySecretJWTKeyForAuthService123456789
JWT_EXPIRATION=86400000         # 24 hours
JWT_REFRESH_EXPIRATION=604800000 # 7 days
```

#### OTP Configuration
```bash
OTP_EXPIRATION=300              # 5 minutes
OTP_MAX_ATTEMPTS=3
OTP_RESEND_COOLDOWN=60          # 1 minute
```

#### Email Configuration
```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=your-email@gmail.com
SMTP_PASSWORD=your-app-password
```

#### SMS Configuration
```bash
SMS_API_KEY=your-sms-api-key
SMS_API_URL=https://api.sms-provider.com/send
SMS_SENDER=ECOMMERCE
```

### Environment Files

#### Development (.env.dev)
```bash
SPRING_PROFILES_ACTIVE=dev
SERVICES_USER_SERVICE_MOCK_ENABLED=true
SERVICES_NOTIFICATION_SERVICE_MOCK_ENABLED=true
SPRING_FLYWAY_ENABLED=false
```

#### Production (.env.prod)
```bash
SPRING_PROFILES_ACTIVE=prod
SERVICES_USER_SERVICE_MOCK_ENABLED=false
SERVICES_NOTIFICATION_SERVICE_MOCK_ENABLED=false
SPRING_FLYWAY_ENABLED=true
# Add all production secrets
```

## 🎯 Usage Examples

### API Testing
```bash
# Health check
curl http://localhost:8600/auth-service/actuator/health

# Generate OTP
curl -X POST http://localhost:8600/auth-service/api/auth/otp/generate \
  -H "Content-Type: application/json" \
  -d '{"identifier":"user@example.com","type":"EMAIL","purpose":"REGISTRATION"}'

# Verify OTP
curl -X POST http://localhost:8600/auth-service/api/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{"identifier":"user@example.com","type":"EMAIL","otpCode":"123456","deviceInfo":"Test Device","userAgent":"Test Agent"}'

# Validate JWT token
curl -X POST http://localhost:8600/auth-service/api/auth/validate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Database Access
```bash
# Connect to MySQL
docker exec -it auth-service-mysql mysql -u root -p auth_service

# Connect to Redis
docker exec -it auth-service-redis redis-cli
```

### Log Monitoring
```bash
# View service logs
docker-compose logs -f auth-service

# View all logs
docker-compose logs -f

# View specific container logs
docker logs auth-service-mysql
```

## 🔍 Monitoring & Health Checks

### Health Endpoints
- **Application**: `GET /auth-service/actuator/health`
- **Database**: `GET /auth-service/actuator/health/db`
- **Redis**: `GET /auth-service/actuator/health/redis`

### Metrics
- **Prometheus**: `GET /auth-service/actuator/prometheus`
- **Metrics**: `GET /auth-service/actuator/metrics`

### Management
- **Info**: `GET /auth-service/actuator/info`
- **Env**: `GET /auth-service/actuator/env`

## 🛠️ Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check container status
docker-compose ps

# Check logs
docker-compose logs auth-service

# Check resource usage
docker stats
```

#### Database Connection Issues
```bash
# Check MySQL health
docker exec auth-service-mysql mysqladmin ping -h localhost -u root -p

# Check database exists
docker exec auth-service-mysql mysql -u root -p -e "SHOW DATABASES;"

# Reset database
docker-compose down -v
docker-compose up --build
```

#### Redis Connection Issues
```bash
# Check Redis health
docker exec auth-service-redis redis-cli ping

# Check Redis info
docker exec auth-service-redis redis-cli info

# Clear Redis data
docker exec auth-service-redis redis-cli FLUSHALL
```

#### Port Conflicts
```bash
# Check port usage
netstat -tulpn | grep :8600

# Use different ports
AUTH_SERVICE_PORT=8601 docker-compose up
```

### Performance Tuning

#### Memory Settings
```bash
# Increase JVM memory
JAVA_OPTS="-Xmx1024m -Xms512m" docker-compose up

# Increase MySQL memory
MYSQL_INNODB_BUFFER_POOL_SIZE=512M docker-compose up
```

#### Connection Pooling
```bash
# Increase database connections
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20 docker-compose up
```

## 🔒 Security Considerations

### Production Security
- Change default passwords
- Use strong JWT secrets
- Enable SSL/TLS
- Implement rate limiting
- Use Redis password protection
- Regular security updates

### Environment Separation
- Never use development configurations in production
- Separate environment files
- Use Docker secrets for sensitive data
- Implement proper logging

## 📊 Scaling

### Horizontal Scaling
```bash
# Scale auth service instances
docker-compose up --scale auth-service=3

# Use load balancer (nginx profile)
docker-compose -f docker-compose.prod.yml --profile nginx up
```

### High Availability
```bash
# Redis Sentinel for HA
docker-compose -f docker-compose.prod.yml --profile sentinel up

# MySQL Master-Slave setup
# (Requires additional configuration)
```

## 🧪 Testing

### Integration Testing
```bash
# Run with test profile
SPRING_PROFILES_ACTIVE=test docker-compose -f docker-compose.dev.yml up

# Run test scripts
./test-scripts/test-auth-apis.sh
```

### Load Testing
```bash
# Use Apache Bench
ab -n 1000 -c 10 http://localhost:8600/auth-service/actuator/health

# Use curl for API testing
curl -X POST http://localhost:8600/auth-service/api/auth/otp/generate \
  -H "Content-Type: application/json" \
  -d '{"identifier":"test@example.com","type":"EMAIL","purpose":"LOGIN"}'
```

## 📝 Maintenance

### Backup
```bash
# Backup MySQL data
docker exec auth-service-mysql mysqldump -u root -p auth_service > backup.sql

# Backup Redis data
docker exec auth-service-redis redis-cli BGSAVE
```

### Updates
```bash
# Update images
docker-compose pull

# Rebuild and restart
docker-compose down
docker-compose up --build
```

### Cleanup
```bash
# Remove containers and volumes
docker-compose down -v

# Clean up unused resources
docker system prune -a
```

## 🔗 Related Services

- **User Service**: http://localhost:8502/user-service
- **Product Service**: http://localhost:8501/product-service
- **Order Service**: http://localhost:8503/order-service
- **API Gateway**: http://localhost:8080

## 📞 Support

For issues and questions:
1. Check logs: `docker-compose logs auth-service`
2. Review health checks: `curl http://localhost:8600/auth-service/actuator/health`
3. Verify configuration: `docker-compose config`
4. Check resource usage: `docker stats`

## 🔄 Version History

- **v1.0.0**: Initial Docker implementation
- **v1.1.0**: Added Redis integration
- **v1.2.0**: Production optimizations
- **v1.3.0**: Multi-environment support 