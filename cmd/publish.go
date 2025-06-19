package cmd

import (
	"fmt"
	"io/ioutil"
	"log"

	"github.com/urfave/cli/v2"
	"slack-notifier-cli/pkg/models"
	"slack-notifier-cli/pkg/service"
	"slack-notifier-cli/pkg/utils"
)

// PublishCommand creates the publish command for the CLI
func PublishCommand() *cli.Command {
	return &cli.Command{
		Name:  "publish",
		Usage: "Publish a Slack notification for GitHub Actions",
		Flags: []cli.Flag{
			// GitHub environment variables (required)
			&cli.StringFlag{
				Name:     "github-event-path",
				Usage:    "Path to GitHub event JSON file",
				EnvVars:  []string{"GITHUB_EVENT_PATH"},
				Required: true,
			},
			&cli.StringFlag{
				Name:     "github-repository",
				Usage:    "GitHub repository (e.g., owner/repo)",
				EnvVars:  []string{"GITHUB_REPOSITORY"},
				Required: true,
			},
			&cli.StringFlag{
				Name:     "github-run-id",
				Usage:    "GitHub Actions run ID",
				EnvVars:  []string{"GITHUB_RUN_ID"},
				Required: true,
			},
			&cli.StringFlag{
				Name:     "github-workflow",
				Usage:    "GitHub workflow name",
				EnvVars:  []string{"GITHUB_WORKFLOW"},
				Required: true,
			},
			&cli.StringFlag{
				Name:     "github-ref-name",
				Usage:    "GitHub ref name (branch/tag)",
				EnvVars:  []string{"GITHUB_REF_NAME"},
				Required: true,
			},

			// Service configuration (optional)
			&cli.StringFlag{
				Name:    "service-name",
				Usage:   "Service name for the notification",
				EnvVars: []string{"PUBLISH_SLACK_SERVICE_NAME"},
				Value:   "GitHub Actions",
			},
			&cli.StringFlag{
				Name:    "service-emoji",
				Usage:   "Service emoji for the notification",
				EnvVars: []string{"PUBLISH_SLACK_SERVICE_EMOJI"},
				Value:   "⚡",
			},

			// Job details (required)
			&cli.StringFlag{
				Name:     "job-type",
				Usage:    "Job type (Test, Build, Deploy, PublishDocs)",
				EnvVars:  []string{"PUBLISH_SLACK_JOB_TYPE"},
				Required: true,
			},
			&cli.StringFlag{
				Name:     "job-status",
				Usage:    "Job status (Progress, Success, Failure, Cancelled, Unknown)",
				EnvVars:  []string{"PUBLISH_SLACK_JOB_STATUS"},
				Required: true,
			},

			// Slack configuration (required)
			&cli.StringFlag{
				Name:     "slack-token",
				Usage:    "Slack app token",
				EnvVars:  []string{"SLACK_APP_TOKEN"},
				Required: true,
			},
			&cli.StringFlag{
				Name:     "slack-channel-id",
				Usage:    "Slack channel ID",
				EnvVars:  []string{"SLACK_CHANNEL_ID"},
				Required: true,
			},
			&cli.StringFlag{
				Name:    "slack-message-id",
				Usage:   "Slack message ID for updates (optional)",
				EnvVars: []string{"SLACK_MESSAGE_ID"},
			},
		},
		Action: publishAction,
	}
}

// publishAction handles the publish command execution
func publishAction(c *cli.Context) error {
	// Validate job type
	jobType := models.JobType(c.String("job-type"))
	if !isValidJobType(jobType) {
		return fmt.Errorf("invalid job type: %s. Valid types: Test, Build, Deploy, PublishDocs", jobType)
	}

	// Validate job status
	jobStatus := models.JobStatus(c.String("job-status"))
	if !isValidJobStatus(jobStatus) {
		return fmt.Errorf("invalid job status: %s. Valid statuses: Progress, Success, Failure, Cancelled, Unknown", jobStatus)
	}

	// Read GitHub event file
	eventPath := c.String("github-event-path")
	eventData, err := ioutil.ReadFile(eventPath)
	if err != nil {
		return fmt.Errorf("failed to read GitHub event file: %w", err)
	}

	// Parse GitHub event
	githubEvent, err := utils.ParseGitHubEvent(
		eventData,
		c.String("github-repository"),
		c.String("github-ref-name"),
		c.String("github-run-id"),
		c.String("github-workflow"),
	)
	if err != nil {
		return fmt.Errorf("failed to parse GitHub event: %w", err)
	}

	// Create publish service
	publishService := service.NewPublishSlackService(c.String("slack-token"))

	// Prepare message ID for updates
	var messageID *string
	if slackMsgID := c.String("slack-message-id"); slackMsgID != "" {
		messageID = &slackMsgID
	}

	// Configure publication
	config := &service.PublishConfig{
		ChannelID:    c.String("slack-channel-id"),
		MessageID:    messageID,
		ServiceName:  c.String("service-name"),
		ServiceEmoji: c.String("service-emoji"),
		JobType:      jobType,
		JobStatus:    jobStatus,
		GitHubEvent:  githubEvent,
	}

	// Publish message
	timestamp, err := publishService.Publish(config)
	if err != nil {
		return fmt.Errorf("failed to publish Slack message: %w", err)
	}

	// Output message ID for GitHub Actions
	if err := utils.SetGitHubOutput("SLACK_MESSAGE_ID", timestamp); err != nil {
		log.Printf("Warning: failed to set GitHub output: %v", err)
	}

	fmt.Printf("Successfully published Slack message: %s\n", timestamp)
	return nil
}

// isValidJobType checks if the job type is valid
func isValidJobType(jobType models.JobType) bool {
	switch jobType {
	case models.JobTypeTest, models.JobTypeBuild, models.JobTypeDeploy, models.JobTypePublishDocs:
		return true
	default:
		return false
	}
}

// isValidJobStatus checks if the job status is valid
func isValidJobStatus(jobStatus models.JobStatus) bool {
	switch jobStatus {
	case models.JobStatusProgress, models.JobStatusSuccess, models.JobStatusFailure, models.JobStatusCancelled, models.JobStatusUnknown:
		return true
	default:
		return false
	}
}