pipeline {
    agent any 
    
    stages{
        stage("Clone Code"){
    agent any

    stages {
        stage('Code checkout') {
            steps {
                echo "Cloning the code"
                git url:"https://github.com/LondheShubham153/django-notes-app.git", branch: "main"
                git branch: 'main', credentialsId: 'github', url: 'https://github.com/vamshireddy903/django-notes-app.git'
            }
        }
        stage("Build"){
          stage('Build') {
            steps {
                echo "Building the image"
                sh "docker build -t my-note-app ."
                echo 'Docker image build'
                sh 'docker build -t django-image . '
            }
        }
        stage("Push to Docker Hub"){
         stage('Image pushing to dockerhub') {
            steps {
                echo "Pushing the image to docker hub"
                withCredentials([usernamePassword(credentialsId:"dockerHub",passwordVariable:"dockerHubPass",usernameVariable:"dockerHubUser")]){
                sh "docker tag my-note-app ${env.dockerHubUser}/my-note-app:latest"
                sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPass}"
                sh "docker push ${env.dockerHubUser}/my-note-app:latest"
                }
                echo 'Pushing image to docker hub'
                withCredentials([usernamePassword(credentialsId: 'dokcerhub', passwordVariable: 'dockerhubpass', usernameVariable: 'dockerhubuser')]) {
                sh "docker login -u ${env.dockerhubuser} -p ${env.dockerhubpass}"
                sh "docker tag django-image ${env.dockerhubuser}/mydjango-app:latest"
                sh "docker push ${env.dockerhubuser}/mydjango-app:latest"
}
            }
        }
        stage("Deploy"){
            steps {
                echo "Deploying the container"
                sh "docker-compose down && docker-compose up -d"
                
        
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
