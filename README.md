# ICE Wrapper

This project is a wrapper around the HLN ICE Framework. Typically the ICE framework needs to be deployed as part of the OPEN CDS framework. 
OPEN CDS, while powerful, can be more complex and much larger than needed to simply run the vaccine forecasting plugin. This project aims to simplify the process of using
the engine as a simple jar. By encapsulated the engines complexities as a jar, it should be easy for users to use this with other frameworks, like Spring Boot, Quarkus etc.

## Getting Started

You will need:

1. Java 8+
2. Maven

#### Building the project:

1. From the root directory `mvn clean install`
2. This creates a jar in the target directory as well as the local .m2 repository
3. Deploying to other servers is possible by setting the RELEASE_REPO/SNAPSHOT_REPO environment variables and using `mvn clean deploy`

#### Running the project

1. Regardless of what engine you use, you will need the Drools files from the ICE project. They can be found in the ice3/opnecds-ice-service/src/main/resources/. The entire director is needed as is.
2. Ice Engine


### Good to know

Java 9+ : This project can run as Java 9 or greater, but extra dependencies are needed. An example of the dependencies is below, note versions may be different at time of reading

````xml
  <dependencies>
    <!-- Added for > java 8 compliance -->  
    <!-- JAXB API only -->  
    <dependency>  
        <groupId>javax.xml.bind</groupId>  
        <artifactId>jaxb-api</artifactId>  
        <version>2.3.1</version>  
    </dependency>
    
    <!-- JAXB RI -->
    <dependency>
        <groupId>com.sun.xml.bind</groupId>
        <artifactId>jaxb-impl</artifactId>
        <version>2.3.1</version>
    </dependency>
    <dependency>
        <groupId>com.sun.xml.bind</groupId>
        <artifactId>jaxb-core</artifactId>
        <version>2.3.0</version>
    </dependency>
  </dependencies>
````
