plugins {
    `java-library`
}

repositories {
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    mavenCentral()
}

dependencies {
    // netty-bom
    api(platform("io.netty:netty-bom:4.1.89.Final"))
    // junit-bom
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    // mockito
    testImplementation(platform("org.mockito:mockito-bom:5.1.1"))
    // jackson2-bom
    api(platform("com.fasterxml.jackson:jackson-bom:2.14.2"))
    // rocketmq
    api(platform("org.apache.rocketmq:rocketmq-all:5.1.0"))
    // kotlin coroutines
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.6.4"))
    // prometheus
    api(platform("io.prometheus:simpleclient_bom:0.16.0"))
    // kotlin
    api(platform("org.jetbrains.kotlin:kotlin-bom:1.8.10"))

    constraints {
        implementation("org.slf4j:slf4j-api:2.0.6")
        implementation("ch.qos.logback:logback-classic:1.4.5")
        api("io.lettuce:lettuce-core:6.2.3.RELEASE")
        api("com.dslplatform:dsl-json-java8:1.10.0")
        api("com.jsoniter:jsoniter:0.9.23")
        api("com.aliyun.openservices:ons-client:2.0.5.Final")
        val mongodbVersion = "4.9.0"
        api("org.mongodb:bson:$mongodbVersion")
        api("org.mongodb:mongodb-driver-core:$mongodbVersion")
        api("org.mongodb:mongodb-driver-sync:$mongodbVersion")
        api("org.mongodb:mongodb-driver-reactivestreams:$mongodbVersion")
        api("org.mongodb:mongodb-driver-legacy:$mongodbVersion")
        val jrubyVersion = "9.4.1.0"
        implementation("org.jruby:jruby-complete:$jrubyVersion")
        implementation("org.jruby:jruby:$jrubyVersion")
        implementation("org.jruby:jruby-core:$jrubyVersion")
        implementation("org.jruby:jruby-stdlib:$jrubyVersion")
        implementation("org.yaml:snakeyaml:1.33")
    }
    // log4j2
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.20.0"))

}

val javaVersion = 17

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
    options.memberLevel = JavadocMemberLevel.PUBLIC
}
