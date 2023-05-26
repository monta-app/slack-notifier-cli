package com.monta.slack.notifier.util

fun buildTitle(
    repository: String?,
    workflow: String?,
    serviceName: String?,
    serviceEmoji: String?
): String {
    val title: String? = getTitle(
        serviceName = serviceName,
        repository = repository
    )

    return when {
        !title.isNullOrBlank() && !serviceEmoji.isNullOrBlank() -> {
            "$serviceEmoji $title - $workflow"
        }

        !title.isNullOrBlank() -> {
            "$title - $workflow"
        }

        else -> {
            workflow ?: "Something went wrong"
        }
    }
}

private fun getTitle(
    serviceName: String?,
    repository: String?
): String? {
    return if (serviceName.isNullOrBlank()) {
        repository.toTitle()
    } else {
        serviceName
    }
}

private fun String?.toTitle(): String? {
    return this?.split("/")
        ?.last()
        ?.split("-")
        ?.joinToString(" ") { word ->
            word.replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) {
                    firstChar.titlecase()
                } else {
                    firstChar.toString()
                }
            }
        }
}
