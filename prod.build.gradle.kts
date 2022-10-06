val packedUi by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    packedUi(libs.frontend) { targetConfiguration = "packedUi" }
}

tasks.withType(JavaExec::class) {
    jvmArgs = listOf("-Xmx1024M", "-Xms1024M")
    environment = mapOf("spring.profiles.active" to "prod")
}

tasks.withType(ProcessResources::class) {
    dependsOn(packedUi)
    from(zipTree(packedUi.singleFile)) {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        into("static")
    }
}
