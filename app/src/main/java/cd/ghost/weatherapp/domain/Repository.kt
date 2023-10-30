package cd.ghost.weatherapp.domain

import cd.ghost.weatherapp.core.Container
import cd.ghost.weatherapp.domain.entity.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getWeatherData(): Flow<Container<WeatherInfo>>

}