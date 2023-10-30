package cd.ghost.weatherapp.core

open class AppException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)


class TestException : AppException()
class NoConnectionException : AppException()
class EnableGPSException : AppException()
class LocationResultUnsuccessful : AppException()
class PermissionHasNotGrantedException : AppException()
class EmptyWeatherInfoException : AppException()
