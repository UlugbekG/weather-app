package cd.ghost.weatherapp.domain.entity

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * The order of the WeatherData object's weatherInfo property is
 * pressure first, humidity second, and wind speed third.
 */
const val PRESSURE_INDEX = 0
const val HUMIDITY_INDEX = 1
const val WIND_SPEED_INDEX = 2

class WeatherUnitData(
    val value: String,
    val label: String,
    val unit: String,
)

data class WeatherData(
    val name: String,
    val time: String,
    val temperatureCelsius: WeatherUnitData,
    val weatherInfo: List<WeatherUnitData>,
    val weatherType: WeatherType,
    val maxTemp: Double,
    val minTemp: Double,
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formattedTime(): String {
        val df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val newLocalDateTime = LocalDateTime.parse(time, df1)
        val format = newLocalDateTime.format(DateTimeFormatter.ofPattern("EEE HH:mm"))
        return format.toString()
    }
}