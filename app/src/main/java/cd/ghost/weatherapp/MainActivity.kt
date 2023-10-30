package cd.ghost.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cd.ghost.weatherapp.components.HourlyForecastItem
import cd.ghost.weatherapp.components.InfoItem
import cd.ghost.weatherapp.core.viewModel
import cd.ghost.weatherapp.domain.entity.WeatherInfo
import cd.ghost.weatherapp.ui.theme.AppTheme
import cd.ghost.weatherapp.ui.theme.appFontFamily
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel by viewModel {
        MainViewModel(DependencyProvider.getRepositoryImpl)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var locationPermissionsGranted by remember {
                mutableStateOf(areLocationPermissionsAlreadyGranted())
            }

            var shouldShowPermissionRationale by remember {
                mutableStateOf(
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                )
            }

            var shouldDirectUserToApplicationSettings by remember {
                mutableStateOf(false)
            }

            var currentPermissionsStatus by remember {
                mutableStateOf(
                    decideCurrentPermissionStatus(
                        locationPermissionsGranted,
                        shouldShowPermissionRationale
                    )
                )
            }

            val locationPermissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )

            val locationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { permissions ->
                        locationPermissionsGranted =
                            permissions.values.reduce { acc, isPermissionGranted ->
                                acc && isPermissionGranted
                            }

                        if (!locationPermissionsGranted) {
                            shouldShowPermissionRationale =
                                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                        shouldDirectUserToApplicationSettings =
                            !shouldShowPermissionRationale && !locationPermissionsGranted

                        currentPermissionsStatus = decideCurrentPermissionStatus(
                            locationPermissionsGranted,
                            shouldShowPermissionRationale
                        )
                    }
                )

            LaunchedEffect(locationPermissionsGranted) {
                viewModel.fetchData()
            }

            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(
                key1 = lifecycleOwner,
                effect = {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_START &&
                            !locationPermissionsGranted &&
                            !shouldShowPermissionRationale
                        ) {
                            locationPermissionLauncher.launch(locationPermissions)
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
            )

            AppTheme(false) {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }
                val state = viewModel.state.collectAsState()
                val scrollState = rememberScrollState()
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = state.value.pending,
                    onRefresh = { viewModel.fetchData() }
                )

                Scaffold(
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    }
                ) { padding ->

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF9595FF),
                                        Color(0xFFB6B6FA),
                                    )
                                )
                            )
                            .pullRefresh(pullRefreshState)
                            .verticalScroll(scrollState),
                        contentAlignment = Alignment.Center
                    ) {

                        when (val errorState = state.value.errorData) {
                            is MainViewModel.ErrorData.OnlyErrorMessage -> {
                                ErrorMessageView(
                                    message = stringResource(id = errorState.message),
                                    buttonText = stringResource(id = R.string.try_again)
                                ) {
                                    viewModel.fetchData()
                                }
                            }

                            is MainViewModel.ErrorData.OpenAppSettings -> {
                                LaunchedEffect(Unit) {
                                    scope.launch {
                                        val userAction = snackbarHostState.showSnackbar(
                                            message = "Please authorize location permissions",
                                            actionLabel = "Approve",
                                            duration = SnackbarDuration.Indefinite,
                                            withDismissAction = false
                                        )
                                        when (userAction) {
                                            SnackbarResult.ActionPerformed -> {
                                                shouldShowPermissionRationale = false
                                                locationPermissionLauncher.launch(
                                                    locationPermissions
                                                )
                                            }

                                            SnackbarResult.Dismissed -> {
                                                shouldShowPermissionRationale = false
                                            }
                                        }
                                    }
                                }
                                ErrorMessageView(
                                    message = stringResource(id = R.string.location_permission_denied),
                                    buttonText = stringResource(id = R.string.open)
                                ) {
                                    openApplicationSettings()
                                }
                            }

                            is MainViewModel.ErrorData.ShowGPSAlert -> {
                                enableGps()
                            }

                            null -> {}
                        }

                        val weather = state.value.weatherInfo
                        WeatherInfoView(weather)

                        PullRefreshIndicator(
                            modifier = Modifier.align(Alignment.TopCenter),
                            refreshing = state.value.pending,
                            state = pullRefreshState,
                        )

                        // open app settings if permission was denied
                        if (shouldDirectUserToApplicationSettings) {
                            ErrorMessageView(
                                message = "Open settings and check permission!",
                                buttonText = "Open"
                            ) {
                                openApplicationSettings()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun WeatherInfoView(weather: WeatherInfo?) {
        if (weather != null) {
            val paddingNormal = 16.dp
            val paddingMid = 20.dp
            val paddingBig = 25.dp
            val paddingM = paddingMid + paddingNormal

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = paddingM, start = paddingM, end = paddingM),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = weather.currentWeatherData?.temperatureCelsius?.value +
                                    weather.currentWeatherData?.temperatureCelsius?.unit,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 80.sp,
                                fontFamily = appFontFamily,
                                color = Color.White
                            )
                        )
                        Text(
                            text = weather.currentWeatherData?.weatherType?.weatherDesc
                                ?: "Unknown",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                fontFamily = appFontFamily,
                                color = Color.White
                            )
                        )
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = weather.currentWeatherData?.name ?: "City",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    fontFamily = appFontFamily,
                                    color = Color.White
                                )
                            )
                            Image(
                                modifier = Modifier.size(20.dp),
                                painter = painterResource(id = R.drawable.ic_location),
                                contentDescription = null
                            )
                        }
                    }
                    Image(
                        modifier = Modifier.size(120.dp),
                        painter = painterResource(
                            id = weather.currentWeatherData?.weatherType?.iconRes
                                ?: R.drawable.ic_cloudy
                        ),
                        contentDescription = weather.currentWeatherData?.weatherType?.weatherDesc
                    )
                }
                Spacer(modifier = Modifier.height(paddingBig))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x14FFFFFF),
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        weather.currentWeatherData?.weatherInfo?.forEach { info ->
                            InfoItem(
                                modifier = Modifier.weight(1f),
                                label = info.label,
                                value = info.value,
                                unit = info.unit
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier.padding(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0x14FFFFFF),
                    )
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        weather.weatherDataPerDay?.forEach { data ->
                            HourlyForecastItem(data)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ErrorMessageView(
        message: String,
        buttonText: String? = null,
        action: (() -> Unit)? = null
    ) {
        val textStyle = TextStyle(
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = appFontFamily,
        )
        Card(
            modifier = Modifier.padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x14FFFFFF),
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(id = R.drawable.ic_warning),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = textStyle
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (action != null && buttonText != null) {
                    Button(onClick = action, modifier = Modifier) {
                        Text(
                            text = buttonText,
                            style = textStyle
                        )
                    }
                }
            }
        }
    }

    private fun enableGps() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled =
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (isGpsEnabled) return

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (30 * 1000).toLong()
        locationRequest.fastestInterval = (5 * 1000).toLong()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> = LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(builder.build())

        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)

            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                        // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                this,
                                REQUEST_LOCATION
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {

                    }
                }
            }
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_LOCATION) {
//            if (resultCode == RESULT_OK) {
//                viewModel.fetchData()
//            }
//            if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, "Operation rejected", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

    private fun areLocationPermissionsAlreadyGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openApplicationSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(PACKAGE, packageName, null)
        ).also {
            startActivity(it)
        }
    }

    private fun decideCurrentPermissionStatus(
        locationPermissionsGranted: Boolean,
        shouldShowPermissionRationale: Boolean
    ): String {
        return if (locationPermissionsGranted) getString(R.string.granted)
        else if (shouldShowPermissionRationale) getString(R.string.rejected)
        else getString(R.string.denied)
    }

    companion object {
        const val REQUEST_LOCATION = 101
        const val PACKAGE = "package"
    }
}
