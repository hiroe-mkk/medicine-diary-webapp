variable "prefix" {
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

variable "target_group_arn" {
  type = string
}

variable "iam_role_ecs_task_arn" {
  type = string
}

variable "iam_role_ecs_task_execution_arn" {
  type = string
}