variable "prefix" {
  type = string
}

variable "aws_iam_role_ecs_task_execution_arn" {
  type = string
}

variable "memory" {
  type    = string
  default = "1024"
}

variable "cpu" {
  type    = string
  default = "512"
}