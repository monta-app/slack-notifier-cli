package com.monta.slack.notifier.model.serializers

class BaseGithubContext(
    val displayName: String?,
    val sha: String?,
    val message: String?,
    val prUrl: String?,
)
