# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Kotlin Multiplatform CLI application for sending Slack notifications, primarily used in GitHub workflows. The project compiles to native executables for different platforms (macOS ARM64, Linux x64/ARM64, Windows x64) using Kotlin/Native.

## Key Commands

### Building and Development
- `./gradlew build` - Full build including compilation and tests
- `./gradlew commonBinaries` - Build native executables (default task)
- `./gradlew linkReleaseExecutableCommon` - Build release executable
- `./gradlew linkDebugExecutableCommon` - Build debug executable

### Running
- `./gradlew runDebugExecutableCommon` - Run debug executable
- `./gradlew runReleaseExecutableCommon` - Run release executable

### Testing
- `./gradlew commonTest` - Run tests for common target
- `./gradlew allTests` - Run all tests with aggregated report

### Code Quality
- `./gradlew ktlintCheck` - Run linting
- `./gradlew ktlintFormat` - Auto-format code
- `./gradlew check` - Run all checks (tests + linting)

## Architecture

### Main Components
- **Main.kt**: Entry point that delegates to PublishSlackCommand
- **PublishSlackCommand**: CLI command parser using Clikt library, handles GitHub Action environment variables
- **PublishSlackService**: Orchestrates the Slack publishing workflow
- **SlackClient**: Handles Slack API interactions (create/update messages)

### Key Models
- **GithubEvent**: Unified GitHub event data from various GitHub webhook payloads
- **JobType/JobStatus**: Enums for workflow job classification
- **SlackMessage/SlackBlock**: Slack message structure for rich formatting

### Data Flow
1. GitHub Action provides environment variables and event JSON file
2. PublishSlackCommand parses CLI options and GitHub event data
3. Event JSON is normalized into GithubEvent via serializers in `model/serializers/`
4. PublishSlackService coordinates message creation/update via SlackClient
5. Output includes SLACK_MESSAGE_ID for subsequent workflow steps

### Dependencies
- **Clikt**: CLI argument parsing
- **Ktor**: HTTP client for Slack API calls
- **kotlinx-serialization**: JSON parsing for GitHub events
- **kotlinx-datetime**: Date/time handling
- **Kotest**: Testing framework

## Development Notes

### Platform Targeting
The project uses cross-compilation with a single "common" target that maps to the host platform. The native target selection happens at build time based on OS detection.

### GitHub Integration
The CLI is designed to run in GitHub Actions and expects specific environment variables (GITHUB_EVENT_PATH, GITHUB_REPOSITORY, etc.). Event parsing handles multiple GitHub webhook formats through dedicated serializers.

### Usage Context
This tool is typically used indirectly through:
1. [slack-notifier-cli-action](https://github.com/monta-app/slack-notifier-cli-action) - GitHub Action wrapper
2. [github-workflows](https://github.com/monta-app/github-workflows) - Shared workflow templates