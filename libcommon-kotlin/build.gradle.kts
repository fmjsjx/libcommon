plugins {
    id("libcommon.java-library-conventions")
    id("libcommon.publish-conventions")
    id("libcommon.kotlin-library-conventions")
}

dependencies {
    implementation("org.slf4j:slf4j-api")
    implementation(project(":libcommon-util"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl")
}

description = "libcommon/Kotlin"

tasks.getByName<Test>("test") {
    useJUnitPlatform()
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
                name.set("libcommon/Kotlin")
                description.set("A set of some common useful libraries for kotlin.")
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