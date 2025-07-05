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
                    sh '''
                        docker login -u $dockerhubuser -p $dockerhubpass
                        docker tag django-image $dockerhubuser/mydjango-app:$IMAGE_VERSION
                        docker push $dockerhubuser/mydjango-app:$IMAGE_VERSION
                        echo "Image pushed: $dockerhubuser/mydjango-app:$IMAGE_VERSION"
                    '''
                }
            }
        }

        stage('Deploy to Kubernetes') {
    steps {
        script {
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
                    sh '''
                        echo "✅ Updating image version in deployment.yaml..."
                        sed -i "s|image:.*|image: $dockerhubuser/mydjango-app:$IMAGE_VERSION|" notesapp/deployment.yaml

                        echo "📦 Applying Kubernetes manifests..."
                        kubectl apply -f notesapp/deployment.yaml
                        kubectl apply -f notesapp/service.yaml
                    '''
                }
            }
        }
    }
}

    }
}
