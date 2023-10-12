package cd.ghost.weatherapp.data.model


import com.google.gson.annotations.SerializedName

data class Wind(
    @SerializedName("deg")
    val deg: Int?, // 306
    @SerializedName("gust")
    val gust: Double?, // 5.22
    @SerializedName("speed")
    val speed: Double? // 4.86
)