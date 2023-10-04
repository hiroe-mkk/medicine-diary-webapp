output "aws_lb_target_group_this_arn" {
  value = aws_lb_target_group.this.arn
}

output "aws_cloudfront_distribution_customer_data_arn" {
  value = aws_cloudfront_distribution.customer_data.arn
}