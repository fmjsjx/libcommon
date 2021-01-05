plugins {
    id("libcommon.java-library-conventions")
}

dependencies {

    implementation("org.slf4j:slf4j-api")
    
    api(project(":libcommon-json"))
    
    api("com.jsoniter:jsoniter")

    testImplementation("org.junit.jupiter:junit-jupiter-api")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl")

}

description = "libcommon/JSON Jsoniter"

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}
