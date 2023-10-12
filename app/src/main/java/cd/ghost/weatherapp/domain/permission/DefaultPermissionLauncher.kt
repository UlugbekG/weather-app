package cd.ghost.weatherapp.domain.permission

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class DefaultPermissionLauncher : PermissionLauncher {

    private var currentActivity: ComponentActivity? = null
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null

    override fun onCreate(activity: ComponentActivity) {
        currentActivity = activity
    }

    override fun onDestroy() {
        currentActivity = null
        permissionLauncher = null
    }

    override fun askPermission(callback: (Map<String, Boolean>?) -> Unit) {
        permissionLauncher = currentActivity?.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            callback
        )
    }

    override fun launch() {
        permissionLauncher?.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
        )
    }

}