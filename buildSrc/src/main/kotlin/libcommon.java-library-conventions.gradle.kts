plugins {
    `java-library`
}

repositories {
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    jcenter()
}

dependencies {
    // netty-bom
    api(platform("io.netty:netty-bom:4.1.59.Final"))
    // junit-bom
    testImplementation(platform("org.junit:junit-bom:5.7.0"))
    // jackson2-bom
    api(platform("com.fasterxml.jackson:jackson-bom:2.12.1"))
    // rocketmq
    api(platform("org.apache.rocketmq:rocketmq-all:4.8.0"))

    constraints {
        implementation("org.slf4j:slf4j-api:1.7.30")
        implementation("ch.qos.logback:logback-classic:1.2.3")
        api("io.lettuce:lettuce-core:6.0.2.RELEASE")
        api("com.dslplatform:dsl-json-java8:1.9.8")
        api("com.jsoniter:jsoniter:0.9.23")
        api("com.aliyun.openservices:ons-client:1.8.7.3.Final")
        api("org.mongodb:bson:4.2.3")
        api("org.mongodb:mongodb-driver-core:4.2.3")
        implementation("org.mongodb:mongodb-driver-sync:4.2.3")
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

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}
