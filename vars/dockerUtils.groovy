def cloneCode(String url, String branch) {
    git url: "${url}", branch: "${branch}"
}

def dockerBuild(string imageName) {
    sh 'docker build -t ${imageName} .'
}

def dockerPush(string imageName, string credID) {
   withCredentials([usernamePassword(credentialsId: '${credID}', usernameVariable: 'dockerhubuser', passwordVariable: 'dockerhubpass')]){
                  sh '''
                  echo "$dockerhubpass" | docker login -u "$dockerhubuser" --password-stdin
                  docker push ${imageName}
                  '''
   }
