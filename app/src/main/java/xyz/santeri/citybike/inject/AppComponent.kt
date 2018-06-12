package xyz.santeri.citybike.inject

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import xyz.santeri.citybike.CitybikeApp
import xyz.santeri.citybike.ui.MainActivityModule
import xyz.santeri.citybike.ui.mapscreen.MapScreenModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    NetworkModule::class,
    ViewModelModule::class,
    MainActivityModule::class,
    MapScreenModule::class
])
interface AppComponent : AndroidInjector<CitybikeApp> {
    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<CitybikeApp>()
}