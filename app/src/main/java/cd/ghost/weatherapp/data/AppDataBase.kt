package cd.ghost.weatherapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cd.ghost.weatherapp.data.db.WeatherDao
import cd.ghost.weatherapp.data.db.WeatherTypeConverters
import cd.ghost.weatherapp.data.db.entity.WeatherInfoEntity

@Database(
    entities = [WeatherInfoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [WeatherTypeConverters::class]
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}