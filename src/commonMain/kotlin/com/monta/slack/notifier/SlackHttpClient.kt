package com.monta.slack.notifier

import com.monta.slack.notifier.SlackClient.MessageResponse
import com.monta.slack.notifier.SlackClient.Response
import com.monta.slack.notifier.model.SlackMessage
import com.monta.slack.notifier.service.Input
import com.monta.slack.notifier.util.JsonUtil
import com.monta.slack.notifier.util.client
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import io.ktor.utils.io.charsets.*

open class SlackHttpClient(
    private val input: Input,
) {

    open suspend fun getSlackMessageById(
        messageId: String,
    ): MessageResponse? {
        val response = client.get {
            header("Authorization", "Bearer ${input.slackToken}")
            url {
                url("https://slack.com/api/conversations.history")
                parameters.append("channel", input.slackChannelId)
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

    open suspend fun makeSlackRequest(url: String, message: SlackMessage): Response? {
        val response = client.post(url) {
            header("Authorization", "Bearer ${input.slackToken}")
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


}
