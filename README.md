# RegreSQL
![Java CI](https://github.com/mdeanda/regresql/workflows/Java%20CI/badge.svg)

## Installation

RegreSQL can be installed from [source](https://github.com/mdeanda/regresql) and requires a Java compiler and Apache Maven.

### Installation from source

To install the latest version you can run the following commands:

```bash
git clone https://github.com/mdeanda/regresql.git
cd regresql
mvn clean install
```

The maven plugin will now be available to your current environment and the command line version will be available at `./cli/target/regresql-cli*.jar`

## Run from Command Line

RegreSQL can be run from command line and the test project included in the source can be used as a simple example:

```bash
cd test-maven-project
java -jar ../cli/target/regresql-cli-*.jar \
    -e ./src/test/resources/expected/ \
    -o ./src/test/resources/output/ \
    -s ./src/test/resources/sql/ \
    -p ./datasource.properties \
    --command test
```

If you run with the `-h` parameter you will get back a list of parameters you can pass in but they will pretty much match what is available for Maven.


## Maven RegreSQL Plugin

RegreSQL can be used entirely from Maven.

### Goals Available

* __regresql:update__ - Updates expected results.
* __regresql:test__ - Tests queries against expected results.

### Configuration

Configuration of the maven plugin is done in the `<plugins>` section of the `pom.xml` file.

#### Example Maven Configuration

```xml
<plugins>
    <plugin>
        <groupId>com.thedeanda.regresql</groupId>
        <artifactId>regresql-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <configuration>
            <propertyFile>./datasource.properties</propertyFile>
            <sourceDir>./src/test/sql</sourceDir>
            <expectedDir>./src/test/expected</expectedDir>
            <outputDir>./output</outputDir>
        </configuration>
    </plugin>
</plugins>
```

#### Supported Configuration Properties

* __sourceDir__ - The source directory to find `.sql` files in.
* __expectedDir__ - The base directory to find corresponding expected results files.
* __outputDir__ - The working directory to write output files to for comparison and test results.
* __url__ - The JDBC url to use to connect to the database.
* __username__ - The username to use when connecting to the database.
* __password__ - The password to use when connecting to the database.
* __propertyFile__ - The property file to use to read configuration settings from.
* __propertyFileWillOverride__ - Allows the property file to overwrite values explicitely set in the `pom.xml` file.
