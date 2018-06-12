package xyz.santeri.citybike.inject

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import xyz.santeri.citybike.CitybikeApp
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideContext(application: CitybikeApp): Context = application.applicationContext

    @Named("app")
    @Singleton
    @Provides
    fun provideAppPreferences(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}