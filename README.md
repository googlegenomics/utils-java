utils-java [![Build Status](https://img.shields.io/travis/googlegenomics/utils-java.svg?style=flat)](https://travis-ci.org/googlegenomics/utils-java) [![Coverage Status](https://img.shields.io/coveralls/googlegenomics/utils-java.svg?style=flat)](https://coveralls.io/r/googlegenomics/utils-java)
==========

This project's goal is to reduce duplicate code across different Google Genomics Java integrations. 

If you have duplicate code appearing in your projects, or see useful functions in the other [googlegenomics Java repositories](https://github.com/googlegenomics?query=-java) that you want to depend on, please [contribute](CONTRIBUTING.rst)!

##Depending on this project

Note: you can find the latest available version of this project in [Maven's central repository](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22google-genomics-utils%22).

### Maven
Add the following to your `pom.xml` file:
```
<project>
  <dependencies>
    <dependency>
      <groupId>com.google.cloud.genomics</groupId>
      <artifactId>google-genomics-utils</artifactId>
      <version>v1beta2-0.1</version>
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
    compile 'com.google.cloud.genomics:google-genomics-utils:v1beta2-0.1'
}
```

##Building this project

1. git clone this repository.
 
1. If you have not already done so, follow the Google Genomics [getting started instructions](https://cloud.google.com/genomics/install-genomics-tools) to set up your environment
  including [installing gcloud](https://cloud.google.com/sdk/) and running `gcloud init`.

1. This project now includes code for calling the Genomics API using [gRPC](http://www.grpc.io).  To use gRPC, you'll need a version of ALPN that matches your JRE version. 

  2. See the [ALPN documentation](http://www.eclipse.org/jetty/documentation/9.2.10.v20150310/alpn-chapter.html) for a table of which ALPN jar to use for your JRE version.
  2. Then download the correct version from [here](http://mvnrepository.com/artifact/org.mortbay.jetty.alpn/alpn-boot).

1. Use a recent version of [Apache Maven](http://maven.apache.org/download.cgi) (e.g., version 3.3.3) to build this code:
```
cd utils-java
mvn package
```

If you wish to modify this code while also modifying a project that depends upon it (e.g., [dataflow-java](https://github.com/googlegenomics/dataflow-java)), just

  1. Build and install the jar to your local maven repository via `mvn install`.
  2. Then update the `pom.xml` of the dependent project to temporarily depend upon the particular SNAPSHOT version of utils-java now installed in your local maven repository.  For example, make a change similar to [this](https://github.com/googlegenomics/dataflow-java/commit/17122798d579e593470c864868a46092f8795dfa) but only in your local copy of `pom.xml`.
  3. Eclipse users: 
    * For each of utils-java, dataflow-java, etc... in Eclipse do: `File -> Import -> Maven -> Existing Maven Projects`
    * If at any point in Eclipse becomes confused (lot of red X's) in Eclipse do: `Project -> Clean -> Clean All Projects`
  4. IntelliJ users:
    * Import the project via File > Open and then double-click on the pom.xml file.

##gRPC

See [Example.java](src/main/java/com/google/cloud/genomics/utils/grpc/Example.java) for some example code that uses gRPC. The protocol buffer schema for the API can be found in [src/main/proto/google/genomics/v1](src/main/proto/google/genomics/v1).

* To run the example:
```
MAVEN_OPTS="-Xbootclasspath/p:/YOUR/PATH/TO/alpn-boot-YOUR-VERSION.jar" \
mvn exec:java -Dexec.mainClass=com.google.cloud.genomics.utils.grpc.Example
```

###Generating gRPC code
Users should typically **not** need to generate gRPC code themselves, as pre-generated code can be found
in [src/main/java/com/google/genomics/v1](src/main/java/com/google/genomics/v1). For developers, code can be generated with
 * Gradle by running `gradle :generateProto` which will create code in target/generated-sources/main.
 * Maven by uncommenting the goals for `maven-protoc-plugin` in pom.xml.

##Code Layout

* [com.google.cloud.genomics.utils](src/main/java/com/google/cloud/genomics/utils)
  * [GenomicsFactory.java](src/main/java/com/google/cloud/genomics/utils/GenomicsFactory.java) Makes it easier to construct an authenticated Genomics service.
  * [Paginator.java](src/main/java/com/google/cloud/genomics/utils/Paginator.java) Lazily paginates through readsets, reads, variants, callsets, etc...
* [com.google.cloud.genomics.utils.grpc](src/main/java/com/google/cloud/genomics/utils/grpc)
  * [Channels.java](src/main/java/com/google/cloud/genomics/utils/grpc/Channels.java) Makes it easier to creating gRPC channels to the Google Genomics API.
  * [Example.java](src/main/java/com/google/cloud/genomics/utils/grpc/Example.java) Demonstrates usage of gRPC via a minimal example.
* [proto](src/main/proto)
  * [Protocol Buffer](http://www.grpc.io/docs/#working-with-protocol-buffers
) files defing the gRPC interface to the Genomics API.
  * code auto-generated from the protobufs (do not edit, but easier to read than the protobuf files)
    * [com.google.api](src/main/java/com/google/api)
    * [com.google.genomics.v1](src/main/java/com/google/genomics/v1)
    * [com.google.longrunning](src/main/java/com/google/longrunning)
    * [com.google.protobuf](src/main/java/com/google/protobuf)
    * [com.google.rpc](src/main/java/com/google/rpc)
  
##Releasing new versions

This section contains details on getting a new release into Maven central and can be safely ignored by most people. If you need a new release of this code, go ahead and just [file an issue](https://github.com/googlegenomics/utils-java/issues/new).

###Prereqs
* [Create a Sonatype Jira Account](http://central.sonatype.org/pages/ossrh-guide.html#initial-setup)
* [File a ticket](https://issues.sonatype.org/browse/OSSRH-11629) to get access to the Sonatype com.google.cloud.genomics group 
* [Setup gpg](http://central.sonatype.org/pages/working-with-pgp-signatures.html) (Don't forget to publish a public key)
* [Setup GitHub SSH keys](https://help.github.com/articles/generating-ssh-keys) (make sure `ssh -T git@github.com` works)
* Create a `~/.m2/settings.xml` file which has the following:
```
<settings>
  <servers>
    <server>
      <id>ossrh</id>
      <username>sonatype-username</username>
      <password>sonatype-password</password>
    </server>
  </servers>
</settings> 
```

###Making a new release
1. Use Maven to tag the code, up the pom version and release into the Sonatype staging area.
```
mvn release:prepare && mvn release:perform
```
> ...  
> **What is the release version for "Google Genomics Utils"?:** \<the next version, e.g. 'v1beta2-0.26'\>  
> **What is SCM release tag or label for "Google Genomics Utils"?:** \<ENTER\>  
> **What is the new development version for "Google Genomics Utils"?** \<the next, next version with a -SNAPSHOT suffix, e.g. v1beta2-0.27-SNAPSHOT\>  

2. Find the repository at https://oss.sonatype.org/#stagingRepositories and close it.
3. If closing succeeds, then release it. See the [detailed instructions](http://central.sonatype.org/pages/releasing-the-deployment.html#close-and-drop-or-release-your-staging-repository) for more info.
4. As long as there aren't any errors - that's it! The new version will be synced to Maven central.

