resource "aws_cloudfront_distribution" "customer_data" {
  origin {
    origin_id                = var.aws_s3_bucket_customer_data_id
    domain_name              = var.aws_s3_bucket_customer_data_bucket_regional_domain_name
    origin_access_control_id = aws_cloudfront_origin_access_control.customer_data.id
  }

  enabled = true

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  default_cache_behavior {
    allowed_methods = ["GET", "HEAD"]
    cached_methods  = ["GET", "HEAD"]

    target_origin_id = var.aws_s3_bucket_customer_data_id

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

resource "aws_cloudfront_origin_access_control" "customer_data" {
  name = "${var.prefix}-cloudfront-oac"

  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}