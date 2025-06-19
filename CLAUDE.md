# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Go CLI application for sending Slack notifications, primarily used in GitHub workflows. The project compiles to native executables for different platforms (macOS ARM64, Linux x64/ARM64, Windows x64) using Go's built-in cross-compilation.

## Key Commands

### Building and Development
- `make build` - Build for current platform
- `make build-all` - Cross-compile for all supported platforms
- `go build -o bin/slack-notifier-cli .` - Direct Go build

### Running
- `./bin/slack-notifier-cli` - Run the built executable
- `go run . publish --help` - Run directly with Go

### Testing
- `make test` - Run all tests
- `make test-coverage` - Run tests with coverage report
- `go test ./...` - Direct Go test execution

### Code Quality
- `make fmt` - Format Go code
- `make lint` - Run linting (requires golangci-lint)
- `make dev` - Development cycle (format, build, test)

## Architecture

### Main Components
- **main.go**: Entry point that sets up the CLI application
- **cmd/publish.go**: CLI command using urfave/cli library, handles GitHub Action environment variables
- **pkg/service/publish_slack.go**: Orchestrates the Slack publishing workflow
- **pkg/client/slack.go**: Handles Slack API interactions (create/update messages)

### Key Models
- **pkg/models/github.go**: Unified GitHub event data from various GitHub webhook payloads
- **pkg/models/job.go**: Job types and status enums for workflow classification
- **pkg/models/slack.go**: Slack message structure for rich formatting

### Data Flow
1. GitHub Action provides environment variables and event JSON file
2. CLI command parses options and GitHub event data using urfave/cli
3. Event JSON is normalized into GitHubEvent via parsers in `pkg/utils/`
4. PublishSlackService coordinates message creation/update via SlackClient
5. Output includes SLACK_MESSAGE_ID for subsequent workflow steps

### Dependencies
- **urfave/cli**: CLI argument parsing and command structure
- **resty**: HTTP client for Slack API calls
- **encoding/json**: Standard JSON parsing for GitHub events
- **time**: Standard date/time handling
- **testify**: Testing framework with assertions

## Development Notes

### Platform Targeting
The project uses Go's built-in cross-compilation to generate native executables for multiple platforms. Build targets are configured in the Makefile and support macOS ARM64/x64, Linux x64/ARM64, and Windows x64.

### GitHub Integration
The CLI is designed to run in GitHub Actions and expects specific environment variables (GITHUB_EVENT_PATH, GITHUB_REPOSITORY, etc.). Event parsing handles multiple GitHub webhook formats through dedicated parsers in pkg/utils/.

### Usage Context
This tool is typically used indirectly through:
1. [slack-notifier-cli-action](https://github.com/monta-app/slack-notifier-cli-action) - GitHub Action wrapper
2. [github-workflows](https://github.com/monta-app/github-workflows) - Shared workflow templates