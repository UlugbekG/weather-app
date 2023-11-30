package cd.ghost.weatherapp.data.remote

import cd.ghost.weatherapp.DependencyProvider
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/forecast")
    suspend fun forecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") cnt: Int = 16,
        @Query("units") units: String = "metric",
        @Query("appid") appid: String
    ): ForecastResponse

}