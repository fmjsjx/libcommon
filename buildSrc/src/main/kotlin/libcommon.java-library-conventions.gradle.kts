plugins {
    `java-library`
}

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
	jcenter()
}

group = "com.github.fmjsjx"
version = "1.0.0-SNAPSHOT"

dependencies {
	// netty-bom
    implementation(platform("io.netty:netty-bom:4.1.56.Final"))
	// junit-bom
	testImplementation(platform("org.junit:junit-bom:5.7.0"))

    constraints {
        implementation("org.slf4j:slf4j-api:1.7.30")
        implementation("ch.qos.logback:logback-classic:1.2.3")
        api("io.lettuce:lettuce-core:6.0.1.RELEASE")
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
