resource "aws_s3_bucket" "this" {
  bucket = var.prefix

  force_destroy = false

  tags = {
    Name = "${var.prefix}-s3-bucket"
  }
}

resource "aws_s3_bucket_public_access_block" "this" {
  bucket                  = aws_s3_bucket.this.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "aws_s3_bucket_policy" "bucket" {
  bucket = aws_s3_bucket.this.id
  policy = data.aws_iam_policy_document.s3_bucket.json
}