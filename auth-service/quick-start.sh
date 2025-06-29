#!/bin/bash

# 🚀 Auth Service Quick Start Script
# This script provides easy commands to start the auth-service in different environments

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo -e "${BLUE}"
    echo "🔐 ========================================"
    echo "🔐    AUTH SERVICE QUICK START"
    echo "🔐 ========================================"
    echo -e "${NC}"
}

# Check if Docker is installed and running
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker is not running. Please start Docker first."
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose is not installed. Please install Docker Compose first."
        exit 1
    fi
}

# Check if ports are available
check_ports() {
    local ports=("8600" "3307" "6380")
    for port in "${ports[@]}"; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_warning "Port $port is already in use. You may need to stop other services or use different ports."
        fi
    done
}

# Development environment with H2 database
start_dev() {
    print_info "Starting Auth Service in Development mode (H2 + Redis + MailHog)..."
    
    # Create logs directory if it doesn't exist
    mkdir -p logs
    
    # Start development environment
    docker-compose -f docker-compose.dev.yml up --build -d
    
    print_success "Development environment started!"
    echo ""
    echo "🌐 Access Points:"
    echo "   - Auth Service: http://localhost:8600/auth-service"
    echo "   - H2 Console: http://localhost:8600/auth-service/h2-console"
    echo "   - MailHog UI: http://localhost:8025"
    echo "   - Redis Commander: http://localhost:8081"
    echo ""
    echo "📋 Quick Test:"
    echo "   curl http://localhost:8600/auth-service/actuator/health"
}

# Standard environment with MySQL
start_standard() {
    print_info "Starting Auth Service in Standard mode (MySQL + Redis)..."
    
    # Create logs directory if it doesn't exist
    mkdir -p logs
    
    # Start standard environment
    docker-compose up --build -d
    
    print_success "Standard environment started!"
    echo ""
    echo "🌐 Access Points:"
    echo "   - Auth Service: http://localhost:8600/auth-service"
    echo "   - MySQL: localhost:3307 (root/secret)"
    echo "   - Redis: localhost:6380"
    echo ""
    echo "📋 Quick Test:"
    echo "   curl http://localhost:8600/auth-service/actuator/health"
}

# Production environment
start_prod() {
    print_info "Starting Auth Service in Production mode..."
    
    if [ ! -f ".env.prod" ]; then
        print_warning "Production environment file not found. Creating from template..."
        cp env.example .env.prod
        print_warning "Please edit .env.prod with your production values before continuing."
        return 1
    fi
    
    # Create logs directory if it doesn't exist
    mkdir -p logs
    
    # Start production environment
    docker-compose -f docker-compose.prod.yml --env-file .env.prod up --build -d
    
    print_success "Production environment started!"
    echo ""
    echo "🌐 Access Points:"
    echo "   - Auth Service: http://localhost:8600/auth-service"
    echo "   - MySQL: localhost:3307"
    echo "   - Redis: localhost:6380"
    echo ""
    echo "📋 Quick Test:"
    echo "   curl http://localhost:8600/auth-service/actuator/health"
}

# Stop all environments
stop_all() {
    print_info "Stopping all Auth Service environments..."
    
    # Stop all possible environments
    docker-compose -f docker-compose.dev.yml down 2>/dev/null || true
    docker-compose down 2>/dev/null || true
    docker-compose -f docker-compose.prod.yml down 2>/dev/null || true
    
    print_success "All environments stopped!"
}

# Clean up everything (including volumes)
cleanup() {
    print_warning "This will remove all containers, networks, and volumes. Are you sure? (y/N)"
    read -r response
    if [[ "$response" =~ ^([yY][eE][sS]|[yY])$ ]]; then
        print_info "Cleaning up all Auth Service resources..."
        
        # Stop and remove everything
        docker-compose -f docker-compose.dev.yml down -v --remove-orphans 2>/dev/null || true
        docker-compose down -v --remove-orphans 2>/dev/null || true
        docker-compose -f docker-compose.prod.yml down -v --remove-orphans 2>/dev/null || true
        
        # Remove any dangling images
        docker image prune -f
        
        print_success "Cleanup completed!"
    else
        print_info "Cleanup cancelled."
    fi
}

# Show logs
show_logs() {
    local service=${1:-auth-service}
    print_info "Showing logs for $service..."
    
    # Try different compose files
    if docker-compose ps $service &>/dev/null; then
        docker-compose logs -f $service
    elif docker-compose -f docker-compose.dev.yml ps $service &>/dev/null; then
        docker-compose -f docker-compose.dev.yml logs -f $service
    elif docker-compose -f docker-compose.prod.yml ps $service &>/dev/null; then
        docker-compose -f docker-compose.prod.yml logs -f $service
    else
        print_error "Service $service not found in any environment."
    fi
}

# Check service status
status() {
    print_info "Checking Auth Service status..."
    
    echo ""
    echo "🐳 Docker Containers:"
    docker ps --filter "name=auth-service" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    
    echo ""
    echo "🌐 Service Health:"
    if curl -s http://localhost:8600/auth-service/actuator/health >/dev/null; then
        print_success "Auth Service is healthy"
    else
        print_error "Auth Service is not responding"
    fi
    
    echo ""
    echo "💾 Database Status:"
    if docker exec auth-service-mysql mysqladmin ping -h localhost -u root -psecret 2>/dev/null; then
        print_success "MySQL is healthy"
    elif docker exec auth-service-mysql-dev mysqladmin ping -h localhost -u root -psecret 2>/dev/null; then
        print_success "MySQL (dev) is healthy"
    elif docker exec auth-service-mysql-prod mysqladmin ping -h localhost -u root -psecret 2>/dev/null; then
        print_success "MySQL (prod) is healthy"
    else
        print_warning "MySQL is not responding"
    fi
    
    echo ""
    echo "🔴 Redis Status:"
    if docker exec auth-service-redis redis-cli ping 2>/dev/null | grep -q PONG; then
        print_success "Redis is healthy"
    elif docker exec auth-service-redis-dev redis-cli ping 2>/dev/null | grep -q PONG; then
        print_success "Redis (dev) is healthy"
    elif docker exec auth-service-redis-prod redis-cli ping 2>/dev/null | grep -q PONG; then
        print_success "Redis (prod) is healthy"
    else
        print_warning "Redis is not responding"
    fi
}

# Test API endpoints
test_api() {
    print_info "Testing Auth Service API endpoints..."
    
    local base_url="http://localhost:8600/auth-service"
    
    echo ""
    echo "🏥 Health Check:"
    if curl -s "$base_url/actuator/health" | jq . 2>/dev/null; then
        print_success "Health endpoint working"
    else
        print_error "Health endpoint failed"
    fi
    
    echo ""
    echo "📧 OTP Generation Test:"
    local otp_response=$(curl -s -X POST "$base_url/api/auth/otp/generate" \
        -H "Content-Type: application/json" \
        -d '{"identifier":"test@example.com","type":"EMAIL","purpose":"REGISTRATION"}')
    
    if echo "$otp_response" | jq -r '.success' 2>/dev/null | grep -q true; then
        print_success "OTP generation working"
        local otp_code=$(echo "$otp_response" | jq -r '.data.otpCode' 2>/dev/null)
        if [ "$otp_code" != "null" ] && [ -n "$otp_code" ]; then
            echo "   Generated OTP: $otp_code"
            
            echo ""
            echo "🔐 OTP Verification Test:"
            local verify_response=$(curl -s -X POST "$base_url/api/auth/otp/verify" \
                -H "Content-Type: application/json" \
                -d "{\"identifier\":\"test@example.com\",\"type\":\"EMAIL\",\"otpCode\":\"$otp_code\",\"deviceInfo\":\"Test Device\",\"userAgent\":\"Test Agent\"}")
            
            if echo "$verify_response" | jq -r '.success' 2>/dev/null | grep -q true; then
                print_success "OTP verification working"
                local access_token=$(echo "$verify_response" | jq -r '.data.accessToken' 2>/dev/null)
                
                if [ "$access_token" != "null" ] && [ -n "$access_token" ]; then
                    echo ""
                    echo "🎫 Token Validation Test:"
                    local validate_response=$(curl -s -X POST "$base_url/api/auth/validate" \
                        -H "Authorization: Bearer $access_token")
                    
                    if echo "$validate_response" | jq -r '.data' 2>/dev/null | grep -q true; then
                        print_success "Token validation working"
                    else
                        print_error "Token validation failed"
                    fi
                fi
            else
                print_error "OTP verification failed"
            fi
        fi
    else
        print_error "OTP generation failed"
    fi
}

# Build only (no start)
build() {
    print_info "Building Auth Service Docker image..."
    docker-compose build --no-cache
    print_success "Build completed!"
}

# Show help
show_help() {
    print_header
    echo "Usage: $0 [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  dev         Start development environment (H2 + Redis + MailHog)"
    echo "  standard    Start standard environment (MySQL + Redis)"
    echo "  prod        Start production environment"
    echo "  stop        Stop all environments"
    echo "  cleanup     Remove all containers, networks, and volumes"
    echo "  logs        Show service logs (optional: specify service name)"
    echo "  status      Check service status and health"
    echo "  test        Test API endpoints"
    echo "  build       Build Docker image only"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 dev                 # Start development environment"
    echo "  $0 logs auth-service   # Show auth-service logs"
    echo "  $0 logs mysql          # Show MySQL logs"
    echo "  $0 status              # Check all services status"
    echo "  $0 test                # Test API endpoints"
    echo ""
    echo "Environment URLs:"
    echo "  - Auth Service: http://localhost:8600/auth-service"
    echo "  - H2 Console (dev): http://localhost:8600/auth-service/h2-console"
    echo "  - MailHog (dev): http://localhost:8025"
    echo "  - Redis Commander (dev): http://localhost:8081"
}

# Main script logic
main() {
    # Check prerequisites
    check_docker
    check_ports
    
    case "${1:-help}" in
        "dev")
            start_dev
            ;;
        "standard")
            start_standard
            ;;
        "prod")
            start_prod
            ;;
        "stop")
            stop_all
            ;;
        "cleanup")
            cleanup
            ;;
        "logs")
            show_logs "${2:-auth-service}"
            ;;
        "status")
            status
            ;;
        "test")
            test_api
            ;;
        "build")
            build
            ;;
        "help"|*)
            show_help
            ;;
    esac
}

# Run main function with all arguments
main "$@" 