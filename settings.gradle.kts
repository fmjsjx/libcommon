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
include(":libcommon-bson-kotlin")
include(":libcommon-collection")
include(":libcommon-function")
include(":libcommon-jdbc")
include(":libcommon-json")
include(":libcommon-json-dsljson")
include(":libcommon-json-fastjson2")
include(":libcommon-json-fastjson2-kotlin")
include(":libcommon-json-jackson2")
include(":libcommon-json-jackson2-kotlin")
include(":libcommon-json-jackson3")
include(":libcommon-json-jackson3-kotlin")
include(":libcommon-json-jsoniter")
include(":libcommon-json-jsoniter-kotlin")
include(":libcommon-jwt")
include(":libcommon-jwt-kotlin")
include(":libcommon-kotlin")
include(":libcommon-prometheus")
include(":libcommon-prometheus-client")
include(":libcommon-r2dbc")
include(":libcommon-r2dbc-kotlin")
include(":libcommon-redis")
include(":libcommon-redis-kotlin")
include(":libcommon-util")
include(":libcommon-util-kotlin")
include(":libcommon-uuid")
include(":libcommon-yaml")
