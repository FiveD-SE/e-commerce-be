#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Base URL
BASE_URL="http://localhost:8500/product-service"

# Function to print test results
print_result() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✓ $2${NC}"
    else
        echo -e "${RED}✗ $2${NC}"
    fi
}

# Function to make API calls and check response
test_endpoint() {
    local method=$1
    local endpoint=$2
    local data=$3
    local expected_status=$4
    local description=$5

    if [ -z "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -d "$data")
    fi

    status_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | sed '$d')

    if [ "$status_code" -eq "$expected_status" ]; then
        print_result 0 "$description"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        print_result 1 "$description (Expected $expected_status, got $status_code)"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    fi
    echo "----------------------------------------"
}

echo "Starting API Tests..."
echo "----------------------------------------"

# Test Category APIs
echo "Testing Category APIs:"

# Create a category
category_data='{
    "name": "Test Category",
    "parentId": null,
    "status": "ACTIVE",
    "displayOrder": 1,
    "attributes": "{\"color\": \"blue\"}"
}'
test_endpoint "POST" "/api/categories" "$category_data" 201 "Create Category"

# Get category ID from response
category_id=$(echo "$body" | jq -r '.id')
if [ -z "$category_id" ]; then
    echo "Failed to get category ID, skipping remaining category tests"
else
    # Get all categories
    test_endpoint "GET" "/api/categories" "" 200 "Get All Categories"

    # Get category by ID
    test_endpoint "GET" "/api/categories/$category_id" "" 200 "Get Category by ID"

    # Update category
    update_category_data='{
        "name": "Updated Test Category",
        "parentId": null,
        "status": "ACTIVE",
        "displayOrder": 2,
        "attributes": "{\"color\": \"red\"}"
    }'
    test_endpoint "PUT" "/api/categories/$category_id" "$update_category_data" 200 "Update Category"

    # Test Product APIs
    echo -e "\nTesting Product APIs:"

    # Create a product
    product_data='{
        "name": "Test Product",
        "description": "A test product",
        "price": 99.99,
        "sku": "TEST-SKU-001",
        "categoryId": "'$category_id'",
        "status": "active",
        "attributes": "{\"color\": \"blue\", \"size\": \"medium\"}",
        "quantity": 100,
        "reservedQuantity": 0
    }'
    test_endpoint "POST" "/api/products" "$product_data" 201 "Create Product"

    # Get product ID from response
    product_id=$(echo "$body" | jq -r '.id')
    if [ -z "$product_id" ]; then
        echo "Failed to get product ID, skipping remaining product tests"
    else
        # Get all products
        test_endpoint "GET" "/api/products" "" 200 "Get All Products"

        # Get product by ID
        test_endpoint "GET" "/api/products/$product_id" "" 200 "Get Product by ID"

        # Update product
        update_product_data='{
            "name": "Updated Test Product",
            "description": "An updated test product",
            "price": 149.99,
            "sku": "TEST-SKU-001",
            "categoryId": "'$category_id'",
            "status": "active",
            "attributes": "{\"color\": \"red\", \"size\": \"large\"}",
            "quantity": 50,
            "reservedQuantity": 10
        }'
        test_endpoint "PUT" "/api/products/$product_id" "$update_product_data" 200 "Update Product"

        # Delete product
        test_endpoint "DELETE" "/api/products/$product_id" "" 204 "Delete Product"
    fi

    # Delete category (only after product is deleted)
    test_endpoint "DELETE" "/api/categories/$category_id" "" 204 "Delete Category"
fi

echo -e "\nAPI Tests Completed!" 