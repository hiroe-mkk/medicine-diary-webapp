module "network" {
  source = "../../modules/network"

  prefix = local.prefix
}

module "security_group" {
  source = "../../modules/security_group"

  prefix      = local.prefix
  vpc_this_id = module.network.vpc_this_id
}

module "iam" {
  source = "../../modules/iam"

  prefix             = local.prefix
  s3_bucket_this_arn = module.s3.s3_bucket_this_arn
}

module "ecs" {
  source = "../../modules/ecs"

  prefix                               = local.prefix
  security_group_webapp_id             = module.security_group.security_group_webapp_id
  subnet_public_ids                    = module.network.subnet_public_ids
  lb_target_group_this_arn             = module.routing.lb_target_group_this_arn
  iam_role_ecs_task_arn                = module.iam.iam_role_ecs_task_arn
  iam_role_ecs_task_execution_arn      = module.iam.iam_role_ecs_task_execution_arn
  cloudwatch_log_group_springboot_name = module.cloudwatch.cloudwatch_log_group_springboot_name
}

module "cloudwatch" {
  source = "../../modules/cloudwatch"

  prefix = local.prefix
}

module "routing" {
  source = "../../modules/routing"

  providers = {
    aws.virginia = aws.virginia
  }

  prefix                                     = local.prefix
  root_domain                                = local.root_domain
  image_domain                               = local.image_domain
  security_group_alb_id                      = module.security_group.security_group_alb_id
  vpc_this_id                                = module.network.vpc_this_id
  subnet_public_ids                          = module.network.subnet_public_ids
  s3_bucket_this_id                          = module.s3.s3_bucket_this_id
  s3_bucket_this_bucket_regional_domain_name = module.s3.s3_bucket_this_bucket_regional_domain_name
}

module "db" {
  source = "../../modules/db"

  prefix                  = local.prefix
  db_subnet_group_this_id = module.network.db_subnet_group_this_id
  security_group_rds_id   = module.security_group.security_group_rds_id
  availability_zone       = "ap-northeast-1a"
  db_name                 = local.prefix_with_underscore
}

module "s3" {
  source = "../../modules/s3"

  prefix                                = local.prefix
  cloudfront_distribution_s3_bucket_arn = module.routing.cloudfront_distribution_s3_bucket_arn
  iam_role_ecs_task_arn                 = module.iam.iam_role_ecs_task_arn
}