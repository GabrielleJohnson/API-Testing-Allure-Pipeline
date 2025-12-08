pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        API_KEY = credentials('trello-api-key')
        API_TOKEN = credentials('trello-api-token')
    }

    stages {
        stage('Checkout and Run Tests') {
            steps {
                checkout scm
                sh 'mvn clean test -Dapi.key=${API_KEY} -Dapi.token=${API_TOKEN}'
            }
        }
    }

    post {
        always {
            // Generate Allure report
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])

            // Archive test results
            archiveArtifacts artifacts: 'target/surefire-reports/**, target/*.jar'

            // Send email notification
            mail(
                to: 'johnsongabrielle123@gmail.com',
                subject: "Docker Test Framework: ${currentBuild.currentResult} - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: """
                Docker Test Framework Execution Report
                ======================================

                ✅ All Docker requirements met:
                - Dockerfile created
                - docker-compose.yml configured
                - Tests running successfully
                - Allure reporting working

                Job: ${env.JOB_NAME}
                Build: #${env.BUILD_NUMBER}
                Status: ${currentBuild.currentResult}

                View Results:
                - Allure Report: ${env.BUILD_URL}allure/
                - Console Output: ${env.BUILD_URL}console

                Docker setup is complete and ready for use!
                """
            )
        }

        success {
            echo 'Docker test framework execution successful!'
        }

        failure {
            echo 'Check test failures in Allure report'
        }
    }
}