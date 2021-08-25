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
    api(platform("io.netty:netty-bom:4.1.67.Final"))
    // junit-bom
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    // jackson2-bom
    api(platform("com.fasterxml.jackson:jackson-bom:2.12.4"))
    // rocketmq
    api(platform("org.apache.rocketmq:rocketmq-all:4.9.1"))

    constraints {
        implementation("org.slf4j:slf4j-api:1.7.32")
        implementation("ch.qos.logback:logback-classic:1.2.5")
        api("io.lettuce:lettuce-core:6.1.4.RELEASE")
        api("com.dslplatform:dsl-json-java8:1.9.8")
        api("com.jsoniter:jsoniter:0.9.23")
        api("com.aliyun.openservices:ons-client:1.8.8.1.Final")
        api("org.mongodb:bson:4.3.1")
        api("org.mongodb:mongodb-driver-core:4.3.1")
        api("org.mongodb:mongodb-driver-sync:4.3.1")
        api("org.mongodb:mongodb-driver-reactivestreams:4.3.1")
        api("org.mongodb:mongodb-driver-legacy:4.3.1")
        implementation("org.jruby:jruby-complete:9.2.19.0")
        implementation("org.jruby:jruby:9.2.19.0")
        implementation("org.jruby:jruby-core:9.2.19.0")
        implementation("org.jruby:jruby-stdlib:9.2.19.0")
        implementation("org.yaml:snakeyaml:1.29")
    }
    // log4j2
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.14.1"))

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

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
