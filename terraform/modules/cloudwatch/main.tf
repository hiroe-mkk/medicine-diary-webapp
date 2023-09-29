resource "aws_cloudwatch_log_group" "springboot" {
  name              = "/ecs/logs/${var.prefix}/springboot"
  retention_in_days = 30
}

resource "aws_cloudwatch_log_group" "rds-error" {
  name              = "/rds/logs/${var.prefix}/error"
  retention_in_days = 30
}

resource "aws_cloudwatch_log_group" "rds-general" {
  name              = "/rds/logs/${var.prefix}/general"
  retention_in_days = 30
}

resource "aws_cloudwatch_log_group" "rds-slowquery" {
  name              = "/rds/logs/${var.prefix}/slowquery"
  retention_in_days = 30
}