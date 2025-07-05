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
                sh 'docker build -t django-image .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    sh """
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker tag django-image $DOCKER_USER/mydjango-app:$IMAGE_VERSION
                        docker push $DOCKER_USER/mydjango-app:$IMAGE_VERSION
                    """
                }
            }
        }

        stage('Update Deployment YAML & Deploy to K8s') {
            steps {
                dir('notesapp') {
                    withKubeConfig(credentialsId: 'kubernetes') {
                        sh """
                            echo "Replacing image tag..."
                            sed -i "s|replacementTag|v$BUILD_NUMBER|" deployment.yaml
                            cat deployment.yaml
                            kubectl apply -f deployment.yaml
                            kubectl apply -f service.yaml
                        """
                    }
                }
            }
        }

        stage('Push Updated Manifest to GitHub') {
            steps {
                dir('notesapp') {
                    withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
                        sh '''
                            git config user.email "vamshireddy903@example.com"
                            git config user.name "vamshireddy903"

                            git add deployment.yaml
                            git commit -m "Update deployment image to version v$BUILD_NUMBER" || echo "No changes to commit"

                            git remote set-url origin https://$GIT_USER:$GIT_TOKEN@github.com/vamshireddy903/django-notes-app.git
                            git push origin main
                        '''
                    }
                }
            }
        }
    }
}
