plugins {
    id("java")
    id("application")
}

version = "1.0-SNAPSHOT"

application {
    mainClass.set("ru.gb.netty.EchoServer.Server")
}

dependencies {

    implementation("io.netty:netty-all:4.1.70.Final")
    implementation(project(":common"))
}

repositories {
    mavenCentral()
}