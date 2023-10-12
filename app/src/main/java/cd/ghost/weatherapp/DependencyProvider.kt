package cd.ghost.weatherapp

import android.app.Application
import cd.ghost.weatherapp.data.Repository
import cd.ghost.weatherapp.domain.location.DefaultLocationTracker
import cd.ghost.weatherapp.domain.location.LocationTracker
import cd.ghost.weatherapp.data.remote.WeatherApi
import cd.ghost.weatherapp.domain.permission.DefaultPermissionLauncher
import cd.ghost.weatherapp.domain.permission.PermissionLauncher
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DependencyProvider {

    private lateinit var currentApp: Application

    private fun converterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org")
            .addConverterFactory(converterFactory())
            .build()
    }

    val getPermissionLauncher: PermissionLauncher by lazy {
        DefaultPermissionLauncher()
    }

    val getWeatherApi: WeatherApi by lazy {
        retrofit().create(WeatherApi::class.java)
    }

    val getLocationTracker: LocationTracker by lazy {
        DefaultLocationTracker(
            LocationServices.getFusedLocationProviderClient(currentApp),
            currentApp
        )
    }

    val getRepository:Repository by lazy {
        Repository(
            weatherApi = getWeatherApi,
            locationTracker = getLocationTracker
        )
    }

    fun init(application: Application) {
        currentApp = application
    }

    const val API_KEY = "2a2f8516ac91f67d50c4343aa85514e6"
}