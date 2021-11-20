plugins {
    id("java")
    id("application")
}

version = "1.0-SNAPSHOT"

application {
    mainClass.set("ru.gb.netty.EchoClient.Client")
}

dependencies {

    implementation("io.netty:netty-all:4.1.70.Final")
}

repositories {
    mavenCentral()
}