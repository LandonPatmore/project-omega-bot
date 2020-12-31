object Dependencies {

    object Bot {
        const val RX_JAVA = "io.reactivex.rxjava3:rxjava:${Versions.RX_JAVA}"
        const val RX_KOTLIN = "io.reactivex.rxjava3:rxkotlin:${Versions.RX_KOTLIN}"
        const val RX_RELAY = "com.jakewharton.rxrelay3:rxrelay:${Versions.RX_RELAY}"
        const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
        const val QUARTZ = "org.quartz-scheduler:quartz:${Versions.QUARTZ}"
        const val SCRIBE = "com.github.scribejava:scribejava-apis:${Versions.SCRIBE}"
        const val POSTGRES = "org.postgresql:postgresql:${Versions.POSTGRES}"
        const val EXPOSED_CORE = "org.jetbrains.exposed:exposed-core:${Versions.EXPOSED}"
        const val EXPOSED_DAO = "org.jetbrains.exposed:exposed-dao:${Versions.EXPOSED}"
        const val EXPOSED_JDBC = "org.jetbrains.exposed:exposed-jdbc:${Versions.EXPOSED}"
        const val OK_HTTP = "com.squareup.okhttp3:okhttp:${Versions.OK_HTTP}"
        const val RETROFIT_GSON = "com.squareup.retrofit2:converter-gson:${Versions.RETROFIT}"
        const val LOGGING = "io.github.microutils:kotlin-logging-jvm:${Versions.LOGGING}"
        const val SLF4J = "org.slf4j:slf4j-simple:${Versions.SLF4J}"
        const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN_REFLECT}"
    }

    object Plugins {
        const val SHADOW = "com.github.johnrengelman.shadow"
        const val KTLINT = "org.jlleitschuh.gradle.ktlint"
    }
}
