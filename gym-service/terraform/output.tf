output "vpc_id" {
  description = "vpc id"
  value       = aws_vpc.gym_vpc.id
}

output "IGW_id" {
  description = "internet gateway id"
  value       = aws_internet_gateway.gym_IGW.id
}

output "SG_id" {
  description = "security group id"
  value       = aws_security_group.gym_sg.id
}

output "eip" {
  description = "public Ip of eip"
  value       = aws_eip.gym_nat.public_ip
}

output "public_subnet_id" {
  description = "public subnet id"
  value       = aws_subnet.gym_public_subnet.id
}
output "main_instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.main_server.id
}
output "main_instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.main_server.public_ip
}
output "main_instance_private_ip" {
  description = "Private IP address of the EC2 instance"
  value       = aws_instance.main_server.private_ip
}

output "private_subnet_id" {
  description = "private subnet id"
  value       = aws_subnet.gym_private_subnet.id
}
output "reporting_instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.reporting_server.id
}
output "reporting_instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.reporting_server.public_ip
}
output "reporting_instance_private_ip" {
  description = "Private IP address of the EC2 instance"
  value       = aws_instance.reporting_server.private_ip
}

output "rds_subnet_id" {
  description = "RDS subnet id"
  value       = aws_subnet.gym_rds_subnet.id
}