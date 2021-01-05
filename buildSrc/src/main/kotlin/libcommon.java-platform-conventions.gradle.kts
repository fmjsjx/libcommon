plugins {
    `java-platform`
}

group = "com.github.fmjsjx"
version = "1.0.0-SNAPSHOT"

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
