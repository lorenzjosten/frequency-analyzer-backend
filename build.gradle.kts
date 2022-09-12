import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

defaultTasks(":bootRun")

plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jvm)
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
    runtimeOnly(libs.bundles.h2)
    testImplementation(libs.bundles.test)
}

val inComposite = gradle.parent != null

if(inComposite) apply(from = "composite.build.gradle.kts")

tasks.withType(KotlinCompile::class) {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

