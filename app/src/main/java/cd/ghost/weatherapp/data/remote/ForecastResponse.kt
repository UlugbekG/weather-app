package cd.ghost.weatherapp.data.remote


import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("city")
    val city: City?,
    @SerializedName("cnt")
    val cnt: Int?, // 7
    @SerializedName("list")
    val list: List<WeatherInfo>?
)

data class City(
    @SerializedName("country")
    val country: String?, // KZ
    @SerializedName("id")
    val id: Int?, // 1519673
    @SerializedName("name")
    val name: String?, // Saryaghash
    @SerializedName("population")
    val population: Int?, // 25139
    @SerializedName("sunrise")
    val sunrise: Int?, // 1697679513
    @SerializedName("sunset")
    val sunset: Int?, // 1697719056
    @SerializedName("timezone")
    val timezone: Int? // 18000
)

data class Main(
    @SerializedName("humidity")
    val humidity: Int?, // 34
    @SerializedName("pressure")
    val pressure: Int?, // 1021
    @SerializedName("temp")
    val temp: Double?, // 295.22
    @SerializedName("temp_max")
    val tempMax: Double?, // 297.65
    @SerializedName("temp_min")
    val tempMin: Double? // 295.22
)

data class Weather(
    @SerializedName("description")
    val description: String?, // scattered clouds
    @SerializedName("icon")
    val icon: String?, // 03d
    @SerializedName("id")
    val id: Int?, // 802
    @SerializedName("main")
    val main: String? // Clouds
)


data class WeatherInfo(
    @SerializedName("dt")
    val dt: Long?, // 1697706000
    @SerializedName("dt_txt")
    val dtTxt: String?, // 2023-10-19 09:00:00
    @SerializedName("main")
    val main: Main?,
    @SerializedName("weather")
    val weather: List<Weather>?,
    @SerializedName("wind")
    val wind: Wind?
)

data class Wind(
    @SerializedName("speed")
    val speed: Double? // 2.77
)