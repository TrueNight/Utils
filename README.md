# Utils
[![Download](https://api.bintray.com/packages/truenight/maven/utils/images/download.svg)](https://bintray.com/truenight/maven/utils/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/utils)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/xyz.truenight.utils/utils/badge.svg)](http://www.javadoc.io/doc/xyz.truenight.utils/utils)
## Installation

Add dependency to your `build.gradle` file:

```groovy
dependencies {
    compile 'xyz.truenight.utils:utils:1.0.6'
}
```

or to your `pom.xml` if you're using Maven:

```xml
<dependency>
  <groupId>xyz.truenight.utils</groupId>
  <artifactId>utils</artifactId>
  <version>1.0.6</version>
  <type>pom</type>
</dependency>
```


#AndroidUtils
[![Download](https://api.bintray.com/packages/truenight/maven/android-utils/images/download.svg)](https://bintray.com/truenight/maven/android-utils/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/android-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/android-utils)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/xyz.truenight.utils/android-utils/badge.svg)](http://www.javadoc.io/doc/xyz.truenight.utils/android-utils)
## Installation

`AndroidUtils` includes `Utils` library

Add dependency to your `build.gradle` file:

```groovy
dependencies {
    compile 'xyz.truenight.utils:android-utils:0.7.8'
}
```

or to your `pom.xml` if you're using Maven:

```xml
<dependency>
  <groupId>xyz.truenight.utils</groupId>
  <artifactId>android-utils</artifactId>
  <version>0.7.8</version>
  <type>pom</type>
</dependency>
```


#ViewUtils
[![Download](https://api.bintray.com/packages/truenight/maven/view-utils/images/download.svg)](https://bintray.com/truenight/maven/view-utils/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/view-utils/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/view-utils)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/xyz.truenight.utils/view-utils/badge.svg)](http://www.javadoc.io/doc/xyz.truenight.utils/view-utils)
## Installation

`ViewUtils` includes `Utils` library

Add dependency to your `build.gradle` file:

```groovy
dependencies {
    compile 'xyz.truenight.utils:view-utils:1.0.4'
}
```

or to your `pom.xml` if you're using Maven:

```xml
<dependency>
  <groupId>xyz.truenight.utils</groupId>
  <artifactId>view-utils</artifactId>
  <version>1.0.4</version>
  <type>pom</type>
</dependency>
```


#AspectProfiler
[![Download](https://api.bintray.com/packages/truenight/maven/aspect-profiler/images/download.svg)](https://bintray.com/truenight/maven/aspect-profiler/_latestVersion)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/aspect-profiler/badge.svg)](https://maven-badges.herokuapp.com/maven-central/xyz.truenight.utils/aspect-profiler)
[![Javadoc](https://javadoc-emblem.rhcloud.com/doc/xyz.truenight.utils/aspect-profiler/badge.svg)](http://www.javadoc.io/doc/xyz.truenight.utils/aspect-profiler)
## Installation

Add dependency to your project `build.gradle` file:

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.uphyca.gradle:gradle-android-aspectj-plugin:0.9.14'
    }
}
```


And then add dependency to your module `build.gradle` file:

```groovy
apply plugin: 'android-aspectj'

dependencies {
    compile 'xyz.truenight.utils:aspect-profiler:1.0'
}
```

or to your `pom.xml` if you're using Maven:

```xml
<dependency>
  <groupId>xyz.truenight.utils</groupId>
  <artifactId>aspect-profiler</artifactId>
  <version>1.0</version>
  <type>pom</type>
</dependency>
```