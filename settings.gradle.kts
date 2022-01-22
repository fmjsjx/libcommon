pluginManagement {
    repositories {
        maven(url = "https://maven.aliyun.com/repository/gradle-plugin")
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
include(":libcommon-redis")
include(":libcommon-rocketmq")
include(":libcommon-util")
include(":libcommon-yaml")
