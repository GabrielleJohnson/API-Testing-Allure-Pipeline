pipeline {
    // Defines where the pipeline runs (e.g., any available agent/node)
    agent any

    // 1. Configure the Maven tool
    tools {
        maven 'Maven' // Use the Maven installation named 'Maven' in Jenkins configuration
    }

    // 2. Define secret credentials from Jenkins Credentials Manager
    environment {
        API_KEY = credentials('trello-api-key')
        API_TOKEN = credentials('trello-api-token')
    }

    // --- Execution Stages ---
    stages {
        stage('Checkout Code') {
            steps {
                // Get the source code
                checkout scm
            }
        }

        stage('Run API Tests & Generate Allure Data') {
            steps {
                // Executes tests and generates raw Allure results in target/allure-results.
                // It uses system properties (-D) to pass credentials to Maven/Surefire/TestNG.
                sh 'mvn clean test allure:report -Dapi.key=${API_KEY} -Dapi.token=${API_TOKEN}'
            }
        }
    }

    // --- Post-Build Actions (Cleanup & Reporting) ---
    post {
        always {
            // Generates the human-readable Allure report from the raw data
            allure(
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            )

            // Archives test evidence
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/allure-results/**'

            // Send Email Notification
            mail(
                to: 'johnsongabrielle123@gmail.com',
                subject: "API Test Run Results - Build #${env.BUILD_NUMBER}",
                body: """
                API Test Framework - Build #${env.BUILD_NUMBER} Complete.

                Tests executed successfully using the Maven configuration.

                View Full Allure Report: ${env.BUILD_URL}allure/
                View Console Output: ${env.BUILD_URL}console

                Note: To run the full Dockerized environment, execute:
                docker-compose up --build
                """
            )
        }
    }
}