pluginManagement {
    repositories {
        maven {
            url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "libcommon"
include(":libcommon-aliyunons")
include(":libcommon-bom")
include(":libcommon-bson")
include(":libcommon-collection")
include(":libcommon-example")
include(":libcommon-function")
include(":libcommon-jdbc")
include(":libcommon-json")
include(":libcommon-json-dsljson")
include(":libcommon-json-jackson2")
include(":libcommon-json-jsoniter")
include(":libcommon-kotlin")
include(":libcommon-redis")
include(":libcommon-rocketmq")
include(":libcommon-util")
include(":libcommon-yaml")
