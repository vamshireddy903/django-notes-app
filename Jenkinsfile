pipeline {
    agent any

    environment {
        IMAGE_NAME = "vamshi589/django-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        FULL_IMAGE = "${IMAGE_NAME}:${IMAGE_TAG}"
    }

    stages {
        stage('Clone Repo') {
            steps {
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t $FULL_IMAGE .'
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'dockerhubuser', passwordVariable: 'dockerhubpass')]) {
                    sh 'docker login -u $dockerhubuser -p $dockerhubpass'
                    sh 'docker push $FULL_IMAGE'
                }
            }
        }

       stage('Update Deployment YAML and Push') {
    steps {
        withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_TOKEN')]) {
            script {
                sh """
                echo "Updating deployment.yaml with new image: ${FULL_IMAGE}"
                sed -i "s|image:.*|image: ${FULL_IMAGE}|" notesapp/deployment.yaml

                git config --global user.email "devops@automation.com"
                git config --global user.name "Jenkins Automation"

                git add notesapp/deployment.yaml
                git diff-index --quiet HEAD || git commit -m "Update image to ${FULL_IMAGE}"

                git remote set-url origin https://${GIT_USER}:${GIT_TOKEN}@github.com/vamshireddy903/django-notes-app.git
                git push origin main
                """
                    }
                }
            }
        }
    }
}
