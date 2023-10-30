package cd.ghost.weatherapp.data.mapper

import cd.ghost.weatherapp.data.db.entity.WeatherDataEntity
import cd.ghost.weatherapp.data.db.entity.WeatherInfoEntity
import cd.ghost.weatherapp.data.db.entity.WeatherUnitValue
import cd.ghost.weatherapp.data.remote.ForecastResponse
import cd.ghost.weatherapp.domain.entity.WeatherData
import cd.ghost.weatherapp.domain.entity.WeatherInfo
import cd.ghost.weatherapp.domain.entity.WeatherType
import cd.ghost.weatherapp.domain.entity.WeatherUnitData
import kotlin.math.roundToInt


/**
 * The order of the Weather object's weatherInfo property is
 * pressure first, humidity second, and wind speed third.
 */
fun ForecastResponse.toWeatherData(): List<WeatherData>? {
    return list?.map { weatherInfo ->
        val time = weatherInfo.dtTxt ?: ""
        val weather = weatherInfo.weather?.get(0)
        val temperature = weatherInfo.main?.temp
        val weatherIcon = weather?.icon
        val windSpeed = weatherInfo.wind?.speed
        val pressure = weatherInfo.main?.pressure
        val humidity = weatherInfo.main?.humidity
        val maxTemp = weatherInfo.main?.tempMax
        val minTemp = weatherInfo.main?.tempMin
        val weatherInfo = listOf(
            WeatherUnitData(
                value = pressure.toString(),
                label = "pressure",
                unit = "hpa"
            ),
            WeatherUnitData(
                value = humidity.toString(),
                label = "humidity",
                unit = "%"
            ),
            WeatherUnitData(
                value = windSpeed.toString(),
                label = "wind speed",
                unit = "km/h"
            )
        )
        WeatherData(
            time = time,
            temperatureCelsius = WeatherUnitData(
                value = temperature?.roundToInt().toString(),
                label = "Celsius",
                unit = "°",
            ),
            weatherInfo = weatherInfo,
            weatherType = WeatherType.fromWMO(weatherIcon),
            minTemp = minTemp ?: 0.0,
            maxTemp = maxTemp ?: 0.0,
            name = city?.name ?: "Unknown",
        )
    }
}

fun ForecastResponse.toWeatherDataEntityList(): List<WeatherDataEntity>? {
    return list?.map { weatherInfo ->
        val time = weatherInfo.dtTxt ?: ""
        val weather = weatherInfo.weather?.get(0)
        val temperature = weatherInfo.main?.temp
        val weatherIcon = weather?.icon
        val description = weather?.description
        val windSpeed = weatherInfo.wind?.speed
        val pressure = weatherInfo.main?.pressure
        val humidity = weatherInfo.main?.humidity
        val maxTemp = weatherInfo.main?.tempMax
        val minTemp = weatherInfo.main?.tempMin
        val weatherInfo = listOf(
            WeatherUnitValue(
                value = pressure.toString(),
                label = "pressure",
                unit = "hpa"
            ),
            WeatherUnitValue(
                value = humidity.toString(),
                label = "humidity",
                unit = "%"
            ),
            WeatherUnitValue(
                value = windSpeed.toString(),
                label = "wind speed",
                unit = "km/h"
            )
        )
        WeatherDataEntity(
            time = time,
            temperatureCelsius = WeatherUnitValue(
                value = temperature?.roundToInt().toString(),
                label = "Celsius",
                unit = "°",
            ),
            weatherInfo = weatherInfo,
            weatherType = cd.ghost.weatherapp.data.db.entity.WeatherType(
                iconId = weatherIcon ?: "Unknown",
                description = description ?: "Unknown",
            ),
            minTemp = minTemp ?: 0.0,
            maxTemp = maxTemp ?: 0.0,
            name = city?.name ?: "Unknown",
        )
    }
}

fun ForecastResponse.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = this.toWeatherData()
    val currentWeather = weatherDataMap?.get(0)
    return WeatherInfo(
        weatherDataPerDay = weatherDataMap,
        currentWeatherData = currentWeather
    )
}

fun ForecastResponse.toWeatherInfoEntity(): WeatherInfoEntity {
    val weatherDataMap = toWeatherDataEntityList()
    val currentWeather = weatherDataMap?.get(0)
    return WeatherInfoEntity(
        weatherDataPerDay = weatherDataMap ?: listOf(),
        currentWeatherData = currentWeather!!
    )
}

fun WeatherDataEntity.toWeatherDataEntity(): WeatherData {
    return WeatherData(
        name = name,
        time = time,
        temperatureCelsius = WeatherUnitData(
            value = temperatureCelsius.value,
            label = temperatureCelsius.label,
            unit = temperatureCelsius.unit,
        ),
        weatherInfo = weatherInfo.map {
            WeatherUnitData(
                value = it.value,
                label = it.label,
                unit = it.unit,
            )
        },
        weatherType = WeatherType.fromWMO(weatherType.iconId),
        maxTemp = maxTemp,
        minTemp = minTemp,
    )
}

fun WeatherInfoEntity.toWeatherInfo(): WeatherInfo {
    return WeatherInfo(
        weatherDataPerDay = weatherDataPerDay.map { it.toWeatherDataEntity() },
        currentWeatherData = currentWeatherData.toWeatherDataEntity()
    )
}