package cd.ghost.weatherapp.core

import androidx.fragment.app.FragmentActivity

interface MyFragmentActivity {
    fun onCreate(activity: FragmentActivity)
    fun onStop()
    fun onDestroy()
}