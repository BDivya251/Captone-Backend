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

        stage('Start Infrastructure') {
            steps {
                // specific container names from your docker-compose.yml
                bat 'docker-compose up -d mysql-db rabbitmq' 
                // Wait for DB to be ready
                sleep time: 40, unit: 'SECONDS'
            }
        }

        stage('Build Infra (Skip Tests)') {
            steps {
                // Build infrastructure services without running their tests
                bat 'mvn package -pl eureka-server,config-server,api-gateway -DskipTests'
            }
        }

        stage('Build Business Services (With Tests)') {
            steps {
                // Build business services with tests enabled, injecting DB, Env, Mail, and App properties
                // We define the list of modules explicitly
                bat 'mvn package -pl user-management-service,vehicle-management-service,inventory-service-management1,service-registry-service,notification-service -Dspring.cloud.config.enabled=false -Dspring.datasource.url=jdbc:mysql://localhost:3307/vehicle_service_db?createDatabaseIfNotExist=true -Dspring.datasource.username=root -Dspring.datasource.password=divya -Deureka.client.register-with-eureka=false -Deureka.client.fetch-registry=false -Dspring.mail.host=localhost -Dspring.mail.username=test -Dspring.mail.password=test -Dspring.mail.port=1025 -Dapp.company-name="Mobility Technologies" -Dapp.support-email="support@example.com" -Dapp.contact-number="+1234567890"'
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
