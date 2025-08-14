import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
}

dependencies {
    // kotlin
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:2.1.0"))
    // kotlin coroutines
    implementation(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))

    constraints {
        // reactor-kotlin-extensions
        implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.3")
        // mockk
        testImplementation("io.mockk:mockk:1.14.4")
    }

}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.addAll("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
    }
}
