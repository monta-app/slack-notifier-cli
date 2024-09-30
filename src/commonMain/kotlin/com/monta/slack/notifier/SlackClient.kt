package com.monta.slack.notifier

import com.monta.slack.notifier.model.GithubEvent
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.model.SlackBlock
import com.monta.slack.notifier.model.SlackMessage
import com.monta.slack.notifier.util.buildTitle
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SlackClient(
    private val serviceName: String?,
    private val serviceEmoji: String?,
    private val slackChannelId: String,
    private val appendAttachments: Boolean,
    private val slackHttpClient: SlackHttpClient,
) {
    suspend fun create(
        githubEvent: GithubEvent,
        jobType: JobType,
        jobStatus: JobStatus,
    ): String {
        val response = slackHttpClient.makeSlackRequest(
            url = "https://slack.com/api/chat.postMessage",
            message = generateMessageFromGithubEvent(
                githubEvent = githubEvent,
                jobType = jobType,
                jobStatus = jobStatus
            )
        )

        return requireNotNull(response?.ts)
    }

    suspend fun update(
        messageId: String,
        githubEvent: GithubEvent,
        jobType: JobType,
        jobStatus: JobStatus,
    ): String {
        val previousMessage = slackHttpClient.getSlackMessageById(messageId)

        val response = slackHttpClient.makeSlackRequest(
            url = "https://slack.com/api/chat.update",
            message = generateMessageFromGithubEvent(
                githubEvent = githubEvent,
                jobType = jobType,
                jobStatus = jobStatus,
                messageId = messageId,
                previousAttachments = previousMessage?.messages?.firstOrNull()?.attachments
            )
        )

        return requireNotNull(response?.ts)
    }

    private fun generateSlackMessageFromEvent(
        githubEvent: GithubEvent,
        serviceName: String?,
        serviceEmoji: String?,
        slackChannelId: String,
        messageId: String?,
        attachments: List<SlackMessage.Attachment>?,
    ): SlackMessage {
        val title = buildTitle(githubEvent.repository, githubEvent.workflow, serviceName, serviceEmoji)

        return SlackMessage(
            channel = slackChannelId,
            ts = messageId,
            text = title,
            blocks = listOf(
                SlackBlock(
                    type = "header",
                    text = SlackBlock.Text(
                        type = "plain_text",
                        text = title
                    )
                ),
                SlackBlock(
                    type = "divider"
                ),
                SlackBlock(
                    type = "section",
                    fields = listOf(
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Branch:*\n${githubEvent.refName}"
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Run:*\n<${githubEvent.getRunUrl()}|${githubEvent.runId}>"
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Committer:*\n${githubEvent.displayName}"
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Message:*\n<${githubEvent.getChangeUrl()}|${githubEvent.getChangeMessage()}>"
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Change:*\n<${githubEvent.getChangeUrl()}|${githubEvent.getChangeIdentifier()}>"
                        )
                    )
                ),
                SlackBlock(
                    type = "divider"
                )
            ),
            attachments = attachments
        )
    }

    private fun generateMessageFromGithubEvent(
        githubEvent: GithubEvent,
        jobType: JobType,
        jobStatus: JobStatus,
        messageId: String? = null,
        previousAttachments: List<SlackMessage.Attachment>? = null,
    ): SlackMessage {
        val attachments = if (appendAttachments) {
            previousAttachments.orEmpty() + SlackMessage.Attachment(
                color = jobStatus.color,
                fields = listOf(
                    SlackMessage.Attachment.Field(
                        title = jobType.label + " ($LocalDateTime)",
                        short = false,
                        value = jobStatus.message
                    )
                )
            )
        } else {
            val attachments = mutableMapOf<JobType, SlackMessage.Attachment>()

            previousAttachments?.forEach { previousAttachment ->
                if (previousAttachment.jobType == null) {
                    return@forEach
                }
                attachments[previousAttachment.jobType] = previousAttachment
            }

            attachments[jobType] = SlackMessage.Attachment(
                color = jobStatus.color,
                fields = listOf(
                    SlackMessage.Attachment.Field(
                        title = jobType.label,
                        short = false,
                        value = jobStatus.message
                    )
                )
            )

            attachments.values.toList()
        }

        return generateSlackMessageFromEvent(
            githubEvent = githubEvent,
            serviceName = serviceName,
            serviceEmoji = serviceEmoji,
            slackChannelId = slackChannelId,
            messageId = messageId,
            attachments = attachments
        )
    }


    @Serializable
    data class Response(
        @SerialName("ok")
        val ok: Boolean, // true
        @SerialName("channel")
        val channel: String, // C024BE91L
        @SerialName("ts")
        val ts: String, // 1401383885.000061
    )

    @Serializable
    data class MessageResponse(
        @SerialName("ok")
        val ok: Boolean, // true
        @SerialName("messages")
        val messages: List<SlackMessage>,
    )
}
