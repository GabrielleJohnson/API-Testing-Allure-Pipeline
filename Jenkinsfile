pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    environment {
        API_KEY = credentials('trello-api-key')
        API_TOKEN = credentials('trello-api-token')
        GITHUB_TOKEN = credentials('github-token')  // GitHub PAT
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/master']],
                    extensions: [],
                    userRemoteConfigs: [[
                        url: "https://${GITHUB_TOKEN}@github.com/GabrielleJohnson/API-Testing-Allure-Pipeline.git"
                    ]]
                ])
            }
        }

        stage('Run Tests') {
            steps {
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

            mail(
                subject: "Test Results: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Tests completed with status: ${currentBuild.currentResult}",
                to: 'johnsongabrielle123@gmail.com'
            )
        }
    }
}