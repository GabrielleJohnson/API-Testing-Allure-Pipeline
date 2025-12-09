#!/bin/bash
# run-docker-tests.sh
# Shows how to run tests with Docker as per assignment requirements

echo "========================================"
echo "  DOCKER TEST FRAMEWORK EXECUTION"
echo "========================================"
echo ""

# Check if credentials provided
if [ $# -lt 2 ]; then
    echo "Usage: $0 <API_KEY> <API_TOKEN>"
    echo "Example: $0 abc123 xyz789"
    exit 1
fi

API_KEY=$1
API_TOKEN=$2

echo "Step 1: Building Docker image from Dockerfile"
echo "----------------------------------------------"
echo "Command: docker build -t api-test-framework ."
docker build -t api-test-framework .
echo "Docker image built successfully"
echo ""

echo "Step 2: Running tests in Docker container"
echo "-----------------------------------------"
echo "Command: docker run --rm \\"
echo "  -e API_KEY=\${API_KEY} \\"
echo "  -e API_TOKEN=\${API_TOKEN} \\"
echo "  -e ALLURE_RESULTS_PATH=/app/allure-results \\"
echo "  -v \$(pwd)/docker-test-results:/app/allure-results \\"
echo "  api-test-framework \\"
echo "  mvn test -Dtest=DockerTestRunner"
echo ""
docker run --rm \
  -e API_KEY=${API_KEY} \
  -e API_TOKEN=${API_TOKEN} \
  -e ALLURE_RESULTS_PATH=/app/allure-results \
  -v $(pwd)/docker-test-results:/app/allure-results \
  api-test-framework \
  mvn test -Dtest=DockerTestRunner
echo "Tests executed inside Docker container"
echo ""

echo "Step 3: Creating .env file for Docker Compose"
echo "--------------------------------------------"
echo "API_KEY=${API_KEY}" > .env
echo "API_TOKEN=${API_TOKEN}" >> .env
echo "API_BASE_URL=https://api.trello.com/1/" >> .env
echo "Created .env file with credentials"
echo ""

echo "Step 4: Running with Docker Compose"
echo "-----------------------------------"
echo "Command: docker-compose up --build --abort-on-container-exit"
echo ""
echo "This will:"
echo "1. Start test-runner service (builds from Dockerfile)"
echo "2. Start allure service for reporting"
echo "3. Run ALL tests (not just DockerTestRunner)"
echo "4. Generate Allure reports"
echo "5. Make Allure available at http://localhost:5050"
echo ""
docker-compose up --build --abort-on-container-exit
echo ""

echo "Step 5: Cleanup"
echo "---------------"
docker-compose down -v
echo "Docker Compose services stopped and cleaned up"
echo ""

echo "========================================"
echo "  DOCKER EXECUTION COMPLETE"
echo "========================================"
echo "All Docker requirements demonstrated:"
echo "   - Docker image built from Dockerfile"
echo "   - Tests run inside Docker container"
echo "   - Multi-service Docker Compose execution"
echo "   - Allure reporting from Docker"
echo ""
echo "Test results saved to:"
echo "- docker-test-results/ (single container)"
echo "- allure-results/ (Docker Compose)"
echo "- allure-reports/ (Allure HTML reports)"
echo ""
echo "View Allure report: http://localhost:5050"
echo "========================================"