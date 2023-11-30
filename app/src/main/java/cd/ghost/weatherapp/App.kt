package cd.ghost.weatherapp

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        DependencyProvider.init(this)
    }

}