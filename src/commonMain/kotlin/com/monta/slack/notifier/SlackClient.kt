package com.monta.slack.notifier

import com.monta.slack.notifier.model.GithubContext
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.util.JsonUtil
import com.monta.slack.notifier.util.client
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString

class SlackClient(
    private val slackToken: String,
    private val slackChannelId: String,
) {

    suspend fun create(
        githubContext: GithubContext,
        jobType: JobType,
        jobStatus: JobStatus,
    ): String {

        val response = makeSlackRequest(
            url = "https://slack.com/api/chat.postMessage",
            message = generateMessage(
                githubContext = githubContext,
                jobType = jobType,
                jobStatus = jobStatus,
            )
        )

        return requireNotNull(response?.ts)
    }

    suspend fun update(
        messageId: String,
        githubContext: GithubContext,
        jobType: JobType,
        jobStatus: JobStatus,
    ): String {

        val previousMessage = getSlackMessageById(messageId)

        val response = makeSlackRequest(
            url = "https://slack.com/api/chat.update",
            message = generateMessage(
                githubContext = githubContext,
                jobType = jobType,
                jobStatus = jobStatus,
                messageId = messageId,
                previousAttachments = previousMessage?.messages?.firstOrNull()?.attachments,
            )
        )

        return requireNotNull(response?.ts)
    }

    private fun generateMessage(
        githubContext: GithubContext,
        jobType: JobType,
        jobStatus: JobStatus,
        messageId: String? = null,
        previousAttachments: List<MessageAttachment>? = null,
    ): Message {

        val attachments = mutableMapOf<JobType, MessageAttachment>()

        previousAttachments?.forEach { previousAttachment ->
            if (previousAttachment.jobType == null) {
                return@forEach
            }
            attachments[previousAttachment.jobType] = previousAttachment
        }

        attachments[jobType] = MessageAttachment(
            color = jobStatus.color,
            fields = listOf(
                MessageAttachment.Field(
                    title = jobType.label,
                    short = false,
                    value = jobStatus.message
                )
            )
        )

        val commitEvent = githubContext.event?.commits?.firstOrNull()

        return Message(
            channel = slackChannelId,
            ts = messageId,
            blocks = listOf(
                SlackBlock(
                    type = "header",
                    text = Text(
                        type = "plain_text",
                        text = "${githubContext.workflow}",
                    )
                ),
                SlackBlock(
                    type = "divider"
                ),
                SlackBlock(
                    type = "section",
                    fields = listOf(
                        Text(
                            type = "mrkdwn",
                            text = " \n*Branch:*\n${githubContext.refName}",
                        ),
                        Text(
                            type = "mrkdwn",
                            text = " \n*Comitter:*\n${commitEvent?.committer?.displayName}",
                        ),
                        Text(
                            type = "mrkdwn",
                            text = " \n*Message:*\n<${commitEvent?.url}|${commitEvent?.message}>",
                        ),
                        Text(
                            type = "mrkdwn",
                            text = " \n*SHA:*\n<${commitEvent?.url}|${commitEvent?.id}>",
                        )
                    )
                ),
                SlackBlock(
                    type = "divider"
                )
            ),
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

    private suspend fun makeSlackRequest(url: String, message: Message): Response? {

        val response = client.post(url) {
            header("Authorization", "Bearer $slackToken")
            contentType(ContentType.Application.Json)
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
    private data class Message(
        val channel: String? = null,
        val ts: String? = null,
        val text: String? = null,
        val blocks: List<SlackBlock>? = null,
        val attachments: List<MessageAttachment>? = null,
    )

    @Serializable
    private class SlackBlock(
        @SerialName("type")
        val type: String,
        @SerialName("text")
        val text: Text? = null,
        @SerialName("fields")
        val fields: List<Text>? = null,
    )

    @Serializable
    private class Text(
        @SerialName("type")
        val type: String,
        @SerialName("text")
        val text: String,
        @SerialName("emoji")
        val emoji: Boolean = true,
        @SerialName("short")
        val short: Boolean = true,
    )

    @Serializable
    private data class MessageAttachment(
        @SerialName("mrkdwn_in")
        val mrkdwnIn: List<String> = listOf("text"),
        @SerialName("color")
        val color: String? = null,
        @SerialName("pretext")
        val pretext: String? = null,
        @SerialName("author_name")
        val authorName: String? = null,
        @SerialName("author_link")
        val authorLink: String? = null,
        @SerialName("author_icon")
        val authorIcon: String? = null,
        @SerialName("title")
        val title: String? = null,
        @SerialName("title_link")
        val titleLink: String? = null,
        @SerialName("text")
        val text: String? = null,
        @SerialName("fields")
        val fields: List<Field>? = null,
        @SerialName("thumb_url")
        val thumbUrl: String? = null,
        @SerialName("footer")
        val footer: String? = null,
        @SerialName("footer_icon")
        val footerIcon: String? = null,
        @SerialName("blocks")
        val blocks: List<SlackBlock>? = null,
    ) {

        val jobType = JobType.fromLabel(
            label = fields?.firstOrNull()?.title
        )

        @Serializable
        data class Field(
            @SerialName("title")
            val title: String, // A field's title
            @SerialName("value")
            val value: String, // This field's value
            @SerialName("short")
            val short: Boolean, // false
        )

        @Serializable
        data class Action(
            @SerialName("type")
            val type: String, // A field's title
            @SerialName("value")
            val text: String, // This field's value
            @SerialName("url")
            val url: String, // false
        )
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
        val messages: List<Message>,
    )
}