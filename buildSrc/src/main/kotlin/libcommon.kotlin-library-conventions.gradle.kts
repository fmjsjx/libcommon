import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

dependencies {
    // kotlin
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.4.10"))
    // kotlin coroutines
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.11.0"))

    // Kotest
    testImplementation(platform("io.kotest:kotest-bom:6.2.4"))

    constraints {
        // reactor-kotlin-extensions
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.3.0")
        // mockk
        testImplementation("io.mockk:mockk:1.14.9")
    }

}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.addAll("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.test {
    // Use JUnit platform for unit tests.
    useJUnitPlatform()
    jvmArgs = listOf(
        "-Xshare:off",
        "-XX:+EnableDynamicAgentLoading",
    )
}