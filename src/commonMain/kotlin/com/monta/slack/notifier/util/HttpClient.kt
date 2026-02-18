package com.monta.slack.notifier.util

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.native.OsFamily
import kotlin.native.Platform
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val client by lazy {
    HttpClient(Curl) {
        engine {
            // Explicitly set CA bundle path on Linux to fix TLS verification
            // for cross-compiled ARM64 binaries where the statically linked
            // libcurl may have an incorrect default CA path from the build sysroot
            if (Platform.osFamily == OsFamily.LINUX) {
                caInfo = "/etc/ssl/certs/ca-certificates.crt"
            }
        }
        expectSuccess = false
        install(ContentNegotiation) {
            json(
                Json {
                    explicitNulls = false
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    }
}
