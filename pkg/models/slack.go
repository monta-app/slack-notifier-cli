package models

// SlackMessage represents a complete Slack message structure
type SlackMessage struct {
	Channel     string            `json:"channel"`
	Text        string            `json:"text"`
	Blocks      []SlackBlock      `json:"blocks,omitempty"`
	Attachments []SlackAttachment `json:"attachments,omitempty"`
	Timestamp   string            `json:"ts,omitempty"`
}

// SlackBlock represents individual message blocks
type SlackBlock struct {
	Type      string                 `json:"type"`
	Text      *SlackText             `json:"text,omitempty"`
	Fields    []SlackField           `json:"fields,omitempty"`
	Accessory map[string]interface{} `json:"accessory,omitempty"`
}

// SlackText represents text content in blocks
type SlackText struct {
	Type string `json:"type"`
	Text string `json:"text"`
}

// SlackField represents a field in a section block
type SlackField struct {
	Type string `json:"type"`
	Text string `json:"text"`
}

// SlackAttachment represents a message attachment (for colored status)
type SlackAttachment struct {
	Color  string       `json:"color"`
	Text   string       `json:"text"`
	Blocks []SlackBlock `json:"blocks,omitempty"`
}

// SlackAPIResponse represents common Slack API response structure
type SlackAPIResponse struct {
	OK        bool   `json:"ok"`
	Error     string `json:"error,omitempty"`
	Timestamp string `json:"ts,omitempty"`
	Message   *struct {
		Text        string            `json:"text"`
		Timestamp   string            `json:"ts"`
		Attachments []SlackAttachment `json:"attachments"`
	} `json:"message,omitempty"`
}

// SlackConversationHistoryResponse represents the conversations.history API response
type SlackConversationHistoryResponse struct {
	OK       bool `json:"ok"`
	Messages []struct {
		Text        string            `json:"text"`
		Timestamp   string            `json:"ts"`
		Attachments []SlackAttachment `json:"attachments"`
	} `json:"messages"`
	Error string `json:"error,omitempty"`
}

// NewHeaderBlock creates a new header block
func NewHeaderBlock(text string) SlackBlock {
	return SlackBlock{
		Type: "header",
		Text: &SlackText{
			Type: "plain_text",
			Text: text,
		},
	}
}

// NewDividerBlock creates a new divider block
func NewDividerBlock() SlackBlock {
	return SlackBlock{
		Type: "divider",
	}
}

// NewSectionBlock creates a new section block with fields
func NewSectionBlock(fields []SlackField) SlackBlock {
	return SlackBlock{
		Type:   "section",
		Fields: fields,
	}
}

// NewMarkdownField creates a new markdown field
func NewMarkdownField(text string) SlackField {
	return SlackField{
		Type: "mrkdwn",
		Text: text,
	}
}
