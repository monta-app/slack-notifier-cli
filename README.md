### About

A TypeScript GitHub Action for sending Slack notifications from GitHub workflows with rich formatting.

### Usage

You can use this action directly in your GitHub workflows:

```yaml
- name: Send Slack Notification
  uses: monta-app/slack-notifier-cli@v1
  with:
    job-type: 'build'
    job-status: 'success'
    slack-token: ${{ secrets.SLACK_BOT_TOKEN }}
    slack-channel-id: 'C1234567890'
    service-name: 'My Service'
    service-emoji: ':rocket:'
```

### Inputs

| Input | Description | Required | Default |
|-------|-------------|----------|---------|
| `github-event-path` | GitHub event JSON file path | No | `${{ github.event_path }}` |
| `github-repository` | GitHub repository name | No | `${{ github.repository }}` |
| `github-run-id` | GitHub run ID | No | `${{ github.run_id }}` |
| `github-workflow` | GitHub workflow name | No | `${{ github.workflow }}` |
| `github-ref-name` | GitHub ref name | No | `${{ github.ref_name }}` |
| `service-name` | Service name for display | No | |
| `service-emoji` | Service emoji | No | |
| `job-type` | Job type (build, test, deploy, etc.) | Yes | |
| `job-status` | Job status (success, failure, progress, etc.) | Yes | |
| `slack-token` | Slack Bot Token | Yes | |
| `slack-channel-id` | Slack channel ID | Yes | |
| `slack-message-id` | Message ID to update (for updates) | No | |

### Outputs

| Output | Description |
|--------|-------------|
| `slack-message-id` | ID of the created/updated Slack message |

### Development

```bash
# Install dependencies
npm install

# Build the action
npm run build

# Run tests
npm test

# Lint code
npm run lint

# Format code
npm run format
```

Most projects don't use this directly, but indirectly through the [github-workflows](https://github.com/monta-app/github-workflows)
