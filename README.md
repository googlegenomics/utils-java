utils-java [![Build Status](https://travis-ci.org/googlegenomics/utils-java.svg?branch=master)](https://travis-ci.org/googlegenomics/utils-java)
==========

This project's goal is to reduce duplicate code across different Google Genomics Java integrations. 

If you have duplicate code appearing in your projects, or see useful functions in the other [googlegenomics Java repositories](https://github.com/googlegenomics?query=-java) that you want to depend on, please [contribute](CONTRIBUTING.rst)!

##Depending on this project

###Note: this doesn't work yet! Maven artifact coming soon!

### Maven
Add the following to your `pom.xml` file:
```
<project>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud.genomics</groupId>
      <artifactId>google-genomics-utils</artifactId>
      <version>0.1</version>
    </dependency>
  </dependencies>
</project>
```

### Gradle
Add the following to your `build.gradle` file:
```
repositories {
    mavenCentral()
}

dependencies {
    compile 'com.google.cloud.genomics:google-genomics-utils:0.1'
}
```

##The code

* [GenomicsAuth.java](src/main/java/com/google/cloud/genomics/utils/GenomicsAuth.java) makes it easier to construct an authenticated Genomics service
