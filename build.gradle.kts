plugins {
    java
    id("com.github.johnrengelman.shadow").version("5.0.0")
}

group = "com.mobiquityinc.packer"
version = "1.0"

repositories {
    mavenCentral()
}

sourceSets {
    create("integrationTest") {
        java {
            compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
            srcDir("src/integration-test/java")
        }
        resources.srcDir("src/integration-test/resources")
    }

    create("parityTest") {
        java {
            compileClasspath += sourceSets.main.get().output
            runtimeClasspath += sourceSets.main.get().output
            srcDir("src/parity-test/java")
        }
        resources.srcDir("src/parity-test/resources")
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val integrationTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntime.get())
}

val parityTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val parityTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntime.get())
}

dependencies {
    compile("com.google.guava:guava:28.0-jre")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testCompile("org.hamcrest:hamcrest:2.1")
    testCompile("org.junit.jupiter:junit-jupiter-params:5.4.2")
    testCompile("org.mockito:mockito-core:2.28.2")
    testCompile("org.mockito:mockito-junit-jupiter:2.28.2")

    integrationTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")

    parityTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    parityTestImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "FAILED")
    }
}

val integrationTest = task<Test>("integrationTest") {
    useJUnitPlatform()
    description = "Runs integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath

    testLogging {
        events("PASSED", "FAILED")
    }
    shouldRunAfter("test")
}

val parityTest = task<Test>("parityTest") {
    useJUnitPlatform()
    description = "Generates random data to find discrepancies on algorithms"
    group = "verification"
    testClassesDirs = sourceSets["parityTest"].output.classesDirs
    classpath = sourceSets["parityTest"].runtimeClasspath
    // Forwarding parity tests duration system property
    systemProperty("durationMinutes", System.getProperty("durationMinutes"))

    testLogging {
        showStandardStreams = true
        showExceptions = true
        showCauses = true
        showStackTraces = true
        events("PASSED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
    }
}

tasks.check { dependsOn(integrationTest) }
