package cd.ghost.weatherapp.data.db

import androidx.room.TypeConverter
import cd.ghost.weatherapp.data.db.entity.WeatherDataEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object WeatherTypeConverters {

    private val gson = Gson()

    @TypeConverter
    fun toWeatherDataEntity(str: String): WeatherDataEntity {
        val typeOfObjectsList = object : TypeToken<WeatherDataEntity>() {}.type
        return gson.fromJson(str, typeOfObjectsList)
    }

    @TypeConverter
    fun toJsonWeatherDataEntity(weatherData: WeatherDataEntity): String {
        return gson.toJson(weatherData)
    }

    @TypeConverter
    fun toWeatherDataEntityList(str: String): List<WeatherDataEntity> {
        val typeOfObjectsList =
            object : TypeToken<List<WeatherDataEntity>>() {}.type
        return gson.fromJson(str, typeOfObjectsList)
    }

    @TypeConverter
    fun toJsonWeatherDataEntityList(list: List<WeatherDataEntity>): String {
        return gson.toJson(list)
    }
}