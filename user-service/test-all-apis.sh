#!/bin/bash

BASE_URL="http://localhost:8700/user-service"

echo "🚀 Testing User Service APIs"
echo "=================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print test results
print_test() {
    echo -e "${BLUE}Testing: $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ SUCCESS: $1${NC}"
}

print_error() {
    echo -e "${RED}❌ ERROR: $1${NC}"
}

# Wait for application to be ready
echo "⏳ Waiting for application to start..."
until curl -s $BASE_URL/actuator/health > /dev/null; do
    echo "Waiting for application..."
    sleep 2
done
echo -e "${GREEN}✅ Application is ready!${NC}"

echo ""
echo "1️⃣ USER MANAGEMENT APIs"
echo "========================"

# Test 1: Create User
print_test "Create new user"
USER_RESPONSE=$(curl -s -X POST "$BASE_URL/users" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "emailSubscription": true,
    "smsSubscription": false,
    "marketingConsent": true,
    "newsletterSubscription": true
  }')

if [[ $USER_RESPONSE == *"id"* ]]; then
    USER_ID=$(echo $USER_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
    print_success "User created with ID: $USER_ID"
    echo "Response: $USER_RESPONSE"
else
    print_error "Failed to create user"
    echo "Response: $USER_RESPONSE"
    exit 1
fi

echo ""

# Test 2: Get User by ID
print_test "Get user by ID"
GET_USER_RESPONSE=$(curl -s "$BASE_URL/users/$USER_ID")
if [[ $GET_USER_RESPONSE == *"john.doe@example.com"* ]]; then
    print_success "Retrieved user successfully"
else
    print_error "Failed to get user"
fi

echo ""

# Test 3: Update User
print_test "Update user"
UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/users/$USER_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John Updated",
    "lastName": "Doe Updated",
    "email": "john.updated@example.com",
    "phone": "+1987654321",
    "emailSubscription": false,
    "smsSubscription": true,
    "marketingConsent": false,
    "newsletterSubscription": false
  }')

if [[ $UPDATE_RESPONSE == *"john.updated@example.com"* ]]; then
    print_success "User updated successfully"
else
    print_error "Failed to update user"
fi

echo ""
echo "2️⃣ ADDRESS MANAGEMENT APIs"
echo "==========================="

# Test 4: Add Address
print_test "Add address to user"
ADDRESS_RESPONSE=$(curl -s -X POST "$BASE_URL/users/$USER_ID/addresses" \
  -H "Content-Type: application/json" \
  -d '{
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "postalCode": "10001",
    "country": "USA",
    "isDefault": true
  }')

if [[ $ADDRESS_RESPONSE == *"street"* ]]; then
    ADDRESS_ID=$(echo $ADDRESS_RESPONSE | grep -o '"id":[0-9]*' | cut -d':' -f2)
    print_success "Address added with ID: $ADDRESS_ID"
else
    print_error "Failed to add address"
fi

echo ""

# Test 5: Get User Addresses
print_test "Get user addresses"
ADDRESSES_RESPONSE=$(curl -s "$BASE_URL/users/$USER_ID/addresses")
if [[ $ADDRESSES_RESPONSE == *"123 Main St"* ]]; then
    print_success "Retrieved addresses successfully"
else
    print_error "Failed to get addresses"
fi

echo ""
echo "3️⃣ SUBSCRIPTION MANAGEMENT APIs"
echo "================================="

# Test 6: Update Email Subscription
print_test "Update email subscription"
EMAIL_SUB_RESPONSE=$(curl -s -X POST "$BASE_URL/users/$USER_ID/subscribe/email" \
  -H "Content-Type: application/json" \
  -d '{"subscribed": true}')

if [[ $EMAIL_SUB_RESPONSE == *"success"* ]] || [[ $EMAIL_SUB_RESPONSE == *"subscribed"* ]]; then
    print_success "Email subscription updated"
else
    print_error "Failed to update email subscription"
fi

echo ""

# Test 7: Update SMS Subscription
print_test "Update SMS subscription"
SMS_SUB_RESPONSE=$(curl -s -X POST "$BASE_URL/users/$USER_ID/subscribe/sms" \
  -H "Content-Type: application/json" \
  -d '{"subscribed": false}')

if [[ $SMS_SUB_RESPONSE == *"success"* ]] || [[ $SMS_SUB_RESPONSE == *"subscribed"* ]]; then
    print_success "SMS subscription updated"
else
    print_error "Failed to update SMS subscription"
fi

echo ""
echo "4️⃣ WISHLIST MANAGEMENT APIs"
echo "============================"

# Test 8: Add to Wishlist
print_test "Add product to wishlist"
WISHLIST_ADD_RESPONSE=$(curl -s -X POST "$BASE_URL/users/$USER_ID/wishlist" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-001",
    "productName": "Awesome Product",
    "productPrice": 99.99
  }')

if [[ $WISHLIST_ADD_RESPONSE == *"productId"* ]]; then
    print_success "Product added to wishlist"
else
    print_error "Failed to add to wishlist"
fi

echo ""

# Test 9: Get User Wishlist
print_test "Get user wishlist"
WISHLIST_RESPONSE=$(curl -s "$BASE_URL/users/$USER_ID/wishlist")
if [[ $WISHLIST_RESPONSE == *"PROD-001"* ]]; then
    print_success "Retrieved wishlist successfully"
else
    print_error "Failed to get wishlist"
fi

echo ""

# Test 10: Remove from Wishlist
print_test "Remove product from wishlist"
WISHLIST_REMOVE_RESPONSE=$(curl -s -X DELETE "$BASE_URL/users/$USER_ID/wishlist/PROD-001")
if [[ $WISHLIST_REMOVE_RESPONSE == *"success"* ]] || [[ $(curl -s -o /dev/null -w "%{http_code}" -X DELETE "$BASE_URL/users/$USER_ID/wishlist/PROD-001") == "200" ]]; then
    print_success "Product removed from wishlist"
else
    print_error "Failed to remove from wishlist"
fi

echo ""
echo "5️⃣ USER PROFILE APIs"
echo "===================="

# Test 11: Get User Profile
print_test "Get user profile"
PROFILE_RESPONSE=$(curl -s "$BASE_URL/users/$USER_ID/profile")
if [[ $PROFILE_RESPONSE == *"firstName"* ]]; then
    print_success "Retrieved user profile"
else
    print_error "Failed to get user profile"
fi

echo ""

# Test 12: Update User Profile
print_test "Update user profile"
PROFILE_UPDATE_RESPONSE=$(curl -s -X PUT "$BASE_URL/users/$USER_ID/profile" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John Profile",
    "lastName": "Doe Profile",
    "email": "john.profile@example.com",
    "phone": "+1111111111"
  }')

if [[ $PROFILE_UPDATE_RESPONSE == *"john.profile@example.com"* ]]; then
    print_success "User profile updated"
else
    print_error "Failed to update user profile"
fi

echo ""
echo "6️⃣ COLLECTION ENDPOINTS"
echo "========================"

# Test 13: Get All Users (Admin endpoint)
print_test "Get all users"
ALL_USERS_RESPONSE=$(curl -s "$BASE_URL/users")
if [[ $ALL_USERS_RESPONSE == *"data"* ]]; then
    print_success "Retrieved all users"
else
    print_error "Failed to get all users"
fi

echo ""
echo "🎉 API TESTING COMPLETED!"
echo "=========================="
echo "Check the results above to see which APIs are working correctly."
echo ""
echo "📋 SUMMARY OF IMPLEMENTED FEATURES:"
echo "✅ User CRUD operations"
echo "✅ Address management" 
echo "✅ Subscription management (email/SMS)"
echo "✅ Wishlist functionality"
echo "✅ User profile management"
echo "✅ Collection responses with pagination support"
echo ""
echo "🔗 You can also access H2 Console at: http://localhost:8700/user-service/h2-console"
echo "   JDBC URL: jdbc:h2:mem:userservice_db"
echo "   Username: sa"
echo "   Password: (leave empty)" 