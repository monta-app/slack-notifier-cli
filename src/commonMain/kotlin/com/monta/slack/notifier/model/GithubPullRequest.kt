package com.monta.slack.notifier.model


import com.monta.slack.notifier.util.buildTitle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubPullRequest(
    @SerialName("job")
    val job: String?, // printJob
    @SerialName("sha")
    val sha: String?, // 0a2debbc56212c2513b58e244cc94e4774aed004
    @SerialName("repository")
    val repository: String?, // monta-app/service-ocppi
    @SerialName("triggering_actor")
    val triggeringActor: String?, // BrianEstrada
    @SerialName("workflow")
    val workflow: String?, // Pull Request Workflow
    @SerialName("head_ref")
    val headRef: String?, // fix/broken_zaptec_query_param
    @SerialName("event")
    val event: Event?,
) : Messageable {

    @Serializable
    data class Event(
        @SerialName("pull_request")
        val pullRequest: PullRequest?,
    ) {
        @Serializable
        data class PullRequest(
            @SerialName("html_url")
            val htmlUrl: String?, // https://github.com/monta-app/service-integrations/pull/468
            @SerialName("id")
            val id: Int?, // 1223442929
            @SerialName("title")
            val title: String?, // fix: broken query param after renaming
        )
    }

    override fun toMessage(
        serviceName: String?,
        serviceEmoji: String?,
        slackChannelId: String,
        messageId: String?,
        attachments: List<SlackMessage.Attachment>?,
    ): SlackMessage {
        return SlackMessage(
            channel = slackChannelId,
            ts = messageId,
            blocks = listOf(
                SlackBlock(
                    type = "header",
                    text = SlackBlock.Text(
                        type = "plain_text",
                        text = buildTitle(repository, workflow, serviceName, serviceEmoji)
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
                            text = " \n*Branch:*\n${headRef}",
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Comitter:*\n${triggeringActor}",
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Message:*\n<${event?.pullRequest?.htmlUrl}|${event?.pullRequest?.title}>",
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*SHA:*\n<${event?.pullRequest?.htmlUrl}|${sha}>",
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
}