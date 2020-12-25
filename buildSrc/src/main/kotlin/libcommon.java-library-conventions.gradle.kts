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
