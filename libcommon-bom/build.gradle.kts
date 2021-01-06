plugins {
    id("libcommon.java-platform-conventions")
}

description = "libcommon/BOM"

dependencies {
	constraints {
        api(project(":libcommon-collection"))
        api(project(":libcommon-json"))
        api(project(":libcommon-json-dsljson"))
        api(project(":libcommon-json-jackson2"))
        api(project(":libcommon-json-jsoniter"))
        api(project(":libcommon-redis"))
        api(project(":libcommon-util"))
    }
}