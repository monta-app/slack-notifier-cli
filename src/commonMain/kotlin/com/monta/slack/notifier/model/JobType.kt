package com.monta.slack.notifier.model

enum class JobType(
    val label: String,
) {
    Test(
        label = "Test :test_tube:",
    ),
    Build(
        label = "Build :building_construction:️"
    ),
    Deploy(
        label = "Deploy :package:"
    );

    companion object {
        fun fromString(value: String): JobType {
            return JobType.values().find { state ->
                state.name.equals(value, true)
            } ?: throw RuntimeException("Unknown job type $value")
        }

        fun fromLabel(label: String?): JobType? {
            return JobType.values().find { state ->
                state.label.equals(label, true)
            }
        }
    }
}