package cd.ghost.weatherapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cd.ghost.weatherapp.core.Container
import cd.ghost.weatherapp.core.EmptyWeatherInfoException
import cd.ghost.weatherapp.core.EnableGPSException
import cd.ghost.weatherapp.core.NoConnectionException
import cd.ghost.weatherapp.core.PermissionHasNotGrantedException
import cd.ghost.weatherapp.data.RepositoryImpl
import cd.ghost.weatherapp.domain.entity.WeatherInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: RepositoryImpl,
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherState())
    val state = _state.asStateFlow()

    private var job: Job? = null

    fun fetchData() {
        job?.cancel()
        job = viewModelScope.launch {
            repository.getWeatherData()
                .catch {
                    _state.value = _state.value.copy(
                        errorData = catchExceptions(it),
                    )
                }
                .collect { container ->
                    when (container) {
                        is Container.Success -> {
                            _state.value = _state.value.copy(
                                weatherInfo = container.value,
                                pending = false,
                                errorData = null,
                            )
                        }

                        is Container.Pending -> {
                            _state.value = _state.value.copy(
                                pending = true,
                                errorData = null,
                            )
                        }

                        is Container.Error -> {
                            _state.value = _state.value.copy(
                                pending = false,
                                errorData = catchExceptions(container.exception),
                            )
                        }
                    }
                }
        }
    }

    private fun catchExceptions(cause: Throwable?): ErrorData = when (cause) {
        is EnableGPSException -> ErrorData.ShowGPSAlert()

        is PermissionHasNotGrantedException -> ErrorData.OpenAppSettings(
            title = R.string.permission_required,
            message = R.string.no_connection,
            button = R.string.open
        )

        is NoConnectionException -> ErrorData.OnlyErrorMessage(
            message = R.string.no_connection,
            button = R.string.try_again
        )

        is EmptyWeatherInfoException -> ErrorData.OnlyErrorMessage(
            message = R.string.weather_info_is_empty,
            button = R.string.try_again
        )

        else -> ErrorData.OnlyErrorMessage(
            message = R.string.something_went_wrong,
            button = R.string.try_again
        )
    }

    data class WeatherState(
        val weatherInfo: WeatherInfo? = null,
        val errorData: ErrorData? = null,
        val pending: Boolean = false,
    )

    sealed class ErrorData(val button: Int? = null) {
        class OpenAppSettings(
            val title: Int, val message: Int, button: Int
        ) : ErrorData(button)

        class ShowGPSAlert : ErrorData()

        class OnlyErrorMessage(
            val message: Int, button: Int
        ) : ErrorData(button)
    }
}
