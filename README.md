# ICE Wrapper

## Table of Contents
- [Description](#description)
- [Pre-requisites](#pre-requisites)
- [Configuration](#configuration)
- [Building the Project](#building-the-project)
- [Testing](#testing)
- [Architecture Diagrams](#architecture-diagrams)
- [Contributing](#contributing)
- [License](#license)
- [Maintainers](#maintainers)
- [Versioning & Deployments & Contributions](#versioning--deployments--contributions)

## Description

Ice Wrapper is mainly the core raw evaluator for vaccines and delivers forecasting by vaccine groups. Such forecasting
core functionalities are done via drools using an extensive set of files for rules for each vaccine and some general
rules.

This project is a wrapper around the HLN ICE Framework. Typically the ICE framework needs to be deployed as part of the
OPEN CDS framework. OPEN CDS, while powerful, can be more complex and much larger than needed to simply run the vaccine
forecasting plugin. This project aims to simplify the process of using the engine as a simple jar. By encapsulated the
engines complexities as a jar, it should be easy for users to use this with other frameworks, like Spring Boot,
Quarkus etc.

- [Link to github project](https://github.com/ehurtado-op/ice-wrapper)
- [Link to mirrored github project on gitlab](https://git.officepracticum.com/github/ice-wrapper/-/pipelines)

## Pre-requisites

Before running this project, ensure the following software is installed on your system:
1. Java 8+
2. Maven (3.6.3 is recommended)


### Java 9+ configuration

This project can run as Java 9 or greater, but extra dependencies are needed. An example of the dependencies is below, note versions may be different at time of reading

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


## Configuration

Configuration for this project could be divided on the following

### ICE configuration

Configuration of how ice is being evaluated can be controlled referring to the
[`src/main/resources/ice.properties`](src/main/resources/ice.properties) file

### Pipeline configuration

Refer to [`.gitlab-ci.yml`](.gitlab-ci.yml)

## Building the project:

### Step 1: Clone the Repository
```bash
git clone https://github.com/ehurtado-op/ice-wrapper.git
```

### Step 2: Open the Project in Intellij

### Step 3: Clean install
From the root directory run:

```bash
mvn clean install -B -U -Dmaven.wagon.http.ssl.insecure=true -f pom.xml
```

This creates a jar in the target directory as well as the local .m2 repository
to be used by hot-ice

### Step 4: Running the project

For running the raw forecasting we would need to import this project inside hot-ice locally.
Be sure to import the target version that was built **and** to use the respective resource folder
(As the [rules folder](src/main/resources/rules) contains different versions)

### (Optional) Step 5: Deploy

Deploying to other servers is possible by setting the RELEASE_REPO/SNAPSHOT_REPO environment variables and using
```bash
mvn clean deploy -DskipTests -B -U -X -Dmaven.wagon.http.ssl.insecure=true
```

Such deployment is configured on the [`.gitlab-ci.yml`](.gitlab-ci.yml) file

## Testing

This project does not support direct unit testing but rather its testing can be done through the
[hot-ice project](https://git.officepracticum.com/op-se/hot-ice), which imports ice-wrapper and can be tested either
through pipeline or locally. Please refer to [How to test changes on ice-wrapper](https://opservice.atlassian.net/wiki/spaces/TEC/pages/564297738/How+to+test+changes+on+ice-wrapper)
confluence article for more info.

## Architecture Diagrams

The following links are provided for architecture, usage and in-depth information for
ice-wrapper:

- [Ice-Wrapper Confluence pages](https://opservice.atlassian.net/wiki/spaces/TEC/pages/797966351/Ice-Wrapper)
- [Ice-wrapper Evaluation Diagram](src/main/resources/diagrams/ice-wrapperDroolsEvaluationDiagram.png)
- [VacLogic+ Sequence Diagram](src/main/resources/diagrams/Vaclogic+Diagram.png)

## Contributing
The main goal on modifying this repository is to change the way evaluations are done on different vaccines. For having more in-depth
knowledge of where to modify when working with vaccines please refer to:
- [this link for resources structure info](https://opservice.atlassian.net/wiki/spaces/TEC/pages/557285377/ice-wrapper+Resources+Structure) 
- [this link for how to maintain drool files](https://opservice.atlassian.net/wiki/spaces/TEC/pages/558170115/How+to+understand+and+maintain+dsl+drl+and+dslr+files+on+ice-wrapper)

## License
This project is licensed under the GNU Lesser General Public License (LGPL). See [LICENSE file](License.txt) for more details

## Maintainers
- Mel Wang
- Elmer Hurtado
- Marco Mamani

## Known issues
- The amount of CPU needed for executing all rules for every vaccine is starting to
  gain unsustainable levels, we need to look for a way to leverage its performance
- There is currently no way to run unit tests on this file

## Versioning & Deployments & Contributions
- [Semantic Versioning](https://semver.org/)
- [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/)
- [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)
- For a new version of rules, copy and paste the latest folder version from [rules directory](src/main/resources/rules),
  add a new version number and add your changes there

### CI/CD
Refer to the [`.gitlab-ci.yml`](.gitlab-ci.yml) file for pipelines configuring and
be sure to review the [pipelines section](https://git.officepracticum.com/github/ice-wrapper/-/pipelines) from the
mirrored gitlab project

