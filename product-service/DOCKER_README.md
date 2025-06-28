# 🐳 Product Service Docker Configuration

## 📋 Overview

This document provides comprehensive instructions for running the Product Service using Docker in different environments.

## 🏗️ Available Configurations

### 1. **Development Environment** (`docker-compose.dev.yml`)
- **Database**: H2 (in-memory)
- **Profile**: `dev`
- **Use Case**: Quick development and testing
- **Data Persistence**: None (data lost on restart)

### 2. **Standard Environment** (`docker-compose.yml`)
- **Database**: MySQL 8.0
- **Profile**: `docker`
- **Use Case**: Integration testing with persistent data
- **Data Persistence**: Yes (MySQL volumes)

### 3. **Production Environment** (`docker-compose.prod.yml`)
- **Database**: MySQL 8.0 with optimizations
- **Additional Services**: Redis, Zipkin
- **Profile**: `docker`
- **Use Case**: Production deployment
- **Features**: Resource limits, enhanced security, monitoring

## 🚀 Quick Start

### Development (H2 Database)
```bash
# Copy environment file
cp env.example .env

# Start with H2 database (fastest startup)
docker-compose -f docker-compose.dev.yml up --build

# Access application
curl http://localhost:8501/product-service/actuator/health
```

### Standard (MySQL Database)
```bash
# Copy and configure environment
cp env.example .env
# Edit .env file with your preferences

# Start with MySQL database
docker-compose up --build

# Access application
curl http://localhost:8501/product-service/actuator/health
```

### Production
```bash
# Configure production environment
cp env.example .env.prod
# Edit .env.prod with production values

# Start production stack
docker-compose -f docker-compose.prod.yml --env-file .env.prod up -d

# Check status
docker-compose -f docker-compose.prod.yml ps
```

## 🔧 Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PRODUCT_SERVICE_PORT` | `8501` | External port for product service |
| `SERVER_PORT` | `8501` | Internal Spring Boot port |
| `SPRING_PROFILES_ACTIVE` | `docker` | Spring profile to use |
| `MYSQL_DATABASE` | `product_service` | MySQL database name |
| `MYSQL_ROOT_PASSWORD` | `secret` | MySQL root password |
| `SPRING_DATASOURCE_USERNAME` | `root` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `secret` | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate DDL mode |
| `SPRING_FLYWAY_ENABLED` | `true` | Enable Flyway migrations |

### Optional Services

#### Redis (Caching)
```bash
# Start with Redis
docker-compose --profile redis up

# Environment variables
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
```

#### Zipkin (Distributed Tracing)
```bash
# Start with Zipkin
docker-compose --profile zipkin up

# Environment variables
ZIPKIN_PORT=9411
SPRING_ZIPKIN_BASE_URL=http://zipkin:9411/
```

#### Eureka (Service Discovery)
```bash
# Start with Eureka
docker-compose --profile eureka up

# Environment variables
EUREKA_PORT=8761
EUREKA_CLIENT_ENABLED=true
```

## 📊 Application Endpoints

### Health & Monitoring
- **Health Check**: `http://localhost:8501/product-service/actuator/health`
- **Metrics**: `http://localhost:8501/product-service/actuator/metrics`
- **Prometheus**: `http://localhost:8501/product-service/actuator/prometheus`

### API Documentation
- **Swagger UI**: `http://localhost:8501/product-service/swagger-ui.html`
- **OpenAPI Docs**: `http://localhost:8501/product-service/api-docs`

### Development Tools (Dev Profile Only)
- **H2 Console**: `http://localhost:8501/product-service/h2-console`
  - JDBC URL: `jdbc:h2:mem:product_service`
  - Username: `sa`
  - Password: (empty)

## 🛠️ Docker Commands Reference

### Build & Start
```bash
# Build and start (development)
docker-compose -f docker-compose.dev.yml up --build

# Build and start (standard)
docker-compose up --build

# Start in background
docker-compose up -d

# Start specific services
docker-compose up product-service mysql
```

### Logs & Debugging
```bash
# View logs
docker-compose logs -f product-service

# View all logs
docker-compose logs -f

# Execute into container
docker-compose exec product-service bash

# Check container health
docker-compose ps
```

### Data Management
```bash
# Stop and remove containers (keeps volumes)
docker-compose down

# Remove containers and volumes (data loss)
docker-compose down -v

# Reset everything
docker-compose down -v --rmi all
```

### Database Operations
```bash
# Access MySQL CLI
docker-compose exec mysql mysql -u root -p product_service

# Backup database
docker-compose exec mysql mysqldump -u root -p product_service > backup.sql

# Restore database
docker-compose exec -T mysql mysql -u root -p product_service < backup.sql
```

## 🐛 Troubleshooting

### Common Issues

#### 1. Port Already in Use
```bash
# Check what's using the port
lsof -i :8501

# Change port in .env file
PRODUCT_SERVICE_PORT=8502
```

#### 2. Database Connection Issues
```bash
# Check MySQL health
docker-compose exec mysql mysqladmin ping -h localhost -u root -p

# View database logs
docker-compose logs mysql

# Reset database
docker-compose down -v
docker volume rm product-service_mysql-data
docker-compose up
```

#### 3. Application Won't Start
```bash
# Check application logs
docker-compose logs product-service

# Check health endpoint
curl http://localhost:8501/product-service/actuator/health

# Verify environment variables
docker-compose exec product-service env | grep SPRING
```

#### 4. Build Issues
```bash
# Clean build
docker-compose down
docker-compose build --no-cache
docker-compose up

# Check disk space
docker system df
docker system prune
```

## 🏥 Health Checks

All services include comprehensive health checks:

### Product Service
- **Endpoint**: `/product-service/actuator/health`
- **Interval**: 30s
- **Timeout**: 10s
- **Retries**: 5

### MySQL
- **Command**: `mysqladmin ping`
- **Interval**: 10s
- **Timeout**: 5s
- **Retries**: 20

### Redis
- **Command**: `redis-cli ping`
- **Interval**: 10s
- **Timeout**: 5s
- **Retries**: 5

## 📈 Performance Tuning

### Production Optimizations
- **JVM Settings**: G1GC, container-aware heap sizing
- **MySQL**: Optimized buffer pools and connection limits
- **Redis**: Memory limits and eviction policies
- **Docker**: Resource limits and reservations

### Monitoring
```bash
# Container resource usage
docker stats

# Check health status
docker-compose ps

# View metrics
curl http://localhost:8501/product-service/actuator/metrics
```

## 🔐 Security Considerations

### Production Security
- Non-root user in containers
- Secrets management via environment variables
- Network isolation
- Resource limits
- Health checks for security monitoring

### Database Security
- Strong passwords
- Limited user privileges
- SSL connections (production)
- Regular backups

## 📝 Development Workflow

### 1. Quick Testing (H2)
```bash
docker-compose -f docker-compose.dev.yml up --build
# Test your changes quickly
docker-compose -f docker-compose.dev.yml down
```

### 2. Integration Testing (MySQL)
```bash
docker-compose up --build
# Test with persistent data
# Run your test suite
docker-compose down
```

### 3. Production Simulation
```bash
docker-compose -f docker-compose.prod.yml up
# Test production-like environment
docker-compose -f docker-compose.prod.yml down
```

## 🆘 Support

For issues or questions:
1. Check logs: `docker-compose logs product-service`
2. Verify health: `curl http://localhost:8501/product-service/actuator/health`
3. Check environment: `docker-compose exec product-service env`
4. Review documentation above

---

**Happy Dockerizing! 🐳** 