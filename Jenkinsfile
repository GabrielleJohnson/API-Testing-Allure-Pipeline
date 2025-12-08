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
                //reportBuildPolicy: 'ALWAYS',
                results: [[path: 'target/allure-results']]  // Changed path
            ])

            emailext(
                subject: "Test Results: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Tests completed with status: ${currentBuild.currentResult}",
                to: 'gajohnson@griddynamics.com'
            )
        }
    }
}