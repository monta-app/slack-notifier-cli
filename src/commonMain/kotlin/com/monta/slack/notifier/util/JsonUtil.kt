package com.monta.slack.notifier.util

import kotlinx.serialization.json.Json

object JsonUtil {
    val instance = Json {
        ignoreUnknownKeys = true
    }
}
