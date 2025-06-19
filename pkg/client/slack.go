package client

import (
	"fmt"

	"github.com/go-resty/resty/v2"
	"slack-notifier-cli/pkg/models"
)

// SlackClient handles Slack API interactions
type SlackClient struct {
	client *resty.Client
	token  string
}

// NewSlackClient creates a new Slack API client
func NewSlackClient(token string) *SlackClient {
	client := resty.New()
	client.SetHeader("Authorization", "Bearer "+token)
	client.SetHeader("Content-Type", "application/json")

	return &SlackClient{
		client: client,
		token:  token,
	}
}

// PostMessage sends a new message to a Slack channel
func (sc *SlackClient) PostMessage(message *models.SlackMessage) (*models.SlackAPIResponse, error) {
	var response models.SlackAPIResponse

	resp, err := sc.client.R().
		SetBody(message).
		SetResult(&response).
		Post("https://slack.com/api/chat.postMessage")

	if err != nil {
		return nil, fmt.Errorf("failed to post message: %w", err)
	}

	if !response.OK {
		return nil, fmt.Errorf("Slack API error: %s", response.Error)
	}

	if resp.StatusCode() >= 400 {
		return nil, fmt.Errorf("HTTP error %d", resp.StatusCode())
	}

	return &response, nil
}

// UpdateMessage updates an existing Slack message
func (sc *SlackClient) UpdateMessage(channelID, timestamp string, message *models.SlackMessage) (*models.SlackAPIResponse, error) {
	var response models.SlackAPIResponse

	// Create update payload
	updatePayload := map[string]interface{}{
		"channel":     channelID,
		"ts":          timestamp,
		"text":        message.Text,
		"blocks":      message.Blocks,
		"attachments": message.Attachments,
	}

	resp, err := sc.client.R().
		SetBody(updatePayload).
		SetResult(&response).
		Post("https://slack.com/api/chat.update")

	if err != nil {
		return nil, fmt.Errorf("failed to update message: %w", err)
	}

	if !response.OK {
		return nil, fmt.Errorf("Slack API error: %s", response.Error)
	}

	if resp.StatusCode() >= 400 {
		return nil, fmt.Errorf("HTTP error %d", resp.StatusCode())
	}

	return &response, nil
}

// GetMessageHistory retrieves message history from a channel
func (sc *SlackClient) GetMessageHistory(channelID, timestamp string) (*models.SlackConversationHistoryResponse, error) {
	var response models.SlackConversationHistoryResponse

	params := map[string]string{
		"channel":   channelID,
		"latest":    timestamp,
		"inclusive": "true",
		"limit":     "1",
	}

	resp, err := sc.client.R().
		SetQueryParams(params).
		SetResult(&response).
		Get("https://slack.com/api/conversations.history")

	if err != nil {
		return nil, fmt.Errorf("failed to get message history: %w", err)
	}

	if !response.OK {
		return nil, fmt.Errorf("Slack API error: %s", response.Error)
	}

	if resp.StatusCode() >= 400 {
		return nil, fmt.Errorf("HTTP error %d", resp.StatusCode())
	}

	return &response, nil
}
