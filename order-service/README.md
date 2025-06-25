# Order Service

This is the Order Service for the E-Commerce platform. It is built with Spring Boot, uses MySQL for persistence, and is designed to work as a microservice with service discovery, configuration, and resilience patterns.

## Features

- RESTful API for managing orders
- MySQL database with Flyway migrations
- Service discovery with Eureka
- Centralized configuration with Spring Cloud Config
- Circuit breaker with Resilience4j
- OpenAPI documentation

## Requirements

- Java 21
- Maven 3.9+
- Docker & Docker Compose

## Build & Run

### Local Development

```
mvn clean package -DskipTests
java -jar target/*.jar
```

### Docker Compose

1. Copy `.env.example` to `.env` and adjust values as needed.
2. Run:

```
docker compose up --build
```

## Environment Variables

See `.env.example` for all supported variables.

## API Documentation

Once running, access OpenAPI docs at:

```
http://localhost:8600/order-service/swagger-ui.html
```

## Health Check

```
http://localhost:8600/order-service/actuator/health
```

## Logs

Logs are written to `/app/logs` inside the container and mapped to `./logs` on the host.

## License

MIT
