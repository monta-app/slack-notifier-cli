package com.monta.slack.notifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubContext(
    @SerialName("token")
    val token: String?, // ***
    @SerialName("job")
    val job: String?, // printJob
    @SerialName("ref")
    val ref: String?, // refs/heads/develop
    @SerialName("sha")
    val sha: String?, // a991375c9aef0a1e6c7f48d60662f807bcfa1fbe
    @SerialName("repository")
    val repository: String?, // monta-app/service-ocpp
    @SerialName("repository_owner")
    val repositoryOwner: String?, // monta-app
    @SerialName("repository_owner_id")
    val repositoryOwnerId: String?, // 70286931
    @SerialName("repositoryUrl")
    val repositoryUrl: String?, // git://github.com/monta-app/service-ocpp.git
    @SerialName("run_id")
    val runId: String?, // 4020820554
    @SerialName("run_number")
    val runNumber: String?, // 15
    @SerialName("retention_days")
    val retentionDays: String?, // 90
    @SerialName("run_attempt")
    val runAttempt: String?, // 1
    @SerialName("artifact_cache_size_limit")
    val artifactCacheSizeLimit: String?, // 10
    @SerialName("repository_visibility")
    val repositoryVisibility: String?, // private
    @SerialName("repository_id")
    val repositoryId: String?, // 302884721
    @SerialName("actor_id")
    val actorId: String?, // 909987
    @SerialName("actor")
    val actor: String?, // BrianEstrada
    @SerialName("triggering_actor")
    val triggeringActor: String?, // BrianEstrada
    @SerialName("workflow")
    val workflow: String?, // Deploy Gateway Dev
    @SerialName("head_ref")
    val headRef: String?,
    @SerialName("base_ref")
    val baseRef: String?,
    @SerialName("event_name")
    val eventName: String?, // push
    @SerialName("event")
    val event: Event?,
    @SerialName("server_url")
    val serverUrl: String?, // https://github.com
    @SerialName("api_url")
    val apiUrl: String?, // https://api.github.com
    @SerialName("graphql_url")
    val graphqlUrl: String?, // https://api.github.com/graphql
    @SerialName("ref_name")
    val refName: String?, // develop
    @SerialName("ref_protected")
    val refProtected: Boolean?, // true
    @SerialName("ref_type")
    val refType: String?, // branch
    @SerialName("secret_source")
    val secretSource: String?, // Actions
    @SerialName("workflow_ref")
    val workflowRef: String?, // monta-app/service-ocpp/.github/workflows/deploy_gateway_dev.yml@refs/heads/develop
    @SerialName("workflow_sha")
    val workflowSha: String?, // a991375c9aef0a1e6c7f48d60662f807bcfa1fbe
    @SerialName("workspace")
    val workspace: String?, // /home/runner/work/service-ocpp/service-ocpp
    @SerialName("action")
    val action: String?, // __run
) {
    @Serializable
    data class Event(
        @SerialName("after")
        val after: String?, // a991375c9aef0a1e6c7f48d60662f807bcfa1fbe
        @SerialName("before")
        val before: String?, // 30410db20f3d9a00b67feaa634b2c52e392b75d1
        @SerialName("commits")
        val commits: List<Commit>?,
        @SerialName("compare")
        val compare: String?, // https://github.com/monta-app/service-ocpp/compare/30410db20f3d...a991375c9aef
        @SerialName("created")
        val created: Boolean?, // false
        @SerialName("deleted")
        val deleted: Boolean?, // false
        @SerialName("forced")
        val forced: Boolean?, // false
        @SerialName("head_commit")
        val headCommit: Commit?,
        @SerialName("ref")
        val ref: String?, // refs/heads/develop
    ) {
        @Serializable
        data class Commit(
            @SerialName("author")
            val author: User?,
            @SerialName("committer")
            val committer: User?,
            @SerialName("distinct")
            val distinct: Boolean?, // true
            @SerialName("id")
            val id: String?, // a991375c9aef0a1e6c7f48d60662f807bcfa1fbe
            @SerialName("message")
            val message: String?, // ignore: github context print
            @SerialName("timestamp")
            val timestamp: String?, // 2023-01-26T17:51:30-08:00
            @SerialName("tree_id")
            val treeId: String?, // 55b21e31b537491d03719527eee5a86a157fbeee
            @SerialName("url")
            val url: String?, // https://github.com/monta-app/service-ocpp/commit/a991375c9aef0a1e6c7f48d60662f807bcfa1fbe
        ) {
            @Serializable
            data class User(
                @SerialName("email")
                val email: String?, // lovesguitar@gmail.com
                @SerialName("name")
                val name: String?, // Brian Estrada
                @SerialName("username")
                val username: String?, // BrianEstrada
            ) {
                val displayName: String = "$name<$email>"
            }
        }
    }
}