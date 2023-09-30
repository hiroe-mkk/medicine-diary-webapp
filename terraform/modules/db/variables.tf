variable "prefix" {
  type = string
}

variable "db_subnet_group_name" {
  type = string
}

variable "vpc_security_group_ids" {
  type = list(string)
}

variable "availability_zone" {
  type = string
}

variable "db_name" {
  type = string
}