package service

import (
	"fmt"
	"strings"

	"slack-notifier-cli/pkg/client"
	"slack-notifier-cli/pkg/models"
	"slack-notifier-cli/pkg/utils"
)

// PublishSlackService orchestrates Slack message publishing
type PublishSlackService struct {
	slackClient *client.SlackClient
}

// NewPublishSlackService creates a new publish service
func NewPublishSlackService(slackToken string) *PublishSlackService {
	return &PublishSlackService{
		slackClient: client.NewSlackClient(slackToken),
	}
}

// PublishConfig contains configuration for publishing
type PublishConfig struct {
	ChannelID    string
	MessageID    *string // Optional - for updates
	ServiceName  string
	ServiceEmoji string
	JobType      models.JobType
	JobStatus    models.JobStatus
	GitHubEvent  *models.GitHubEvent
}

// Publish creates a new message or updates an existing one
func (ps *PublishSlackService) Publish(config *PublishConfig) (string, error) {
	message := ps.buildSlackMessage(config)

	if config.MessageID != nil && *config.MessageID != "" {
		// Update existing message
		return ps.updateMessage(config.ChannelID, *config.MessageID, message, config)
	}

	// Create new message
	return ps.createMessage(message)
}

// createMessage posts a new message to Slack
func (ps *PublishSlackService) createMessage(message *models.SlackMessage) (string, error) {
	response, err := ps.slackClient.PostMessage(message)
	if err != nil {
		return "", fmt.Errorf("failed to create message: %w", err)
	}

	return response.Timestamp, nil
}

// updateMessage updates an existing Slack message, preserving previous job statuses
func (ps *PublishSlackService) updateMessage(channelID, messageID string, newMessage *models.SlackMessage, config *PublishConfig) (string, error) {
	// First, get the existing message to preserve previous attachments
	historyResponse, err := ps.slackClient.GetMessageHistory(channelID, messageID)
	if err != nil {
		return "", fmt.Errorf("failed to get message history: %w", err)
	}

	if len(historyResponse.Messages) == 0 {
		return "", fmt.Errorf("message not found")
	}

	existingMessage := historyResponse.Messages[0]

	// Merge existing attachments with new one
	existingAttachments := existingMessage.Attachments
	newAttachment := ps.buildJobStatusAttachment(config.JobType, config.JobStatus)

	// Check if we already have an attachment for this job type
	attachmentExists := false
	for i, attachment := range existingAttachments {
		if strings.Contains(attachment.Text, config.JobType.DisplayName()) {
			existingAttachments[i] = newAttachment
			attachmentExists = true
			break
		}
	}

	if !attachmentExists {
		existingAttachments = append(existingAttachments, newAttachment)
	}

	newMessage.Attachments = existingAttachments

	// Update the message
	response, err := ps.slackClient.UpdateMessage(channelID, messageID, newMessage)
	if err != nil {
		return "", fmt.Errorf("failed to update message: %w", err)
	}

	return response.Timestamp, nil
}

// buildSlackMessage constructs the complete Slack message
func (ps *PublishSlackService) buildSlackMessage(config *PublishConfig) *models.SlackMessage {
	blocks := []models.SlackBlock{}

	// Header block with service name and emoji
	headerText := fmt.Sprintf("%s %s", config.ServiceEmoji, config.ServiceName)
	blocks = append(blocks, models.NewHeaderBlock(headerText))

	// Divider
	blocks = append(blocks, models.NewDividerBlock())

	// Main content section
	fields := ps.buildMessageFields(config.GitHubEvent)
	if len(fields) > 0 {
		blocks = append(blocks, models.NewSectionBlock(fields))
	}

	message := &models.SlackMessage{
		Channel:     config.ChannelID,
		Text:        fmt.Sprintf("%s - %s", config.ServiceName, config.JobType.DisplayName()),
		Blocks:      blocks,
		Attachments: []models.SlackAttachment{ps.buildJobStatusAttachment(config.JobType, config.JobStatus)},
	}

	return message
}

// buildMessageFields creates fields for the message section
func (ps *PublishSlackService) buildMessageFields(event *models.GitHubEvent) []models.SlackField {
	fields := []models.SlackField{}

	// Repository
	if event.Repository != "" {
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Repository:*\n%s", event.Repository)))
	}

	// Branch/Ref
	if event.RefName != "" {
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Branch:*\n%s", event.RefName)))
	}

	// Author/User
	if event.DisplayName != nil && *event.DisplayName != "" {
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Author:*\n%s", *event.DisplayName)))
	}

	// Workflow
	if event.Workflow != nil && *event.Workflow != "" {
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Workflow:*\n%s", *event.Workflow)))
	}

	// Commit message or PR title
	if event.Message != nil && *event.Message != "" {
		sanitizedMessage := utils.SanitizeMessage(*event.Message)
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Message:*\n%s", sanitizedMessage)))
	}

	// Commit SHA
	if event.CommitSHA != nil && *event.CommitSHA != "" {
		shortSHA := *event.CommitSHA
		if len(shortSHA) > 7 {
			shortSHA = shortSHA[:7]
		}
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Commit:*\n`%s`", shortSHA)))
	}

	// PR URL
	if event.PRURL != nil && *event.PRURL != "" {
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*PR/Issue:*\n<%s|View>", *event.PRURL)))
	}

	// Run ID (GitHub Actions link)
	if event.RunID != "" && event.Repository != "" {
		runURL := fmt.Sprintf("https://github.com/%s/actions/runs/%s", event.Repository, event.RunID)
		fields = append(fields, models.NewMarkdownField(fmt.Sprintf("*Run:*\n<%s|%s>", runURL, event.RunID)))
	}

	return fields
}

// buildJobStatusAttachment creates a colored attachment for the job status
func (ps *PublishSlackService) buildJobStatusAttachment(jobType models.JobType, jobStatus models.JobStatus) models.SlackAttachment {
	text := fmt.Sprintf("%s: %s", jobType.DisplayName(), jobStatus.DisplayName())

	return models.SlackAttachment{
		Color: jobStatus.Color(),
		Text:  text,
	}
}
