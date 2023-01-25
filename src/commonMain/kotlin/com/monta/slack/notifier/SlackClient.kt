package com.monta.slack.notifier

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SlackClient(
    private val slackToken: String,
    private val slackChannelId: String,
) {

    private val jsonInstance = Json {
        ignoreUnknownKeys = true
    }

    suspend fun create(
        title: String,
        color: String,
        message: String,
    ): String {

        val response = makeSlackRequest(
            url = "https://slack.com/api/chat.postMessage",
            message = getMessage(
                messageId = null,
                title = title,
                color = color,
                message = message
            )
        )

        return requireNotNull(response?.ts)
    }

    suspend fun update(
        messageId: String,
        title: String,
        color: String,
        message: String,
    ): String {

        val response = makeSlackRequest(
            url = "https://slack.com/api/chat.update",
            message = getMessage(
                messageId = messageId,
                title = title,
                color = color,
                message = message
            )
        )
        return requireNotNull(response?.ts)
    }

    private fun getMessage(
        messageId: String?,
        title: String,
        color: String,
        message: String,
    ): Message {
        return Message(
            channel = slackChannelId,
            ts = messageId,
            text = title,
            attachments = listOf(
                MessageAttachment(
                    color = color,
                    fields = listOf(
                        MessageAttachment.Field(
                            title = "Status",
                            short = true,
                            value = message
                        )
                    )
                )
            )
        )
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
            jsonInstance.decodeFromString(bodyString)
        } else {
            println("failed to post message $bodyString")
            null
        }
    }

    @Serializable
    private data class Message(
        val channel: String,
        val ts: String?,
        val text: String,
        val attachments: List<MessageAttachment>,
    )

    @Serializable
    private data class MessageAttachment(
        @SerialName("color")
        val color: String, // dbab09
        @SerialName("fields")
        val fields: List<Field>,
    ) {
        @Serializable
        data class Field(
            @SerialName("title")
            val title: String, // Status
            @SerialName("short")
            val short: Boolean, // true
            @SerialName("value")
            val value: String, // In Progress 🚧
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
}