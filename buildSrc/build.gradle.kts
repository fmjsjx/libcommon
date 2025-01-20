plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    gradlePluginPortal()
}

dependencies {
    implementation(plugin("org.jetbrains.kotlin.jvm"))
    implementation(plugin("org.jetbrains.kotlin.kapt"))
}

fun plugin(id: String, version: String = embeddedKotlinVersion) = "$id:$id.gradle.plugin:$version"
