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
include(":libcommon-bom")
include(":libcommon-bson")
include(":libcommon-json")
include(":libcommon-json-fastjson2")
include(":libcommon-json-jackson2")
include(":libcommon-json-jackson3")
include(":libcommon-json-jsoniter")
include(":libcommon-jwt")
include(":libcommon-prometheus-client")
include(":libcommon-r2dbc")
include(":libcommon-redis")
include(":libcommon-util")
