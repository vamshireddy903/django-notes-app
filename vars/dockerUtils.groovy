def cloneCode(String url, String branch) {
    git url: "${url}", branch: "${branch}"
}

def dockerBuild(String imageName) {
    sh """
    docker build -t ${imageName} .
    """
}

def dockerPush(String imageName) {
   withCredentials([usernamePassword(credentialsId: 'dockerhubCred', usernameVariable: 'dockerhubuser', passwordVariable: 'dockerhubpass')]){
                  sh """
                  echo "$dockerhubpass" | docker login -u "$dockerhubuser" --password-stdin
                  docker push ${imageName}
                  """
   }
}
