resource "aws_s3_bucket" "customer_data" {
  bucket = "${var.prefix}-customer-data"

  force_destroy = false // TODO: trueに変更する

  tags = {
    Name = "${var.prefix}-s3-bucket-customer-data"
  }
}