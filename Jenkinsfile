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

        stage('Image pushing to dockerhub') {
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

        stage('Deploy') {
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
                            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                                sh """
                                    sed -i 's|image:.*|image: ${dockerhubuser}/mydjango-app:${IMAGE_VERSION}|' deployment.yaml
                                    cat deployment.yaml
                                    kubectl apply -f deployment.yaml
                                    kubectl apply -f service.yaml
                                """
                            }
                        }
                    }
                }
            }
        }
    }
}
