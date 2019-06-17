Requires Java 12.

To build, execute:
> ./gradlew clean build shadowJar

This will execute all tests and create two jars in the `build/libs` directory:

* `packer-1.0.jar`: library-only jar
* `packer-1.0-all.jar`: uber jar (also includes all dependencies)