output "s3_bucket_customer_data_arn" {
  value = aws_s3_bucket.customer_data.arn
}

output "s3_bucket_customer_data_id" {
  value = aws_s3_bucket.customer_data.id
}

output "s3_bucket_customer_data_bucket_regional_domain_name" {
  value = aws_s3_bucket.customer_data.bucket_regional_domain_name
}