package cd.ghost.weatherapp.data.remote

import cd.ghost.weatherapp.DependencyProvider
import cd.ghost.weatherapp.data.model.WhetherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("/data/2.5/weather")
    suspend fun getDaily(
        @Query("lat") lat: Int,
        @Query("lon") lon: Int,
        @Query("appid") appid: String = DependencyProvider.API_KEY,
    ): WhetherModel

}