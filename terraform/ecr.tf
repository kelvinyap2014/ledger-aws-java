resource "aws_ecr_repository" "ledger_stream" {
  name                 = "ledger-stream"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}
