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

variable "security_groups" {
  type = list(string)
}

variable "subnets" {
  type = list(string)
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "container_name" {
  type = string
}

variable "container_port" {
  type = string
}

variable "target_group_arn" {
  type = string
}