plugins {
    `java-library`
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    mavenCentral()
}

dependencies {
    // Netty
    api(platform("io.netty:netty-bom:4.2.12.Final"))
    // MongoDB Driver
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.5"))
    // Junit
    testImplementation(platform("org.junit:junit-bom:6.0.3"))
    // Mockito
    testImplementation(platform("org.mockito:mockito-bom:5.23.0"))
    // Jackson 2
    api(platform("com.fasterxml.jackson:jackson-bom:2.21.2"))
    // Jackson 3
    api(platform("tools.jackson:jackson-bom:3.1.2"))
    // Kotlin Coroutines
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2"))
    // Prometheus
    api(platform("io.prometheus:simpleclient_bom:0.16.0"))
    api(platform("io.prometheus:prometheus-metrics-bom:1.5.1"))
    // Kotlin
    api(platform("org.jetbrains.kotlin:kotlin-bom:2.3.0"))
    // Spring Boot
    api(platform("org.springframework.boot:spring-boot-dependencies:4.0.5"))

    constraints {
        implementation("org.slf4j:slf4j-api:2.0.17")
        implementation("ch.qos.logback:logback-classic:1.5.32")
        api("io.lettuce:lettuce-core:7.5.1.RELEASE")
        api("com.jsoniter:jsoniter:0.9.23")
        val fastjson2Version = "2.0.61"
        api("com.alibaba.fastjson2:fastjson2:$fastjson2Version")
        api("com.alibaba.fastjson2:fastjson2-kotlin:$fastjson2Version")
        val jrubyVersion = "10.0.5.0"
        implementation("org.jruby:jruby-complete:$jrubyVersion")
        implementation("org.jruby:jruby:$jrubyVersion")
        implementation("org.jruby:jruby-core:$jrubyVersion")
        implementation("org.jruby:jruby-stdlib:$jrubyVersion")
        implementation("org.yaml:snakeyaml:2.6")
        api("javax.annotation:javax.annotation-api:1.3.2")
        api("jakarta.annotation:jakarta.annotation-api:3.0.0")
        api("com.google.code.findbugs:jsr305:3.0.2")
        api("com.fasterxml.uuid:java-uuid-generator:5.2.0")
    }
    // log4j2
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.25.4"))

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
