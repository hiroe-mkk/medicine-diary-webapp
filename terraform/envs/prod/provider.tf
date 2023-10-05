provider "aws" {
  region = "ap-northeast-1"

  default_tags {
    tags = {
      Project     = local.project
      Environment = local.env
    }
  }
}

provider "aws" {
  alias  = "virginia"
  region = "us-east-1"

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
      source                = "hashicorp/aws"
      version               = "~> 5.17.0"
      configuration_aliases = [aws, aws.virginia]
    }
  }
  required_version = "1.5.7"
}