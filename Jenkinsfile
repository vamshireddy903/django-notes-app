pipeline {
    agent any

    environment {
        IMAGE_VERSION = "v${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Code') {
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
                    sh '''
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker tag django-image $DOCKER_USER/mydjango-app:v$BUILD_NUMBER
                        docker push $DOCKER_USER/mydjango-app:v$BUILD_NUMBER
                    '''
                }
            }
        }

        stage('Update and Push Deployment YAML') {
            steps {
                dir('notesapp') {
                    withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh '''
                            echo "Replacing image tag in deployment.yaml..."
                            sed -i "s|replacementTag|v$BUILD_NUMBER|" deployment.yaml
                            
                            git config user.email "vamshireddy903@example.com"
                            git config user.name "vamshireddy903"
                            git add deployment.yaml
                            git commit -m "Update deployment image to version v$BUILD_NUMBER" || echo "No changes to commit"
                            
                            git remote set-url origin https://$GIT_USER:$GIT_PASS@github.com/vamshireddy903/django-notes-app.git
                            git push origin main
                        '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                dir('notesapp') {
                    withKubeConfig(credentialsId: 'kubernetes') {
                        sh '''
                            kubectl apply -f deployment.yaml
                            kubectl apply -f service.yaml
                        '''
                    }
                }
            }
        }
    }
}
