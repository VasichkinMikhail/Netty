plugins {
    id("java")
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation("io.netty:netty-all:4.1.70.Final")


    implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.13.0")

    implementation ("org.xerial:sqlite-jdbc:3.30.1")
}

repositories {
    mavenCentral()
}