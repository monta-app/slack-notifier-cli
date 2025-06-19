package com.monta.slack.notifier

import com.github.ajalt.clikt.core.main
import com.monta.slack.notifier.command.PublishSlackCommand

fun main(args: Array<String>) {
    PublishSlackCommand().main(args)
}
