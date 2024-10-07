plugins {
    kotlin("jvm") version "2.0.20"
    id("java-library")
}

dependencies {
    implementation(project(":core"))
    implementation("org.kodein.di:kodein-di:7.22.0")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.22.0")
    implementation("org.jetbrains.exposed:exposed-core:0.55.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.55.0")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}
