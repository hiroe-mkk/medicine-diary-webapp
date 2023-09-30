data "aws_kms_alias" "rds" {
  name = "alias/aws/rds"
}

data "aws_region" "current" {}