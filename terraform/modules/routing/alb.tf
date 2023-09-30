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

resource "aws_lb_listener" "https" {
  certificate_arn   = aws_acm_certificate.root.arn
  load_balancer_arn = aws_lb.this.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-TLS13-1-2-2021-06"

  default_action {
    type = "forward"

    target_group_arn = aws_lb_target_group.this.arn
  }
}

resource "aws_lb_listener" "redirect_http_to_https" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "redirect"

    redirect {
      port        = "443"
      protocol    = "HTTPS"
      status_code = "HTTP_301"
    }
  }
}

resource "aws_lb_target_group" "this" {
  name = "${var.prefix}-tg"

  deregistration_delay = 60
  port                 = 8080
  protocol             = "HTTP"
  target_type          = "ip"
  vpc_id               = var.vpc_id

  health_check {
    path                = "/"
    port                = "traffic-port"
    protocol            = "HTTP"
    matcher             = 200
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }

  tags = {
    Name = "${var.prefix}-tg"
  }
}