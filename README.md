# Simple Notes App
This is a simple notes app built with React and Django.

## Requirements
1. Python 3.9
2. Node.js
3. React

# To Run the application manually

**Pre-requisites**

1. Install docker

https://docs.docker.com/engine/install/ubuntu/
   
2. Clone the repository
```
git clone https://github.com/vamshireddy903/django-notes-app.git
```

3. Build the app
```
docker build -t django-app .
```

4. Run the app
```
docker run -d -p 8000:8000 django-app
```

5. Access the application

        http://<EC2 public IP>:8000
   
Note: Make SG has allowed 8000 port
