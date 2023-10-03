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

  execution_role_arn = aws_iam_role.ecs_task_execution.arn

  memory = var.memory
  cpu    = var.cpu

  container_definitions = jsonencode(
    [
      {
        name  = "springboot"
        image = "${aws_ecr_repository.this.repository_url}:latest"

        portMappings = [
          {
            containerPort = 8080
          }
        ]

        "secrets" = [
          {
            "name"      = "PROFILES"
            "valueFrom" = "/${var.prefix}/springboot/profiles"
          },
          {
            "name"      = "GOOGLE_OAUTH2_CLIENT_ID"
            "valueFrom" = "/${var.prefix}/springboot/google_oauth2_client_id"
          },
          {
            "name"      = "GOOGLE_OAUTH2_CLIENT_SECRET"
            "valueFrom" = "/${var.prefix}/springboot/google_oauth2_client_secret"
          },
          {
            "name"      = "MYSQL_HOST"
            "valueFrom" = "/${var.prefix}/springboot/mysql_host"
          },
          {
            "name"      = "MYSQL_DATABASE_NAME"
            "valueFrom" = "/${var.prefix}/springboot/mysql_database_name"
          },
          {
            "name"      = "MYSQL_USERNAME"
            "valueFrom" = "/${var.prefix}/springboot/mysql_username"
          },
          {
            "name"      = "MYSQL_PASSWORD"
            "valueFrom" = "/${var.prefix}/springboot/mysql_password"
          }
        ]

        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-group         = "/ecs/logs/${var.prefix}/springboot"
            awslogs-region        = data.aws_region.current.id
            awslogs-stream-prefix = "ecs"
          }
        }
      }
    ]
  )

  tags = {
    Name = "${var.prefix}-service"
  }
}

resource "aws_ecs_service" "this" {
  name = "${var.prefix}-service"

  launch_type      = "FARGATE"
  platform_version = "1.4.0"

  cluster         = aws_ecs_cluster.this.arn
  task_definition = aws_ecs_task_definition.this.arn

  desired_count                      = var.desired_count
  deployment_minimum_healthy_percent = 100
  deployment_maximum_percent         = 200

  network_configuration {
    assign_public_ip = true
    security_groups  = var.security_groups
    subnets          = var.subnets
  }

  load_balancer {
    container_name   = "springboot"
    container_port   = 8080
    target_group_arn = var.target_group_arn
  }

  tags = {
    Name = "${var.prefix}-service"
  }
}