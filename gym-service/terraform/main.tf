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
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "${var.project_name}-Network"
  }
}

# Create an internet gateway and attach it to the VPC
resource "aws_internet_gateway" "gym_IGW" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    Name = "${var.project_name}-IGW"
  }
}

# PUBLIC SUBNET
# Create a public Route Table
resource "aws_route_table" "gym_public_route_table" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    Name = "${var.project_name}-PublicRouteTable"
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
    Name = "${var.project_name}-PublicSubnet-Main"
  }
}

# PRIVATE SUBNET
# Create a private Route Table
resource "aws_route_table" "gym_private_route_table" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    Name = "${var.project_name}-PrivateRouteTable"
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
    Name = "${var.project_name}-PrivateSubnet-Report"
  }
}

# RDS SUBNET FOR DATABASE
# Create a rds private Route Table
resource "aws_route_table" "gym_rds_route_table" {
  vpc_id = aws_vpc.gym_vpc.id
  tags = {
    Name = "${var.project_name}-RdsRouteTable"
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
    Name = "${var.project_name}-DBSubnet-RDS"
  }
}

# Create an Elastic IP address
resource "aws_eip" "gym_nat" {
  vpc = true
  tags = {
    Name = "${var.project_name}-EIP-NGW"
  }
}

# Create the NAT Gateway and attach the Elastic IP address
resource "aws_nat_gateway" "gym_nat" {
  allocation_id = aws_eip.gym_nat.id
  subnet_id     = aws_subnet.gym_public_subnet.id

  tags = {
    Name = "${var.project_name}-NGW"
  }
  depends_on = [aws_internet_gateway.gym_IGW]
}

# VPC Endpoints
# SQS VPC Endpoint
resource "aws_vpc_endpoint" "sqs_endpoint" {
  vpc_id            = aws_vpc.gym_vpc.id
  service_name      = "com.amazonaws.${var.region}.sqs"
  vpc_endpoint_type = "Interface"

  subnet_ids          = [aws_subnet.gym_public_subnet.id]
  security_group_ids  = [aws_security_group.gym_sg.id]
  private_dns_enabled = true

  tags = {
    Name = "${var.project_name}-VPC-SQS-Endpoint"
  }
}

# DynamoDB VPC Endpoint
resource "aws_vpc_endpoint" "dynamodb_endpoint" {
  vpc_id            = aws_vpc.gym_vpc.id
  service_name      = "com.amazonaws.${var.region}.dynamodb"
  vpc_endpoint_type = "Gateway"

  tags = {
    Name = "${var.project_name}-VPC-DynamoDB-Endpoint"
  }
}

resource "aws_vpc_endpoint_route_table_association" "dynamodb_endpoint_rtb" {
  vpc_endpoint_id = aws_vpc_endpoint.dynamodb_endpoint.id
  route_table_id  = aws_route_table.gym_private_route_table.id
}

# Create security group to allow ingoing ports
resource "aws_security_group" "gym_sg" {
  name        = "allow-web"
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
    Name = "${var.project_name}-SecurityGroup"
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
