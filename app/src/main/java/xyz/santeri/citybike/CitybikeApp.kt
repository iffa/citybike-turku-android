package xyz.santeri.citybike

import com.github.ajalt.timberkt.Timber
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import xyz.santeri.citybike.inject.DaggerAppComponent

class CitybikeApp : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(timber.log.Timber.DebugTree())
        AndroidThreeTen.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().create(this)
    }
}
