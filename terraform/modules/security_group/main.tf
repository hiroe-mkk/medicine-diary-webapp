# ---------------------------
# ALB のセキュリティグループ
# ---------------------------
resource "aws_security_group" "alb" {
  name   = "${var.prefix}-alb-sg"
  vpc_id = var.vpc_this_id

  tags = {
    Name = "${var.prefix}-alb-sg"
  }
}

resource "aws_security_group_rule" "alb-ingress-http-ipv4" {
  security_group_id = aws_security_group.alb.id
  type              = "ingress"
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]

  from_port = 80
  to_port   = 80
}

resource "aws_security_group_rule" "alb-ingress-http-ipv6" {
  security_group_id = aws_security_group.alb.id
  type              = "ingress"
  protocol          = "tcp"
  ipv6_cidr_blocks  = ["::/0"]

  from_port = 80
  to_port   = 80
}

resource "aws_security_group_rule" "alb-ingress-https-ipv4" {
  security_group_id = aws_security_group.alb.id
  type              = "ingress"
  protocol          = "tcp"
  cidr_blocks       = ["0.0.0.0/0"]

  from_port = 443
  to_port   = 443
}

resource "aws_security_group_rule" "alb-ingress-https-ipv6" {
  security_group_id = aws_security_group.alb.id
  type              = "ingress"
  protocol          = "tcp"
  ipv6_cidr_blocks  = ["::/0"]

  from_port = 443
  to_port   = 443
}

resource "aws_security_group_rule" "alb-egress" {
  security_group_id        = aws_security_group.alb.id
  type                     = "egress"
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.webapp.id

  from_port = 8080
  to_port   = 8080
}


# ---------------------------
# Web アプリケーションのセキュリティグループ
# ---------------------------
resource "aws_security_group" "webapp" {
  name   = "${var.prefix}-webapp-sg"
  vpc_id = var.vpc_this_id

  tags = {
    Name = "${var.prefix}-webapp-sg"
  }
}

resource "aws_security_group_rule" "webapp-ingress" {
  security_group_id        = aws_security_group.webapp.id
  type                     = "ingress"
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.alb.id

  from_port = 8080
  to_port   = 8080
}

resource "aws_security_group_rule" "webapp-egress" {
  security_group_id = aws_security_group.webapp.id
  type              = "egress"
  protocol          = "all"
  cidr_blocks       = ["0.0.0.0/0"]

  from_port = 0
  to_port   = 0
}


# ---------------------------
# RDS のセキュリティグループ
# ---------------------------
resource "aws_security_group" "rds" {
  name   = "${var.prefix}-rds-sg"
  vpc_id = var.vpc_this_id

  tags = {
    Name = "${var.prefix}-rds-sg"
  }
}

resource "aws_security_group_rule" "rds-ingress" {
  security_group_id        = aws_security_group.rds.id
  type                     = "ingress"
  protocol                 = "tcp"
  source_security_group_id = aws_security_group.webapp.id

  from_port = 3306
  to_port   = 3306
}