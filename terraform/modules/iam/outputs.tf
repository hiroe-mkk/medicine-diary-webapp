output "iam_role_ecs_task_arn" {
  value = aws_iam_role.ecs_task.arn
}

output "iam_role_ecs_task_execution_arn" {
  value = aws_iam_role.ecs_task_execution.arn
}