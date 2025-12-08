pipeline {
    agent any

    environment {
        API_KEY = credentials('trello-api-key')
        API_TOKEN = credentials('trello-api-token')
        API_BASE_URL = 'https://api.trello.com/1/'
    }

    stages {
        stage('Prepare Environment') {
            steps {
                checkout scm

                // Create necessary directories
                sh '''
                    mkdir -p allure-results allure-reports surefire-reports
                    chmod -R 777 allure-results allure-reports surefire-reports
                '''

                // Create .env file for Docker Compose
                sh '''
                    echo "API_KEY=${API_KEY}" > .env
                    echo "API_TOKEN=${API_TOKEN}" >> .env
                    echo "API_BASE_URL=${API_BASE_URL}" >> .env
                '''
            }
        }

        stage('Run Tests with Docker Compose') {
            steps {
                script {
                    echo "Running tests inside Docker containers..."

                    // Run Docker Compose
                    sh '''
                        docker-compose up \
                          --build \
                          --abort-on-container-exit \
                          --exit-code-from test-runner \
                          test-runner
                    '''
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                script {
                    echo "Generating Allure report from Docker results..."

                    // Generate Allure report from the results
                    sh '''
                        if [ -d "allure-results" ]; then
                            docker run --rm \
                              -v $(pwd)/allure-results:/app/allure-results \
                              -v $(pwd)/allure-reports:/app/default-reports \
                              frankescobar/allure-docker-service \
                              allure generate /app/allure-results -o /app/default-reports --clean
                        fi
                    '''
                }
            }
        }
    }

    post {
        always {
            // Cleanup Docker resources
            sh 'docker-compose down -v --remove-orphans || true'
            sh 'docker system prune -f || true'

            // Archive test results
            archiveArtifacts artifacts: 'allure-results/**, allure-reports/**, surefire-reports/**, target/**'

            // Publish Allure report
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'allure-results']],
                report: [path: 'allure-reports']
            ])

            // Send email notification
            mail(
                to: 'johnsongabrielle123@gmail.com',
                subject: "Docker Test Execution: ${currentBuild.currentResult} - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                Docker Test Framework Execution Report
                ======================================

                Tests executed INSIDE Docker containers
                Multi-service Docker Compose setup
                Allure reporting from Docker environment

                Execution Details:
                - Job: ${env.JOB_NAME}
                - Build: #${env.BUILD_NUMBER}
                - Status: ${currentBuild.currentResult}
                - Environment: Docker containers

                Services Used:
                1. test-runner: Custom Docker image with Maven, Chrome, Allure
                2. allure: Allure report server

                View Results:
                - Allure Report: ${env.BUILD_URL}allure/
                - Console Output: ${env.BUILD_URL}console

                Docker Configuration Verified:
                - Dockerfile builds successfully
                - docker-compose.yml orchestrates services
                - Tests run in isolated containers
                - Results persist to host machine
                """
            )
        }

        success {
            echo 'Docker test execution successful! Containers cleaned up.'
        }

        failure {
            echo 'Docker test execution failed. Check container logs.'
        }
    }
}