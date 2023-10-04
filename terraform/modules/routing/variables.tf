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