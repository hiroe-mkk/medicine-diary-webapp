provider "aws" {
  region = "ap-northeast-1"

  default_tags {
    tags = {
      Project     = local.project
      Environment = local.env
    }
  }
}

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.17.0"
    }
  }
  required_version = "1.5.7"
}