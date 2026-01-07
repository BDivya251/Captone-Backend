pipeline {
    agent any

    tools {
        maven 'M3'
    }

    environment {
        // credentialId matches what you configured in Jenkins (sonar-token1)
        SONAR_TOKEN = credentials('sonar-token1')
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/BDivya251/Captone-Backend.git'
            }
        }

        stage('Build & Test') {
            steps {
                // 'package' will run unit tests and generate coverage reports (if configured in pom)
                bat 'mvn clean package'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                // Using specific plugin version as requested
                bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.10.0.2594:sonar -Dsonar.token=%SONAR_TOKEN% -Dsonar.projectVersion=1.0.%BUILD_NUMBER%'
            }
        }

        stage('Docker Compose Build') {
            steps {
                bat 'docker-compose build'
            }
        }

        stage('Docker Compose Up') {
            steps {
                bat 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
