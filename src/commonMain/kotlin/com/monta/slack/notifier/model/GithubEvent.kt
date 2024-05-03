package com.monta.slack.notifier.model

class GithubEvent(
    val repository: String,
    val refName: String,
    var runId: String,
    val displayName: String?,
    val commitSHA: String?,
    val message: String?,
    val workflow: String?,
    val prUrl: String?,
) {
    fun getRunUrl(): String {
        return "https://github.com/$repository/actions/runs/$runId"
    }
    fun getChangeIdentifier(): String? {
        if (commitSHA != null) {
            return commitSHA
        } else if (prUrl != null) {
            return getPRidentifier(prUrl)
        }
        return null
    }
    fun getChangeUrl(): String {
        if (commitSHA != null) {
            return "https://github.com/$repository/commit/$commitSHA"
        } else if (prUrl != null) {
            return prUrl
        }
        return "https://github.com/$repository/"
    }
    fun getChangeMessage(): String? {
        return message
            ?.replace("\n", " ")
            ?.replace("\r", " ")
            ?.replace("<", "")
            ?.replace(">", "")
            ?.take(120)
    }
    fun getPRidentifier(url: String): String? {
        // Will extract the "pull/51" part of
        // "https://github.com/monta-app/data-smart-charge/pull/51",
        val regex = Regex("""pull/\d+""")
        val matchResult = regex.find(url)
        return matchResult?.value
    }
}
