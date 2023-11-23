variable "prefix" {
  type = string
}

variable "cloudfront_distribution_s3_bucket_arn" {
  type = string
}

variable "iam_role_ecs_task_arn" {
  type = string
}