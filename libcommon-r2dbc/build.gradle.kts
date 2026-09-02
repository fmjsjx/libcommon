plugins {
    id("libcommon.java-library-conventions")
    id("libcommon.kotlin-library-conventions")
    id("libcommon.publish-conventions")
}

dependencies {

    implementation("org.slf4j:slf4j-api")
    compileOnly("com.google.code.findbugs:jsr305")
    implementation(project(":libcommon-util"))
    api("org.springframework.data:spring-data-r2dbc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl")
    testImplementation("org.mockito:mockito-core")
    testImplementation("io.mockk:mockk")
    testImplementation("io.kotest:kotest-assertions-core")
}

description = "libcommon/R2DBC"

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
            suppressAllPomMetadataWarnings()
            pom {
                name.set("libcommon/R2DBC")
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
