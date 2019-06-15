plugins {
    java
    application
}

group = "com.github.andrepnh"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile("com.google.guava:guava:28.0-jre")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testCompile("org.hamcrest:hamcrest:2.1")
    testCompile("org.junit.jupiter:junit-jupiter-params:5.4.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}

application {
    mainClassName = "com.github.andrepnh.packer.Packer"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "FAILED", "SKIPPED")
    }
}