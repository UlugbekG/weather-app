package cd.ghost.weatherapp.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cd.ghost.weatherapp.DependencyProvider
import cd.ghost.weatherapp.R
import cd.ghost.weatherapp.core.Container
import cd.ghost.weatherapp.core.screenViewModel
import cd.ghost.weatherapp.data.model.WhetherModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel = screenViewModel { MainViewModel(DependencyProvider.getRepository) }
) {
    val context = LocalContext.current
    val vertElementPadding = 24.dp
    val padding = 16.dp

    val state = viewModel.state.collectAsState()

    when (state.value) {
        is Container.Success -> {
            val whether = (state.value as Container.Success<WhetherModel>).value
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                TopSection(
                    clickSearch = {
                        toast(context, "search")
                    },
                    clickMenu = {
                        toast(context, "menu")
                    }
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Column {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = vertElementPadding),
                            text = whether.name ?: "",
                            style = TextStyle(
                                fontSize = 50.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        )
                        // TODO: Need to convert time to readable type
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = vertElementPadding),
                            text = whether.dt?.toString() ?: "Date",
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray
                            )
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = vertElementPadding),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(200.dp)
                                .padding(padding),
                            painter = painterResource(id = R.drawable.ic_cloud_snow),
                            contentDescription = "description"
                        )
                        Row {
                            Column {
                                Text(
                                    text = whether.main?.temp?.toString() ?: "Temp",
                                    style = TextStyle(
                                        fontSize = 50.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                                Text(
                                    text = whether.weather[0]?.description ?: "Description",
                                    style = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black
                                    )
                                )
                            }
                            Text(
                                text = "C",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(10) {
                            ItemDaily("Rainy") {}
                        }
                    }
                }
            }
        }

        is Container.Pending -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is Container.Error -> {
            val message = (state.value as Container.Error).exception
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message?.message ?: "Something went wrong!!!")
            }
        }
    }
}

@Composable
fun TopSection(
    modifier: Modifier = Modifier,
    clickSearch: () -> Unit,
    clickMenu: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(clickSearch) {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search"
            )
        }
        IconButton(clickMenu) {
            Icon(
                Icons.Rounded.Menu,
                contentDescription = "Menu"
            )
        }
    }
}

@Composable
fun ItemDaily(
    condition: String,
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(100.dp)
                .padding(8.dp),
            painter = painterResource(id = R.drawable.ic_cloud),
            contentDescription = "whether"
        )
        Text(
            text = condition,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

private fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@Composable
@Preview(
    showBackground = true
)
fun HomePreview() {
    HomeScreen()
}
