resource "aws_lb" "this" {
  name = "${var.prefix}-alb"

  internal                   = false
  load_balancer_type         = "application"
  enable_deletion_protection = false
  idle_timeout               = 60

  security_groups = var.security_groups
  subnets         = var.subnets

  tags = {
    Name = "${var.prefix}-alb"
  }
}

resource "aws_lb_listener" "this" {
  load_balancer_arn = aws_lb.this.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"

    fixed_response {
      content_type = "text/plain"
      message_body = "Fixed response content"
      status_code  = "200"
    }
  }
}