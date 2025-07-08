pipeline {
    agent any

    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
        PROJECT_KEY = "first-jenkins-pipeline"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', 
                    credentialsId: 'github', 
                    url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t django-app:${IMAGE_VERSION} ."
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub', 
                    passwordVariable: 'DOCKER_PASS', 
                    usernameVariable: 'DOCKER_USER')]) {

                    sh """
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker tag django-app:${IMAGE_VERSION} $DOCKER_USER/django-app:${IMAGE_VERSION}
                        docker push $DOCKER_USER/django-app:${IMAGE_VERSION}
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withKubeConfig(credentialsId: 'kubernetes') {
                    dir('notesapp'){
                    sh 'kubectl delete pods --all'
                    sh 'kubectl apply -f deployment.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }
}
}
