output "lb_target_group_this_arn" {
  value = aws_lb_target_group.this.arn
}

output "cloudfront_distribution_s3_bucket_arn" {
  value = aws_cloudfront_distribution.s3_bucket.arn
}