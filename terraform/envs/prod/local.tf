locals {
  project = "medicine-diary"
  env     = "prod"
  prefix  = "${local.project}-${local.env}"
}