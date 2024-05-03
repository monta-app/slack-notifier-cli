package com.monta.slack.notifier.util

import com.monta.slack.notifier.model.serializers.BaseGithubContext
import com.monta.slack.notifier.model.serializers.GithubCreatedContext
import com.monta.slack.notifier.model.serializers.GithubOpenedContext
import com.monta.slack.notifier.model.serializers.GithubPushContext
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import kotlinx.serialization.SerializationException
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen

@OptIn(ExperimentalForeignApi::class)
fun readStringFromFile(
    filePath: String,
): String {
    val returnBuffer = StringBuilder()
    val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open file $filePath for reading")
    try {
        memScoped {
            val readBufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                returnBuffer.append(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        }
    } finally {
        fclose(file)
    }
    return returnBuffer.toString()
}

/**
 * Populates the existing event type with information needed to generate
 * an entire Slack notification.
 */
fun populateEventFromJson(eventJson: String): BaseGithubContext {
    return populateOnJsonPush(eventJson) ?: populateOnJsonOpened(eventJson) ?: populateOnJsonCreated(eventJson) ?: handleFailure()
}

private fun populateOnJsonPush(eventJson: String): BaseGithubContext? {
    @Suppress("SwallowedException")
    return try {
        val event = JsonUtil.instance.decodeFromString<GithubPushContext.Event>(eventJson)
        println("Matched with a Push event")
        return BaseGithubContext(
            displayName = event.pusher.displayName,
            sha = event.headCommit.id,
            message = event.headCommit.message,
            prUrl = null
        )
    } catch (e: SerializationException) {
        null
    }
}

private fun populateOnJsonOpened(eventJson: String): BaseGithubContext? {
    @Suppress("SwallowedException")
    return try {
        val event = JsonUtil.instance.decodeFromString<GithubOpenedContext>(eventJson)
        println("Matched with a Opened event")
        return BaseGithubContext(
            displayName = event.pullRequest.user.login,
            sha = event.pullRequest.head.sha,
            message = event.pullRequest.title,
            prUrl = null
        )
    } catch (e: SerializationException) {
        null
    }
}

private fun populateOnJsonCreated(eventJson: String): BaseGithubContext? {
    @Suppress("SwallowedException")
    return try {
        val event = JsonUtil.instance.decodeFromString<GithubCreatedContext>(eventJson)
        println("Matched with a Created event")
        return BaseGithubContext(
            displayName = event.sender.login,
            sha = null,
            message = event.issue.title,
            prUrl = event.issue.url
        )
    } catch (e: SerializationException) {
        null
    }
}

private fun handleFailure(): BaseGithubContext {
    println("DIDNT MATCH an event :((((")
    return BaseGithubContext(
        displayName = null,
        sha = null,
        message = null,
        prUrl = null
    )
}
