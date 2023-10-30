package cd.ghost.weatherapp.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather-info-entity")
data class WeatherInfoEntity(
    val weatherDataPerDay: List<WeatherDataEntity>,
    @PrimaryKey(autoGenerate = false)
    val currentWeatherData: WeatherDataEntity
)