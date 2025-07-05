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
    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
    }
    steps {
        echo 'Pushing image to Docker Hub'
        withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
            sh "docker login -u ${env.dockerhubuser} -p ${env.dockerhubpass}"
            sh "docker tag django-image ${env.dockerhubuser}/mydjango-app:${env.IMAGE_VERSION}"
            sh "docker push ${env.dockerhubuser}/mydjango-app:${env.IMAGE_VERSION}"
            echo "Image pushed: ${env.dockerhubuser}/mydjango-app:${env.IMAGE_VERSION}"
        }
    }
}


       stage('Deploy') {
    steps {
        script {
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
                    // Replace image version in deployment.yaml dynamically
                    sh """
                        sed -i 's|image: .*$|image: ${env.dockerhubuser}/mydjango-app:${env.IMAGE_VERSION}|' deployment.yaml
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
