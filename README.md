# Slack Notifier CLI (Go)

A cross-platform CLI tool for sending structured Slack notifications from GitHub Actions workflows. This is a complete rewrite of the original Kotlin Native application in Go.

## Features

- **GitHub Actions Integration**: Seamlessly integrates with GitHub workflows
- **Rich Slack Messages**: Sends formatted messages with blocks, attachments, and colors
- **Multiple Event Types**: Supports push, pull request, and issue events
- **Message Updates**: Can update existing messages to show job status progression
- **Cross-Platform**: Builds native executables for macOS, Linux, and Windows

## Quick Start

### Installation

Download the latest binary for your platform from the releases page, or build from source:

```bash
make build
```

### Usage

The CLI is designed to run in GitHub Actions with environment variables:

```bash
slack-notifier-cli publish \
  --job-type "Test" \
  --job-status "Success" \
  --slack-token "$SLACK_APP_TOKEN" \
  --slack-channel-id "$SLACK_CHANNEL_ID"
```

All GitHub-specific environment variables are automatically detected when running in GitHub Actions.

## Command Line Options

| Option | Environment Variable | Required | Description |
|--------|---------------------|----------|-------------|
| `--github-event-path` | `GITHUB_EVENT_PATH` | Yes | Path to GitHub event JSON file |
| `--github-repository` | `GITHUB_REPOSITORY` | Yes | GitHub repository (e.g., owner/repo) |
| `--github-run-id` | `GITHUB_RUN_ID` | Yes | GitHub Actions run ID |
| `--github-workflow` | `GITHUB_WORKFLOW` | Yes | GitHub workflow name |
| `--github-ref-name` | `GITHUB_REF_NAME` | Yes | GitHub ref name (branch/tag) |
| `--service-name` | `PUBLISH_SLACK_SERVICE_NAME` | No | Service name (default: "GitHub Actions") |
| `--service-emoji` | `PUBLISH_SLACK_SERVICE_EMOJI` | No | Service emoji (default: "⚡") |
| `--job-type` | `PUBLISH_SLACK_JOB_TYPE` | Yes | Job type (Test, Build, Deploy, PublishDocs) |
| `--job-status` | `PUBLISH_SLACK_JOB_STATUS` | Yes | Job status (Progress, Success, Failure, Cancelled, Unknown) |
| `--slack-token` | `SLACK_APP_TOKEN` | Yes | Slack app token |
| `--slack-channel-id` | `SLACK_CHANNEL_ID` | Yes | Slack channel ID |
| `--slack-message-id` | `SLACK_MESSAGE_ID` | No | Slack message ID for updates |

## Development

### Building

```bash
# Build for current platform
make build

# Cross-compile for all platforms
make build-all

# Development cycle (format, build, test)
make dev
```

### Testing

```bash
# Run tests
make test

# Run tests with coverage
make test-coverage
```

### Project Structure

```
├── cmd/                    # CLI commands
├── pkg/
│   ├── client/            # Slack API client
│   ├── models/            # Data models
│   ├── service/           # Business logic
│   └── utils/             # Utilities
├── main.go                # Application entry point
├── Makefile              # Build configuration
└── .goreleaser.yml       # Release configuration
```

## Migration from Kotlin Native

This Go version maintains 100% feature compatibility with the original Kotlin Native implementation while offering:

- **Faster builds**: Go compilation is significantly faster
- **Smaller binaries**: Go produces more compact executables
- **Simpler cross-compilation**: Built-in cross-platform support
- **Better error handling**: Explicit error handling throughout
- **Improved maintainability**: Cleaner architecture and testing

The CLI interface remains identical, ensuring drop-in compatibility with existing GitHub Actions workflows.

## License

MIT License - see the original repository for details.