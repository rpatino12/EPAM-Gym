variable "project_name" {
  description = "The name of the project"
  type        = string
  default     = "GymApp"
}

variable "region" {
  description = "The AWS region in which the resources will be created."
  type        = string
  default     = "us-east-1"
}

variable "availability_zone" {
  description = "The availability zone where the resources will reside."
  type        = string
  default     = "us-east-1a"
}

variable "ami" {
  description = "The ID of the Amazon Machine Image (AMI) used to create the EC2 instance: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type"
  type        = string
  default     = "ami-0e001c9271cf7f3b9"
}

variable "key_name" {
  description = "The name of the Access Key"
  type        = string
  default     = "ec2_key"
}

variable "instance_type" {
  description = "The type of EC2 instance used to create the instance."
  type        = string
  default     = "t2.micro"
}