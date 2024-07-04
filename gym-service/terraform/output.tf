output "SG_id" {
  description = "security group id"
  value       = aws_security_group.gym_sg.id
}

output "main_instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.main_server.id
}
output "main_instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.main_server.public_ip
}

output "reporting_instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.reporting_server.id
}
output "reporting_instance_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.reporting_server.public_ip
}