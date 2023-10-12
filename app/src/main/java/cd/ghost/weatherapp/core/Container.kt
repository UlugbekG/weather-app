package cd.ghost.weatherapp.core

sealed class Container<out T> {
    data class Success<out T>(val value: T) : Container<T>()
    object Pending : Container<Nothing>()
    class Error(val exception: Throwable?) : Container<Nothing>()
}