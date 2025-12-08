pipeline {
    agent any

    tools {
        maven 'Maven'  // Make sure this matches your Jenkins Maven installation name
    }

    environment {
        API_KEY = credentials('trello-api-key')
        API_TOKEN = credentials('trello-api-token')
    }

    stages {
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
                results: [[path: 'target/allure-results']]  // Changed path
            ])

            emailext(
                subject: "Test Results: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Tests completed with status: ${currentBuild.currentResult}",
                to: 'johnsongabrielle123@gmail.com'
            )
        }
    }
}

stage('Debug Email') {
    steps {
        script {
            // Test if email variables are accessible
            echo "JOB_NAME: ${env.JOB_NAME}"
            echo "BUILD_NUMBER: ${env.BUILD_NUMBER}"
            echo "BUILD_URL: ${env.BUILD_URL}"

            // Try a simple test email
            emailext(
                to: 'johnsongabrielle123@gmail.com',
                subject: "TEST EMAIL from Jenkins",
                body: "This is a test email sent at ${new Date()}",
                mimeType: 'text/plain'
            )
        }
    }
}