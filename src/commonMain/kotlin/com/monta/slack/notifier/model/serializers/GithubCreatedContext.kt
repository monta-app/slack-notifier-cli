package com.monta.slack.notifier.model.serializers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GithubCreatedContext(
    @SerialName("after") val sha: String,
    @SerialName("pull_request") val pullRequest: PullRequest,
) {
    @Serializable
    data class PullRequest(
        @SerialName("title") val title: String,
        @SerialName("user") val user: PullRequestUser,
    )

    @Serializable
    data class PullRequestUser(
        @SerialName("login") val login: String,
    )
}
