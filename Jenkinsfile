pipeline {
    agent any

    stages {
        stage('Code checkout') {
            steps {
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }
          stage('Build') {
            steps {
                echo 'Docker image build'
                sh 'docker build -t django-image . '
            }
        }
         stage('Image pushing to dockerhub') {
            steps {
                echo 'Pushing image to docker hub'
                withCredentials([usernamePassword(credentialsId: 'dokcerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                sh "docker login -u ${env.dockerhubuser} -p ${env.dockerhubpass}"
                sh "docker tag django-image ${env.dockerhubuser}/mydjango-app:latest"
                sh "docker push ${env.dockerhubuser}/mydjango-app:latest"
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
                           serverUrl: ''  ) {
                    sh 'kubectl delete --all pods'
                    sh 'kubectl apply -f deployment.yaml'
                    sh 'kubectl apply -f service.yaml'
                }
            }
        }
    }
}
    }
}
