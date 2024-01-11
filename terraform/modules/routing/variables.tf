variable "prefix" {
  type = string
}

variable "root_domain" {
  type = string
}

variable "image_domain" {
  type = string
}

variable "security_group_alb_id" {
  type = string
}

variable "vpc_this_id" {
  type = string
}

variable "subnet_public_ids" {
  type = list(string)
}

variable "s3_bucket_this_id" {
  type = string
}

variable "s3_bucket_this_bucket_regional_domain_name" {
  type = string
}