package cd.ghost.weatherapp.data.db.entity


data class WeatherDataEntity(
    val name: String,
    val time: String,
    val temperatureCelsius: WeatherUnitValue,
    val weatherInfo: List<WeatherUnitValue>,
    val weatherType: WeatherType,
    val maxTemp: Double,
    val minTemp: Double,
)

class WeatherUnitValue(
    val value: String,
    val label: String,
    val unit: String
)

class WeatherType(
    val iconId: String,
    val description: String
)

