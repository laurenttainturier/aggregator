pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build') {
            steps {
                sh 'cd aggregator; mvn clean install'
                sh 'cd aggregator-socket; mvn clean verify exec:java'
            }
        }
        stage('Test') { 
            steps {
                sh 'cd aggregator;mvn test'
		sh 'cd aggregator-socket;mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml' 
                }
            }
        }
    }
}
