output "aws_vpc_this_id" {
  value = aws_vpc.this.id
}

output "aws_subnet_public_ids" {
  value = [for s in aws_subnet.public : s.id]
}