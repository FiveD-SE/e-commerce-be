# User Service

A microservice component of the E-commerce platform responsible for managing user-related operations. This service is built using Spring Boot and follows microservices architecture principles.

## 🚀 Technologies

- Java 21
- Spring Boot 3.3.0
- Spring Cloud 2023.0.3
- Spring Data JPA
- MySQL 8.0
- Flyway (Database Migration)
- Docker & Docker Compose
- Lombok
- MapStruct
- Spring Cloud Netflix Eureka Client

## 🛠️ Prerequisites

- Java 21 or higher
- Maven
- Docker and Docker Compose
- MySQL 8.0 (if running locally)

## 🔧 Configuration

The service can be configured using environment variables:

| Variable                             | Description               | Default                           |
| ------------------------------------ | ------------------------- | --------------------------------- |
| USER_SERVICE_PORT                    | Port for the user service | 8700                              |
| SPRING_DATASOURCE_USERNAME           | MySQL username            | root                              |
| SPRING_DATASOURCE_PASSWORD           | MySQL password            | secret                            |
| MYSQL_PORT                           | MySQL port                | 3306                              |
| MYSQL_ROOT_PASSWORD                  | MySQL root password       | -                                 |
| MYSQL_DATABASE                       | MySQL database name       | -                                 |
| EUREKA_CLIENT_SERVICEURL_DEFAULTZONE | Eureka server URL         | http://eureka-server:8761/eureka/ |
| SPRING_PROFILES_ACTIVE               | Active Spring profile     | dev                               |
| LOG_FILE_PATH                        | Path for log files        | /app/logs                         |

## 🚀 Running the Service

### Using Docker Compose

1. Create a `.env` file with the required environment variables:

```bash
USER_SERVICE_PORT=8700
MYSQL_PORT=3306
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=user_service
```

2. Start the service:

```bash
docker-compose up -d
```

### Running Locally

1. Build the project:

```bash
./mvnw clean install
```

2. Run the application:

```bash
./mvnw spring-boot:run
```

## 📁 Project Structure

```
user-service/
├── src/                    # Source code
├── logs/                   # Application logs
├── docker-compose.yml      # Docker Compose configuration
├── Dockerfile             # Docker configuration
└── pom.xml                # Maven dependencies
```

## 🔍 Health Check

The service includes a health check endpoint at:

```
http://localhost:8700/user-service/actuator/health
```

## 🔄 Database Migrations

Database migrations are handled by Flyway. Migration scripts should be placed in:

```
src/main/resources/db/migration/
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.
