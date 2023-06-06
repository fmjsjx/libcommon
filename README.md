# LibCommon Project

A set of some common useful libraries.

> Since 3.0, members of libraries always based on JDK 17.


## Add Dependencies

### Add Maven Dependencies
`pom.xml`
```
<pom>
  <dependencyManagement>
    <dependencies>
      <!-- BOM -->
      <dependency>
        <groupId>com.github.fmjsjx</groupId>
        <artifactId>libcommon-bom</artifactId>
        <version>3.1.0-RC1</version>
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
```

repositories {
    mavenCentral
}

dependencies {
    // BOM
    implementation platform('com.github.fmjsjx:libcommon-bom:3.1.0-RC1')
    // Common Utility
    implementation 'com.github.fmjsjx:libcommon-util'
    // JSON library based on Jackson2
    implementation 'com.github.fmjsjx:libcommon-json-jackson2'
    // JSON library based on Json-Iter
    implementation 'com.github.fmjsjx:libcommon-json-jsoniter'
}
```
#### Kotlin DSL
```

repositories {
    mavenCentral()
}

dependencies {
    // BOM
    implementation(platform("com.github.fmjsjx:libcommon-bom:3.1.0-RC1"))
    // Common Utility
    implementation("com.github.fmjsjx:libcommon-util")
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

### libcommon-json-jackson2

The [`libcommon-json-jackson2`](libcommon-json-jackson2) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`Jackson2`](https://github.com/FasterXML/jackson).

### libcommon-json-jsoniter

The [`libcommon-json-jsoniter`](libcommon-json-jsoniter) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`json-iterator`](https://jsoniter.com/).

### libcommon-json-dsljson

The [`libcommon-json-dsljson`](libcommon-json-dsljson) module provides an implementation of [`libcommon-json`](libcommon-json) based on [`DSL-JSON`](https://github.com/ngs-doo/dsl-json).

### libcommon-kotlin

The [`libcommon-kotlin`](libcommon-kotlin) module provides some utilitiy class for [**Kotlin**](https://kotlinlang.org/).

### libcommon-prometheus

The [`libcommon-prometheus`](libcommon-prometheus) module provides Hotspot JVM metrics exports, that allow users set custom labels, based on [**prometheus**](https://prometheus.io).

### libcommon-yaml

The [`libcommon-yaml`](libcommon-yaml) module provides a library interface to encode/decode YAML and it's default implementation based on [`Jackson2`](https://github.com/FasterXML/jackson).

### libcommon-redis

The [`libcommon-redis`](libcommon-redis) module provides additional utility functions for [`Lettuce`](https://lettuce.io/).

### libcommon-jdbc

The [`libcommon-util`](libcommon-util) module provides additional utility functions for JDBC.

### libcommon-aliyunons

The [`libcommon-aliyunons`](libcommon-aliyunons) module provides additional utility functions for [`ALIYUN Open Services/RocketMQ`](https://help.aliyun.com/product/29530.html).

### libcommon-rocketmq

The [`libcommon-rocketmq`](libcommon-rocketmq) module provides additional utility functions for [`RocketMQ`](https://rocketmq.apache.org/).

### libcommon-bson

The [`libcommon-bson`](libcommon-bson) module provides some additional kotlin extensions for [`BSON`](https://www.mongodb.com/basics/bson).
