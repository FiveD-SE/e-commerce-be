#!/bin/bash

# Auth Service OTP Flow Test Script
# This script tests the complete OTP generation and verification flow

BASE_URL="http://localhost:8600"
EMAIL="test@example.com"
PHONE="+1234567890"

echo "🧪 Auth Service OTP Flow Test"
echo "==============================="

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

# Function to extract JSON field
extract_json_field() {
    echo "$1" | grep -o "\"$2\":[^,}]*" | cut -d':' -f2 | tr -d '"' | tr -d ' '
}

echo "Testing service health..."
health_response=$(curl -s -w "%{http_code}" -o /tmp/health_response.json "$BASE_URL/api/auth/health")
health_code="${health_response: -3}"
print_result $([[ "$health_code" == "200" ]] && echo 0 || echo 1) "Health check"

if [ "$health_code" != "200" ]; then
    echo -e "${RED}Service is not healthy. Please start the service first.${NC}"
    exit 1
fi

echo -e "\n${YELLOW}🔐 Testing Email OTP Flow${NC}"
echo "1. Generating OTP for email: $EMAIL"

# Generate OTP for email
otp_response=$(curl -s -X POST "$BASE_URL/api/auth/otp/generate" \
    -H "Content-Type: application/json" \
    -d "{
        \"identifier\": \"$EMAIL\",
        \"type\": \"EMAIL\",
        \"purpose\": \"LOGIN\"
    }")

echo "$otp_response" | jq . 2>/dev/null || echo "$otp_response"

# Extract OTP code (in development mode)
otp_code=$(extract_json_field "$otp_response" "otpCode")
success=$(extract_json_field "$otp_response" "success")

print_result $([[ "$success" == "true" ]] && echo 0 || echo 1) "Email OTP generation"

if [ "$success" == "true" ] && [ ! -z "$otp_code" ]; then
    echo "Generated OTP: $otp_code"
    
    echo "2. Verifying OTP..."
    sleep 2 # Wait a moment
    
    # Verify OTP
    verify_response=$(curl -s -X POST "$BASE_URL/api/auth/otp/verify" \
        -H "Content-Type: application/json" \
        -d "{
            \"identifier\": \"$EMAIL\",
            \"type\": \"EMAIL\",
            \"otpCode\": \"$otp_code\",
            \"deviceInfo\": \"Test Device\",
            \"userAgent\": \"Test Script\"
        }")
    
    echo "$verify_response" | jq . 2>/dev/null || echo "$verify_response"
    
    # Extract tokens
    access_token=$(extract_json_field "$verify_response" "accessToken")
    refresh_token=$(extract_json_field "$verify_response" "refreshToken")
    
    print_result $([[ ! -z "$access_token" ]] && echo 0 || echo 1) "Email OTP verification"
    
    if [ ! -z "$access_token" ]; then
        echo "Access Token: ${access_token:0:50}..."
        
        echo "3. Testing token validation..."
        validate_response=$(curl -s -X POST "$BASE_URL/api/auth/validate" \
            -H "Content-Type: application/json" \
            -d "{\"token\": \"$access_token\"}")
        
        echo "$validate_response" | jq . 2>/dev/null || echo "$validate_response"
        is_valid=$(extract_json_field "$validate_response" "valid")
        print_result $([[ "$is_valid" == "true" ]] && echo 0 || echo 1) "Token validation"
        
        echo "4. Testing get current user..."
        user_response=$(curl -s -X GET "$BASE_URL/api/auth/me" \
            -H "Authorization: Bearer $access_token")
        
        echo "$user_response" | jq . 2>/dev/null || echo "$user_response"
        user_id=$(extract_json_field "$user_response" "userId")
        print_result $([[ ! -z "$user_id" ]] && echo 0 || echo 1) "Get current user"
        
        if [ ! -z "$refresh_token" ]; then
            echo "5. Testing token refresh..."
            refresh_response=$(curl -s -X POST "$BASE_URL/api/auth/refresh" \
                -H "Content-Type: application/json" \
                -d "{
                    \"refreshToken\": \"$refresh_token\",
                    \"deviceInfo\": \"Test Device Updated\"
                }")
            
            echo "$refresh_response" | jq . 2>/dev/null || echo "$refresh_response"
            new_access_token=$(extract_json_field "$refresh_response" "accessToken")
            print_result $([[ ! -z "$new_access_token" ]] && echo 0 || echo 1) "Token refresh"
            
            if [ ! -z "$new_access_token" ]; then
                access_token="$new_access_token"
            fi
        fi
        
        echo "6. Testing logout..."
        logout_response=$(curl -s -X POST "$BASE_URL/api/auth/logout" \
            -H "Authorization: Bearer $access_token")
        
        echo "$logout_response" | jq . 2>/dev/null || echo "$logout_response"
        print_result $? "Logout"
    fi
fi

echo -e "\n${YELLOW}📱 Testing Phone OTP Flow${NC}"
echo "1. Generating OTP for phone: $PHONE"

# Generate OTP for phone
phone_otp_response=$(curl -s -X POST "$BASE_URL/api/auth/otp/generate" \
    -H "Content-Type: application/json" \
    -d "{
        \"identifier\": \"$PHONE\",
        \"type\": \"PHONE\",
        \"purpose\": \"REGISTRATION\"
    }")

echo "$phone_otp_response" | jq . 2>/dev/null || echo "$phone_otp_response"

phone_otp_code=$(extract_json_field "$phone_otp_response" "otpCode")
phone_success=$(extract_json_field "$phone_otp_response" "success")

print_result $([[ "$phone_success" == "true" ]] && echo 0 || echo 1) "Phone OTP generation"

echo -e "\n${YELLOW}🔍 Testing Input Validation${NC}"

echo "1. Testing empty identifier..."
empty_response=$(curl -s -X POST "$BASE_URL/api/auth/otp/generate" \
    -H "Content-Type: application/json" \
    -d "{
        \"identifier\": \"\",
        \"type\": \"EMAIL\",
        \"purpose\": \"LOGIN\"
    }")

echo "$empty_response" | jq . 2>/dev/null || echo "$empty_response"
print_result $([[ "$empty_response" == *"cannot be empty"* ]] && echo 0 || echo 1) "Empty identifier validation"

echo "2. Testing invalid email format..."
invalid_email_response=$(curl -s -X POST "$BASE_URL/api/auth/otp/generate" \
    -H "Content-Type: application/json" \
    -d "{
        \"identifier\": \"invalid-email\",
        \"type\": \"EMAIL\",
        \"purpose\": \"LOGIN\"
    }")

echo "$invalid_email_response" | jq . 2>/dev/null || echo "$invalid_email_response"
print_result $([[ "$invalid_email_response" == *"Invalid email"* ]] && echo 0 || echo 1) "Invalid email validation"

echo "3. Testing wrong OTP code..."
wrong_otp_response=$(curl -s -X POST "$BASE_URL/api/auth/otp/verify" \
    -H "Content-Type: application/json" \
    -d "{
        \"identifier\": \"$EMAIL\",
        \"type\": \"EMAIL\",
        \"otpCode\": \"000000\"
    }")

echo "$wrong_otp_response" | jq . 2>/dev/null || echo "$wrong_otp_response"
print_result $([[ "$wrong_otp_response" == *"Invalid OTP"* ]] && echo 0 || echo 1) "Wrong OTP validation"

echo -e "\n${GREEN}🎉 Test Suite Completed!${NC}"
echo "Check the results above for any failures."
echo "Visit http://localhost:8025 to see email OTPs in MailHog"
echo "Visit http://localhost:8600/swagger-ui.html for API documentation" 