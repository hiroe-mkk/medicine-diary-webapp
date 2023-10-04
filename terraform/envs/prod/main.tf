module "network" {
  source = "../../modules/network"

  prefix = local.prefix
}

module "security_group" {
  source = "../../modules/security_group"

  prefix = local.prefix
  vpc_id = module.network.aws_vpc_this_id
}

module "ecs" {
  source = "../../modules/ecs"

  prefix           = local.prefix
  security_groups  = [module.security_group.aws_security_group_webapp_id]
  subnets          = module.network.aws_subnet_public_ids
  target_group_arn = module.routing.aws_lb_target_group_this_arn
  resource         = module.s3.aws_s3_bucket_customer_data_arn
}

module "cloudwatch" {
  source = "../../modules/cloudwatch"

  prefix = local.prefix
}

module "routing" {
  source = "../../modules/routing"

  prefix          = local.prefix
  security_groups = [module.security_group.aws_security_group_alb_id]
  vpc_id          = module.network.aws_vpc_this_id
  subnets         = module.network.aws_subnet_public_ids
}

module "cloudfront" {
  source = "../../modules/cloudfront"

  prefix                                                  = local.prefix
  aws_s3_bucket_customer_data_id                          = module.s3.aws_s3_bucket_customer_data_id
  aws_s3_bucket_customer_data_bucket_regional_domain_name = module.s3.aws_s3_bucket_customer_data_bucket_regional_domain_name
}

module "db" {
  source = "../../modules/db"

  prefix                 = local.prefix
  db_subnet_group_name   = module.network.aws_db_subnet_group_this_id
  vpc_security_group_ids = [module.security_group.aws_security_group_rds_id]
  availability_zone      = "ap-northeast-1a"
  db_name                = local.prefix_with_underscore
}

module "s3" {
  source = "../../modules/s3"

  prefix                                        = local.prefix
  aws_cloudfront_distribution_customer_data_arn = module.cloudfront.aws_cloudfront_distribution_customer_data_arn
}