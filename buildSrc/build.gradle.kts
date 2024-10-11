plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    gradlePluginPortal()
}

dependencies {
    val kotlinVersion = "1.9.0"
    implementation(plugin("org.jetbrains.kotlin.jvm", kotlinVersion))
    implementation(plugin("org.jetbrains.kotlin.kapt", kotlinVersion))
}

fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"
