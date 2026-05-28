plugins {
    kotlin("jvm") version "2.0.0"
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.beryx.jlink") version "3.1.1"
}

group = "com.backgammon"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation(
        "org.junit.jupiter:junit-jupiter:5.10.2"
    )

    testImplementation(
        "org.testfx:testfx-junit5:4.0.18"
    )
}
dependencies {


    implementation("org.xerial:sqlite-jdbc:3.46.0.0")

    testImplementation(kotlin("test"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("io.mockk:mockk:1.13.10")
}

javafx {

    version = "21"

    modules = listOf(
        "javafx.controls",
        "javafx.fxml"
    )
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("com.backgammon.gui.BackgammonApplicationKt")
}
tasks.test {

    useJUnitPlatform()
}