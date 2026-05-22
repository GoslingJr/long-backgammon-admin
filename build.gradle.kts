plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.backgammon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(kotlin("stdlib"))

    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.mockk:mockk:1.13.8")
}

tasks.test {

    useJUnitPlatform()
}