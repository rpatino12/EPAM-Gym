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

# Create security group to allow ingoing ports
resource "aws_security_group" "gym_sg" {
  name        = "gym_sec_group"
  description = "Security group for the EC2 instance. Allow https, http and ssh."
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

resource "aws_instance" "main_server" {
  ami                  = var.ami
  instance_type        = var.instance_type
  availability_zone    = var.availability_zone
  key_name             = "ec2_key"
  iam_instance_profile = data.aws_iam_role.s3_readonly.name

  user_data = file("${path.module}/user_data_main.sh")

  vpc_security_group_ids = [aws_security_group.gym_sg.id]

  tags = {
    Name = "MainMicroserviceServer"
  }
}

resource "aws_instance" "reporting_server" {
  ami                  = var.ami
  instance_type        = var.instance_type
  availability_zone    = var.availability_zone
  key_name             = "ec2_key"
  iam_instance_profile = data.aws_iam_role.s3_readonly.name

  user_data = file("${path.module}/user_data_reporting.sh")

  vpc_security_group_ids = [aws_security_group.gym_sg.id]

  tags = {
    Name = "ReportingMicroserviceServer"
  }
}
