# LibCommon Project

A set of some common useful libraries.

> Members of libraries always based on JDK 11+.

## Modules

There are a number of modules in LibCommon, here is a quick overview:

### libcommon-util

The [`libcommon-util`](libcommon-util) module provides some common utility classes.

### ~~libcommon-bson~~ (deprecated since 2.5, added since 2.0)

**The [~~`libcommon-bson`~~](libcommon-bson) was deprecated since 2.5 and may be removed on higher version(e.g. 3.x).**

**Please use [`bson-model`](https://github.com/fmjsjx/bson-model) instead.**

The [~~`libcommon-bson`~~](libcommon-bson) module provides a ORM like model framework for BSON/MongoDB. 

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

### libcommon-yaml

The [`libcommon-yaml`](libcommon-yaml) module provides a library interface to encode/decode YAML and it's default implementation based on [`Jackson2`](https://github.com/FasterXML/jackson).

### libcommon-redis

The [`libcommon-redis`](libcommon-redis) module provides additional utility functions for [`Lettuce`](https://lettuce.io/).

### libcommon-jdbc

The [`libcommon-util`](libcommon-util) module provides additional utility functions for JDBC.

### libcommon-aliyunons

The [`libcommon-util`](libcommon-util) module provides additional utility functions for [`ALIYUN Open Services/RocketMQ`](https://help.aliyun.com/product/29530.html).

### libcommon-rocketmq

The [`libcommon-util`](libcommon-util) module provides additional utility functions for [`RocketMQ`](https://rocketmq.apache.org/).
