plugins {
    id("libcommon.java-library-conventions")
}

dependencies {

    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

}

description = "libcommon/Util"

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}
