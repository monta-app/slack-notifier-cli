package com.monta.slack.notifier.model.serializers

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubPushContext(
    @SerialName("ref")
    val ref: String? = null, // refs/heads/develop
    @SerialName("sha")
    val sha: String? = null, // c545a1613f18937a88a13935c4d644e8f81b71d6
    @SerialName("repository")
    val repository: String? = null, // monta-app/service-integrations
    @SerialName("run_id")
    val runId: String? = null, // 4399287439
    @SerialName("actor")
    val actor: String? = null, // BrianEstrada
    @SerialName("triggering_actor")
    val triggeringActor: String? = null, // BrianEstrada
    @SerialName("workflow")
    val workflow: String? = null, // Deploy Dev
    @SerialName("event_name")
    val eventName: String? = null, // push
    @SerialName("event")
    val event: Event? = null,
    @SerialName("ref_name")
    val refName: String? = null, // develop
    @SerialName("ref_type")
    val refType: String? = null, // branch
) {

    @Serializable
    data class Event(
        @SerialName("head_commit")
        var headCommit: Commit,
        @SerialName("pusher")
        val pusher: Committer,
        @SerialName("ref")
        val ref: String? = null, // refs/heads/develop
    )

    @Serializable
    data class Commit(
        @SerialName("author")
        val author: Committer? = null,
        @SerialName("committer")
        val committer: Committer? = null,
        @SerialName("distinct")
        val distinct: Boolean? = null, // true
        @SerialName("id")
        val id: String, // c545a1613f18937a88a13935c4d644e8f81b71d6
        @SerialName("message")
        val message: String, // ignore: test
        @SerialName("timestamp")
        val timestamp: String? = null, // 2023-03-12T21:24:19+01:00
        @SerialName("tree_id")
        val treeId: String? = null, // f3667a7332372de2ccb6a1cc5c310e780915a28e
        @SerialName("url")
        val url: String? = null, // https://github.com/monta-app/service-integrations/commit/c545a1613f18937a88a13935c4d644e8f81b71d6
    )

    @Serializable
    data class Committer(
        @SerialName("email")
        val email: String? = null, // lovesguitar@gmail.com
        @SerialName("name")
        val name: String? = null, // Brian Estrada
        @SerialName("username")
        val username: String? = null, // BrianEstrada
    ) {
        val displayName: String = when {
            email == null && name == null -> "N/A"
            email == null && name != null -> name
            email != null && name == null -> email
            else -> "$name<$email>"
        }
    }
}
