package com.monta.slack.notifier

import com.monta.slack.notifier.model.GithubEvent
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.model.SlackBlock
import com.monta.slack.notifier.model.SlackMessage
import com.monta.slack.notifier.util.JsonUtil
import com.monta.slack.notifier.util.buildTitle
import com.monta.slack.notifier.util.client
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SlackClient(
    private val serviceName: String?,
    private val serviceEmoji: String?,
    private val slackToken: String,
    private val slackChannelId: String,
) {

    suspend fun create(
        githubEvent: GithubEvent,
        jobType: JobType,
        jobStatus: JobStatus,
    ): String {
        val response = makeSlackRequest(
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
        val previousMessage = getSlackMessageById(messageId)

        val response = makeSlackRequest(
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
                            text = " \n*Comitter:*\n${githubEvent.displayName}"
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

        return generateSlackMessageFromEvent(
            githubEvent = githubEvent,
            serviceName = serviceName,
            serviceEmoji = serviceEmoji,
            slackChannelId = slackChannelId,
            messageId = messageId,
            attachments = attachments.values.toList()
        )
    }

    private suspend fun getSlackMessageById(
        messageId: String,
    ): MessageResponse? {
        val response = client.get {
            header("Authorization", "Bearer $slackToken")
            url {
                url("https://slack.com/api/conversations.history")
                parameters.append("channel", slackChannelId)
                parameters.append("oldest", messageId)
                parameters.append("inclusive", "true")
                parameters.append("limit", "1")
            }
        }

        val bodyString = response.bodyAsText()

        return if (response.status.value in 200..299) {
            println("successfully got message bodyString=$bodyString")
            JsonUtil.instance.decodeFromString(bodyString)
        } else {
            println("failed to get message $bodyString")
            null
        }
    }

    private suspend fun makeSlackRequest(url: String, message: SlackMessage): Response? {
        val response = client.post(url) {
            header("Authorization", "Bearer $slackToken")
            contentType(ContentType.Application.Json.withParameter("charset", Charsets.UTF_8.name))
            setBody(message)
        }

        val bodyString = response.bodyAsText()

        return if (response.status.value in 200..299) {
            println("successfully posted message bodyString=$bodyString")
            JsonUtil.instance.decodeFromString(bodyString)
        } else {
            println("failed to post message $bodyString")
            null
        }
    }

    @Serializable
    private data class Response(
        @SerialName("ok")
        val ok: Boolean, // true
        @SerialName("channel")
        val channel: String, // C024BE91L
        @SerialName("ts")
        val ts: String, // 1401383885.000061
    )

    @Serializable
    private data class MessageResponse(
        @SerialName("ok")
        val ok: Boolean, // true
        @SerialName("messages")
        val messages: List<SlackMessage>,
    )
}
