plugins {
    id("java")
    id("io.qameta.allure") version "2.11.2"
}

group = "ru.kostyanetskaya"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
}

dependencies {
    implementation("net.datafaker:datafaker:2.2.2")
    implementation("com.jayway.jsonpath:json-path:2.9.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation ("io.qameta.allure:allure-java-commons:2.27.0")
    testImplementation ("io.qameta.allure:allure-assertj:2.27.0")
    testImplementation ("io.qameta.allure:allure-rest-assured:2.27.0")
    testImplementation ("io.qameta.allure:allure-commandline:2.27.0")
    testImplementation ("org.aspectj:aspectjweaver:1.9.22.1")
    testImplementation ("io.qameta.allure:allure-selenide:2.27.0")
    testImplementation("io.rest-assured:rest-assured:5.4.0")
    testImplementation("org.assertj:assertj-core:3.26.0")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.3")
}

tasks.test {
    useJUnitPlatform()
}