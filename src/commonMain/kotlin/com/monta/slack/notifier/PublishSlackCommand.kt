package com.monta.slack.notifier

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import kotlinx.coroutines.runBlocking

class PublishSlackCommand : CliktCommand() {
    private val banner = """
 __    __   ______   __   __   ______  ______    
/\ "-./  \ /\  __ \ /\ "-.\ \ /\__  _\/\  __ \   
\ \ \-./\ \\ \ \/\ \\ \ \-.  \\/_/\ \/\ \  __ \  
 \ \_\ \ \_\\ \_____\\ \_\\"\_\  \ \_\ \ \_\ \_\ 
  \/_/  \/_/ \/_____/ \/_/ \/_/   \/_/  \/_/\/_/              
        """.trimIndent()

    private val status: String by option(
        help = "Name of the service",
        envvar = "PUBLISH_SLACK_STATUS"
    ).required()

    private val title: String by option(
        help = "Name of the service",
        envvar = "PUBLISH_SLACK_TITLE"
    ).required()

    private val slackToken: String by option(
        help = "Slack token used for publishing",
        envvar = "SLACK_TOKEN"
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
                slackToken = slackToken,
                slackChannelId = slackChannelId
            ).publish(
                messageId = slackMessageId,
                status = status,
                title = title
            )
        }
    }
}
