#!/bin/bash

# Auth Service Quick Start Script
# This script helps you quickly set up and test the auth service

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Auth Service Quick Start${NC}"
echo "=============================="

# Function to print colored messages
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

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not in PATH"
    print_info "Please install Docker and try again"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    print_error "Docker Compose is not installed or not in PATH"
    print_info "Please install Docker Compose and try again"
    exit 1
fi

print_success "Docker and Docker Compose are available"

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_attempts=30
    local attempt=0

    print_info "Waiting for $service_name to be ready on port $port..."
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s -f "http://localhost:$port/api/auth/health" > /dev/null 2>&1; then
            print_success "$service_name is ready!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        echo -n "."
        sleep 2
    done
    
    print_error "$service_name did not start within expected time"
    return 1
}

# Main menu
while true; do
    echo ""
    echo "What would you like to do?"
    echo "1. 🐳 Start dependencies only (MySQL, Redis, MailHog)"
    echo "2. 🏗️  Build and start everything"
    echo "3. 🧪 Run tests (requires service to be running)"
    echo "4. 📊 View logs"
    echo "5. 🧹 Clean up (stop all services)"
    echo "6. 🔄 Restart auth service"
    echo "7. 🌐 Open web interfaces"
    echo "8. ❓ Help"
    echo "9. 🚪 Exit"
    echo ""
    read -p "Choose an option (1-9): " choice

    case $choice in
        1)
            print_info "Starting dependencies..."
            docker-compose up -d mysql redis mailhog redis-commander
            print_success "Dependencies started"
            print_info "MySQL: localhost:3307"
            print_info "Redis: localhost:6380"
            print_info "MailHog UI: http://localhost:8025"
            print_info "Redis Commander: http://localhost:8081"
            ;;
        2)
            print_info "Building and starting all services..."
            docker-compose up -d
            
            if wait_for_service "Auth Service" 8600; then
                print_success "All services are running!"
                print_info "Auth Service: http://localhost:8600"
                print_info "API Documentation: http://localhost:8600/swagger-ui.html"
                print_info "Health Check: http://localhost:8600/api/auth/health"
            fi
            ;;
        3)
            if [ -f "test-scripts/test-otp-flow.sh" ]; then
                print_info "Running comprehensive test suite..."
                ./test-scripts/test-otp-flow.sh
            else
                print_error "Test script not found"
            fi
            ;;
        4)
            echo "Choose which logs to view:"
            echo "1. Auth Service"
            echo "2. MySQL"
            echo "3. Redis" 
            echo "4. All services"
            read -p "Choose (1-4): " log_choice
            
            case $log_choice in
                1) docker-compose logs -f auth-service ;;
                2) docker-compose logs -f mysql ;;
                3) docker-compose logs -f redis ;;
                4) docker-compose logs -f ;;
                *) print_error "Invalid choice" ;;
            esac
            ;;
        5)
            print_info "Stopping all services..."
            docker-compose down
            print_success "All services stopped"
            
            read -p "Do you want to remove volumes as well? (y/N): " remove_volumes
            if [[ $remove_volumes =~ ^[Yy]$ ]]; then
                docker-compose down -v
                print_warning "Volumes removed - all data will be lost"
            fi
            ;;
        6)
            print_info "Restarting auth service..."
            docker-compose restart auth-service
            
            if wait_for_service "Auth Service" 8600; then
                print_success "Auth service restarted successfully!"
            fi
            ;;
        7)
            print_info "Opening web interfaces..."
            
            if command -v open &> /dev/null; then
                # macOS
                open "http://localhost:8600/swagger-ui.html" 2>/dev/null &
                open "http://localhost:8025" 2>/dev/null &
                open "http://localhost:8081" 2>/dev/null &
            elif command -v xdg-open &> /dev/null; then
                # Linux
                xdg-open "http://localhost:8600/swagger-ui.html" 2>/dev/null &
                xdg-open "http://localhost:8025" 2>/dev/null &
                xdg-open "http://localhost:8081" 2>/dev/null &
            else
                print_info "Please open these URLs manually:"
            fi
            
            echo "🔗 Auth Service API: http://localhost:8600/swagger-ui.html"
            echo "📧 MailHog (Email): http://localhost:8025"
            echo "🗄️  Redis Commander: http://localhost:8081"
            ;;
        8)
            echo ""
            echo "📚 Help Information"
            echo "==================="
            echo ""
            echo "🐳 Docker Services:"
            echo "   - auth-service: Main authentication service (port 8600)"
            echo "   - mysql: Database server (port 3307)"
            echo "   - redis: Session storage (port 6380)"
            echo "   - mailhog: Email testing server (ports 1025/8025)"
            echo "   - redis-commander: Redis web UI (port 8081)"
            echo ""
            echo "🧪 Testing:"
            echo "   - Use option 3 to run automated tests"
            echo "   - Check MailHog UI to see OTP emails"
            echo "   - Use Swagger UI to test endpoints manually"
            echo ""
            echo "🔧 Configuration:"
            echo "   - Environment variables: env.template"
            echo "   - Docker settings: docker-compose.yml"
            echo "   - Application config: src/main/resources/application.yml"
            echo ""
            echo "📁 Important Files:"
            echo "   - TESTING.md: Comprehensive testing guide"
            echo "   - README.md: Project documentation"
            echo "   - test-scripts/: Automated test scripts"
            echo ""
            ;;
        9)
            print_info "Goodbye! 👋"
            exit 0
            ;;
        *)
            print_error "Invalid option. Please choose 1-9."
            ;;
    esac
done 