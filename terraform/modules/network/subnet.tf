resource "aws_subnet" "public" {
  for_each = var.azs

  availability_zone       = "${data.aws_region.current.name}${each.key}"
  cidr_block              = each.value.public_cidr
  map_public_ip_on_launch = true
  vpc_id                  = aws_vpc.this.id

  tags = {
    Name = "${var.prefix}-public-subnet-${each.key}"
  }
}

resource "aws_subnet" "private" {
  for_each = var.azs

  availability_zone       = "${data.aws_region.current.name}${each.key}"
  cidr_block              = each.value.private_cidr
  map_public_ip_on_launch = false
  vpc_id                  = aws_vpc.this.id

  tags = {
    Name = "${var.prefix}-private-subnet-${each.key}"
  }
}