plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.2"
}

group = "dev.forst"
version = "0.0.1"

application {
    mainClass.set("dev.forst.idt.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "2.1.2"
    implementation("io.ktor", "ktor-server-core-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-netty-jvm", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-serialization-jackson", ktorVersion)
    // DI
    val kodeinVersion = "7.14.0"
    implementation("org.kodein.di", "kodein-di-jvm", kodeinVersion)
    implementation("org.kodein.di", "kodein-di-framework-ktor-server-jvm", kodeinVersion)

    // logging
    implementation("ch.qos.logback", "logback-classic", "1.4.3")
    // just to have Swagger UI where one can test this
    implementation("dev.forst", "ktor-openapi-generator", "0.5.1")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor", "ktor-server-tests-jvm", ktorVersion)
    testImplementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)
    testImplementation("io.mockk", "mockk", "1.12.3")

    val jupiterVersion = "5.9.0"
    testImplementation("org.junit.jupiter", "junit-jupiter-api", jupiterVersion)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", jupiterVersion)
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }
}
