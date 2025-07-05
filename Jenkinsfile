pipeline {
    agent any

    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
    }

    stages {
        stage('Code Checkout') {
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
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKERHUB_PASS', usernameVariable: 'DOCKERHUB_USER')]) {
                    sh """
                        docker login -u $DOCKERHUB_USER -p $DOCKERHUB_PASS
                        docker tag django-image $DOCKERHUB_USER/mydjango-app:$IMAGE_VERSION
                        docker push $DOCKERHUB_USER/mydjango-app:$IMAGE_VERSION
                    """
                }
            }
        }

        stage('Update Deployment Manifest') {
            steps {
                dir('notesapp') {
                    echo 'Updating image tag in deployment.yaml'
                    sh """
                        sed -i "s|replacementTag|$IMAGE_VERSION|" deployment.yaml
                        cat deployment.yaml
                    """
                }
            }
        }

        stage('Push Updated Manifest to GitHub') {
            steps {
                dir('notesapp') {
                    withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                        sh """
                            git config user.email "vamshireddy903@example.com"
                            git config user.name "vamshireddy903"
                            git add deployment.yaml
                            git commit -m "Update deployment image to version v${BUILD_NUMBER}" || echo "No changes to commit"
                            git remote set-url origin https://$GIT_USER:$GIT_PASS@github.com/vamshireddy903/django-notes-app.git
                            git push origin main
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
                            sh """
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
