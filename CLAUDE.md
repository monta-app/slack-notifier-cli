# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a TypeScript GitHub Action for sending Slack notifications from GitHub workflows. The action provides rich formatting and supports creating new messages or updating existing ones to track workflow progress.

## Key Commands

### Building and Development
- `npm install` - Install dependencies
- `npm run build` - Build the action (TypeScript compilation + bundling)
- `npm test` - Run tests
- `npm run lint` - Run ESLint
- `npm run format` - Format code with Prettier

### Code Quality
- `npm run lint` - Run linting with ESLint
- `npm run format` - Auto-format code with Prettier

## Architecture

### Main Components
- **src/index.ts**: Entry point that processes GitHub Action inputs and coordinates the workflow
- **src/services/publish-slack-service.ts**: Orchestrates the Slack publishing workflow
- **src/services/slack-client.ts**: Handles Slack API interactions using @slack/web-api
- **src/utils/github-event-parser.ts**: Parses GitHub event JSON files from various webhook formats
- **src/models/types.ts**: TypeScript types and enums for the application

### Key Models
- **GitHubEvent**: Unified GitHub event data from various GitHub webhook payloads
- **JobType/JobStatus**: Enums for workflow job classification with associated labels and colors
- **SlackMessage/SlackBlock/SlackAttachment**: Slack message structure for rich formatting

### Data Flow
1. GitHub Action inputs are processed from action.yml metadata
2. GitHub event JSON file is parsed and normalized into GitHubEvent
3. PublishSlackService coordinates message creation/update via SlackClient
4. Output includes SLACK_MESSAGE_ID for subsequent workflow steps

### Dependencies
- **@actions/core**: GitHub Actions toolkit for inputs/outputs
- **@actions/github**: GitHub Actions context utilities
- **@slack/web-api**: Official Slack Web API client
- **TypeScript**: Language and type system
- **@vercel/ncc**: Bundler for packaging the action

## Development Notes

### GitHub Action Structure
The project follows GitHub Action conventions:
- `action.yml`: Metadata defining inputs, outputs, and runtime
- `dist/index.js`: Bundled JavaScript entry point (auto-generated)
- Inputs can come from workflow YAML or environment variables

### GitHub Integration
The action processes GitHub event JSON files and environment variables:
- GITHUB_EVENT_PATH, GITHUB_REPOSITORY, GITHUB_RUN_ID, etc.
- Supports multiple GitHub webhook formats (push, pull_request, issues)
- Outputs SLACK_MESSAGE_ID for workflow chaining

### Build Process
The action is bundled using @vercel/ncc to create a single JavaScript file with all dependencies included. This eliminates the need for node_modules in the distributed action.

### Usage Context
This action can be used:
1. Directly in GitHub workflows
2. Indirectly through [github-workflows](https://github.com/monta-app/github-workflows) - Shared workflow templates