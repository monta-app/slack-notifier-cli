package com.monta.slack.notifier.model

interface Messageable {
    fun toMessage(
        serviceName: String?,
        serviceEmoji: String?,
        slackChannelId: String,
        messageId: String?,
        attachments: List<SlackMessage.Attachment>?,
    ): SlackMessage
}