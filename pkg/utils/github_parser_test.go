package utils

import (
	"testing"

	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
)

func TestSanitizeMessage(t *testing.T) {
	tests := []struct {
		name     string
		input    string
		expected string
	}{
		{
			name:     "empty string",
			input:    "",
			expected: "",
		},
		{
			name:     "normal message",
			input:    "fix: update dependencies",
			expected: "fix: update dependencies",
		},
		{
			name:     "message with angle brackets",
			input:    "fix: update <dependency> version",
			expected: "fix: update dependency version",
		},
		{
			name:     "long message gets truncated",
			input:    "This is a very long commit message that exceeds the 120 character limit and should be truncated with ellipsis at the end!",
			expected: "This is a very long commit message that exceeds the 120 character limit and should be truncated with ellipsis at the ...",
		},
		{
			name:     "exactly 120 characters",
			input:    "This commit message is exactly one hundred and twenty characters long and should not be truncated at all here",
			expected: "This commit message is exactly one hundred and twenty characters long and should not be truncated at all here",
		},
	}

	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			result := SanitizeMessage(tt.input)
			assert.Equal(t, tt.expected, result)
		})
	}
}

func TestParseGitHubEvent_PushEvent(t *testing.T) {
	pushPayload := `{
		"pusher": {
			"name": "testuser",
			"email": "test@example.com"
		},
		"ref": "refs/heads/main",
		"head_commit": {
			"id": "abc123",
			"message": "fix: update dependencies",
			"author": {
				"name": "Test Author",
				"email": "author@example.com"
			},
			"committer": {
				"name": "Test Committer",
				"email": "committer@example.com"
			}
		}
	}`

	event, err := ParseGitHubEvent(
		[]byte(pushPayload),
		"owner/repo",
		"main",
		"12345",
		"CI",
	)

	require.NoError(t, err)
	assert.Equal(t, "owner/repo", event.Repository)
	assert.Equal(t, "main", event.RefName)
	assert.Equal(t, "12345", event.RunID)
	assert.Equal(t, "CI", *event.Workflow)
	assert.Equal(t, "abc123", *event.CommitSHA)
	assert.Equal(t, "fix: update dependencies", *event.Message)
	assert.Equal(t, "Test Committer", *event.DisplayName)
}

func TestParseGitHubEvent_PullRequestEvent(t *testing.T) {
	prPayload := `{
		"action": "opened",
		"pull_request": {
			"title": "Add new feature",
			"html_url": "https://github.com/owner/repo/pull/1",
			"head": {
				"sha": "def456"
			},
			"user": {
				"login": "contributor"
			}
		},
		"repository": {
			"full_name": "owner/repo"
		}
	}`

	event, err := ParseGitHubEvent(
		[]byte(prPayload),
		"owner/repo",
		"feature-branch",
		"67890",
		"PR Check",
	)

	require.NoError(t, err)
	assert.Equal(t, "owner/repo", event.Repository)
	assert.Equal(t, "feature-branch", event.RefName)
	assert.Equal(t, "67890", event.RunID)
	assert.Equal(t, "PR Check", *event.Workflow)
	assert.Equal(t, "def456", *event.CommitSHA)
	assert.Equal(t, "Add new feature", *event.Message)
	assert.Equal(t, "contributor", *event.DisplayName)
	assert.Equal(t, "https://github.com/owner/repo/pull/1", *event.PRURL)
}

func TestParseGitHubEvent_UnknownEvent(t *testing.T) {
	unknownPayload := `{
		"some_field": "some_value"
	}`

	event, err := ParseGitHubEvent(
		[]byte(unknownPayload),
		"owner/repo",
		"main",
		"12345",
		"CI",
	)

	require.NoError(t, err)
	assert.Equal(t, "owner/repo", event.Repository)
	assert.Equal(t, "main", event.RefName)
	assert.Equal(t, "12345", event.RunID)
	assert.Equal(t, "CI", *event.Workflow)
	assert.Nil(t, event.CommitSHA)
	assert.Nil(t, event.Message)
	assert.Nil(t, event.DisplayName)
}
