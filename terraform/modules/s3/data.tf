data "aws_iam_policy_document" "s3_bucket" {
  statement {
    effect = "Allow"
    principals {
      type        = "Service"
      identifiers = ["cloudfront.amazonaws.com"]
    }
    actions   = ["s3:GetObject"]
    resources = ["${aws_s3_bucket.this.arn}/*"]
    condition {
      test     = "StringEquals"
      variable = "aws:SourceArn"
      values   = [var.cloudfront_distribution_s3_bucket_arn]
    }
  }

  statement {
    effect = "Allow"
    principals {
      type        = "AWS"
      identifiers = [var.iam_role_ecs_task_arn]
    }
    actions = [
      "s3:PutObject",
      "s3:GetObject",
      "s3:DeleteObject"
    ]
    resources = ["${aws_s3_bucket.this.arn}/*"]
  }
}