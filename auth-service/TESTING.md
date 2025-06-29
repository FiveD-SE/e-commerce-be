# Auth Service Testing Plan

## 🎯 **Testing Phases**

### **Phase 1: Environment Setup**
1. **Docker Environment**
   ```bash
   # Start dependencies
   docker-compose up -d mysql redis mailhog
   
   # Verify services are running
   docker-compose ps
   
   # Check logs
   docker-compose logs -f
   ```

2. **Database Verification**
   ```bash
   # Connect to MySQL and verify tables
   docker exec -it auth-service_mysql_1 mysql -u auth_user -p auth_service_db
   
   # Should see tables: auth_tokens, otp_tokens, auth_sessions
   SHOW TABLES;
   ```

3. **Redis Verification**
   ```bash
   # Test Redis connection
   docker exec -it auth-service_redis_1 redis-cli ping
   # Should return: PONG
   ```

### **Phase 2: Service Startup Testing**

1. **Build and Start Service**
   ```bash
   # Build the service
   docker-compose build auth-service
   
   # Start the service
   docker-compose up auth-service
   
   # Health check
   curl http://localhost:8600/api/auth/health
   ```

2. **API Documentation**
   - Visit: http://localhost:8600/swagger-ui.html
   - Verify all endpoints are listed
   - Test JWT authentication setup

## 🧪 **Functional Testing Scenarios**

### **Test Suite 1: OTP Generation & Validation**

#### **1.1 Email OTP Generation**
```bash
# Valid email OTP request
curl -X POST http://localhost:8600/api/auth/otp/generate \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "test@example.com",
    "type": "EMAIL",
    "purpose": "LOGIN"
  }'

# Expected: 200 OK with masked email and expiration
```

#### **1.2 Phone OTP Generation**
```bash
# Valid phone OTP request
curl -X POST http://localhost:8600/api/auth/otp/generate \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "+1234567890",
    "type": "PHONE",
    "purpose": "REGISTRATION"
  }'

# Expected: 200 OK with masked phone and expiration
```

#### **1.3 Input Validation Tests**
```bash
# Test empty identifier
curl -X POST http://localhost:8600/api/auth/otp/generate \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "",
    "type": "EMAIL",
    "purpose": "LOGIN"
  }'

# Expected: 400 Bad Request - "Identifier cannot be empty"

# Test invalid email format
curl -X POST http://localhost:8600/api/auth/otp/generate \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "invalid-email",
    "type": "EMAIL",
    "purpose": "LOGIN"
  }'

# Expected: 400 Bad Request - "Invalid email format"
```

### **Test Suite 2: OTP Verification & Authentication**

#### **2.1 Valid OTP Verification**
```bash
# First generate OTP, then verify (use OTP from response in dev mode)
curl -X POST http://localhost:8600/api/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "test@example.com",
    "type": "EMAIL",
    "otpCode": "123456",
    "deviceInfo": "Test Device",
    "userAgent": "Test Agent"
  }'

# Expected: 200 OK with JWT tokens and user info
```

#### **2.2 Invalid OTP Tests**
```bash
# Wrong OTP code
curl -X POST http://localhost:8600/api/auth/otp/verify \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "test@example.com",
    "type": "EMAIL",
    "otpCode": "000000"
  }'

# Expected: 400 Bad Request - "Invalid OTP code"

# Expired OTP (wait 5+ minutes after generation)
# Expected: 400 Bad Request - "OTP has expired"
```

### **Test Suite 3: Token Management**

#### **3.1 Token Validation**
```bash
# Use access token from previous authentication
curl -X POST http://localhost:8600/api/auth/validate \
  -H "Content-Type: application/json" \
  -d '{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'

# Expected: 200 OK with validation result
```

#### **3.2 Token Refresh**
```bash
# Use refresh token from authentication
curl -X POST http://localhost:8600/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "deviceInfo": "Test Device"
  }'

# Expected: 200 OK with new tokens
```

#### **3.3 Get Current User**
```bash
# Use valid access token
curl -X GET http://localhost:8600/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Expected: 200 OK with user information
```

### **Test Suite 4: Logout Operations**

#### **4.1 Single Device Logout**
```bash
curl -X POST http://localhost:8600/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Expected: 200 OK, token should be invalidated
```

#### **4.2 All Devices Logout**
```bash
curl -X POST http://localhost:8600/api/auth/logout-all \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Expected: 200 OK, all user tokens invalidated
```

## 🔧 **Integration Testing**

### **Test Suite 5: Service Integration**

#### **5.1 User Service Integration**
- **Prerequisite**: User service running on port 8500
- Test user existence checks
- Test user creation during registration
- Test user retrieval by email/phone/ID

#### **5.2 Email Integration**
- Check MailHog UI: http://localhost:8025
- Verify OTP emails are received
- Test email content and formatting

#### **5.3 Database Integration**
```sql
-- Check OTP tokens are saved
SELECT * FROM otp_tokens ORDER BY created_at DESC LIMIT 5;

-- Check auth tokens are saved
SELECT * FROM auth_tokens WHERE is_active = TRUE LIMIT 5;

-- Check cleanup is working
SELECT COUNT(*) FROM otp_tokens WHERE expires_at < NOW();
```

## 🚀 **Performance Testing**

### **Test Suite 6: Load & Performance**

#### **6.1 OTP Generation Load Test**
```bash
# Using Apache Bench (install if needed)
ab -n 100 -c 10 -T application/json -p otp_request.json http://localhost:8600/api/auth/otp/generate

# otp_request.json content:
# {"identifier":"test@example.com","type":"EMAIL","purpose":"LOGIN"}
```

#### **6.2 Concurrent OTP Verification**
- Test multiple simultaneous OTP verifications
- Verify no race conditions in attempt counting
- Check database integrity under load

## 🛡️ **Security Testing**

### **Test Suite 7: Security Validation**

#### **7.1 Rate Limiting**
- Test OTP generation cooldown (60 seconds)
- Test maximum attempts (3 attempts)
- Verify proper error messages

#### **7.2 JWT Security**
- Test token expiration
- Test token tampering detection
- Verify proper token invalidation

#### **7.3 Input Sanitization**
- Test SQL injection attempts
- Test XSS in input fields
- Validate proper error handling

## 📊 **Monitoring & Observability**

### **Test Suite 8: Operational Testing**

#### **8.1 Health Checks**
```bash
# Service health
curl http://localhost:8600/api/auth/health

# Actuator endpoints
curl http://localhost:8600/actuator/health
curl http://localhost:8600/actuator/metrics
```

#### **8.2 Logging Verification**
```bash
# Check application logs
docker-compose logs auth-service | grep -i "error\|warn"

# Check successful operations
docker-compose logs auth-service | grep -i "success"
```

## 🔄 **Automated Testing**

### **Test Suite 9: Unit Tests**
```bash
# Run unit tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

### **Test Suite 10: Integration Tests**
```bash
# Run integration tests
./mvnw test -Dspring.profiles.active=test

# Test with embedded database
./mvnw test -Dspring.datasource.url=jdbc:h2:mem:testdb
```

## 📋 **Testing Checklist**

### **Pre-deployment Checklist**
- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] OTP generation and verification work
- [ ] JWT tokens are properly generated and validated
- [ ] User service integration works
- [ ] Email notifications are sent
- [ ] Database migrations apply correctly
- [ ] Redis connection is established
- [ ] Security validations pass
- [ ] Performance benchmarks meet requirements
- [ ] Error handling is comprehensive
- [ ] Logging is appropriate
- [ ] Health checks work
- [ ] Docker containerization works
- [ ] Environment variables are properly configured

### **Production Readiness**
- [ ] Remove development mode OTP exposure
- [ ] Configure proper SMS provider
- [ ] Set up production JWT secrets
- [ ] Configure production database
- [ ] Set up proper logging levels
- [ ] Configure monitoring and alerting
- [ ] Set up backup and recovery
- [ ] Security audit completed
- [ ] Load testing completed
- [ ] Documentation updated

## 🚨 **Known Issues & Limitations**

1. **SMS Integration**: Currently configured for development with logging. Production SMS provider needed.
2. **User Service Dependency**: Requires user-service to be running for full functionality.
3. **Java Version**: Requires Java 21+ for compilation and runtime.
4. **Database**: MySQL 8.0+ required for proper JSON support and performance.

## 🔧 **Troubleshooting Guide**

### **Common Issues**
1. **Java not found**: Install Java 21+ or use Docker
2. **Port conflicts**: Change ports in docker-compose.yml
3. **Database connection failed**: Check MySQL container and credentials
4. **Redis connection failed**: Verify Redis container is running
5. **OTP not received**: Check MailHog UI and SMTP configuration
6. **Token validation fails**: Check JWT secret and expiration settings

### **Debug Commands**
```bash
# Check service logs
docker-compose logs -f auth-service

# Check database
docker exec -it auth-service_mysql_1 mysql -u auth_user -p

# Check Redis
docker exec -it auth-service_redis_1 redis-cli

# Test endpoints
curl -v http://localhost:8600/api/auth/health
``` 