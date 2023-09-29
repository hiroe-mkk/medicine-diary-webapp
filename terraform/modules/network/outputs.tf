output "aws_vpc_this_id" {
  value = aws_vpc.this.id
}

output "aws_subnet_public_ids" {
  value = [for s in aws_subnet.public : s.id]
}

output "aws_db_subnet_group_this_id" {
  value = aws_db_subnet_group.this.id
}