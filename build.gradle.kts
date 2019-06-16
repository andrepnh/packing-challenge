plugins {
    java
}

group = "com.github.andrepnh"
version = "1.0-SNAPSHOT"

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
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val integrationTestRuntimeOnly by configurations.getting {
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

tasks.check { dependsOn(integrationTest) }
