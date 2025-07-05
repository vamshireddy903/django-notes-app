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
                    script {
                        def imageName = "${dockerhubuser}/mydjango-app:${IMAGE_VERSION}"
                        sh "docker login -u ${dockerhubuser} -p ${dockerhubpass}"
                        sh "docker tag django-image ${imageName}"
                        sh "docker push ${imageName}"
                        echo "Image pushed: ${imageName}"
                    }
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
                            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                                script {
                                    def imageName = "${dockerhubuser}/mydjango-app:${IMAGE_VERSION}"
                                    // replace line in YAML
                                    sh """
                                        sed -i 's|image:.*|image: ${imageName}|' deployment.yaml
                                        echo "Updated deployment.yaml:"
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
}
