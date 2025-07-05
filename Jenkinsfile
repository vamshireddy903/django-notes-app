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
                echo 'Building Docker image...'
                sh 'docker build -t django-image .'
            }
        }

        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing image to Docker Hub...'
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

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    dir('notesapp') {
                        withKubeConfig(credentialsId: 'kubernetes') {
                            sh """
                                echo "Replacing image tag in deployment.yaml..."
                                sed -i "s|replacementTag|$IMAGE_VERSION|" deployment.yaml
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

        stage('Push Updated Manifest to GitHub') {
            steps {
                dir('notesapp') {
                    withCredentials([usernamePassword(credentialsId: 'github-userpass', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                        sh """
                            git config user.email "vamshireddy903@example.com"
                            git config user.name "vamshireddy903"
                            git add deployment.yaml
                            git commit -m "Update deployment image to version $IMAGE_VERSION" || echo "No changes to commit"
                            git remote set-url origin "https://$GIT_USER:$GIT_PASS@github.com/vamshireddy903/django-notes-app.git"
                            git push origin HEAD:main
                        """
                    }
                }
            }
        }
    }
}
