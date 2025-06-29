#!/bin/bash

# Payment Service API Testing Script
# This script tests the main endpoints of the Payment Service

BASE_URL="http://localhost:8800/payment-service"
CONTENT_TYPE="Content-Type: application/json"

echo "🚀 Testing Payment Service APIs"
echo "================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print test results
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅ $2${NC}"
    else
        echo -e "${RED}❌ $2${NC}"
    fi
}

# Function to test endpoint
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local description=$4
    
    echo -e "\n${YELLOW}Testing: $description${NC}"
    echo "Endpoint: $method $endpoint"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "$CONTENT_TYPE" \
            -d "$data" \
            "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method \
            -H "$CONTENT_TYPE" \
            "$BASE_URL$endpoint")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    echo "Response Code: $http_code"
    echo "Response Body: $body"
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        print_result 0 "$description"
        return 0
    else
        print_result 1 "$description"
        return 1
    fi
}

# Wait for service to be ready
echo "⏳ Waiting for Payment Service to be ready..."
for i in {1..30}; do
    if curl -s "$BASE_URL/actuator/health" > /dev/null; then
        echo -e "${GREEN}✅ Payment Service is ready!${NC}"
        break
    fi
    echo "Attempt $i/30: Service not ready yet..."
    sleep 2
done

# Test Health Check
test_endpoint "GET" "/actuator/health" "" "Health Check"

# Test API Documentation
test_endpoint "GET" "/v3/api-docs" "" "API Documentation"

# Test Create Payment Method
payment_method_data='{
    "userId": 1,
    "name": "Test Credit Card",
    "type": "CREDIT_CARD",
    "gateway": "STRIPE",
    "cardHolderName": "John Doe",
    "cardExpMonth": 12,
    "cardExpYear": 2025,
    "setAsDefault": true,
    "billingAddress": "123 Test St",
    "billingCity": "Test City",
    "billingPostalCode": "12345",
    "billingCountry": "US"
}'

test_endpoint "POST" "/api/payment-methods" "$payment_method_data" "Create Payment Method"

# Test Get Payment Methods for User
test_endpoint "GET" "/api/payment-methods/user/1" "" "Get Payment Methods for User"

# Test Create Payment
payment_data='{
    "orderId": 1,
    "userId": 1,
    "userEmail": "test@example.com",
    "amount": 100.00,
    "currency": "VND",
    "gateway": "STRIPE",
    "paymentMethodType": "CREDIT_CARD",
    "description": "Test payment for order #1",
    "timeoutMinutes": 15
}'

test_endpoint "POST" "/api/payments" "$payment_data" "Create Payment"

# Test Get All Payments
test_endpoint "GET" "/api/payments?page=0&size=10" "" "Get All Payments"

# Test Get Payments by User
test_endpoint "GET" "/api/payments/user/1?page=0&size=10" "" "Get Payments by User"

# Test Payment Analytics
test_endpoint "GET" "/api/payments/count/status/PENDING" "" "Count Payments by Status"

# Test Payment Method Analytics
test_endpoint "GET" "/api/payment-methods/count/user/1" "" "Count Payment Methods by User"

echo -e "\n🏁 API Testing Complete!"
echo "================================"

# Test with invalid data
echo -e "\n${YELLOW}Testing Error Handling...${NC}"

# Test with invalid payment data
invalid_payment_data='{
    "orderId": null,
    "userId": null,
    "amount": -100.00
}'

test_endpoint "POST" "/api/payments" "$invalid_payment_data" "Create Payment with Invalid Data (Should Fail)"

# Test non-existent payment
test_endpoint "GET" "/api/payments/99999" "" "Get Non-existent Payment (Should Fail)"

echo -e "\n${GREEN}🎉 All tests completed!${NC}"
echo "Check the results above to see which tests passed or failed."
