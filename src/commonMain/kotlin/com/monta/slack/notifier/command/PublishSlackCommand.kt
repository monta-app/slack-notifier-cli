package com.monta.slack.notifier.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.service.PublishSlackService
import kotlinx.coroutines.runBlocking

class PublishSlackCommand : CliktCommand() {

    private val githubContext: String by option(
        help = "Github Context",
        envvar = "PUBLISH_SLACK_GITHUB_CONTEXT"
    ).required()


    private val serviceName: String? by option(
        help = "Emoji for the app!",
        envvar = "PUBLISH_SLACK_SERVICE_NAME"
    )

    private val serviceEmoji: String? by option(
        help = "Emoji for the app!",
        envvar = "PUBLISH_SLACK_SERVICE_EMOJI"
    )

    private val jobType: String by option(
        help = "Job Type",
        envvar = "PUBLISH_SLACK_JOB_TYPE"
    ).required()

    private val jobStatus: String by option(
        help = "Job Status",
        envvar = "PUBLISH_SLACK_JOB_STATUS"
    ).required()

    private val slackToken: String by option(
        help = "Slack token used for publishing",
        envvar = "SLACK_APP_TOKEN"
    ).required()

    private val slackChannelId: String by option(
        help = "Slack channel where the changelog will be published to (i.e #my-channel)",
        envvar = "SLACK_CHANNEL_ID"
    ).required()

    private val slackMessageId: String? by option(
        help = "Slack message id to be updated",
        envvar = "SLACK_MESSAGE_ID"
    )

    override fun run() {
        runBlocking {
            PublishSlackService(
                serviceName = serviceName,
                serviceEmoji = serviceEmoji,
                slackToken = slackToken,
                slackChannelId = slackChannelId
            ).publish(
                githubContext = githubContext,
                jobType = JobType.fromString(jobType),
                jobStatus = JobStatus.fromString(jobStatus),
                slackMessageId = slackMessageId,
            )
        }
    }
}
