package models

// GitHubEvent represents normalized GitHub event data from various webhook payloads
type GitHubEvent struct {
	Repository  string  `json:"repository"`   // e.g., "monta-app/slack-notifier-cli"
	RefName     string  `json:"ref_name"`     // e.g., "main", "develop"
	RunID       string  `json:"run_id"`       // GitHub Actions run ID
	DisplayName *string `json:"display_name"` // Committer display name
	CommitSHA   *string `json:"commit_sha"`   // Git commit SHA
	Message     *string `json:"message"`      // Commit message or PR title
	Workflow    *string `json:"workflow"`     // GitHub workflow name
	PRURL       *string `json:"pr_url"`       // Pull request URL
}

// GitHubPushPayload represents a GitHub push webhook payload
type GitHubPushPayload struct {
	Repository struct {
		FullName string `json:"full_name"`
	} `json:"repository"`
	Ref        string `json:"ref"`
	HeadCommit *struct {
		ID      string `json:"id"`
		Message string `json:"message"`
		Author  struct {
			Name  string `json:"name"`
			Email string `json:"email"`
		} `json:"author"`
		Committer struct {
			Name  string `json:"name"`
			Email string `json:"email"`
		} `json:"committer"`
	} `json:"head_commit"`
	Pusher struct {
		Name  string `json:"name"`
		Email string `json:"email"`
	} `json:"pusher"`
}

// GitHubPullRequestPayload represents a GitHub pull request webhook payload
type GitHubPullRequestPayload struct {
	Action      string `json:"action"`
	PullRequest struct {
		Title string `json:"title"`
		URL   string `json:"html_url"`
		Head  struct {
			SHA string `json:"sha"`
		} `json:"head"`
		User struct {
			Login string `json:"login"`
		} `json:"user"`
	} `json:"pull_request"`
	Repository struct {
		FullName string `json:"full_name"`
	} `json:"repository"`
}

// GitHubIssuePayload represents a GitHub issue webhook payload
type GitHubIssuePayload struct {
	Action string `json:"action"`
	Issue  struct {
		Title string `json:"title"`
		URL   string `json:"html_url"`
		User  struct {
			Login string `json:"login"`
		} `json:"user"`
	} `json:"issue"`
	Repository struct {
		FullName string `json:"full_name"`
	} `json:"repository"`
}