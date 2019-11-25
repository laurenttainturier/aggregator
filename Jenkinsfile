pipeline {
    agent any
    tools {
        maven 'Maven 3.6.0'
    }
    stages {
        stage ('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                '''
            }
        }

        stage ('Build') {
            steps {
                sh 'cd aggregator; mvn clean install'
                sh 'cd aggregator-socket; mvn clean verify'
            }
        }

        stage('Test') {
            steps {
                sh 'cd aggregator;mvn test'
                sh 'cd aggregator-socket;mvn test'
            }
        }
    }
}