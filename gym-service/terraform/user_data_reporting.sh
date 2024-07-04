#!/bin/bash
# Update packages
sudo apt-get update -y

# Install Apache HTTP Server
sudo apt-get install apache2 -y
sudo systemctl start apache2
echo "Deploy the Gym reporting microservice webserver on AWS" | sudo tee /var/www/html/index.html

# Install AWS CLI
sudo apt-get install unzip -y
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install --bin-dir /usr/local/bin --install-dir /usr/local/aws-cli --update

# Install Docker
# Add Docker's official GPG key:
sudo apt-get install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update -y

# Install the Docker packages
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

# Download .tar image from S3
aws s3 cp s3://mybucket-ricardo/trainer-service.tar /home/ubuntu/trainer-service.tar

# Load Docker image from .tar file and run Docker container
sudo docker load -i /home/ubuntu/trainer-service.tar
sudo docker run -d --name reporting-microservice -p 8080:8080 trainer-service:latest