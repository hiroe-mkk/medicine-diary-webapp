# ---------------------------
# タスク実行ロール
# ---------------------------
resource "aws_iam_role" "ecs_task_execution" {
  name = "${var.prefix}-ecs-task-execution"

  assume_role_policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Effect" : "Allow",
          "Principal" : {
            "Service" : "ecs-tasks.amazonaws.com"
          },
          "Action" : "sts:AssumeRole"
        }
      ]
    }
  )

  tags = {
    Name = "${var.prefix}-ecs-task-execution"
  }
}

resource "aws_iam_policy" "ssm" {
  name = "${var.prefix}-ssm"
  policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Effect" : "Allow",
          "Action" : [
            "ssm:GetParameters",
            "ssm:GetParameter",
            "kms:Decrypt"
          ],
          "Resource" : "arn:aws:ssm:${data.aws_region.current.id}:${data.aws_caller_identity.self.account_id}:parameter/${var.prefix}/*"
        }
      ]
    }
  )

  tags = {
    Name = "${var.prefix}-ssm"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = data.aws_iam_policy.ecs_task_execution.arn
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_ssm" {
  role       = aws_iam_role.ecs_task_execution.name
  policy_arn = aws_iam_policy.ssm.arn
}


# ---------------------------
# タスクロール
# ---------------------------
resource "aws_iam_role" "ecs_task" {
  name = "${var.prefix}-ecs-task"

  assume_role_policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Effect" : "Allow",
          "Principal" : {
            "Service" : "ecs-tasks.amazonaws.com"
          },
          "Action" : "sts:AssumeRole"
        }
      ]
    }
  )

  tags = {
    Name = "${var.prefix}-ecs-task"
  }
}

resource "aws_iam_policy" "s3" {
  name = "${var.prefix}-s3"
  policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Sid" : "S3PutGEList",
          "Effect" : "Allow",
          "Action" : [
            "s3:PutObject",
            "s3:GetObject",
            "s3:DeleteObject"
          ],
          "Resource" : "arn:aws:s3:::${var.s3_bucket_customer_data_arn}/*"
        }
      ]
    }
  )

  tags = {
    Name = "${var.prefix}-s3"
  }
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_s3" {
  role       = aws_iam_role.ecs_task.name
  policy_arn = aws_iam_policy.s3.arn
}