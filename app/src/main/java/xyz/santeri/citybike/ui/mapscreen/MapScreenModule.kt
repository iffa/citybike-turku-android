package xyz.santeri.citybike.ui.mapscreen

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MapScreenModule {
    @ContributesAndroidInjector
    internal abstract fun mapScreenFragment(): MapScreenFragment
}