package com.monta.slack.notifier.model

class GithubEvent(
    val repository: String,
    val refName: String,
    var runId: String,
    val displayName: String?,
    val commitSHA: String?,
    val commitMessage: String?,
    val workflow: String?,
) {
    fun getRunUrl(): String {
        return "https://github.com/$repository/actions/runs/$runId"
    }
    fun getCommitUrl(): String {
        return "https://github.com/$repository/commit/$commitSHA"
    }
    fun getCommitMessage(): String? {
        return commitMessage
            ?.replace("\n", " ")
            ?.replace("\r", " ")
            ?.take(120)
    }
}
