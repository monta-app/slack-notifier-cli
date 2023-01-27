package com.monta.slack.notifier.service

import com.monta.slack.notifier.SlackClient
import com.monta.slack.notifier.model.GithubContext
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.util.writeToOutput

class PublishSlackService(
    slackToken: String,
    slackChannelId: String,
) {

    private val slackClient = SlackClient(
        slackToken = slackToken,
        slackChannelId = slackChannelId
    )

    suspend fun publish(
        githubContext: GithubContext,
        jobType: JobType,
        jobStatus: JobStatus,
        slackMessageId: String?,
    ): String {

        val messageId = if (slackMessageId.isNullOrBlank()) {
            slackClient.create(
                githubContext = githubContext,
                jobType = jobType,
                jobStatus = jobStatus,
            )
        } else {
            slackClient.update(
                messageId = slackMessageId,
                githubContext = githubContext,
                jobType = jobType,
                jobStatus = jobStatus,
            )
        }

        writeToOutput("SLACK_MESSAGE_ID", messageId)

        return messageId
    }
}
