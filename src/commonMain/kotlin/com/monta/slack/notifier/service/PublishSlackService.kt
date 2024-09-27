package com.monta.slack.notifier.service

import com.monta.slack.notifier.SlackClient
import com.monta.slack.notifier.SlackHttpClient
import com.monta.slack.notifier.model.GithubEvent
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.util.writeToOutput

data class Input(
    val serviceName: String?,
    val serviceEmoji: String?,
    val slackToken: String,
    val slackChannelId: String,
    val appendAttachments: Boolean = false,
)

class PublishSlackService(
    input: Input,
    slackHttpClient: SlackHttpClient,
) {
    private val slackClient = SlackClient(
        input = input,
        slackHttpClient = slackHttpClient
    )

    suspend fun publish(
        githubEvent: GithubEvent,
        jobType: JobType,
        jobStatus: JobStatus,
        slackMessageId: String?,
    ): String {
        val messageId = if (slackMessageId.isNullOrBlank()) {
            slackClient.create(
                githubEvent = githubEvent,
                jobType = jobType,
                jobStatus = jobStatus
            )
        } else {
            slackClient.update(
                messageId = slackMessageId,
                githubEvent = githubEvent,
                jobType = jobType,
                jobStatus = jobStatus,
            )
        }

        writeToOutput("SLACK_MESSAGE_ID", messageId)

        return messageId
    }
}
