package com.monta.slack.notifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubTrunkBasedPushContext(
    @SerialName("run_id")
    val runId: String? = null, // 4. Fourth var, Name of run (should be )
) {
    // In this class we grab variables from a trunk based event flow
    // Where each thing we need to populate the slack message are listed
    @Serializable
    data class PullRequestHead(
        @SerialName("ref") val ref: String,
    )

    @Serializable
    data class PullRequest(
        @SerialName("head") val head: PullRequestHead,
        @SerialName("title") val title: String,
        @SerialName("user") val user: PullRequestUser,
    )

    @Serializable
    data class PullRequestUser(
        @SerialName("login") val login: String,
    )

    @Serializable
    data class Repository(
        @SerialName("full_name") val fullName: String,
    )

    @Serializable
    data class Event(
        @SerialName("after") val sha: String,
        @SerialName("pull_request") val pullRequest: PullRequest,
        @SerialName("repository") val repository: Repository,
    )
}
