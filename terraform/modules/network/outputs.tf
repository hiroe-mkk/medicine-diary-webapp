output "vpc_this_id" {
  value = aws_vpc.this.id
}

output "subnet_public_ids" {
  value = [for s in aws_subnet.public : s.id]
}

output "db_subnet_group_this_id" {
  value = aws_db_subnet_group.this.id
}