resource "aws_db_option_group" "this" {
  name = "${var.prefix}-db-option-group"

  engine_name          = "mysql"
  major_engine_version = "8.0"

  tags = {
    Name = "${var.prefix}-db-option-group"
  }
}