package com.monta.slack.notifier.model.serializers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GithubOpenedContext(
    @SerialName("pull_request") val pullRequest: PullRequest,
) {
    @Serializable
    data class PullRequest(
        @SerialName("title") val title: String,
        @SerialName("user") val user: PullRequestUser,
        @SerialName("head") val head: PullRequestHead,
    )

    @Serializable
    data class PullRequestHead(
        @SerialName("sha") val sha: String,
    )

    @Serializable
    data class PullRequestUser(
        @SerialName("login") val login: String,
    )
}
