#!/bin/bash

echo "🐳 Testing User Service Docker Setup"
echo "===================================="

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

print_test() {
    echo -e "${BLUE}🔍 Testing: $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ SUCCESS: $1${NC}"
}

print_error() {
    echo -e "${RED}❌ ERROR: $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  WARNING: $1${NC}"
}

# Test 1: Check Docker and Docker Compose
print_test "Docker installation"
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    print_success "Docker is installed: $DOCKER_VERSION"
else
    print_error "Docker is not installed"
    exit 1
fi

if command -v docker-compose &> /dev/null; then
    COMPOSE_VERSION=$(docker-compose --version)
    print_success "Docker Compose is installed: $COMPOSE_VERSION"
else
    print_error "Docker Compose is not installed"
    exit 1
fi

echo ""

# Test 2: Check required files
print_test "Required Docker files"
files=("Dockerfile" "docker-compose.yml" "docker-compose.dev.yml" "docker-compose.prod.yml" ".dockerignore" "env.example")
for file in "${files[@]}"; do
    if [[ -f "$file" ]]; then
        print_success "$file exists"
    else
        print_error "$file is missing"
    fi
done

echo ""

# Test 3: Validate Dockerfile
print_test "Dockerfile syntax"
if docker build --dry-run . &> /dev/null; then
    print_success "Dockerfile syntax is valid"
else
    print_error "Dockerfile has syntax errors"
fi

echo ""

# Test 4: Validate docker-compose files
print_test "Docker Compose file validation"
compose_files=("docker-compose.yml" "docker-compose.dev.yml" "docker-compose.prod.yml")
for file in "${compose_files[@]}"; do
    if [[ -f "$file" ]]; then
        if docker-compose -f "$file" config &> /dev/null; then
            print_success "$file is valid"
        else
            print_error "$file has validation errors"
        fi
    fi
done

echo ""

# Test 5: Test Development mode (H2)
print_test "Development mode startup (H2 database)"
echo "Starting development environment..."
docker-compose -f docker-compose.dev.yml up --build -d

# Wait for service to start
echo "Waiting for service to start..."
sleep 30

# Check if container is running
if docker-compose -f docker-compose.dev.yml ps | grep -q "user-service-dev.*Up"; then
    print_success "Development container is running"
    
    # Test health check
    for i in {1..10}; do
        if curl -f http://localhost:8700/user-service/actuator/health &> /dev/null; then
            print_success "Health check passed"
            break
        else
            echo "Waiting for health check... ($i/10)"
            sleep 10
        fi
    done
    
    # Test H2 console
    if curl -f http://localhost:8700/user-service/h2-console &> /dev/null; then
        print_success "H2 Console is accessible"
    else
        print_warning "H2 Console might not be accessible"
    fi
    
    # Test API endpoint
    if curl -f http://localhost:8700/user-service/api/users &> /dev/null; then
        print_success "API endpoint is accessible"
    else
        print_warning "API endpoint might not be accessible"
    fi
    
else
    print_error "Development container failed to start"
    docker-compose -f docker-compose.dev.yml logs user-service
fi

# Cleanup development environment
echo ""
print_test "Cleaning up development environment"
docker-compose -f docker-compose.dev.yml down
print_success "Development environment cleaned up"

echo ""

# Test 6: Build check for production
print_test "Production build validation"
if docker-compose -f docker-compose.prod.yml build &> /dev/null; then
    print_success "Production build completed successfully"
else
    print_error "Production build failed"
fi

echo ""

# Summary
echo "🎉 Docker Setup Validation Complete!"
echo "====================================="
echo ""
echo "📋 Quick Start Commands:"
echo "  Development:  docker-compose -f docker-compose.dev.yml up --build"
echo "  Standard:     docker-compose up --build"
echo "  Production:   docker-compose -f docker-compose.prod.yml up --build"
echo ""
echo "🔗 Service URLs:"
echo "  API:          http://localhost:8700/user-service/api/users"
echo "  Health:       http://localhost:8700/user-service/actuator/health"
echo "  H2 Console:   http://localhost:8700/user-service/h2-console (dev mode)"
echo ""
echo "📚 Documentation: See DOCKER_README.md for detailed instructions" 