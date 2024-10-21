plugins {
    kotlin("multiplatform") version "1.9.25"
    kotlin("plugin.serialization") version "1.9.25"
    id("io.kotest.multiplatform") version "5.9.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.monta.slack.notifier"
version = "1.1.3"

repositories {
    mavenCentral()
}

defaultTasks("commonBinaries")

kotlin {

    val hostOs = System.getProperty("os.name")
    val hostArch = System.getProperty("os.arch")

    // Cross Compilation
    val commonTarget = when {
        hostOs == "Mac OS X" -> macosArm64("common")
        hostOs == "Linux" && hostArch == "aarch64" -> linuxArm64("common")
        hostOs == "Linux" && hostArch == "amd64" -> linuxX64("common")
        hostOs.startsWith("Windows") -> mingwX64("common")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    commonTarget.apply {
        binaries {
            executable {
                entryPoint = "com.monta.slack.notifier.main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // CLI
                implementation("com.github.ajalt.clikt:clikt:4.4.0")
                // Date Time Support
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                // Atomic
                implementation("org.jetbrains.kotlinx:atomicfu:0.26.0")
                // Http Client
                val ktorVersion = "2.3.12"
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-curl:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                val kotestVersion = "5.9.1"
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            }
        }
    }
}

kotlin.targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    binaries.all {
        freeCompilerArgs = freeCompilerArgs + "-Xdisable-phases=EscapeAnalysis"
    }
}
