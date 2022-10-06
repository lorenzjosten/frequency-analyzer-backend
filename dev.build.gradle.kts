tasks.withType(JavaExec::class) {
    environment = mapOf("spring.profiles.active" to "dev")
}
