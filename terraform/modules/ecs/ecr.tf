resource "aws_ecr_repository" "this" {
  name = var.prefix

  tags = {
    name = "${var.prefix}"
  }
}

resource "aws_ecr_lifecycle_policy" "this" {
  policy = jsonencode(
    {
      "rules" : [
        # 最新のイメージを5個まで残し、それより古いイメージは削除する
        {
          "rulePriority" : 1,
          "description" : "Hold only 5 images, expire all others",
          "selection" : {
            "tagStatus" : "any",
            "countType" : "imageCountMoreThan",
            "countNumber" : 5
          },
          "action" : {
            "type" : "expire"
          }
        }
      ]
    }
  )

  repository = aws_ecr_repository.this.name
}