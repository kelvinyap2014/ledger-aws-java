# Agent Guidance: LedgerStream (Ledger/Accounts, FinTech) Pet Project

## Project Overview

**LedgerStream** is a core financial ledger and transaction processing service designed for high reliability, strict ACID compliance, and secure cloud-native deployment on **AWS ECS with Fargate**, backed by **Amazon RDS PostgreSQL**, **AWS S3**, and **AWS SQS/Lambda**.

This pet project demonstrates AI-assisted technical design and implementation using:
- **Backend:** Java 17, Spring Boot, PostgreSQL
- **Serverless:** AWS ECS/Fargate, AWS Lambda (Go), AWS SQS
- **Infrastructure:** AWS ALB, AWS RDS, AWS S3
- **IaC:** Terraform
- **Version Control:** Git/GitHub

---

## Key Architecture Components

### System Flow
```
Client → ALB → Spring Boot (Fargate) → PostgreSQL (RDS) + S3 + SQS
                                    ↓
                              AWS Lambda (Settlement Processor)
```

### Core Features
1. **Double-Entry Ledger** — All transactions recorded as debit/credit pairs with ACID guarantees
2. **Pessimistic Locking** — Account-level locks prevent race conditions during concurrent transfers
3. **Idempotency** — Request deduplication via idempotency keys
4. **Asynchronous Settlement** — Events published to SQS, processed by Lambda
5. **Statement Export** — Pre-signed S3 URLs for secure document retrieval
6. **Liquibase Migrations** — Version-controlled schema and mock data

---

## Build & Development

### Java / Spring Boot

Use the `java-builder` skill for detailed instructions.

**Quick Reference:**
```bash
# Build & Test (using Gradle wrapper)
./gradlew clean build

# Run Tests
./gradlew test

# Run App (Test Profile - H2 in-memory)
./gradlew bootRun --args='--spring.profiles.active=test'

# Run App (Dev Profile - PostgreSQL)
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**Requirements:**
- Java 17 (enforced via `build.gradle` toolchain config)
- Local Gradle wrapper (`./gradlew`) — do NOT use global gradle
- Spring Boot version defined in `build.gradle`

### Go / AWS Lambda

Use the `golang-build` skill for detailed instructions.

**Quick Reference:**
```bash
# Complete pipeline (format → vet → test → build)
make all

# Or run individually:
make format      # Format code (gofmt)
make vet         # Run linter (go vet)
make test        # Run unit tests
make build       # Compile binaries
make clean       # Remove artifacts
```

**Output:** Lambda binaries compile to `lambda/<handler>/bootstrap` and are zipped as `function.zip` for Terraform deployment.

**Requirements:**
- Go version specified in README
- `GOOS=linux GOARCH=amd64 CGO_ENABLED=0` (static linking for Lambda)
- AWS SDK for Go v2 (configured in `go.mod`)

---

## AWS Infrastructure (Terraform)

All Terraform code is in the `terraform/` directory. Key files:
- `main.tf` — Provider config, AWS region, Terraform version
- `iam.tf` — Lambda execution roles, IAM policies
- `lambda.tf` — Lambda function definitions (Go binaries)
- `dynamodb.tf` or database config — RDS PostgreSQL setup (if applicable)
- `api_gateway.tf` — HTTP API routes
- `*-dev.tfvars` / `*-prod.tfvars` — Environment-specific variables

**Standard Terraform Workflow:**
```bash
cd terraform

# Initialize (first time or after provider upgrades)
terraform init

# Plan changes
terraform plan -var-file="terraform-dev.tfvars"

# Apply changes
terraform apply -var-file="terraform-dev.tfvars"

# Destroy resources (cleanup)
terraform destroy -var-file="terraform-dev.tfvars"
```

**AWS Region:** All resources must be deployed in your project's assigned region (check AWS Settings > View all projects > Overview > Additional Info > Region).

---

## Available Skills

Load a skill with `read_file` for detailed step-by-step guidance:

| Skill | When to Use |
|---|---|
| `java-build` (.agents/skills/java-build/SKILL.md) | Building, testing, and compiling Java Spring Boot projects via Gradle |
| `golang-build` (.agents/skills/golang-build/SKILL.md) | Building, testing, and packaging Go Lambda functions (make all \| make test) |

---

## Database Schema & Migrations

Liquibase changelog files are located at:
- `src/main/resources/db/changelog/`

**Master Changelog:** `db.changelog-master.yaml`

**Schema Includes:**
- `001-initial-schema.sql` — Accounts, ledger entries, idempotency keys, indexes
- `src/test/resources/db/changelog/test/001-test-sample-mock-data.sql` — Sample test accounts and transactions

**Key Tables:**
- `accounts` — Holder name, balance, currency (NZD), status (ACTIVE/SUSPENDED)
- `ledger_entries` — Debit/credit records, transaction reference, timestamps
- `idempotency_keys` — Request deduplication for fault tolerance

---

## Development Workflow (Typical)

1. **Feature Branch** — Create a feature branch in Git
2. **Code Changes** — Edit Java service code or Go Lambda functions
3. **Build & Test**:
   - `./gradlew clean build` (Java)
   - `make all` (Go Lambda functions)
4. **Manual Testing** — Run Spring Boot locally with test or dev profile
5. **Terraform Validation** — Run `terraform plan` to validate IaC changes
6. **Commit & Push** — Commit changes, push feature branch
7. **Code Review** — Create pull request, wait for approval
8. **Merge & Deploy** — Merge to main, deploy via Terraform apply

---

## Performance Targets & Testing

Refer to README for:
- Integration test examples
- Load testing strategies
- Performance benchmarks for ledger operations
- Settlement processor concurrency patterns

---

## Key Environment Files

| File | Purpose |
|---|---|
| `build.gradle` | Java/Gradle build config, Spring Boot version, dependencies |
| `go.mod` / `go.sum` | Go module dependencies |
| `Makefile` | Go build targets (format, vet, test, build, all) |
| `terraform/main.tf` | Terraform provider & region config |
| `src/main/resources/application-*.yml` | Spring Boot profiles (test, dev, prod) |
| `src/main/resources/db/changelog/` | Liquibase migration files |

---

## Helpful Commands Quick Reference

```bash
# Java / Spring Boot
./gradlew clean build           # Full build & test
./gradlew bootRun --args='...'  # Run locally with profile
./gradlew test                  # Test only

# Go / Lambda
make all                        # Format, vet, test, build
make test                       # Run tests only
make build                      # Build binaries only

# Terraform
cd terraform && terraform init          # Initialize
terraform plan -var-file="terraform-dev.tfvars"
terraform apply -var-file="terraform-dev.tfvars"
terraform destroy -var-file="terraform-dev.tfvars"

# Git
git checkout -b feature/xyz             # New feature branch
git commit -m "message"                 # Commit changes
git push origin feature/xyz             # Push to GitHub
```

---

## Stop & Consult User If

- Build or compilation fails with unclear error messages (attempt 5+ times, then ask)
- Terraform plan shows unexpected resource changes
- AWS credentials are missing or expired
- Need to modify region or deploy to production
- Significant architectural changes are proposed
