output "aws_security_group_alb_id" {
  value = aws_security_group.alb.id
}

output "aws_security_group_webapp_id" {
  value = aws_security_group.webapp.id
}

output "aws_security_group_rds_id" {
  value = aws_security_group.rds.id
}