pipeline {
    agent any

    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
    }

    stages {
        stage('Code Checkout') {
            steps {
                git branch: 'main', 
                    credentialsId: 'github', 
                    url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t django-image .'
            }
        }

        stage('Push Image to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', 
                    usernameVariable: 'DOCKER_USER', 
                    passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker tag django-image $DOCKER_USER/mydjango-app:$IMAGE_VERSION
                        docker push $DOCKER_USER/mydjango-app:$IMAGE_VERSION
                    """
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                dir('notesapp') {
                    withKubeConfig(
                        caCertificate: '', 
                        clusterName: '', 
                        contextName: '', 
                        credentialsId: 'kubernetes', 
                        namespace: '', 
                        restrictKubeConfigAccess: false, 
                        serverUrl: ''
                    ) {
                        sh """
                            echo "Applying existing Kubernetes manifests..."
                            kubectl apply -f deployment.yaml
                            kubectl apply -f service.yaml
                        """
                    }
                }
            }
        }
    }
}
