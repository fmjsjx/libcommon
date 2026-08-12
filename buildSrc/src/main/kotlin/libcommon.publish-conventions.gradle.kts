plugins {
    `maven-publish`
    signing
}

group = "com.github.fmjsjx"
version = "4.3.0-SNAPSHOT"

// Prevent Gradle from generating high-order checksums (like sha256/sha512).
// This restricts the output to only standard md5 and sha1.
System.setProperty("org.gradle.internal.publish.checksums.official", "true")

// Completely disable the Gradle Module Metadata (.module) generation task.
// This instantly eliminates the .module file, its .asc signature, and all associated checksums.
tasks.withType<GenerateModuleMetadata>().configureEach { enabled = false }

publishing {
    repositories {
        maven {
            url = uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

// Security guard: Ensures insecure/extra checksums are intercepted during the local repository generation phase
tasks.withType<PublishToMavenRepository>().configureEach {
    System.setProperty("org.gradle.internal.publish.checksums.insecure", "true")
}