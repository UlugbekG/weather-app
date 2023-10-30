package cd.ghost.weatherapp.core

import kotlinx.coroutines.runBlocking

/**
 * Container is a wrapper class that have Pending, Success, and Error.
 * This class represents process of the operation.
 */
sealed class Container<out T> {

    abstract fun getOrNull(): T?
    data class Success<out T>(val value: T) : Container<T>() {
        override fun getOrNull(): T? = value

    }

    object Pending : Container<Nothing>() {
        override fun getOrNull(): Nothing? = null
    }

    class Error(val exception: Throwable?) : Container<Nothing>() {
        override fun getOrNull(): Nothing? = null
    }
}