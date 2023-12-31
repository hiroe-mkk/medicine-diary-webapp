# ---------------------------
# ルートドメイン
# ---------------------------
resource "aws_route53_record" "certificate_validation_root" {
  for_each = {
    for dvo in aws_acm_certificate.root.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      type   = dvo.resource_record_type
      record = dvo.resource_record_value
    }
  }

  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = data.aws_route53_zone.this.id
  allow_overwrite = true
}

resource "aws_route53_record" "root_a" {
  name    = data.aws_route53_zone.this.name
  type    = "A"
  zone_id = data.aws_route53_zone.this.zone_id

  alias {
    evaluate_target_health = true
    name                   = aws_lb.this.dns_name
    zone_id                = aws_lb.this.zone_id
  }
}


# ---------------------------
# 画像用ドメイン
# ---------------------------
resource "aws_route53_record" "certificate_validation_image" {
  for_each = {
    for dvo in aws_acm_certificate.image.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      type   = dvo.resource_record_type
      record = dvo.resource_record_value
    }
  }

  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = data.aws_route53_zone.this.id
  allow_overwrite = true
}

resource "aws_route53_record" "image_cname" {
  name    = var.image_domain
  type    = "CNAME"
  zone_id = data.aws_route53_zone.this.zone_id
  ttl     = 60

  records = [aws_cloudfront_distribution.s3_bucket.domain_name]
}