variable "prefix" {
  type = string
}

variable "root_domain" {
  type = string
}

variable "security_groups" {
  type = list(string)
}

variable "vpc_id" {
  type = string
}

variable "subnets" {
  type = list(string)
}

variable "aws_s3_bucket_customer_data_id" {
  type = string
}

variable "aws_s3_bucket_customer_data_bucket_regional_domain_name" {
  type = string
}