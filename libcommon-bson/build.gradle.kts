plugins {
    id("libcommon.java-library-conventions")
    id("libcommon.publish-conventions")
}

java {
    registerFeature("generatorSupport") {
        usingSourceSet(sourceSets["main"])
    }
    registerFeature("mongodbSupport") {
        usingSourceSet(sourceSets["main"])
    }
}

dependencies {

    implementation("org.slf4j:slf4j-api")

    api(project(":libcommon-util"))
    api(project(":libcommon-json-jackson2"))
    api(project(":libcommon-json-jsoniter"))

//    api("com.fasterxml.jackson.core:jackson-databind")
//    api("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")
//    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
//    api("com.jsoniter:jsoniter")

    api("org.mongodb:bson")
    api("org.mongodb:mongodb-driver-core")
    "mongodbSupportApi"("org.mongodb:mongodb-driver-sync")
    "mongodbSupportApi"("org.mongodb:mongodb-driver-reactivestreams")

    "generatorSupportImplementation"("org.jruby:jruby")

    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl")

}

description = "libcommon/BSON"

tasks.test {
    // Use junit platform for unit tests.
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
                name.set("libcommon/BSON")
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
