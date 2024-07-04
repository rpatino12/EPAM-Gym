#!/bin/bash
sudo apt update -y
sudo apt install apache2 -y
sudo systemctl start apache2
echo "Deploy the Gym main microservice webserver on AWS" | sudo tee /var/www/html/index.html