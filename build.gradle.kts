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

val packedUi by configurations.creating {
	isCanBeConsumed = false
	isCanBeResolved = true
}

val uiDir = "src/main/resources/static"

repositories {
	mavenCentral()
}

dependencies {
	packedUi(libs.frontend) { targetConfiguration = "packedUi" }
	implementation(libs.bundles.spring)
	implementation(libs.bundles.kotlin)
	runtimeOnly(libs.bundles.h2)
	testImplementation(libs.bundles.test)
}

val unpackUi by tasks.registering(Copy::class) {
	dependsOn(packedUi)
	from(zipTree(packedUi.singleFile))
	into(uiDir)
}

val run by tasks.registering(JavaExec::class) {
	classpath = files(tasks.bootJar)
	jvmArgs = listOf("-Xmx1024M", "-Xms1024M")
	environment = mapOf("spring.profiles.active" to "prod")
}

tasks.clean {
	delete(unpackUi)
}

tasks.withType<ProcessResources> {
	dependsOn(unpackUi)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
