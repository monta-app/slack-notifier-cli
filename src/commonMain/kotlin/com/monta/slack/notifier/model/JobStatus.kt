package com.monta.slack.notifier.model

enum class JobStatus(
    val message: String,
    val color: String,
) {
    Progress(
        message = "In Progress :construction:",
        color = "#DBAB09"
    ),
    Success(
        message = "Success :white_check_mark:",
        color = "#00FF00"
    ),
    Failure(
        message = "Failure :x:",
        color = "#FF0000"
    ),
    Cancelled(
        message = "Cancelled :warning:",
        color = "#FFFF00"
    ),
    Unknown(
        message = "Something went wrong :question:",
        color = "#DBAB09"
    ),
    ;

    companion object {
        fun fromString(value: String?): JobStatus {
            return values().find { state ->
                state.name.equals(value, true)
            } ?: Unknown
        }
    }
}
