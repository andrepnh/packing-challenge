Requires Java 12.

To build, execute:
```bash
> ./gradlew clean build shadowJar
```

This will execute all tests and create two jars in the `build/libs` directory:

* `packer-1.0.jar`: library-only jar
* `packer-1.0-all.jar`: uber jar (also includes all dependencies)

### Parity tests

There are two algorithms for packing: `BruteForcePacking` and `MeetInTheMiddlePacking`. The brute force variation is not enabled and won't be used, but the parity tests will execute both.

Parity tests will generate random input data, feed it to both algorithms and compare the results, failing if they differ. They don't run as part of the `build` task, but can be executed on demand using:
```bash
> ./gradlew -DdurationMinutes=1 -i clean parityTest
```

By default they'll run for one minute, but this can be changed using the `durationMinutes` system property on the example above. If no comparisons failed during that time you'll eventually see: 

```
    === Performed 47963 successful comparisons ===
```