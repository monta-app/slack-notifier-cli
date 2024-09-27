package com.monta.slack.notifier.service

import com.monta.slack.notifier.SlackClient.MessageResponse
import com.monta.slack.notifier.SlackClient.Response
import com.monta.slack.notifier.SlackHttpClient
import com.monta.slack.notifier.model.GithubEvent
import com.monta.slack.notifier.model.JobStatus
import com.monta.slack.notifier.model.JobType
import com.monta.slack.notifier.model.SlackMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class TestSlackHttpClient(input: Input) : SlackHttpClient(input) {

    val sentMessages = mutableListOf<SlackMessage>()

    override suspend fun getSlackMessageById(
        messageId: String,
    ): MessageResponse? {
        return MessageResponse(true, sentMessages.toList())
    }

    override suspend fun makeSlackRequest(url: String, message: SlackMessage): Response? {
        sentMessages.add(message)
        return Response(true, "channel", "00:00:00")
    }

    fun listMessages(): List<SlackMessage> {
        return sentMessages.toList()
    }
}

class PublishSlackServiceTest {
    @Test
    fun test_replacing_attachments() {
        val input = Input(
            serviceName = "gineau pig",
            serviceEmoji = "🐷",
            slackToken = "token",
            slackChannelId = "#anni-test",
            appendAttachments = false
        )
        val testSlackHttpClient = TestSlackHttpClient(input)
        val service = PublishSlackService(
            input = input,
            testSlackHttpClient
        )
        runTest {
            // given one message is published
            service.publish(
                githubEvent = GithubEvent(
                    "repository",
                    "master",
                    "1",
                    "nickelsen",
                    "a1b2c3d4",
                    "message",
                    "workflow",
                    "url"
                ),
                jobType = JobType.Test,
                jobStatus = JobStatus.Progress,
                slackMessageId = null
            )
            val slackMessagesBefore = testSlackHttpClient.listMessages()
            slackMessagesBefore.size shouldBe 1
            slackMessagesBefore.first().attachments?.size shouldBe 1

            // when a second message is published
            service.publish(
                githubEvent = GithubEvent(
                    "repository",
                    "master",
                    "1",
                    "nickelsen",
                    "a1b2c3d4",
                    "message",
                    "workflow",
                    "url"
                ),
                jobType = JobType.Test,
                jobStatus = JobStatus.Success,
                slackMessageId = "00:00:00"
            )

            // then the attachment of the second message is replaced
            val slackMessagesAfter = testSlackHttpClient.listMessages()
            slackMessagesAfter.size shouldBe 2
            slackMessagesAfter.last().attachments?.size shouldBe 1

        }

    }

    @Test
    fun test_appending_attachments() {
        val input = Input(
            serviceName = "gineau pig",
            serviceEmoji = "🐷",
            slackToken = "token",
            slackChannelId = "#anni-test",
            appendAttachments = true
        )
        val testSlackHttpClient = TestSlackHttpClient(input)
        val service = PublishSlackService(
            input = input,
            testSlackHttpClient
        )
        runTest {
            // given one message is published
            service.publish(
                githubEvent = GithubEvent(
                    "repository",
                    "master",
                    "1",
                    "nickelsen",
                    "a1b2c3d4",
                    "message",
                    "workflow",
                    "url"
                ),
                jobType = JobType.Test,
                jobStatus = JobStatus.Progress,
                slackMessageId = null
            )
            val slackMessagesBefore = testSlackHttpClient.listMessages()
            slackMessagesBefore.size shouldBe 1
            slackMessagesBefore.first().attachments?.size shouldBe 1

            // when a second message is published
            service.publish(
                githubEvent = GithubEvent(
                    "repository",
                    "master",
                    "1",
                    "nickelsen",
                    "a1b2c3d4",
                    "message",
                    "workflow",
                    "url"
                ),
                jobType = JobType.Test,
                jobStatus = JobStatus.Success,
                slackMessageId = "00:00:00"
            )

            // then an additional attachment is added to the second message
            val slackMessagesAfter = testSlackHttpClient.listMessages()
            slackMessagesAfter.size shouldBe 2
            slackMessagesAfter.last().attachments?.size shouldBe 2
        }
    }
}
