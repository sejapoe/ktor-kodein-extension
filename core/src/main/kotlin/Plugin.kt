package ru.sejapoe

import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.ktor.di

class PluginConfiguration {
    val configuration: DI.MainBuilder.() -> Unit = {}
}

val KtorDIPlugin = createApplicationPlugin(name = "KtorDI", ::PluginConfiguration) {
    application.di {
        pluginConfig.configuration.invoke(this)
    }
}