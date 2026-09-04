# LibCommon Project

A set of some common useful libraries.

## JDK version compatibility list
| version | JDK version |
|---------|-------------|
| 4.x     | JDK 21+     |
| 3.x     | JDK 17+     |
| older   | JDK 11+     |

## Add Dependencies

### Add Maven Dependencies
```xml
<pom>
  <dependencyManagement>
    <dependencies>
      <!-- BOM -->
      <dependency>
        <groupId>com.github.fmjsjx</groupId>
        <artifactId>libcommon-bom</artifactId>
        <version>4.3.0-RC4-SNAPSHOT</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    <!-- Common Utility -->
    <dependency>
      <groupId>com.github.fmjsjx</groupId>
      <artifactId>libcommon-util</artifactId>
    </dependency>
    <!-- JSON library based on Jackson3 -->
    <dependency>
      <groupId>com.github.fmjsjx</groupId>
      <artifactId>libcommon-json-jackson3</artifactId>
    </dependency>
    <!-- JSON library based on Jsoniter -->
    <dependency>
      <groupId>com.github.fmjsjx</groupId>
      <artifactId>libcommon-json-jsoniter</artifactId>
    </dependency>
  </dependencies>
</pom>
```

### Add Gradle Dependencies

#### Groovy DSL
```groovy
repositories {
    mavenCentral
}

dependencies {
    // BOM
    implementation platform('com.github.fmjsjx:libcommon-bom:4.3.0-RC4-SNAPSHOT')
    // Common Utility
    implementation 'com.github.fmjsjx:libcommon-util'
    // JSON library based on Fastjson2
    implementation 'com.github.fmjsjx:libcommon-json-fastjson2'
    // JSON library based on Jackson3
    implementation 'com.github.fmjsjx:libcommon-json-jackson3'
    // JSON library based on Json-Iter
    implementation 'com.github.fmjsjx:libcommon-json-jsoniter'
}
```
#### Kotlin DSL
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    // BOM
    implementation(platform("com.github.fmjsjx:libcommon-bom:4.3.0-RC4-SNAPSHOT"))
    // Common Utility
    implementation("com.github.fmjsjx:libcommon-util")
    // JSON library based on Fastjson2
    implementation("com.github.fmjsjx:libcommon-json-fastjson2")
    // JSON library based on Jackson3
    implementation("com.github.fmjsjx:libcommon-json-jackson3")
    // JSON library based on Json-Iter
    implementation("com.github.fmjsjx:libcommon-json-jsoniter")
}
```

## Modules

There are a number of modules in LibCommon, here is a quick overview:

### libcommon-util

The [`libcommon-util`](libcommon-util) module provides some common utility classes, additional functional interfaces, additional collection/map utilities.

### libcommon-json

The [`libcommon-json`](libcommon-json) module provides a library interface to encode/decode JSON.

### libcommon-json-fastjson2

The [`libcommon-json-fastjson2`](libcommon-json-fastjson2) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`Fastjson2`](https://github.com/alibaba/fastjson2).

### libcommon-json-jackson2

The [`libcommon-json-jackson2`](libcommon-json-jackson2) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`Jackson2`](https://github.com/FasterXML/jackson).

### libcommon-json-jackson3

The [`libcommon-json-jackson3`](libcommon-json-jackson3) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`Jackson3`](https://github.com/FasterXML/jackson).

### libcommon-json-jsoniter

The [`libcommon-json-jsoniter`](libcommon-json-jsoniter) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`json-iterator`](https://jsoniter.com/).

### libcommon-jwt

The [`libcommon-jwt`](libcommon-jwt) module provides a library to build/parse JWTs.

### libcommon-prometheus-client

The [`libcommon-prometheus-client`](libcommon-prometheus-client) module provides Hotspot JVM metrics exports, that allow users set custom labels, based on [**prometheus client java**](https://prometheus.github.io/client_java/).

### libcommon-redis

The [`libcommon-redis`](libcommon-redis) module provides additional utility functions and optional kotlin extensions for [`Lettuce`](https://lettuce.io/).

### libcommon-bson

The [`libcommon-bson`](libcommon-bson) module provides some additional utility functions and optional kotlin extensions for [`BSON`](https://www.mongodb.com/basics/bson).
