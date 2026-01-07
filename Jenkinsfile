pipeline {
    agent any

    tools {
        maven 'maven'
    }

    stages {

        stage('Clone Repository') {
            steps {
                // Update URL if different
                git branch: 'main',
                    url: 'https://github.com/BDivya251/Captone-Backend.git'
            }
        }

        stage('Build & Coverage') {
            steps {
                // Run verify to execute tests and generate JaCoCo coverage reports
                // Coverage settings (includes/excludes) are defined in the root pom.xml
                bat 'mvn clean verify'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // Requires 'sonar-token' to be configured in Jenkins Credentials
                withCredentials([string(credentialsId: 'sonar-token1', variable: 'SONAR_TOKEN')]) {
                    script {
                        if (isUnix()) {
                            sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.token=$SONAR_TOKEN'
                        } else {
                            // Windows batch syntax for variables
                            bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.token=%SONAR_TOKEN%'
                        }
                    }
                }
            }
        }

        stage('Docker Build Images') {
            steps {
                // Building images using docker build command
                bat 'docker build -t eureka-server ./eureka-server'
                bat 'docker build -t config-server ./config-server'
                bat 'docker build -t api-gateway ./api-gateway'
                bat 'docker build -t user-service ./user-management-service'
                bat 'docker build -t vehicle-service ./vehicle-management-service'
                bat 'docker build -t service-registry ./service-registry-service'
                bat 'docker build -t inventory-service ./inventory-service-management1'
                bat 'docker build -t notification-service ./notification-service'
            }
        }
        
        stage('Docker Compose Deploy') {
            steps {
                // Optional: Deploy using Compose if needed
                bat 'docker-compose down'
                bat 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo 'All services built and Docker images created successfully!'
        }
        failure {
            echo 'Build failed. Please check the logs.'
        }
    }
}
