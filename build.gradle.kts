plugins {
    kotlin("multiplatform") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"
    id("io.kotest.multiplatform") version "5.9.1"
    id("org.jlleitschuh.gradle.ktlint") version "12.3.0"
}

group = "com.monta.slack.notifier"
version = "1.2.0"

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
                implementation("com.github.ajalt.clikt:clikt:5.0.3")
                // Date Time Support
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
                // Serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
                // Atomic
                implementation("org.jetbrains.kotlinx:atomicfu:0.28.0")
                // Http Client
                val ktorVersion = "3.2.0"
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
