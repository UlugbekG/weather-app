package cd.ghost.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cd.ghost.weatherapp.core.viewModel
import cd.ghost.weatherapp.domain.permission.PermissionLauncher
import cd.ghost.weatherapp.screens.HomeScreen
import cd.ghost.weatherapp.screens.MainViewModel
import cd.ghost.weatherapp.ui.theme.WetherAppTheme

class MainActivity : ComponentActivity() {

    private lateinit var permissionLauncher: PermissionLauncher
    private val viewModel: MainViewModel by viewModel {
        MainViewModel(DependencyProvider.getRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = DependencyProvider.getPermissionLauncher
        permissionLauncher.onCreate(this)
        permissionLauncher.askPermission { viewModel.fetchData() }
        permissionLauncher.launch()

        setContent {
            WetherAppTheme {
                HomeScreen(
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        permissionLauncher.onDestroy()
    }
}
