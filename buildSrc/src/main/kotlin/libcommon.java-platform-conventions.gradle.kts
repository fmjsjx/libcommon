plugins {
    `java-platform`
    `maven-publish`
    signing
}

group = "com.github.fmjsjx"
version = "1.0.0.M1"

dependencies {
	constraints {
        api(project(":libcommon-collection"))
        api(project(":libcommon-json"))
        api(project(":libcommon-json-dsljson"))
        api(project(":libcommon-json-jackson2"))
        api(project(":libcommon-json-jsoniter"))
        api(project(":libcommon-redis"))
        api(project(":libcommon-util"))
    }
}

publishing {
    publications {
    	create<MavenPublication>("mavenJava") {
        	from(components["javaPlatform"])
            pom {
                name.set(project.description)
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
    repositories {
        maven {
            name = "sonatypeOss"
            credentials(PasswordCredentials)
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["mavenJava"])
}