variable "prefix" {
  type = string
}

variable "security_groups" {
  type = list(string)
}

variable "subnets" {
  type = list(string)
}