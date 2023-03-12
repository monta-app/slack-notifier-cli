package com.monta.slack.notifier.model


import com.monta.slack.notifier.util.buildTitle
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubPushContext(
    @SerialName("ref")
    val ref: String?, // refs/heads/develop
    @SerialName("sha")
    val sha: String?, // c545a1613f18937a88a13935c4d644e8f81b71d6
    @SerialName("repository")
    val repository: String?, // monta-app/service-integrations
    @SerialName("actor")
    val actor: String?, // BrianEstrada
    @SerialName("triggering_actor")
    val triggeringActor: String?, // BrianEstrada
    @SerialName("workflow")
    val workflow: String?, // Deploy Dev
    @SerialName("event_name")
    val eventName: String?, // push
    @SerialName("event")
    val event: Event?,
    @SerialName("ref_name")
    val refName: String?, // develop
    @SerialName("ref_type")
    val refType: String? // branch
) {

    @Serializable
    data class Event(
        @SerialName("head_commit")
        val headCommit: Commit?,
        @SerialName("pusher")
        val pusher: Committer?,
        @SerialName("ref")
        val ref: String? // refs/heads/develop
    )

    @Serializable
    data class Commit(
        @SerialName("author")
        val author: Committer?,
        @SerialName("committer")
        val committer: Committer?,
        @SerialName("distinct")
        val distinct: Boolean?, // true
        @SerialName("id")
        val id: String?, // c545a1613f18937a88a13935c4d644e8f81b71d6
        @SerialName("message")
        val message: String?, // ignore: test
        @SerialName("timestamp")
        val timestamp: String?, // 2023-03-12T21:24:19+01:00
        @SerialName("tree_id")
        val treeId: String?, // f3667a7332372de2ccb6a1cc5c310e780915a28e
        @SerialName("url")
        val url: String? // https://github.com/monta-app/service-integrations/commit/c545a1613f18937a88a13935c4d644e8f81b71d6
    )

    @Serializable
    data class Committer(
        @SerialName("email")
        val email: String?, // lovesguitar@gmail.com
        @SerialName("name")
        val name: String?, // Brian Estrada
        @SerialName("username")
        val username: String? // BrianEstrada
    ) {
        val displayName: String? = when {
            email == null && name == null -> null
            email == null && name != null -> name
            email != null && name == null -> email
            else -> "$name<$email>"
        }
    }

    fun toMessage(
        serviceName: String?,
        serviceEmoji: String?,
        slackChannelId: String,
        messageId: String?,
        attachments: List<SlackMessage.Attachment>?,
    ): SlackMessage {

        val commit = event?.headCommit

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
                            text = " \n*Branch:*\n${refName}",
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Comitter:*\n${commit?.committer?.displayName}",
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*Message:*\n<${commit?.url}|${commit?.message}>",
                        ),
                        SlackBlock.Text(
                            type = "mrkdwn",
                            text = " \n*SHA:*\n<${commit?.url}|${commit?.id}>",
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