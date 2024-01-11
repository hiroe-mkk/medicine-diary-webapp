variable "prefix" {
  type = string
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "azs" {
  type = map(object({
    public_cidr  = string
    private_cidr = string
  }))
  default = {
    a = {
      public_cidr  = "10.0.1.0/24"
      private_cidr = "10.0.11.0/24"
    },
    c = {
      public_cidr  = "10.0.2.0/24"
      private_cidr = "10.0.12.0/24"
    }
  }
}