pipeline {
    agent any

    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
    }

    stages {
        stage('Code checkout') {
            steps {
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }

        stage('Build') {
            steps {
                echo 'Building Docker image'
                sh 'docker build -t django-image .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing image to Docker Hub'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                    sh "docker login -u ${dockerhubuser} -p ${dockerhubpass}"
                    sh "docker tag django-image ${dockerhubuser}/mydjango-app:${IMAGE_VERSION}"
                    sh "docker push ${dockerhubuser}/mydjango-app:${IMAGE_VERSION}"
                    echo "Image pushed: ${dockerhubuser}/mydjango-app:${IMAGE_VERSION}"
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    dir('notesapp') {
                        withKubeConfig(
                            credentialsId: 'kubernetes', 
                            caCertificate: '', 
                            clusterName: '', 
                            contextName: '', 
                            namespace: '', 
                            restrictKubeConfigAccess: false, 
                            serverUrl: ''
                        ) {
                            // Replace image version in deployment.yaml
                            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                                sh "sed -i 's|image: .*|image: ${dockerhubuser}/mydjango-app:${IMAGE_VERSION}|' deployment.yaml"
                            }

                            sh 'kubectl apply -f deployment.yaml'
                            sh 'kubectl apply -f service.yaml'
                            sh 'kubectl rollout status deployment/mydjango-deployment'
                        }
                    }
                }
            }
        }
    }
}
