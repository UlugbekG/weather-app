package cd.ghost.weatherapp.data

import cd.ghost.weatherapp.core.Container
import cd.ghost.weatherapp.domain.location.LocationTracker
import cd.ghost.weatherapp.data.model.WhetherModel
import cd.ghost.weatherapp.data.remote.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class Repository(
    private val weatherApi: WeatherApi,
    private val locationTracker: LocationTracker
) {

    suspend fun getDailyForecast(): Flow<Container<WhetherModel>> =
        flow {
            try {
                val currentLocation = locationTracker.getCurrentLocation()

//                val lat = currentLocation!!.latitude.toInt()
//                val lon = currentLocation!!.longitude.toInt()
                val daily = weatherApi.getDaily(12, 33)
                emit(Container.Success(daily))
            } catch (e: Exception) {
                emit(Container.Error(e))
            }

        }.flowOn(Dispatchers.IO)

}


