pluginManagement {
    repositories {
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        }
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "libcommon"
include(":libcommon-aliyunons")
include(":libcommon-bom")
include(":libcommon-bson")
include(":libcommon-bson-kotlin")
include(":libcommon-collection")
include(":libcommon-function")
include(":libcommon-jdbc")
include(":libcommon-json")
include(":libcommon-json-dsljson")
include(":libcommon-json-jackson2")
include(":libcommon-json-jackson2-kotlin")
include(":libcommon-json-jsoniter")
include(":libcommon-json-jsoniter-kotlin")
include(":libcommon-kotlin")
include(":libcommon-prometheus")
include(":libcommon-redis")
include(":libcommon-redis-kotlin")
include(":libcommon-rocketmq")
include(":libcommon-util")
include(":libcommon-yaml")
