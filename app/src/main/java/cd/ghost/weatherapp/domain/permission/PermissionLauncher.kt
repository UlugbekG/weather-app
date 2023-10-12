package cd.ghost.weatherapp.domain.permission

import androidx.activity.ComponentActivity

interface PermissionLauncher {

    fun onCreate(activity: ComponentActivity)

    fun onDestroy()

    /**
     * Result handler. Handle the result
     */
    fun askPermission(callback: (Map<String, Boolean>?) -> Unit)

    /**
     * Permission launcher ask for permissions.
     */
    fun launch()

}