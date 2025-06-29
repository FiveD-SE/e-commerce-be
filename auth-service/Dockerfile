# Multi-stage build for optimized production image
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven files first for better layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (cached layer if pom.xml unchanged)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# Runtime stage with minimal footprint
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 app && adduser -D -s /bin/sh -u 1001 -G app app

# Set working directory
WORKDIR /app

# Create logs directory
RUN mkdir -p /app/logs && chown -R app:app /app

# Copy JAR file from build stage
COPY --from=build --chown=app:app /app/target/*.jar app.jar

# Switch to non-root user
USER app

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT:-8600}/auth-service/actuator/health || exit 1

# Expose port (configurable via environment variable)
EXPOSE ${SERVER_PORT:-8600}

# JVM tuning for container environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] 