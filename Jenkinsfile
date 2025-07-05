pipeline {
    agent any

    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
    }

    stages {
        stage('Code Checkout') {
            steps {
                echo "Cloning the code"
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image"
                sh 'docker build -t django-image .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    script {
                        sh """
                            docker login -u $DOCKER_USER -p $DOCKER_PASS
                            docker tag django-image $DOCKER_USER/mydjango-app:${IMAGE_VERSION}
                            docker push $DOCKER_USER/mydjango-app:${IMAGE_VERSION}
                        """
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    dir('notesapp') {
                        withKubeConfig(credentialsId: 'kubernetes') {
                            sh 'kubectl apply -f deployment.yaml'
                            sh 'kubectl apply -f service.yaml'
                        }
                    }
                }
            }
        }
    }
}
