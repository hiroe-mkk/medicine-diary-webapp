resource "aws_db_instance" "this" {
  engine         = "mysql"
  engine_version = "8.0.33"

  identifier = "${var.prefix}-db-instance"

  username = "admin"
  password = "password"

  instance_class = "db.t3.micro"

  storage_type          = "gp2"
  allocated_storage     = 20
  max_allocated_storage = 0

  multi_az = false

  db_subnet_group_name   = var.db_subnet_group_name
  publicly_accessible    = false
  vpc_security_group_ids = var.vpc_security_group_ids
  availability_zone      = var.availability_zone
  port                   = 3306

  iam_database_authentication_enabled = false

  db_name              = var.db_name
  parameter_group_name = aws_db_parameter_group.this.name
  option_group_name    = aws_db_option_group.this.name

  backup_retention_period  = 1
  backup_window            = "17:00-18:00"
  copy_tags_to_snapshot    = true
  delete_automated_backups = true
  skip_final_snapshot      = true

  storage_encrypted = true
  kms_key_id        = data.aws_kms_alias.rds.target_key_arn

  performance_insights_enabled = false

  enabled_cloudwatch_logs_exports = ["error", "general", "slowquery"]

  auto_minor_version_upgrade = false
  maintenance_window         = "fri:18:00-fri:19:00"

  deletion_protection = false // TODO: trueに変更する

  lifecycle {
    ignore_changes = [password]
  }

  tags = {
    Name = "${var.prefix}-db-instance"
  }
}