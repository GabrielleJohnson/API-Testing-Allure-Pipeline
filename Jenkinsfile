pipeline {
    agent any

    environment {
        // These will be loaded from Jenkins credentials
        API_KEY = credentials('trello-api-key')
        API_TOKEN = credentials('trello-api-token')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Create Test Directories') {
            steps {
                sh 'mkdir -p allure-results test-results'
            }
        }

        stage('Run Tests') {
            steps {
                sh '''
                    echo "Running tests with secured credentials..."
                    mvn clean test \
                      -Dapi.key=${API_KEY} \
                      -Dapi.token=${API_TOKEN} \
                      -Dallure.results.directory=${WORKSPACE}/allure-results
                '''
            }
        }

        stage('Generate Allure Report') {
            steps {
                sh '''
                    # Try to generate Allure report if Allure is installed
                    if command -v allure &> /dev/null; then
                        allure generate allure-results -o allure-report --clean
                        echo "Allure report generated"
                    else
                        echo "Allure not installed, skipping report generation"
                    fi
                '''
            }
        }
    }

    post {
        always {
            // Archive test results
            archiveArtifacts artifacts: 'allure-results/**, target/surefire-reports/**, target/*.jar'

            // Publish Allure report if it exists
            script {
                if (fileExists('allure-report/index.html')) {
                    allure([
                        includeProperties: false,
                        jdk: '',
                        properties: [],
                        reportBuildPolicy: 'ALWAYS',
                        results: [[path: 'allure-results']],
                        report: [path: 'allure-report']
                    ])
                }
            }

            // Send email notification
            emailext(
                subject: "${currentBuild.currentResult}: ${env.JOB_NAME} - Build #${env.BUILD_NUMBER}",
                body: """
                Test Execution Report
                =====================

                Job: ${env.JOB_NAME}
                Build: #${env.BUILD_NUMBER}
                Status: ${currentBuild.currentResult}
                Duration: ${currentBuild.durationString}

                View Results:
                - Console Output: ${env.BUILD_URL}console
                ${fileExists('allure-report/index.html') ? "- Allure Report: ${env.BUILD_URL}allure/" : ""}

                Test Artifacts:
                - Surefire Reports: ${env.BUILD_URL}artifact/target/surefire-reports/
                - Allure Results: ${env.BUILD_URL}artifact/allure-results/
                """,
                to: 'gajohnson@example.com',  // CHANGE TO YOUR EMAIL
                attachLog: currentBuild.currentResult == 'FAILURE'
            )
        }

        success {
            echo "✅ All tests passed successfully!"
        }

        failure {
            echo "❌ Some tests failed. Check the Allure report for details."
        }
    }
}