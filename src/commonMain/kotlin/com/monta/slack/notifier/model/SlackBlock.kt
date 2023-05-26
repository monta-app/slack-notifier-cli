package com.monta.slack.notifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class SlackBlock(
    @SerialName("type")
    val type: String,
    @SerialName("text")
    val text: Text? = null,
    @SerialName("fields")
    val fields: List<Text>? = null
) {
    @Serializable
    class Text(
        @SerialName("type")
        val type: String,
        @SerialName("text")
        val text: String,
        @SerialName("emoji")
        val emoji: Boolean = true,
        @SerialName("short")
        val short: Boolean = true
    )
}
