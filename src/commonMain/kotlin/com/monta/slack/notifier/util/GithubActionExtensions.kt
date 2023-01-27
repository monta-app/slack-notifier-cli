package com.monta.slack.notifier.util

import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import platform.posix.EOF
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fputs
import platform.posix.getenv

fun writeToOutput(key: String, value: String) {

    println("Writing to output $key $value")

    val githubOutput = getenv("GITHUB_OUTPUT")?.toKString()

    val file = fopen(githubOutput, "w")

    if (file == null) {
        println("Cannot open output file $githubOutput")
        return
    }

    try {
        memScoped {
            val writeResult = fputs("$key=$value", file)
            if (writeResult == EOF) {
                println("File write error")
            }
        }
    } finally {
        fclose(file)
    }
}