resource "aws_cloudfront_distribution" "s3_bucket" {
  origin {
    origin_id                = var.s3_bucket_this_id
    domain_name              = var.s3_bucket_this_bucket_regional_domain_name
    origin_access_control_id = aws_cloudfront_origin_access_control.s3_bucket.id
  }

  enabled = true

  aliases = ["image.${data.aws_route53_zone.this.name}"]

  viewer_certificate {
    acm_certificate_arn      = aws_acm_certificate.cloudfront.arn
    ssl_support_method       = "sni-only"
    minimum_protocol_version = "TLSv1"
  }

  default_cache_behavior {
    allowed_methods = ["GET", "HEAD"]
    cached_methods  = ["GET", "HEAD"]

    target_origin_id = var.s3_bucket_this_id

    forwarded_values {
      query_string = false

      cookies {
        forward = "none"
      }
    }

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  restrictions {
    geo_restriction {
      restriction_type = "whitelist"
      locations        = ["JP"]
    }
  }
}

resource "aws_cloudfront_origin_access_control" "s3_bucket" {
  name = "${var.prefix}-cloudfront-oac"

  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}