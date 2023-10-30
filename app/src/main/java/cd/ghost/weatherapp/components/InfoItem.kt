package cd.ghost.weatherapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cd.ghost.weatherapp.ui.theme.appFontFamily

@Composable
fun InfoItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String
) {
    val textStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        fontFamily = appFontFamily,
        color = Color.White
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = textStyle
        )
        Row(
            modifier = Modifier
        ) {
            Text(
                text = value,
                style = textStyle
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                style = textStyle
            )
        }
    }
}
