package com.monta.slack.notifier.model

interface Messageable {
    fun toMessage(
        slackChannelId: String,
        messageId: String?,
        attachments: List<SlackMessage.Attachment>?,
    ): SlackMessage
}