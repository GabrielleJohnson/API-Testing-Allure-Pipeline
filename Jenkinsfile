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
        stage('Debug Email Configuration') {
            steps {
                script {
                    echo "=== Debug Information ==="
                    echo "JOB_NAME: ${env.JOB_NAME}"
                    echo "BUILD_NUMBER: ${env.BUILD_NUMBER}"
                    echo "BUILD_URL: ${env.BUILD_URL}"
                    echo "EMAIL TO: johnsongabrielle123@gmail.com"
                    echo ""

                    // Test sending a simple email
                    emailext(
                        to: 'johnsongabrielle123@gmail.com',
                        subject: "DEBUG TEST from Jenkins",
                        body: "This is a debug test email sent at ${new Date()}",
                        mimeType: 'text/plain'
                    )

                    echo "Debug email sent (or attempted)"
                }
            }
        }

        stage('Run Tests') {
            steps {
                checkout scm
                sh 'mvn clean test -Dapi.key=${API_KEY} -Dapi.token=${API_TOKEN}'
            }
        }
    }

    post {
        always {
            allure([
                includeProperties: false,
                jdk: '',
                properties: [],
                reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]
            ])

            emailext(
                subject: "Test Results: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Tests completed with status: ${currentBuild.currentResult}",
                to: 'johnsongabrielle123@gmail.com'
            )
        }
    }
}