package com.monta.slack.notifier.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.monta.slack.notifier.model.GithubEvent
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.model.serializers.BaseGithubContext
import com.monta.slack.notifier.service.PublishSlackService
import com.monta.slack.notifier.util.populateEventFromJson
import com.monta.slack.notifier.util.readStringFromFile
import kotlinx.coroutines.runBlocking

class PublishSlackCommand : CliktCommand() {
    val githubEventPath: String by option(
        help = "Github Context event json file path",
        envvar = "GITHUB_EVENT_PATH"
    ).required()

    val githubRepository: String by option(
        help = "Github Context repository",
        envvar = "GITHUB_REPOSITORY"
    ).required()

    val githubRunId: String by option(
        help = "Github Context run id",
        envvar = "GITHUB_RUN_ID"
    ).required()

    val githubWorkflow: String by option(
        help = "Github Context workflow",
        envvar = "GITHUB_WORKFLOW"
    ).required()

    val githubRefName: String by option(
        help = "Github Context ref name",
        envvar = "GITHUB_REF_NAME"
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
            val githubEvent = getGithubEvent()
            PublishSlackService(
                serviceName = serviceName.valueOrNull(),
                serviceEmoji = serviceEmoji.valueOrNull(),
                slackToken = slackToken,
                slackChannelId = slackChannelId
            ).publish(
                githubEvent = githubEvent,
                jobType = JobType.fromString(jobType),
                jobStatus = JobStatus.fromString(jobStatus),
                slackMessageId = slackMessageId.valueOrNull()
            )
        }
    }

    private fun getGithubEvent(): GithubEvent {
        val baseGithubContext: BaseGithubContext

        val eventJson = readStringFromFile(githubEventPath)

        // The Github events can take many shapes, therefore we
        // sort them and transfer them into a simpler object
        baseGithubContext = populateEventFromJson(eventJson)

        return GithubEvent(
            repository = githubRepository,
            refName = githubRefName,
            runId = githubRunId,
            displayName = baseGithubContext.displayName,
            commitSHA = baseGithubContext.sha,
            commitMessage = baseGithubContext.message,
            workflow = githubWorkflow
        )
    }

    /**
     * Needed for optional parameters as the return the empty string instead of null
     * if set via ENV variables (as we do from our GitHub Actions)
     */
    private fun String?.valueOrNull() = if (this.isNullOrBlank()) null else this
}
