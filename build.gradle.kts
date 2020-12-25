import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

group = "com.sunykarasuno"
version = "1.0.0"

plugins {
    kotlin("jvm") version Versions.KOTLIN
    id(Dependencies.Plugins.SHADOW) version Versions.SHADOW
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

ktlint {
    version.set("0.40.0")
    reporters {
        reporter(ReporterType.JSON)
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("buildSrc/"))
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    // Rx
    implementation(Dependencies.Bot.RX_JAVA)
    implementation(Dependencies.Bot.RX_KOTLIN)
    implementation(Dependencies.Bot.RX_RELAY)

    // Networking and Authentication
    implementation(Dependencies.Bot.RETROFIT)
    implementation(Dependencies.Bot.SCRIBE)

    // Cron
    implementation(Dependencies.Bot.QUARTZ)

    // Database
    implementation(Dependencies.Bot.POSTGRES)
    implementation(Dependencies.Bot.EXPOSED_CORE)
    implementation(Dependencies.Bot.EXPOSED_JDBC)
    implementation(Dependencies.Bot.EXPOSED_DAO)
}
