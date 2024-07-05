terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.16"
    }
  }

  required_version = ">= 1.2.0"
}

provider "aws" {
  region  = var.region
  profile = "default"
}

# Set the S3 readonly IAM role
data "aws_iam_role" "s3_readonly" {
  name = "ReadAccessRoleS3"
}

# Create a VPC
resource "aws_vpc" "gym_vpc" {
  cidr_block = "10.0.0.0/16"
  tags = {
    name = "GymApp-Network"
  }
}

# Create an internet gateway and attach it to the VPC
resource "aws_internet_gateway" "gym_IGW" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    name = "GymApp-IGW"
  }
}

# PUBLIC SUBNET
# Create a public Route Table
resource "aws_route_table" "gym_public_route_table" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    name = "GymApp-PublicRouteTable"
  }
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gym_IGW.id
  }
}

# Associate the public RouteTable with the PublicSubnet
resource "aws_route_table_association" "gym_public_route_table_assoc" {
  subnet_id      = aws_subnet.gym_public_subnet.id
  route_table_id = aws_route_table.gym_public_route_table.id
}

# Create a public subnet
resource "aws_subnet" "gym_public_subnet" {
  vpc_id            = aws_vpc.gym_vpc.id
  cidr_block        = "10.0.11.0/24"
  availability_zone = var.availability_zone

  tags = {
    name = "GymApp-PublicSubnet-Main"
  }
}

# PRIVATE SUBNET
# Create a private Route Table
resource "aws_route_table" "gym_private_route_table" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    name = "GymApp-PrivateRouteTable"
  }
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_nat_gateway.gym_nat.id
  }
}

# Associate the RouteTable with the PrivateSubnet
resource "aws_route_table_association" "gym_private_route_table_assoc" {
  subnet_id      = aws_subnet.gym_private_subnet.id
  route_table_id = aws_route_table.gym_private_route_table.id
}

# Create a private subnet
resource "aws_subnet" "gym_private_subnet" {
  vpc_id            = aws_vpc.gym_vpc.id
  cidr_block        = "10.0.12.0/24"
  availability_zone = var.availability_zone

  tags = {
    name = "GymApp-PrivateSubnet-Report"
  }
}

# RDS SUBNET FOR DATABASE
# Create a rds private Route Table
resource "aws_route_table" "gym_rds_route_table" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    name = "GymApp-RdsRouteTable"
  }
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_nat_gateway.gym_nat.id
  }
}

# Associate the RouteTable with the RDSSubnet
resource "aws_route_table_association" "gym_rds_route_table_assoc" {
  subnet_id      = aws_subnet.gym_rds_subnet.id
  route_table_id = aws_route_table.gym_rds_route_table.id
}

# Create a RDS subnet
resource "aws_subnet" "gym_rds_subnet" {
  vpc_id            = aws_vpc.gym_vpc.id
  cidr_block        = "10.0.13.0/24"
  availability_zone = var.availability_zone

  tags = {
    name = "GymApp-DBSubnet-RDS"
  }
}

# Create an Elastic IP address
resource "aws_eip" "gym_nat" {
  vpc = true
  tags = {
    Name = "GymApp-eip-ngw"
  }
}

# Create the NAT Gateway and attach the Elastic IP address
resource "aws_nat_gateway" "gym_nat" {
  allocation_id = aws_eip.gym_nat.id
  subnet_id     = aws_subnet.gym_public_subnet.id

  tags = {
    Name = "GymApp-ngw"
  }
  depends_on = [aws_internet_gateway.gym_IGW]
}

# Create security group to allow ingoing ports
resource "aws_security_group" "gym_sg" {
  name        = "gym_sec_group"
  description = "Security group for the EC2 instance. Allow https, http and ssh."
  vpc_id      = aws_vpc.gym_vpc.id
  ingress = [
    {
      description      = "https traffic"
      from_port        = 443
      to_port          = 443
      protocol         = "tcp"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    },
    {
      description      = "http traffic"
      from_port        = 80
      to_port          = 80
      protocol         = "tcp"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    },
    {
      description      = "ssh"
      from_port        = 22
      to_port          = 22
      protocol         = "tcp"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    },
    {
      description      = "custom tcp on port 8080"
      from_port        = 8080
      to_port          = 8080
      protocol         = "tcp"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]
  egress = [
    {
      description      = "Outbound traffic rule"
      from_port        = 0
      to_port          = 0
      protocol         = "-1"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]
  tags = {
    name = "allow_web"
  }
}

# Create Main EC2 instance
resource "aws_instance" "main_server" {
  ami                  = var.ami
  instance_type        = var.instance_type
  availability_zone    = var.availability_zone
  key_name             = var.key_name
  iam_instance_profile = data.aws_iam_role.s3_readonly.name

  user_data = file("${path.module}/user_data_main.sh")

  vpc_security_group_ids = [aws_security_group.gym_sg.id]

  subnet_id                   = aws_subnet.gym_public_subnet.id
  associate_public_ip_address = true

  tags = {
    Name = "MainMicroserviceServer"
  }
}

# Create Reporting EC2 instance
resource "aws_instance" "reporting_server" {
  ami                  = var.ami
  instance_type        = var.instance_type
  availability_zone    = var.availability_zone
  key_name             = var.key_name
  iam_instance_profile = data.aws_iam_role.s3_readonly.name

  user_data = file("${path.module}/user_data_reporting.sh")

  vpc_security_group_ids = [aws_security_group.gym_sg.id]

  subnet_id                   = aws_subnet.gym_private_subnet.id
  associate_public_ip_address = true

  tags = {
    Name = "ReportingMicroserviceServer"
  }
}
