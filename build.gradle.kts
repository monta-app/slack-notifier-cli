plugins {
    kotlin("multiplatform") version "2.3.21"
    kotlin("plugin.serialization") version "2.3.21"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "com.monta.slack.notifier"
version = "1.2.0"

repositories {
    mavenCentral()
}

defaultTasks("hostBinaries")

kotlin {

    val hostOs = System.getProperty("os.name")

    // Host target (always matches the build machine)
    val hostTarget = when {
        hostOs == "Mac OS X" -> macosArm64("host")
        hostOs == "Linux" -> linuxX64("host")
        hostOs.startsWith("Windows") -> mingwX64("host")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    hostTarget.apply {
        binaries {
            executable {
                entryPoint = "com.monta.slack.notifier.main"
            }
        }
    }

    // Cross-compile Linux ARM64 from x64 host
    if (hostOs == "Linux") {
        linuxArm64("linuxArm64") {
            binaries {
                executable {
                    entryPoint = "com.monta.slack.notifier.main"
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                // CLI
                implementation("com.github.ajalt.clikt:clikt:5.1.0")
                // Date Time Support
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1-0.6.x-compat")
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
                // Atomic
                implementation("org.jetbrains.kotlinx:atomicfu:0.32.1")
                // Http Client
                val ktorVersion = "3.4.3"
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
    }
}

kotlin.targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    binaries.all {
        freeCompilerArgs = freeCompilerArgs + "-Xdisable-phases=EscapeAnalysis"
    }
}

val hostOs: String = System.getProperty("os.name")
val isLinux = hostOs == "Linux"

// Task to build both x64 and ARM64 binaries on Linux
if (isLinux) {
    tasks.register("buildAllLinuxBinaries") {
        group = "build"
        description = "Build binaries for both Linux x64 and ARM64"
        dependsOn("linkReleaseExecutableHost", "linkReleaseExecutableLinuxArm64")
    }
}
