import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

dependencies {
    // kotlin
    val kotlinVersion = "1.9.0"
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))

    constraints {
        // reactor-kotlin-extensions
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")
        // mockk
        testImplementation("io.mockk:mockk:1.13.16")
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
        jvmTarget = JvmTarget.JVM_17
    }
}
