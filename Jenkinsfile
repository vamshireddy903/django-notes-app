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

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image'
                sh 'docker build -t django-image .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing image to Docker Hub'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                    sh """
                        docker login -u $dockerhubuser -p $dockerhubpass
                        docker tag django-image $dockerhubuser/mydjango-app:$IMAGE_VERSION
                        docker push $dockerhubuser/mydjango-app:$IMAGE_VERSION
                        echo "Image pushed: $dockerhubuser/mydjango-app:$IMAGE_VERSION"
                    """
                }
            }
        }

        stage('Update Deployment File & Deploy to Kubernetes') {
            steps {
                script {
                    dir('notesapp') {
                        withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                            sh """
    echo "Replacing image tag in deployment.yaml..."
    sed -i "s|replacementTag|$IMAGE_VERSION|" deployment.yaml
    echo "Updated deployment.yaml:"
    cat deployment.yaml
    kubectl apply -f deployment.yaml --validate=false
    kubectl apply -f service.yaml --validate=false
"""
                        }
                    }
                }
            }
        }

        stage('Push Updated Manifest to GitHub') {
            steps {
                dir('notesapp') {
                    withCredentials([string(credentialsId: 'github', variable: 'GITHUB_TOKEN')]) {
                        sh '''
                            git config user.email "vamshireddy903@example.com"
                            git config user.name "vamshireddy903"
                            git add deployment.yaml
                            git commit -m "Update deployment image to version v$BUILD_NUMBER" || echo "No changes to commit"
                            git push https://$GITHUB_TOKEN@github.com/vamshireddy903/django-notes-app.git HEAD:main
                        '''
                    }
                }
            }
        }
    }
}
