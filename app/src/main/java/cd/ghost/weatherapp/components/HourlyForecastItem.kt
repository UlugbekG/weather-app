package cd.ghost.weatherapp.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cd.ghost.weatherapp.R
import cd.ghost.weatherapp.domain.entity.HUMIDITY_INDEX
import cd.ghost.weatherapp.domain.entity.WeatherData
import kotlin.math.roundToInt


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastItem(
    data: WeatherData,
) {
    val textStyle = TextStyle(
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = Color.White
    )
    Row(
        modifier = Modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = data.formattedTime(),
            style = textStyle.copy(textAlign = TextAlign.Start),
            modifier = Modifier.weight(4f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.ic_water_drop),
            contentDescription = null
        )
        Text(
            modifier = Modifier.weight(1f),
            text = data.weatherInfo[HUMIDITY_INDEX].value +
                    data.weatherInfo[HUMIDITY_INDEX].unit,
            style = textStyle
        )
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = data.weatherType.iconRes),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = "${data.minTemp.roundToInt()}°",
            style = textStyle,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = "${data.maxTemp.roundToInt()}°",
            style = textStyle,
        )
    }
}