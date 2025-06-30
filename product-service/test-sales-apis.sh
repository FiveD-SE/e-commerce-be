#!/bin/bash

# Test script for Product Sales API functionality
# This script demonstrates the sales tracking features of the product service

BASE_URL="http://localhost:8082/api/products"

echo "==============================================="
echo "Testing Product Sales API Functionality"
echo "==============================================="

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to make HTTP requests and display response
make_request() {
    local method=$1
    local url=$2
    local data=$3
    
    echo -e "\n${YELLOW}Request:${NC} $method $url"
    if [ -n "$data" ]; then
        echo -e "${YELLOW}Data:${NC} $data"
    fi
    
    if [ -n "$data" ]; then
        response=$(curl -s -X $method "$url" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -X $method "$url" -H "Content-Type: application/json")
    fi
    
    echo -e "${GREEN}Response:${NC}"
    echo "$response" | jq '.' 2>/dev/null || echo "$response"
    echo "----------------------------------------"
}

# Test 1: Get all products to see current sales data
print_status "1. Fetching all products to see current sales data"
make_request "GET" "$BASE_URL?sort=salesCount&direction=desc&size=5"

# Test 2: Get a specific product by ID (using first product from above)
print_status "2. Getting a specific product to check its current sales count"
# You'll need to replace this with an actual product ID from your database
PRODUCT_ID="replace-with-actual-product-id"
print_warning "Please replace PRODUCT_ID with an actual product ID from your database"
# make_request "GET" "$BASE_URL/$PRODUCT_ID"

# Test 3: Update sales count for a product by ID
print_status "3. Updating sales count for a product by ID"
print_warning "Replace PRODUCT_ID with actual ID before running"
# make_request "PUT" "$BASE_URL/$PRODUCT_ID/sales?additionalSales=5"

# Test 4: Update sales count for a product by SKU
print_status "4. Updating sales count for a product by SKU"
print_warning "Replace 'SAMPLE-SKU-001' with actual SKU before running"
# make_request "PUT" "$BASE_URL/sku/SAMPLE-SKU-001/sales?additionalSales=3"

# Test 5: Get top selling products
print_status "5. Fetching top selling products"
make_request "GET" "$BASE_URL/top-selling?page=0&size=10"

# Test 6: Get products sorted by sales count (descending)
print_status "6. Fetching all products sorted by sales count (highest first)"
make_request "GET" "$BASE_URL?sort=salesCount&direction=desc&page=0&size=10"

# Test 7: Get products sorted by sales count (ascending)
print_status "7. Fetching all products sorted by sales count (lowest first)"
make_request "GET" "$BASE_URL?sort=salesCount&direction=asc&page=0&size=10"

# Test 8: Get product with specific filters including minimum sales
print_status "8. Testing product search with various filters"
make_request "GET" "$BASE_URL?search=product&sort=salesCount&direction=desc&page=0&size=5"

echo -e "\n${GREEN}===============================================${NC}"
echo -e "${GREEN}Sales API Testing Complete!${NC}"
echo -e "${GREEN}===============================================${NC}"

echo -e "\n${YELLOW}Note:${NC} To test the update functionality, you need to:"
echo "1. Replace 'PRODUCT_ID' with an actual product ID from your database"
echo "2. Replace 'SAMPLE-SKU-001' with an actual product SKU"
echo "3. Uncomment the relevant test lines"

echo -e "\n${YELLOW}New Features Added:${NC}"
echo "✓ Products now include salesCount and totalSalesAmount fields"
echo "✓ GET /api/products - supports sorting by 'salesCount'"
echo "✓ GET /api/products/top-selling - get top selling products"
echo "✓ PUT /api/products/{id}/sales - update sales count by product ID"
echo "✓ PUT /api/products/sku/{sku}/sales - update sales count by SKU"
echo "✓ All product responses now include sales information" 