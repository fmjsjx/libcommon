# LibCommon Project

A set of some common useful libraries.

> Since 3.0, members of libraries always based on JDK 17.


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
        <version>3.13.0</version>
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
    <!-- JSON library based on Jackson2 -->
    <dependency>
      <groupId>com.github.fmjsjx</groupId>
      <artifactId>libcommon-json-jackson2</artifactId>
    </dependency>
    <!-- JSON library based on Json-Iter -->
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
    implementation platform('com.github.fmjsjx:libcommon-bom:3.13.0')
    // Common Utility
    implementation 'com.github.fmjsjx:libcommon-util'
    // JSON library based on Fastjson2
    implementation 'com.github.fmjsjx:libcommon-json-fastjson2'
    // JSON library based on Jackson2
    implementation 'com.github.fmjsjx:libcommon-json-jackson2'
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
    implementation(platform("com.github.fmjsjx:libcommon-bom:3.13.0"))
    // Common Utility
    implementation("com.github.fmjsjx:libcommon-util")
    // JSON library based on Fastjson2
    implementation("com.github.fmjsjx:libcommon-json-fastjson2")
    // JSON library based on Jackson2
    implementation("com.github.fmjsjx:libcommon-json-jackson2")
    // JSON library based on Json-Iter
    implementation("com.github.fmjsjx:libcommon-json-jsoniter")
}
```

## Modules

There are a number of modules in LibCommon, here is a quick overview:

### libcommon-util

The [`libcommon-util`](libcommon-util) module provides some common utility classes.

### libcommon-collection

The [`libcommon-collection`](libcommon-collection) module provides additional collection/map.

### libcommon-function

The [`libcommon-function`](libcommon-function) module provides additional functional interfaces.

### libcommon-json

The [`libcommon-json`](libcommon-json) module provides a library interface to encode/decode JSON.

### libcommon-json-dsljson

The [`libcommon-json-dsljson`](libcommon-json-dsljson) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`DSL-JSON`](https://github.com/ngs-doo/dsl-json).

### libcommon-json-fastjson2

The [`libcommon-json-fastjson2`](libcommon-json-fastjson2) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`Fastjson2`](https://github.com/alibaba/fastjson2).

### libcommon-json-fastjson2-kotlin

The [`libcommon-json-fastjson2-kotlin`](libcommon-json-fastjson2-kotlin) module provides the kotlin extensions of [`libcommon-json-fastjson2`](libcommon-json-fastjson2).

### libcommon-json-jackson2

The [`libcommon-json-jackson2`](libcommon-json-jackson2) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`Jackson2`](https://github.com/FasterXML/jackson).

### libcommon-json-jackson2-kotlin

The [`libcommon-json-jackson2-kotlin`](libcommon-json-jackson2-kotlin) module provides the kotlin extensions of [`libcommon-json-jackson2`](libcommon-json-jackson2).

### libcommon-json-jsoniter

The [`libcommon-json-jsoniter`](libcommon-json-jsoniter) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`json-iterator`](https://jsoniter.com/).

### libcommon-json-jsoniter-kotlin

The [`libcommon-json-jsoniter-kotlin`](libcommon-json-jsoniter-kotlin) module provides the kotlin extensions of [`libcommon-json-jsoniter`](libcommon-json-jsoniter).

### libcommon-jwt

The [`libcommon-jwt`](libcommon-jwt) module provides a library to build/parse JWTs.

### libcommon-kotlin

The [`libcommon-kotlin`](libcommon-kotlin) module provides some utilitiy class for [**Kotlin**](https://kotlinlang.org/).

### libcommon-prometheus (legacy)

The [`libcommon-prometheus`](libcommon-prometheus) module provides Hotspot JVM metrics exports, that allow users set custom labels, based on [**prometheus simple client**](https://github.com/prometheus/client_java/tree/simpleclient).

### libcommon-prometheus-client

The [`libcommon-prometheus-client`](libcommon-prometheus-client) module provides Hotspot JVM metrics exports, that allow users set custom labels, based on [**prometheus client java**](https://prometheus.github.io/client_java/).

### libcommon-yaml

The [`libcommon-yaml`](libcommon-yaml) module provides a library interface to encode/decode YAML, with the default implementation based on [`Jackson2`](https://github.com/FasterXML/jackson).

### libcommon-redis

The [`libcommon-redis`](libcommon-redis) module provides additional utility functions for [`Lettuce`](https://lettuce.io/).

### libcommon-redis-kotlin

The [`libcommon-redis-kotlin`](libcommon-redis-kotlin) module provides the kotlin extensions of [`Lettuce`](https://lettuce.io/).

### libcommon-jdbc

The [`libcommon-util`](libcommon-util) module provides additional utility functions for JDBC.

### libcommon-aliyunons

The [`libcommon-aliyunons`](libcommon-aliyunons) module provides additional utility functions for [`ALIYUN Open Services/RocketMQ`](https://help.aliyun.com/product/29530.html).

### libcommon-rocketmq

The [`libcommon-rocketmq`](libcommon-rocketmq) module provides additional utility functions for [`RocketMQ`](https://rocketmq.apache.org/).

### libcommon-bson

The [`libcommon-bson`](libcommon-bson) module provides some additional utility functions for [`BSON`](https://www.mongodb.com/basics/bson).

### libcommon-bson-kotlin

The [`libcommon-bson-kotlin`](libcommon-bson-kotlin) module provides some additional kotlin extensions for [`BSON`](https://www.mongodb.com/basics/bson).

### libcommon-uuid

The [`libcommon-uuid`](libcommon-uuid) module provides additional implementations of `UUID`.
