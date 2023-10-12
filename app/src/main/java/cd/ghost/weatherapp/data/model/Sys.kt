package cd.ghost.weatherapp.data.model


import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("country")
    val country: String?, // ER
    @SerializedName("sunrise")
    val sunrise: Int?, // 1696734110
    @SerializedName("sunset")
    val sunset: Int? // 1696777036
)