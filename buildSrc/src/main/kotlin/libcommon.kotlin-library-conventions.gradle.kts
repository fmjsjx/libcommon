import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    // kotlin
    val kotlinVersion = "1.8.10"
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))

    constraints {
        // reactor-kotlin-extensions
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.1")
        // mockk
        testImplementation("io.mockk:mockk:1.13.4")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
        jvmTarget = "17"
    }
}
