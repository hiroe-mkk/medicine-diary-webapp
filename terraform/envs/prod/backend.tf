terraform {
  backend "s3" {
    bucket = "medicine-diary-prod-tfstate"
    key    = "prod/tfstate-v1.0.0.tfstate"
    region = "ap-northeast-1"
  }
}