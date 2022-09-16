import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

defaultTasks(":bootRun")

plugins {
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.lint)
}

group = "io.github"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.spring)
    implementation(libs.bundles.kotlin)
    implementation(libs.liquibase)
    runtimeOnly(libs.bundles.h2)
    runtimeOnly(libs.spring.jdbc)
    testImplementation(libs.bundles.test)
}

val inComposite = gradle.parent != null

if (inComposite) apply("composite.build.gradle.kts")

tasks.withType(KotlinCompile::class) {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport

tasks.withType(JacocoReport::class) {
    dependsOn(tasks.test)

    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(layout.buildDirectory.dir("jacoco"))
    }
}
