plugins {
    `java-platform`
    id("libcommon.publish-conventions")
}

description = "libcommon/BOM"

dependencies {
    constraints {
        api(project(":libcommon-aliyunons"))
        api(project(":libcommon-bson"))
        api(project(":libcommon-bson-kotlin"))
        api(project(":libcommon-collection"))
        api(project(":libcommon-function"))
        api(project(":libcommon-jdbc"))
        api(project(":libcommon-json"))
        api(project(":libcommon-json-dsljson"))
        api(project(":libcommon-json-fastjson2"))
        api(project(":libcommon-json-fastjson2-kotlin"))
        api(project(":libcommon-json-jackson2"))
        api(project(":libcommon-json-jackson2-kotlin"))
        api(project(":libcommon-json-jsoniter"))
        api(project(":libcommon-json-jsoniter-kotlin"))
        api(project(":libcommon-jwt"))
        api(project(":libcommon-jwt-kotlin"))
        api(project(":libcommon-kotlin"))
        api(project(":libcommon-prometheus"))
        api(project(":libcommon-prometheus-client"))
        api(project(":libcommon-r2dbc"))
        api(project(":libcommon-r2dbc-kotlin"))
        api(project(":libcommon-redis"))
        api(project(":libcommon-redis-kotlin"))
        api(project(":libcommon-rocketmq"))
        api(project(":libcommon-util"))
        api(project(":libcommon-util-kotlin"))
        api(project(":libcommon-uuid"))
        api(project(":libcommon-yaml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["javaPlatform"])
            pom {
                name.set("libcommon/BOM")
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
}

signing {
    sign(publishing.publications["mavenJava"])
}
