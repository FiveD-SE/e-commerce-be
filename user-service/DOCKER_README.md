# 🐳 User Service Docker Setup

## 📋 Overview

This document provides comprehensive instructions for running the User Service using Docker, following the microservices architecture pattern.

## 🏗️ Architecture

- **Service**: User Service
- **Port**: 8700
- **Database**: MySQL 8.0
- **Framework**: Spring Boot 3.3.0
- **Java**: OpenJDK 21

## 🚀 Quick Start

### Prerequisites
- Docker 20.10+
- Docker Compose 2.0+

### 1. Environment Setup
```bash
# Copy environment template
cp env.example .env

# Edit environment variables
nano .env
```

### 2. Development Mode (H2 Database)
```bash
# Start with H2 in-memory database
docker-compose -f docker-compose.dev.yml up --build

# Access H2 Console: http://localhost:8700/user-service/h2-console
# JDBC URL: jdbc:h2:mem:userservice_db
# Username: sa
# Password: (leave empty)
```

### 3. Standard Mode (MySQL)
```bash
# Start with MySQL database
docker-compose up --build

# View logs
docker-compose logs -f user-service
```

### 4. Production Mode
```bash
# Start production-optimized setup
docker-compose -f docker-compose.prod.yml up --build
```

## 📋 Available Configurations

### Development (`docker-compose.dev.yml`)
- **Database**: H2 in-memory
- **Profile**: `local`
- **Features**: H2 Console, Debug logging
- **Use Case**: Local development, testing

### Standard (`docker-compose.yml`)
- **Database**: MySQL 8.0
- **Profile**: `docker`
- **Features**: Persistent storage, Service discovery
- **Use Case**: Integration testing, staging

### Production (`docker-compose.prod.yml`)
- **Database**: MySQL 8.0 (optimized)
- **Profile**: `prod`
- **Features**: Resource limits, optimized JVM, logging rotation
- **Use Case**: Production deployment

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `USER_SERVICE_PORT` | Service port | `8700` | No |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `docker` | No |
| `MYSQL_PORT` | MySQL port | `3307` | No |
| `MYSQL_ROOT_PASSWORD` | MySQL root password | `secret` | Yes |
| `MYSQL_DATABASE` | Database name | `user_service` | No |
| `SPRING_DATASOURCE_USERNAME` | DB username | `root` | Yes |
| `SPRING_DATASOURCE_PASSWORD` | DB password | `secret` | Yes |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | Eureka URL | `http://eureka-server:8761/eureka/` | No |

### Spring Profiles

#### `local` (Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:userservice_db
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
  h2:
    console:
      enabled: true
```

#### `docker` (Standard)
```yaml
spring:
  datasource:
    url: jdbc:mysql://user-mysql:3306/user_service
  jpa:
    hibernate:
      ddl-auto: update
  flyway:
    enabled: true
```

#### `prod` (Production)
```yaml
spring:
  datasource:
    url: jdbc:mysql://user-mysql-prod:3306/user_service
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
logging:
  level:
    root: WARN
```

## 🛠️ Management Commands

### Service Management
```bash
# Start services
docker-compose up -d

# Stop services
docker-compose down

# Restart user service
docker-compose restart user-service

# View service logs
docker-compose logs -f user-service

# Scale service (if needed)
docker-compose up --scale user-service=2
```

### Database Management
```bash
# Access MySQL
docker-compose exec user-mysql mysql -u root -p

# Backup database
docker-compose exec user-mysql mysqldump -u root -p user_service > backup.sql

# Restore database
docker-compose exec -T user-mysql mysql -u root -p user_service < backup.sql
```

### Health Checks
```bash
# Check service health
curl http://localhost:8700/user-service/actuator/health

# Check all container health
docker-compose ps
```

## 📊 API Endpoints

### Core User Management
- `GET /api/users` - Get all users
- `POST /api/users` - Create user
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### Profile Management
- `GET /api/users/{id}/profile` - Get user profile
- `PUT /api/users/{id}/profile` - Update user profile

### Address Management
- `GET /api/users/{id}/addresses` - Get user addresses
- `POST /api/users/{id}/addresses` - Add user address

### Subscription Management
- `POST /api/users/{id}/subscribe/email` - Subscribe to email
- `DELETE /api/users/{id}/subscribe/email` - Unsubscribe from email
- `POST /api/users/{id}/subscribe/phone` - Subscribe to SMS
- `DELETE /api/users/{id}/subscribe/phone` - Unsubscribe from SMS

### Wishlist Management
- `GET /api/users/{id}/wishlist` - Get user wishlist
- `POST /api/users/{id}/wishlist` - Add to wishlist
- `DELETE /api/users/{id}/wishlist/{productId}` - Remove from wishlist

## 🔍 Monitoring & Debugging

### Application Metrics
- **Health Check**: `http://localhost:8700/user-service/actuator/health`
- **Info**: `http://localhost:8700/user-service/actuator/info`
- **Metrics**: `http://localhost:8700/user-service/actuator/metrics`

### Database Console (Development)
- **H2 Console**: `http://localhost:8700/user-service/h2-console`
- **MySQL**: Use any MySQL client connecting to `localhost:3307`

### Log Files
```bash
# Application logs
tail -f logs/spring.log

# Container logs
docker-compose logs -f user-service

# MySQL logs
docker-compose logs -f user-mysql
```

## 🚨 Troubleshooting

### Common Issues

#### Service Won't Start
```bash
# Check container status
docker-compose ps

# Check logs
docker-compose logs user-service

# Rebuild image
docker-compose build --no-cache user-service
```

#### Database Connection Issues
```bash
# Verify MySQL is running
docker-compose ps user-mysql

# Check MySQL logs
docker-compose logs user-mysql

# Test connection
docker-compose exec user-mysql mysql -u root -p -e "SHOW DATABASES;"
```

#### Port Conflicts
```bash
# Check if port is in use
lsof -i :8700
lsof -i :3307

# Change ports in .env file
echo "USER_SERVICE_PORT=8701" >> .env
echo "MYSQL_PORT=3308" >> .env
```

## 🔒 Security

### Production Security Checklist
- [ ] Change default passwords
- [ ] Use strong database passwords
- [ ] Enable SSL for database connections
- [ ] Configure proper network security
- [ ] Set up container resource limits
- [ ] Enable log rotation
- [ ] Use non-root container user

### Environment Variables Security
```bash
# Use Docker secrets for sensitive data
echo "my_secret_password" | docker secret create mysql_root_password -

# Or use external secret management
# - HashiCorp Vault
# - AWS Secrets Manager
# - Azure Key Vault
```

## 📈 Performance Optimization

### JVM Tuning
```bash
# Production JVM options
JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
```

### Database Optimization
```sql
-- Recommended MySQL settings
SET GLOBAL innodb_buffer_pool_size = 268435456;  -- 256MB
SET GLOBAL max_connections = 100;
SET GLOBAL innodb_log_file_size = 67108864;      -- 64MB
```

## 🌐 Integration with Other Services

### Service Discovery (Eureka)
```yaml
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-server:8761/eureka/
```

### API Gateway Integration
```yaml
# API Gateway routes to: /user-service/**
# Public endpoints: /api/users (GET), /api/users (POST)
# Authenticated endpoints: All others
```

### Inter-Service Communication
- **Order Service**: User validation
- **Cart Service**: User session management
- **Payment Service**: User payment methods
- **Notification Service**: User preferences

## 📚 Additional Resources

- [Spring Boot Docker Guide](https://spring.io/guides/topicals/spring-boot-docker/)
- [Docker Compose Reference](https://docs.docker.com/compose/compose-file/)
- [MySQL Docker Hub](https://hub.docker.com/_/mysql)
- [Health Check Best Practices](https://docs.docker.com/engine/reference/builder/#healthcheck) 