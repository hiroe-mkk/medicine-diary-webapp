resource "aws_ecs_cluster" "this" {
  name = "${var.prefix}-cluster"

  tags = {
    Name = "${var.prefix}-cluster"
  }
}

resource "aws_ecs_task_definition" "this" {
  family = "${var.prefix}-service"

  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]

  execution_role_arn = var.aws_iam_role_ecs_task_execution_arn

  memory = var.memory
  cpu    = var.cpu

  container_definitions = file("./container_definitions.json")

  tags = {
    Name = "${var.prefix}-service"
  }
}