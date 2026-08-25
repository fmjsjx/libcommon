plugins {
    `java-library`
}

repositories {
    exclusiveContent {
        forRepositories(
            maven { url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/") },
            mavenCentral(),
        )
        filter {
            includeGroupByRegex(".*")
        }
    }
}

dependencies {
    // Netty
    api(platform("io.netty:netty-bom:4.2.17.Final"))
    // MongoDB Driver
    implementation(platform("org.mongodb:mongodb-driver-bom:5.9.1"))
    // Junit
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    // Mockito
    testImplementation(platform("org.mockito:mockito-bom:5.23.0"))
    // Jackson 2
    api(platform("com.fasterxml.jackson:jackson-bom:2.22.1"))
    // Jackson 3
    api(platform("tools.jackson:jackson-bom:3.2.1"))
    // Kotlin Coroutines
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.11.0"))
    // Prometheus
    api(platform("io.prometheus:simpleclient_bom:0.16.0"))
    api(platform("io.prometheus:prometheus-metrics-bom:1.8.0"))
    // Kotlin
    api(platform("org.jetbrains.kotlin:kotlin-bom:2.4.10"))
    // Spring Boot
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.7"))
    // assertj
    testImplementation(platform("org.assertj:assertj-bom:3.27.7"))

    constraints {
        implementation("org.slf4j:slf4j-api:2.0.18")
        implementation("ch.qos.logback:logback-classic:1.6.1")
        api("io.lettuce:lettuce-core:7.6.0.RELEASE")
        api("com.jsoniter:jsoniter:0.9.23")
        val fastjson2Version = "2.0.62"
        api("com.alibaba.fastjson2:fastjson2:$fastjson2Version")
        api("com.alibaba.fastjson2:fastjson2-kotlin:$fastjson2Version")
        val jrubyVersion = "10.1.1.0"
        implementation("org.jruby:jruby-complete:$jrubyVersion")
        implementation("org.jruby:jruby:$jrubyVersion")
        implementation("org.jruby:jruby-core:$jrubyVersion")
        implementation("org.jruby:jruby-stdlib:$jrubyVersion")
        implementation("org.yaml:snakeyaml:2.6")
        api("javax.annotation:javax.annotation-api:1.3.2")
        api("jakarta.annotation:jakarta.annotation-api:3.0.0")
        api("com.google.code.findbugs:jsr305:3.0.2")
        api("com.fasterxml.uuid:java-uuid-generator:5.2.0")
        api("org.jspecify:jspecify:1.0.1")
    }
    // log4j2
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.26.1"))

}

val javaVersion = 21

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    testImplementation {
        extendsFrom(
            configurations.compileOnly.get(),
            configurations.compileOnlyApi.get(),
        )
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = javaVersion
    options.compilerArgs = options.compilerArgs + listOf("-Xlint:deprecation")
}

tasks.withType<Javadoc> {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
    options.memberLevel = JavadocMemberLevel.PUBLIC
}
