locals {
  project                 = "medicine-diary"
  project_with_underscore = "medicine_diary"

  env                     = "prod"

  prefix                  = "${local.project}-${local.env}"
  prefix_with_underscore  = "${local.project_with_underscore}_${local.env}"

  root_domain             = "okusuri-nikki-kk.link"
}