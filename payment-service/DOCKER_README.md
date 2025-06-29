# Payment Service Docker Setup

## Overview
This document provides instructions for running the Payment Service using Docker and Docker Compose.

## Prerequisites
- Docker 20.10+
- Docker Compose 2.0+
- At least 2GB RAM available
- Ports 8800 and 3310 available

## Quick Start

### Development Environment
```bash
# Copy environment file
cp env.example .env

# Edit .env file with your configuration
nano .env

# Start services in development mode
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# View logs
docker-compose logs -f payment-service
```

### Production Environment
```bash
# Copy environment file
cp env.example .env.prod

# Edit .env.prod file with production configuration
nano .env.prod

# Start services in production mode
docker-compose -f docker-compose.yml -f docker-compose.prod.yml --env-file .env.prod up -d
```

## Service Endpoints

### Health Check
- **URL**: http://localhost:8800/payment-service/actuator/health
- **Method**: GET
- **Description**: Check service health status

### API Documentation
- **Swagger UI**: http://localhost:8800/payment-service/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8800/payment-service/v3/api-docs

### Main APIs
- **Payments**: http://localhost:8800/payment-service/api/payments
- **Payment Methods**: http://localhost:8800/payment-service/api/payment-methods

## Database Access

### MySQL Connection
- **Host**: localhost
- **Port**: 3310
- **Database**: payment_service
- **Username**: root
- **Password**: secret (default)

### Database Management
```bash
# Connect to MySQL
docker-compose exec payment-mysql mysql -u root -p payment_service

# Backup database
docker-compose exec payment-mysql mysqldump -u root -p payment_service > backup.sql

# Restore database
docker-compose exec -T payment-mysql mysql -u root -p payment_service < backup.sql
```

## Monitoring

### Logs
```bash
# View all logs
docker-compose logs

# View payment service logs only
docker-compose logs payment-service

# Follow logs in real-time
docker-compose logs -f payment-service

# View last 100 lines
docker-compose logs --tail=100 payment-service
```

### Metrics
- **Actuator Endpoints**: http://localhost:8800/payment-service/actuator
- **Metrics**: http://localhost:8800/payment-service/actuator/metrics
- **Prometheus**: http://localhost:8800/payment-service/actuator/prometheus

## Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Check what's using the port
   netstat -tulpn | grep :8800
   
   # Change port in .env file
   PAYMENT_SERVICE_PORT=8801
   ```

2. **Database Connection Failed**
   ```bash
   # Check MySQL container status
   docker-compose ps payment-mysql
   
   # Check MySQL logs
   docker-compose logs payment-mysql
   
   # Restart MySQL
   docker-compose restart payment-mysql
   ```

3. **Service Won't Start**
   ```bash
   # Check service logs
   docker-compose logs payment-service
   
   # Rebuild image
   docker-compose build --no-cache payment-service
   
   # Remove and recreate containers
   docker-compose down
   docker-compose up -d
   ```

### Performance Tuning

1. **Memory Settings**
   ```bash
   # Add to docker-compose.yml
   environment:
     - JAVA_OPTS=-Xmx1g -Xms512m
   ```

2. **Database Optimization**
   ```bash
   # Add to MySQL configuration
   command: --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci --innodb-buffer-pool-size=256M
   ```

## Security

### Environment Variables
- Never commit `.env` files to version control
- Use strong passwords for production
- Rotate secrets regularly

### Network Security
```bash
# Use custom networks
networks:
  payment-network:
    driver: bridge
    ipam:
      config:
        - subnet: 172.20.0.0/16
```

## Backup and Recovery

### Automated Backup
```bash
# Create backup script
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker-compose exec payment-mysql mysqldump -u root -p$MYSQL_ROOT_PASSWORD payment_service > backup_$DATE.sql
```

### Data Volumes
```bash
# List volumes
docker volume ls

# Backup volume
docker run --rm -v payment_payment-mysql-data:/data -v $(pwd):/backup alpine tar czf /backup/payment-data.tar.gz /data

# Restore volume
docker run --rm -v payment_payment-mysql-data:/data -v $(pwd):/backup alpine tar xzf /backup/payment-data.tar.gz -C /
```

## Development

### Hot Reload
```bash
# Enable development mode with hot reload
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d
```

### Testing
```bash
# Run tests in container
docker-compose exec payment-service mvn test

# Run specific test
docker-compose exec payment-service mvn test -Dtest=PaymentServiceTest
```

## Integration with Other Services

### Service Discovery
The Payment Service registers with Eureka Server for service discovery.

### API Gateway
Configure API Gateway to route requests to Payment Service:
```yaml
routes:
  - id: payment-service
    uri: lb://payment-service
    predicates:
      - Path=/payment-service/**
```
