package com.monta.slack.notifier.service

import com.monta.slack.notifier.SlackClient
import com.monta.slack.notifier.model.GithubPullRequest
import com.monta.slack.notifier.model.GithubPushRequest
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.model.Messageable
import com.monta.slack.notifier.util.JsonUtil
import com.monta.slack.notifier.util.writeToOutput
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class PublishSlackService(
    slackToken: String,
    slackChannelId: String,
) {

    private val slackClient = SlackClient(
        slackToken = slackToken,
        slackChannelId = slackChannelId
    )

    suspend fun publish(
        githubContext: String,
        jobType: JobType,
        jobStatus: JobStatus,
        slackMessageId: String?,
    ): String {

        val jsonObject = JsonUtil.instance.decodeFromString<JsonObject>(githubContext)

        val eventName = jsonObject["event_name"]?.jsonPrimitive?.contentOrNull

        val messageable: Messageable = if (eventName == "pull_request") {
            JsonUtil.instance.decodeFromString<GithubPullRequest>(githubContext)
        } else {
            JsonUtil.instance.decodeFromString<GithubPushRequest>(githubContext)
        }

        val messageId = if (slackMessageId.isNullOrBlank()) {
            slackClient.create(
                messageable = messageable,
                jobType = jobType,
                jobStatus = jobStatus,
            )
        } else {
            slackClient.update(
                messageId = slackMessageId,
                messageable = messageable,
                jobType = jobType,
                jobStatus = jobStatus,
            )
        }

        writeToOutput("SLACK_MESSAGE_ID", messageId)

        return messageId
    }
}
