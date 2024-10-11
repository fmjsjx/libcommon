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
    api(platform("io.netty:netty-bom:4.1.114.Final"))
    // junit-bom
    testImplementation(platform("org.junit:junit-bom:5.11.2"))
    // mockito
    testImplementation(platform("org.mockito:mockito-bom:5.14.1"))
    // jackson2-bom
    api(platform("com.fasterxml.jackson:jackson-bom:2.18.0"))
    // rocketmq
    api(platform("org.apache.rocketmq:rocketmq-all:5.3.1"))
    // kotlin coroutines
    api(platform("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.9.0"))
    // prometheus
    api(platform("io.prometheus:simpleclient_bom:0.16.0"))
    // kotlin
    api(platform("org.jetbrains.kotlin:kotlin-bom:1.9.0"))

    constraints {
        implementation("org.slf4j:slf4j-api:2.0.16")
        implementation("ch.qos.logback:logback-classic:1.5.9")
        api("io.lettuce:lettuce-core:6.4.0.RELEASE")
        api("com.dslplatform:dsl-json-java8:1.10.0")
        api("com.jsoniter:jsoniter:0.9.23")
        api("com.aliyun.openservices:ons-client:2.0.7.Final")
        val fastjson2Version = "2.0.53"
        api("com.alibaba.fastjson2:fastjson2:$fastjson2Version")
        api("com.alibaba.fastjson2:fastjson2-kotlin:$fastjson2Version")
        val mongodbVersion = "5.2.0"
        api("org.mongodb:bson:$mongodbVersion")
        api("org.mongodb:mongodb-driver-core:$mongodbVersion")
        api("org.mongodb:mongodb-driver-sync:$mongodbVersion")
        api("org.mongodb:mongodb-driver-reactivestreams:$mongodbVersion")
        api("org.mongodb:mongodb-driver-legacy:$mongodbVersion")
        val jrubyVersion = "9.4.8.0"
        implementation("org.jruby:jruby-complete:$jrubyVersion")
        implementation("org.jruby:jruby:$jrubyVersion")
        implementation("org.jruby:jruby-core:$jrubyVersion")
        implementation("org.jruby:jruby-stdlib:$jrubyVersion")
        implementation("org.yaml:snakeyaml:2.2")
        val prometheusVersion = "1.3.1"
        api("io.prometheus:prometheus-metrics-core:$prometheusVersion")
        api("io.prometheus:prometheus-metrics-model:$prometheusVersion")
        api("io.prometheus:prometheus-metrics-tracer:$prometheusVersion")
        api("io.prometheus:prometheus-metrics-exposition-formats:$prometheusVersion")
        api("io.prometheus:prometheus-metrics-instrumentation-jvm:$prometheusVersion")
    }
    // log4j2
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.24.1"))

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
