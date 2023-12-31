# ---------------------------
# ルートドメイン
# ---------------------------
resource "aws_acm_certificate" "root" {
  domain_name       = data.aws_route53_zone.this.name
  validation_method = "DNS"

  tags = {
    Name = "${var.prefix}-certificate-root"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "root" {
  certificate_arn = aws_acm_certificate.root.arn
}


# ---------------------------
# 画像用ドメイン
# ---------------------------
resource "aws_acm_certificate" "image" {
  provider          = aws.virginia
  domain_name       = var.image_domain
  validation_method = "DNS"

  tags = {
    Name = "${var.prefix}-certificate-image"
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_acm_certificate_validation" "image" {
  provider        = aws.virginia
  certificate_arn = aws_acm_certificate.image.arn
}