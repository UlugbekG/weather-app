package cd.ghost.weatherapp.data

import android.util.Log
import cd.ghost.weatherapp.core.Container
import cd.ghost.weatherapp.core.EmptyWeatherInfoException
import cd.ghost.weatherapp.core.EnableGPSException
import cd.ghost.weatherapp.core.LocationResultUnsuccessful
import cd.ghost.weatherapp.core.PermissionHasNotGrantedException
import cd.ghost.weatherapp.data.db.WeatherDao
import cd.ghost.weatherapp.data.mapper.toWeatherInfo
import cd.ghost.weatherapp.data.mapper.toWeatherInfoEntity
import cd.ghost.weatherapp.data.remote.WeatherApi
import cd.ghost.weatherapp.domain.Repository
import cd.ghost.weatherapp.domain.entity.WeatherInfo
import cd.ghost.weatherapp.domain.location.LocationTracker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import okio.IOException

class RepositoryImpl(
    private val weatherApi: WeatherApi,
    private val locationTracker: LocationTracker,
    private val ioDispatcher: CoroutineDispatcher,
    private val weatherDao: WeatherDao,
) : Repository {

    /**
     * Fetch data from remote source
     * @return [WeatherInfo]
     */
    override suspend fun getWeatherData(): Flow<Container<WeatherInfo>> = flow {
        try {
            // get location coordinates
            val currentLocation = locationTracker.getCurrentLocation()
                ?: throw LocationResultUnsuccessful()

            val forecast = weatherApi.forecast(
                lat = currentLocation.latitude,
                lon = currentLocation.longitude,
            )

            // map data into weatherData to database object.
            val toWeatherInfo = forecast.toWeatherInfoEntity()

            // save data with transaction delete old one and save new data
            weatherDao.withTransaction(toWeatherInfo)

            // get data from database and map it into display object as WeatherInfo
            emit(getWeatherDataFromDb())
        } catch (e: IOException) {
            emit(getWeatherDataFromDb())
        } catch (e: PermissionHasNotGrantedException) {
            emit(Container.Error(e))
        } catch (e: LocationResultUnsuccessful) {
            emit(getWeatherDataFromDb())
        } catch (e: EnableGPSException) {
            emit(Container.Error(e))
        } catch (e: Exception) {
            emit(Container.Error(e))
        }
    }
        .onStart { emit(Container.Pending) }
        .flowOn(ioDispatcher)

    private suspend fun getWeatherDataFromDb(): Container<WeatherInfo> {
        val weatherInfo = weatherDao.getWeatherInfo()?.toWeatherInfo()
        return if (weatherInfo == null) Container.Error(EmptyWeatherInfoException())
        else Container.Success(weatherInfo)
    }

}


