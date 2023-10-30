package cd.ghost.weatherapp.domain.entity

data class WeatherInfo(
    val weatherDataPerDay: List<WeatherData>?,
    val currentWeatherData: WeatherData?
)