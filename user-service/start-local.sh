#!/bin/bash

echo "🚀 Starting User Service locally..."
echo "==================================="

export JAVA_HOME=/opt/homebrew/opt/openjdk@21
export PATH="$JAVA_HOME/bin:$PATH"
export SPRING_PROFILES_ACTIVE="local"

echo "✅ Java version: $(java -version 2>&1 | head -1)"
echo "✅ Profile: $SPRING_PROFILES_ACTIVE"
echo "✅ Port: 8700"
echo ""
echo "🔗 Application will be available at: http://localhost:8700/user-service"
echo "🔗 H2 Console: http://localhost:8700/user-service/h2-console"
echo "🔗 Health Check: http://localhost:8700/user-service/actuator/health"
echo ""
echo "⏳ Starting application..."

./mvnw spring-boot:run 