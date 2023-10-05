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

variable "security_group_webapp_id" {
  type = string
}

variable "subnet_public_ids" {
  type = list(string)
}

variable "desired_count" {
  type    = number
  default = 1
}

variable "lb_target_group_this_arn" {
  type = string
}

variable "iam_role_ecs_task_arn" {
  type = string
}

variable "iam_role_ecs_task_execution_arn" {
  type = string
}