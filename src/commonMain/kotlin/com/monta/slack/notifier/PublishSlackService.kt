package com.monta.slack.notifier

class PublishSlackService(
    slackToken: String,
    slackChannelId: String,
) {

    private val slackClient = SlackClient(
        slackToken = slackToken,
        slackChannelId = slackChannelId
    )

    suspend fun publish(
        messageId: String?,
        status: String,
        title: String,
    ) {

        val message: String
        val color: String

        when (status) {
            "success" -> {
                message = "Success ✅"
                color = "#00FF00"
            }

            "failure" -> {
                message = "Failure ❌"
                color = "#FF0000"
            }

            "cancelled" -> {
                message = "Cancelled \uD83D\uDEB8"
                color = "#FFFF00"
            }

            "progress" -> {
                message = "In Progress \uD83D\uDEA7"
                color = "#DBAB09"
            }

            else -> {
                message = "Something went wrong"
                color = "#DBAB09"
            }
        }


        val slackMessageId = if (messageId == null) {
            slackClient.create(
                title = title,
                color = color,
                message = message
            )

        } else {
            slackClient.update(
                messageId = messageId,
                title = title,
                color = color,
                message = message
            )
        }

        writeToOutput("SLACK_MESSAGE_ID", slackMessageId)
    }
}
