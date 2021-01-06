plugins {
    `java-library`
    `maven-publish`
    signing
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
	jcenter()
}

group = "com.github.fmjsjx"
version = "1.0.0.M1"

dependencies {
	// netty-bom
    api(platform("io.netty:netty-bom:4.1.56.Final"))
	// junit-bom
	testImplementation(platform("org.junit:junit-bom:5.7.0"))
	// jackson2-bom
	api(platform("com.fasterxml.jackson:jackson-bom:2.11.4"))

    constraints {
        implementation("org.slf4j:slf4j-api:1.7.30")
        implementation("ch.qos.logback:logback-classic:1.2.3")
        api("io.lettuce:lettuce-core:6.0.1.RELEASE")
        api("com.dslplatform:dsl-json-java8:1.9.7")
        api("com.jsoniter:jsoniter:0.9.23")
    }
    // log4j2
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.0"))

}

java {
    withSourcesJar()
	withJavadocJar()
	toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
	options.release.set(11)
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
                name.set(project.getDescription())
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
            credentials(PasswordCredentials::class)
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

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}