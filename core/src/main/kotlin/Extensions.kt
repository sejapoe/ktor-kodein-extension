package ru.sejapoe

import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.singleton
import org.kodein.type.erased
import kotlin.reflect.KClass
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.typeOf

fun Application.stringProperty(property: String) = environment.config.propertyOrNull(property)?.getString()

fun Application.stringArrayProperty(property: String) =
    environment.config.propertyOrNull(property)?.getList()?.toTypedArray()


fun <T : Any> Application.createFromProperty(kClass: KClass<T>, basePath: String): T =
    kClass.primaryConstructor?.let { constructor ->
        constructor.callBy(
            constructor.parameters.mapNotNull {
                val stringType = String::class.createType()
                val stringArrayType = Array::class.createType(listOf(KTypeProjection.invariant(stringType)))
                val value: Any? = when (it.type) {
                    stringType -> stringProperty("$basePath.${it.name}")
                    stringArrayType -> stringArrayProperty("$basePath.${it.name}")
                    else -> null
                }
                if (value == null) {
                    if (it.type.isMarkedNullable) {
                        it to null
                    } else if (it.isOptional) {
                        null
                    } else {
                        throw IllegalArgumentException(
                            "Unable to create propertyClass ${kClass}." +
                                    " Specify property '$basePath.${it.name}'"
                        )
                    }
                } else {
                    it to value
                }
            }.toMap()
        )
    } ?: throw IllegalArgumentException("Unable to create propertyClass ${kClass}. Primary constructor is not found")

context(Application)
inline fun <reified T : Any> DI.MainBuilder.bindProperty(basePath: String) {
    bindSingleton<T> { createFromProperty(T::class, basePath) }
}

inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}

inline fun DI.MainBuilder.bindSingleton(
    kClass: KClass<*>,
    crossinline callback: (DI) -> Any
) {
    Bind(erased(kClass), null, null) with singleton { callback(this@singleton.di) }
}

inline fun <reified T> DI.MainBuilder.bindAll() = bindAll(T::class)

fun DI.MainBuilder.bindAll(clazz: KClass<*>) {
    clazz.nestedClasses
        .filter {
            DIAware::class.isSuperclassOf(it) && (it.primaryConstructor?.let { c ->
                c.parameters.isEmpty() || c.parameters.size == 1 && c.parameters[0].type.isSubtypeOf(typeOf<DI>())
            } ?: false)
        }.forEach { kClass ->
            val primaryConstructor = kClass.primaryConstructor ?: return@forEach
            when {
                primaryConstructor.parameters.isEmpty()
                    -> bindSingleton(kClass) { primaryConstructor.call() }

                primaryConstructor.parameters.size == 1 && primaryConstructor.parameters[0].type.isSubtypeOf(typeOf<DI>())
                    -> bindSingleton(kClass) { primaryConstructor.call(it) }
            }
        }

}
