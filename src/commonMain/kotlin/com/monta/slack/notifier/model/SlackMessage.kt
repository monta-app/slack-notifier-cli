package com.monta.slack.notifier.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlackMessage(
    val channel: String? = null,
    val ts: String? = null,
    val text: String? = null,
    val blocks: List<SlackBlock>? = null,
    val attachments: List<Attachment>? = null
) {
    @Serializable
    data class Attachment(
        @SerialName("mrkdwn_in")
        val mrkdwnIn: List<String> = listOf("text"),
        @SerialName("color")
        val color: String? = null,
        @SerialName("pretext")
        val pretext: String? = null,
        @SerialName("author_name")
        val authorName: String? = null,
        @SerialName("author_link")
        val authorLink: String? = null,
        @SerialName("author_icon")
        val authorIcon: String? = null,
        @SerialName("title")
        val title: String? = null,
        @SerialName("title_link")
        val titleLink: String? = null,
        @SerialName("text")
        val text: String? = null,
        @SerialName("fields")
        val fields: List<Field>? = null,
        @SerialName("thumb_url")
        val thumbUrl: String? = null,
        @SerialName("footer")
        val footer: String? = null,
        @SerialName("footer_icon")
        val footerIcon: String? = null,
        @SerialName("blocks")
        val blocks: List<SlackBlock>? = null
    ) {

        val jobType = JobType.fromLabel(
            label = fields?.firstOrNull()?.title
        )

        @Serializable
        data class Field(
            @SerialName("title")
            val title: String, // A field's title
            @SerialName("value")
            val value: String, // This field's value
            @SerialName("short")
            val short: Boolean // false
        )

        @Serializable
        data class Action(
            @SerialName("type")
            val type: String, // A field's title
            @SerialName("value")
            val text: String, // This field's value
            @SerialName("url")
            val url: String // false
        )
    }
}
