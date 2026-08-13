plugins {
    id("libcommon.java-library-conventions")
    id("libcommon.kotlin-library-conventions")
    id("libcommon.publish-conventions")
}

ext["kotlin.stdlib.default.dependency"] = "false"

dependencies {

    implementation("org.slf4j:slf4j-api")
    implementation(project(":libcommon-json-jsoniter"))
    implementation(project(":libcommon-json-fastjson2"))
    implementation(project(":libcommon-util"))
    compileOnly(kotlin("stdlib"))
    compileOnly(project(":libcommon-json-jsoniter-kotlin"))
    compileOnly(project(":libcommon-json-fastjson2-kotlin"))
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    testImplementation("org.mockito:mockito-core")
    testImplementation("io.mockk:mockk")
}

description = "libcommon/JWT"

tasks.test {
    // Use JUnit platform for unit tests.
    useJUnitPlatform()
    jvmArgs = listOf(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off",
        classpath.find { "mockito-core" in it.name }?.let { "-javaagent:${it.absolutePath}" } ?: "",
    )
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("libcommon/JWT")
                description.set("A set of some common useful libraries.")
                url.set("https://github.com/fmjsjx/libcommon")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("fmjsjx")
                        name.set("MJ Fang")
                        email.set("fmjsjx@163.com")
                        url.set("https://github.com/fmjsjx")
                        organization.set("fmjsjx")
                        organizationUrl.set("https://github.com/fmjsjx")
                    }
                }
                scm {
                    url.set("https://github.com/fmjsjx/libcommon")
                    connection.set("scm:git:https://github.com/fmjsjx/libcommon.git")
                    developerConnection.set("scm:git:https://github.com/fmjsjx/libcommon.git")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
