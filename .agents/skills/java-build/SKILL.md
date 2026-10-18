---
name: java-builder
description: Guidelines and instructions for building, testing, and compiling Java Spring Boot projects using Gradle.
version: 1.0.0
---

## Overview
Use this skill when tasked with compiling, testing, or building Java projects, particularly Spring Boot applications managed via Gradle.

## Core Instructions
1. **Build Tool:** Always use the local Gradle wrapper (`./gradlew`) instead of a global gradle installation to ensure version consistency.
2. **Profile Execution:** When running or testing locally, pass the appropriate profile argument (e.g., `--args='--spring.profiles.active=test'` for H2 or `dev` for PostgreSQL).
3. **Java Toolchain:** Ensure compliance with Java 17 toolchain configurations defined in `build.gradle`.
4. **Code Quality Standards:** Ensure all files adhere to final newline and trailing space rules upon saving.

## Common Gradle Commands
* **Clean & Build:** `./gradlew clean build`
* **Run Tests:** `./gradlew test`
* **Run App (Test Profile):** `./gradlew bootRun --args='--spring.profiles.active=test'`
* **Run App (Dev Profile):** `./gradlew bootRun --args='--spring.profiles.active=dev'`
