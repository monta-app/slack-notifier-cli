package utils

import (
	"encoding/json"
	"fmt"
	"strings"

	"slack-notifier-cli/pkg/models"
)

// ParseGitHubEvent parses various GitHub webhook payloads into a normalized GitHubEvent
func ParseGitHubEvent(eventData []byte, repository, refName, runID, workflow string) (*models.GitHubEvent, error) {
	// Try to determine event type by checking for key fields
	var raw map[string]interface{}
	if err := json.Unmarshal(eventData, &raw); err != nil {
		return nil, fmt.Errorf("failed to parse JSON: %w", err)
	}

	event := &models.GitHubEvent{
		Repository: repository,
		RefName:    refName,
		RunID:      runID,
		Workflow:   &workflow,
	}

	// Check for push event
	if _, hasPusher := raw["pusher"]; hasPusher {
		return parsePushEvent(eventData, event)
	}

	// Check for pull request event
	if pr, hasPR := raw["pull_request"]; hasPR && pr != nil {
		return parsePullRequestEvent(eventData, event)
	}

	// Check for issue event
	if issue, hasIssue := raw["issue"]; hasIssue && issue != nil {
		return parseIssueEvent(eventData, event)
	}

	// Unknown event type - return basic event
	return event, nil
}

// parsePushEvent handles GitHub push webhook payloads
func parsePushEvent(eventData []byte, event *models.GitHubEvent) (*models.GitHubEvent, error) {
	var payload models.GitHubPushPayload
	if err := json.Unmarshal(eventData, &payload); err != nil {
		return nil, fmt.Errorf("failed to parse push payload: %w", err)
	}

	// Extract ref name (remove refs/heads/ prefix)
	if strings.HasPrefix(payload.Ref, "refs/heads/") {
		event.RefName = strings.TrimPrefix(payload.Ref, "refs/heads/")
	}

	// Extract commit information
	if payload.HeadCommit != nil {
		event.CommitSHA = &payload.HeadCommit.ID
		event.Message = &payload.HeadCommit.Message

		// Prefer committer over author, fallback to pusher
		displayName := payload.HeadCommit.Committer.Name
		if displayName == "" {
			displayName = payload.HeadCommit.Author.Name
		}
		if displayName == "" {
			displayName = payload.Pusher.Name
		}
		if displayName != "" {
			event.DisplayName = &displayName
		}
	}

	return event, nil
}

// parsePullRequestEvent handles GitHub pull request webhook payloads
func parsePullRequestEvent(eventData []byte, event *models.GitHubEvent) (*models.GitHubEvent, error) {
	var payload models.GitHubPullRequestPayload
	if err := json.Unmarshal(eventData, &payload); err != nil {
		return nil, fmt.Errorf("failed to parse pull request payload: %w", err)
	}

	// Extract PR information
	event.Message = &payload.PullRequest.Title
	event.CommitSHA = &payload.PullRequest.Head.SHA
	event.PRURL = &payload.PullRequest.URL
	event.DisplayName = &payload.PullRequest.User.Login

	return event, nil
}

// parseIssueEvent handles GitHub issue webhook payloads
func parseIssueEvent(eventData []byte, event *models.GitHubEvent) (*models.GitHubEvent, error) {
	var payload models.GitHubIssuePayload
	if err := json.Unmarshal(eventData, &payload); err != nil {
		return nil, fmt.Errorf("failed to parse issue payload: %w", err)
	}

	// Extract issue information
	event.Message = &payload.Issue.Title
	event.PRURL = &payload.Issue.URL // Reuse PRURL field for issue URL
	event.DisplayName = &payload.Issue.User.Login

	return event, nil
}

// SanitizeMessage sanitizes GitHub commit messages for Slack
func SanitizeMessage(message string) string {
	if message == "" {
		return message
	}

	// Remove angle brackets that can interfere with Slack formatting
	sanitized := strings.ReplaceAll(message, "<", "")
	sanitized = strings.ReplaceAll(sanitized, ">", "")

	// Limit length to 120 characters
	if len(sanitized) > 120 {
		sanitized = sanitized[:117] + "..."
	}

	return sanitized
}