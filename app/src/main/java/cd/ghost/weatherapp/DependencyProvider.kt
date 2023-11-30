package cd.ghost.weatherapp

import android.app.Application
import androidx.room.Room
import cd.ghost.weatherapp.core.ConnectivityManagerNetworkMonitor
import cd.ghost.weatherapp.core.MyInterceptor
import cd.ghost.weatherapp.core.NetworkMonitor
import cd.ghost.weatherapp.data.AppDataBase
import cd.ghost.weatherapp.data.RepositoryImpl
import cd.ghost.weatherapp.data.db.WeatherDao
import cd.ghost.weatherapp.data.location.DefaultLocationTracker
import cd.ghost.weatherapp.data.remote.WeatherApi
import cd.ghost.weatherapp.domain.location.LocationTracker
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object DependencyProvider {

    private lateinit var application: Application

    private fun converterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    private fun myHttpClient(): OkHttpClient {
        return OkHttpClient()
            .newBuilder()
            .addInterceptor(MyInterceptor())
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .client(myHttpClient())
            .addConverterFactory(converterFactory())
            .build()
    }

    private val getWeatherApi: WeatherApi by lazy {
        retrofit().create(WeatherApi::class.java)
    }

    private val getLocationTracker: LocationTracker by lazy {
        DefaultLocationTracker(
            locationClient = LocationServices.getFusedLocationProviderClient(application),
//            connectivityManager = connectivityManager,
            application = application
        )
    }

    private val getWeatherDao: WeatherDao by lazy {
        getDatabase().weatherDao()
    }

    private fun getDatabase(): AppDataBase {
        return Room.databaseBuilder(application, AppDataBase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    private val connectivityManager: NetworkMonitor by lazy {
        ConnectivityManagerNetworkMonitor(application)
    }

    val getRepositoryImpl: RepositoryImpl by lazy {
        RepositoryImpl(
            weatherApi = getWeatherApi,
            locationTracker = getLocationTracker,
            ioDispatcher = Dispatchers.IO,
            weatherDao = getWeatherDao,
            appId = BuildConfig.API_KEY
        )
    }

    fun init(app: Application) {
        application = app
    }

    private const val DB_NAME = "weather-app-db"

}

