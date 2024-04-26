package com.monta.slack.notifier.util

import com.monta.slack.notifier.model.GithubPushContext
import com.monta.slack.notifier.model.GithubTrunkBasedPushContext

/**
 * Populates the existing event type with information needed to generate
 * an entire Slack notification. This is super hacky but an easy solution
 * without deintegrating GithubPushContext from the rest of the code.
 */
fun populateEventFromTrunkBasedEvent(eventJson: String, event: GithubPushContext.Event): GithubPushContext.Event {
    val trunkBasedEvent = JsonUtil.instance.decodeFromString<GithubTrunkBasedPushContext.Event>(eventJson)
    event.headCommit = GithubPushContext.Commit(
        committer = GithubPushContext.Committer(name = trunkBasedEvent.pullRequest.user.login),
        id = trunkBasedEvent.sha,
        url = "https://github.com/${trunkBasedEvent.repository.fullName}/compare/${trunkBasedEvent.pullRequest.head.ref}",
        message = trunkBasedEvent.pullRequest.title
    )
    return event
}
