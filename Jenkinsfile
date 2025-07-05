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
                echo 'Building Docker image...'
                sh 'docker build -t django-image .'
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                echo 'Pushing Docker image...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                    sh '''
                        docker login -u $DOCKER_USER -p $DOCKER_PASS
                        docker tag django-image $DOCKER_USER/mydjango-app:$IMAGE_VERSION
                        docker push $DOCKER_USER/mydjango-app:$IMAGE_VERSION
                        echo "Image pushed: $DOCKER_USER/mydjango-app:$IMAGE_VERSION"
                    '''
                }
            }
        }

        stage('Update and Apply Kubernetes Manifest') {
            steps {
                script {
                    dir('notesapp') {
                        withKubeConfig(credentialsId: 'kubernetes') {
                            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                                sh '''
                                    echo "Replacing image tag in deployment.yaml..."
                                    sed -i "s|replacementTag|v$BUILD_NUMBER|" deployment.yaml
                                    echo "Updated deployment.yaml:"
                                    cat deployment.yaml
                                    kubectl apply -f deployment.yaml
                                    kubectl apply -f service.yaml
                                '''
                            }
                        }
                    }
                }
            }
        }

        stage('Push Updated Manifest to GitHub') {
            steps {
                dir('notesapp') {
                    withCredentials([usernamePassword(credentialsId: 'github', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                        sh '''
                            git config user.email "vamshireddy903@example.com"
                            git config user.name "vamshireddy903"
                            git add deployment.yaml
                            git commit -m "Update deployment image to version v$BUILD_NUMBER" || echo "No changes to commit"
                            
                            git config credential.helper store
                            echo "https://$GIT_USER:$GIT_PASS@github.com" > ~/.git-credentials
                            
                            git push https://github.com/vamshireddy903/django-notes-app.git HEAD:main

                            rm ~/.git-credentials
                        '''
                    }
                }
            }
        }
    }
}
