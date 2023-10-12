package cd.ghost.weatherapp.data.model


import com.google.gson.annotations.SerializedName

data class Coord(
    @SerializedName("lat")
    val lat: Int?, // 14
    @SerializedName("lon")
    val lon: Int? // 42
)