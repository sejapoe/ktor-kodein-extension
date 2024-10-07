plugins {
    kotlin("jvm") version "2.0.20"
    id("java-library")
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:3.0.0-rc-2")
    implementation("org.kodein.di:kodein-di:7.22.0")
    implementation("org.kodein.di:kodein-di-framework-ktor-server-jvm:7.22.0")
    testImplementation(kotlin("test"))
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
