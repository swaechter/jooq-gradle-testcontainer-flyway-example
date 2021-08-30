# jOOQ Gradle Example with Testcontainers and Flyway

## Background and the trauma of jOOQ

This example project shows how you can use jOOQ in combination with Gradle. But it doesn't stop there: We all love jOOQ, but are quite annoyed by the fact, that we need an SQL server to generate type safe Java SQL DSL code. It's annoying and error-prone:

* The end user/developer has to configure an SQL server: Which version? Which extensions? Which credentials?
* What about conflicts with local existing SQL servers?
* What about conflicts with a previous major version that isn't compatible anymore?
* How to apply migration scripts and an initial test set for testing?
* How to do the exact same thing a freaking second time for the unit/integration tests? How to rollback after a test/error?

All in all local SQL installations don't scale [across developers/teams] and software developer should take advantage of [automated] modern containerization technique like Docker and Testcontainers:

* Bootstrap an SQL Docker container on a random port with given credentials
* Connect to this SQL server and apply the [Flyway] migration scripts
* Run the jOOQ code generation and ensure all migrations were performed and the code generation works
* Take the generated jOOQ DSL code and use it in the desired project
* Stop and throw away the container

What does someone gain with this approach:

* A scalable and local-configuration-independent project configuration across several developer systems (The only requirement is to have Docker installed)
* A completely isolated and fresh build that isn't influenced/affected by the local SQL server/existing permissions/existing tables or data
* Execution of all migration scripts (Ever had to debug migration script issues in production?). Note: Add test data for unit/integration test, otherwise this bullet point is pointless.

## Solution

To accomplish this, this example project was created. It consists of two modules:

* Module `generator`: This module provides a custom SQL testcontainer that bootstraps an [PostgreSQL] SQL server in a Docker container, establishes a connection to it and executes the migration scripts. After the execution, the jOOQ code generation can be applied/executed and the container can be stopped and thrown away. This example provides an implementation for PostgreSQL, but it should be interchangeable with other SQL servers.
* Module `application`: The main [web] application that contains the Flyway migration scripts and that triggers the jOOQ code generation. The application writes and reads account data to a PostgreSQL database(This database has to exist locally/provided by another persistent Docker container). For the code generation, the custom `generator` is used. This generator will start a test database, apply the Flyway migration scripts and generate the DSL code that the `application` will use and package.

So all in all the `generator` code only exists at compile time to bootstrap and trigger the jOOQ code generation. The module won't be packaged into the application.jar.

## Instructions

```bash
git clone https://github.com/swaechter/jooq-gradle-testcontainer-flyway-example

cd jooq-gradle-testcontainer-flyway-example

.\gradlew clean build

java -jar src/application/build/libs/application-1.0-SNAPSHOT-all.jar
```

## Credits

* Lukas Eder for creating jOOQ and entertaining on Twitter: https://twitter.com/lukaseder
* Etienne Studer for the great Gradle plugin: https://github.com/etiennestuder/gradle-jooq-plugin
