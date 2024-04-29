package com.monta.slack.notifier.model.serializers

import kotlinx.serialization.Serializable

class BaseGithubContext(
    val displayName: String?,
    val sha: String?,
    val message: String?,
)
