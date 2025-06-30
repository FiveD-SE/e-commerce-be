# 🐳 Docker Implementation Step-by-Step Guide

## 📋 Overview

This guide provides **actionable steps** to dockerize all microservices following the **product-service** pattern that's already successfully implemented.

## 🎯 Product-Service Pattern Analysis

Based on the existing `product-service` Docker setup, here's the proven pattern:

### 📁 File Structure (Per Service)
```
service-name/
├── Dockerfile              # Multi-stage build
├── docker-compose.yml      # Standard environment  
├── docker-compose.dev.yml  # Development (H2/simple)
├── docker-compose.prod.yml # Production (optimized)
├── .dockerignore          # Build exclusions
├── env.example           # Environment template
└── DOCKER_README.md      # Service documentation
```

### 🏗️ Docker Components
1. **Multi-stage Dockerfile** (build + runtime)
2. **Health checks** for all services
3. **Environment-specific configurations**
4. **Proper networking** (`e-commerce-network`)
5. **Volume management** for persistence
6. **Resource limits** for production
7. **Service dependencies** with health conditions

## 🚀 Step-by-Step Implementation

### Phase 1: Infrastructure Services (Week 1-2)

#### Step 1.1: Create Eureka Server Docker Setup
```bash
# Create directory structure
mkdir -p infrastructure/eureka-server
cd infrastructure/eureka-server

# Create Dockerfile
cat > Dockerfile << 'EOF'
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache curl
RUN addgroup -g 1001 app && adduser -D -s /bin/sh -u 1001 -G app app
WORKDIR /app
RUN mkdir -p /app/logs && chown -R app:app /app
COPY --from=build --chown=app:app /app/target/*.jar app.jar
USER app
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1
EXPOSE 8761
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
EOF

# Create docker-compose.yml
cat > docker-compose.yml << 'EOF'
services:
  eureka-server:
    build: .
    container_name: eureka-server
    ports:
      - "${EUREKA_PORT:-8761}:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
      - EUREKA_CLIENT_FETCH_REGISTRY=false
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    restart: unless-stopped

networks:
  e-commerce-network:
    driver: bridge
    name: e-commerce-network
EOF

# Create .dockerignore
cat > .dockerignore << 'EOF'
target/
!target/*.jar
*.class
.idea/
.vscode/
*.log
src/test/
.git/
README.md
*.sh
EOF

# Create env.example
cat > env.example << 'EOF'
EUREKA_PORT=8761
SPRING_PROFILES_ACTIVE=docker
EOF
```

#### Step 1.2: Create Config Server Docker Setup
```bash
mkdir -p infrastructure/config-server
cd infrastructure/config-server

# Follow same Dockerfile pattern as eureka-server
# Update docker-compose.yml with config-server specific settings:
# - Port: 8888
# - Depends on: eureka-server
# - Add: SPRING_CLOUD_CONFIG_SERVER_GIT_URI environment variable
```

#### Step 1.3: Create API Gateway Docker Setup
```bash
mkdir -p infrastructure/api-gateway
cd infrastructure/api-gateway

# Follow same pattern
# - Port: 8080
# - Depends on: eureka-server, config-server
# - Add routing configurations
```

### Phase 2: Enhance Existing Services (Week 3-4)

#### Step 2.1: Enhance User Service Docker Setup
```bash
cd user-service

# Copy product-service Docker files and modify:
# 1. Update ports (8502)
# 2. Update database name (user_service)
# 3. Update service name references
# 4. Keep same multi-stage build pattern
# 5. Keep same health check pattern
# 6. Add user-specific environment variables

# Update docker-compose.yml
cat > docker-compose.yml << 'EOF'
services:
  user-service:
    build: .
    container_name: user-service
    ports:
      - "${USER_SERVICE_PORT:-8502}:8502"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SERVER_PORT=8502
      - SPRING_DATASOURCE_URL=jdbc:mysql://user-mysql:3306/user_service?createDatabaseIfNotExist=true
      - SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME:-root}
      - SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD:-secret}
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    depends_on:
      user-mysql:
        condition: service_healthy
    networks:
      - e-commerce-network
    volumes:
      - ./logs:/app/logs
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8502/user-service/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    restart: unless-stopped

  user-mysql:
    image: mysql:8.0
    container_name: user-mysql
    ports:
      - "${MYSQL_PORT:-3306}:3306"
    environment:
      - MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-secret}
      - MYSQL_DATABASE=user_service
    volumes:
      - user-mysql-data:/var/lib/mysql
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-p${MYSQL_ROOT_PASSWORD:-secret}"]
      interval: 10s
      timeout: 5s
      retries: 20
    restart: unless-stopped

networks:
  e-commerce-network:
    driver: bridge
    name: e-commerce-network

volumes:
  user-mysql-data:
    driver: local
EOF
```

#### Step 2.2: Implement Order Service Docker Setup
```bash
cd order-service

# Copy entire Docker setup from product-service
# Update all references:
# - Service name: order-service
# - Port: 8503
# - Database: order_service
# - Container names: order-service, order-mysql
# - Volume names: order-mysql-data
```

### Phase 3: Create New Services (Week 5-8)

#### Step 3.1: Create Cart Service (Redis-based)
```bash
mkdir -p services/cart-service
cd services/cart-service

# Create Spring Boot project first, then Docker setup
# Special consideration: Uses Redis instead of MySQL

cat > docker-compose.yml << 'EOF'
services:
  cart-service:
    build: .
    container_name: cart-service
    ports:
      - "${CART_SERVICE_PORT:-8504}:8504"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SERVER_PORT=8504
      - SPRING_DATA_REDIS_HOST=cart-redis
      - SPRING_DATA_REDIS_PORT=6379
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    depends_on:
      cart-redis:
        condition: service_healthy
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8504/cart-service/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    restart: unless-stopped

  cart-redis:
    image: redis:7-alpine
    container_name: cart-redis
    ports:
      - "${REDIS_PORT:-6379}:6379"
    command: redis-server --appendonly yes
    volumes:
      - cart-redis-data:/data
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    restart: unless-stopped

networks:
  e-commerce-network:
    driver: bridge
    name: e-commerce-network

volumes:
  cart-redis-data:
    driver: local
EOF
```

#### Step 3.2: Create Payment Service
```bash
mkdir -p services/payment-service
cd services/payment-service

# Follow product-service pattern exactly
# - Port: 8505
# - Database: payment_service  
# - Add PAYOS_API_KEY environment variable
```

#### Step 3.3: Create Inventory Service
```bash
mkdir -p services/inventory-service
cd services/inventory-service

# Follow product-service pattern exactly
# - Port: 8506
# - Database: inventory_service
```

#### Step 3.4: Create Auth Service (Redis-based)
```bash
mkdir -p services/auth-service
cd services/auth-service

# Similar to cart-service (Redis-based)
# - Port: 8507
# - Redis for JWT tokens and sessions
# - Add JWT_SECRET environment variable
```

#### Step 3.5: Create Notification Service
```bash
mkdir -p services/notification-service
cd services/notification-service

# Follow product-service pattern
# - Port: 8508
# - Database: notification_service
# - Add MAIL_HOST, SMS_API_KEY environment variables
```

### Phase 4: Advanced Services (Week 9-10)

#### Step 4.1: Create Content Management Service
```bash
mkdir -p services/content-management-service
cd services/content-management-service

# Follow product-service pattern
# - Port: 8509
# - Database: content_service
```

#### Step 4.2: Create Media Service
```bash
mkdir -p services/media-service
cd services/media-service

# Follow product-service pattern
# - Port: 8510
# - Database: media_service
# - Add volume for file uploads
# - Add AWS_S3_BUCKET environment variable
```

#### Step 4.3: Create Search Service (Elasticsearch-based)
```bash
mkdir -p services/search-service
cd services/search-service

# Special setup with Elasticsearch
cat > docker-compose.yml << 'EOF'
services:
  search-service:
    build: .
    container_name: search-service
    ports:
      - "${SEARCH_SERVICE_PORT:-8511}:8511"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SERVER_PORT=8511
      - SPRING_ELASTICSEARCH_URIS=http://elasticsearch:9200
      - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
    depends_on:
      elasticsearch:
        condition: service_healthy
    networks:
      - e-commerce-network
    restart: unless-stopped

  elasticsearch:
    image: elasticsearch:8.12.0
    container_name: search-elasticsearch
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
      - xpack.security.enabled=false
    volumes:
      - search-elasticsearch-data:/usr/share/elasticsearch/data
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:9200/_cluster/health"]
      interval: 30s
      timeout: 10s
      retries: 5

networks:
  e-commerce-network:
    driver: bridge
    name: e-commerce-network

volumes:
  search-elasticsearch-data:
    driver: local
EOF
```

## 🔗 Root Docker Compose Setup

### Step 5: Create Full Stack Docker Compose
```bash
# In project root
cat > docker-compose.yml << 'EOF'
services:
  # Infrastructure Services  
  eureka-server:
    build: ./infrastructure/eureka-server
    container_name: eureka-server
    ports:
      - "8761:8761"
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8761/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5

  config-server:
    build: ./infrastructure/config-server
    container_name: config-server
    ports:
      - "8888:8888"
    depends_on:
      eureka-server:
        condition: service_healthy
    networks:
      - e-commerce-network

  api-gateway:
    build: ./infrastructure/api-gateway
    container_name: api-gateway
    ports:
      - "8080:8080"
    depends_on:
      eureka-server:
        condition: service_healthy
      config-server:
        condition: service_healthy
    networks:
      - e-commerce-network

  # Business Services
  user-service:
    build: ./services/user-service
    depends_on:
      user-mysql:
        condition: service_healthy
      eureka-server:
        condition: service_healthy

  product-service:
    build: ./services/product-service
    depends_on:
      product-mysql:
        condition: service_healthy
      eureka-server:
        condition: service_healthy

  order-service:
    build: ./services/order-service
    depends_on:
      order-mysql:
        condition: service_healthy
      eureka-server:
        condition: service_healthy

  # ... continue for all services

  # Databases
  user-mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=secret
      - MYSQL_DATABASE=user_service
    volumes:
      - user-mysql-data:/var/lib/mysql
    networks:
      - e-commerce-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost", "-u", "root", "-psecret"]

  # ... continue for all databases

networks:
  e-commerce-network:
    driver: bridge
    name: e-commerce-network

volumes:
  user-mysql-data:
  product-mysql-data:
  # ... continue for all volumes
EOF
```

## 🛠️ Management Scripts

### Step 6: Create Helper Scripts
```bash
mkdir scripts

# Create start-all.sh
cat > scripts/start-all.sh << 'EOF'
#!/bin/bash
echo "Starting E-Commerce Microservices..."

# Start infrastructure first
docker-compose up -d eureka-server config-server
sleep 30

# Start API Gateway
docker-compose up -d api-gateway
sleep 20

# Start business services
docker-compose up -d user-service product-service order-service cart-service payment-service inventory-service auth-service notification-service content-management-service media-service search-service

echo "All services started!"
echo "API Gateway: http://localhost:8080"
echo "Eureka: http://localhost:8761"
EOF

chmod +x scripts/start-all.sh

# Create health-check.sh
cat > scripts/health-check.sh << 'EOF'
#!/bin/bash
services=("eureka-server:8761" "user-service:8502" "product-service:8501" "order-service:8503")

for service in "${services[@]}"; do
  name=$(echo $service | cut -d':' -f1)
  port=$(echo $service | cut -d':' -f2)
  
  if curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
    echo "✅ $name is healthy"
  else
    echo "❌ $name is not responding"
  fi
done
EOF

chmod +x scripts/health-check.sh
```

## 📋 Implementation Checklist

### Infrastructure Services
- [ ] eureka-server Docker setup
- [ ] config-server Docker setup  
- [ ] api-gateway Docker setup

### Existing Services Enhancement
- [ ] user-service Docker enhancement
- [ ] order-service Docker implementation

### New Services Implementation
- [ ] cart-service Docker setup (Redis)
- [ ] payment-service Docker setup
- [ ] inventory-service Docker setup
- [ ] auth-service Docker setup (Redis)
- [ ] notification-service Docker setup
- [ ] content-management-service Docker setup
- [ ] media-service Docker setup
- [ ] search-service Docker setup (Elasticsearch)

### Full Stack Setup
- [ ] Root docker-compose.yml
- [ ] Management scripts
- [ ] Environment configurations
- [ ] Documentation

## 🎯 Key Success Patterns

1. **Consistency**: Every service follows the exact same Docker pattern as product-service
2. **Health Checks**: All services have proper health checks
3. **Dependencies**: Services wait for their dependencies to be healthy
4. **Networking**: All services use the same `e-commerce-network`
5. **Volumes**: Proper data persistence for databases
6. **Environment Variables**: Configurable through .env files
7. **Documentation**: Each service has its own DOCKER_README.md

## 🚦 Testing Each Service

```bash
# Test individual service
cd service-name
docker-compose up --build

# Test health
curl http://localhost:PORT/actuator/health

# Test API endpoint
curl http://localhost:PORT/service-name/api/endpoint
```

This guide provides the exact steps to implement Docker for all services following the proven product-service pattern. Each service maintains consistency while adapting to its specific requirements (MySQL vs Redis vs Elasticsearch). 