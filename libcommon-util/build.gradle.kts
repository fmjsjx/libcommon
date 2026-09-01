plugins {
    id("libcommon.java-library-conventions")
    id("libcommon.kotlin-library-conventions")
    id("libcommon.publish-conventions")
}

ext["kotlin.stdlib.default.dependency"] = "false"

dependencies {

    implementation("org.slf4j:slf4j-api")

    compileOnlyApi("io.netty:netty-common")
    compileOnlyApi("com.fasterxml.uuid:java-uuid-generator")
    annotationProcessor("org.jspecify:jspecify:1.0.1")

    compileOnlyApi("com.jsoniter:jsoniter")

    compileOnlyApi(kotlin("stdlib"))
    compileOnlyApi("org.jetbrains.kotlin:kotlin-reflect")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-reactive")
    compileOnlyApi("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl")

}

description = "libcommon/Util"

tasks.test {
    // Use JUnit platform for unit tests.
    useJUnitPlatform()
    jvmArgs = listOf(
        "-Xshare:off",
        "-XX:+EnableDynamicAgentLoading",
        "--add-opens",
        "java.base/java.util=ALL-UNNAMED",
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
                name.set("libcommon/Util")
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
