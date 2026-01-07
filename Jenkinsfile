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

        stage('Build Eureka Server') {
            steps {
                dir('eureka-server') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Config Server') {
            steps {
                dir('config-server') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build API Gateway') {
            steps {
                dir('api-gateway') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build User Service') {
            steps {
                dir('user-management-service') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Vehicle Service') {
            steps {
                dir('vehicle-management-service') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Service Registry') {
            steps {
                dir('service-registry-service') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Inventory Service') {
            steps {
                dir('inventory-service-management1') {
                    bat 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Notification Service') {
            steps {
                dir('notification-service') {
                    bat 'mvn clean package -DskipTests'
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
