resource "aws_cloudwatch_log_group" "springboot" {
  name              = "/ecs/logs/${var.prefix}/springboot"
  retention_in_days = 30
}