import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.30"
}

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.projectreactor:reactor-core:3.2.9.RELEASE")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.0-SNAPSHOT")
    testImplementation("org.junit.vintage:junit-vintage-engine:5.5.0-SNAPSHOT")
    testImplementation("org.junit.platform:junit-platform-launcher:1.5.0-SNAPSHOT")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.1.4.RELEASE")
    testImplementation("io.projectreactor:reactor-test:3.2.9.RELEASE")
    testImplementation("org.testfx:testfx-junit5:4.0.15-alpha")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}