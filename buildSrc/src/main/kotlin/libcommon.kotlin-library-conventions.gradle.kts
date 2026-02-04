import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

dependencies {
    // kotlin
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.3.0"))
    // kotlin coroutines
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))

    constraints {
        // reactor-kotlin-extensions
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.3.0")
        // mockk
        testImplementation("io.mockk:mockk:1.14.7")
    }

}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
        freeCompilerArgs.addAll("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
    }
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
    jvmArgs = listOf(
        "-Xshare:off",
        "-XX:+EnableDynamicAgentLoading",
    )
}