module "network" {
  source = "../../modules/network"

  prefix = local.prefix
}

module "security_group" {
  source = "../../modules/security_group"

  prefix = local.prefix
  vpc_id = module.network.aws_vpc_this_id
}

module "alb" {
  source = "../../modules/alb"

  prefix = local.prefix
  security_groups = [
    module.security_group.aws_security_group_web_id,
    module.security_group.aws_security_group_vpc_id
  ]
  vpc_id  = module.network.aws_vpc_this_id
  subnets = module.network.aws_subnet_public_ids
}

module "ecs" {
  source = "../../modules/ecs"

  prefix                              = local.prefix
  aws_iam_role_ecs_task_execution_arn = module.iam.aws_iam_role_ecs_task_execution_arn
}

module "iam" {
  source = "../../modules/iam"

  prefix = local.prefix
}

module "cloudwatch" {
  source = "../../modules/cloudwatch"

  prefix = local.prefix
}