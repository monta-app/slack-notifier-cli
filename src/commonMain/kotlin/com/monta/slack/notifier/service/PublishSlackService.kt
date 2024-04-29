package com.monta.slack.notifier.service

import com.monta.slack.notifier.SlackClient
import com.monta.slack.notifier.model.GithubEvent
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.util.writeToOutput

class PublishSlackService(
    serviceName: String?,
    serviceEmoji: String?,
    slackToken: String,
    slackChannelId: String,
) {

    private val slackClient = SlackClient(
        serviceName = serviceName,
        serviceEmoji = serviceEmoji,
        slackToken = slackToken,
        slackChannelId = slackChannelId
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
                jobStatus = jobStatus
            )
        }

        writeToOutput("SLACK_MESSAGE_ID", messageId)

        return messageId
    }
}
