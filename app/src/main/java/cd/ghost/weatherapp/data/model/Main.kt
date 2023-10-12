package cd.ghost.weatherapp.data.model


import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("feels_like")
    val feelsLike: Double?, // 311.31
    @SerializedName("grnd_level")
    val grndLevel: Int?, // 1008
    @SerializedName("humidity")
    val humidity: Int?, // 78
    @SerializedName("pressure")
    val pressure: Int?, // 1008
    @SerializedName("sea_level")
    val seaLevel: Int?, // 1008
    @SerializedName("temp")
    val temp: Double?, // 304.31
    @SerializedName("temp_max")
    val tempMax: Double?, // 304.31
    @SerializedName("temp_min")
    val tempMin: Double? // 304.31
)