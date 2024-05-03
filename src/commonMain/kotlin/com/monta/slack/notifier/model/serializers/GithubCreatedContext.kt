package com.monta.slack.notifier.model.serializers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GithubCreatedContext(
    @SerialName("sender") val sender: Sender,
    @SerialName("issue") val issue: Issue,
) {
    @Serializable
    data class Sender(
        @SerialName("login") val login: String,
    )

    @Serializable
    data class Issue(
        @SerialName("title") val title: String,
        @SerialName("html_url") val url: String,
    )
}
