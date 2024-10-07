package ru.sejapoe

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.DIAware
import org.kodein.di.instance

abstract class KodeinComponent : DIAware {
    protected val application: Application by instance()
    protected val log
        get() = application.log
}

abstract class KodeinController(val basePath: String = "") : KodeinComponent() {
    abstract fun Route.registerRoutes()
}