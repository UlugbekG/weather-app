package cd.ghost.weatherapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cd.ghost.weatherapp.data.db.entity.WeatherInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(weatherData: WeatherInfoEntity)

    @Query("SELECT * FROM `weather-info-entity`")
    suspend fun getWeatherInfo(): WeatherInfoEntity?

    @Transaction
    suspend fun withTransaction(weatherData: WeatherInfoEntity) {
        delete()
        save(weatherData)
    }

    @Query("DELETE FROM `weather-info-entity`")
    suspend fun delete()

}