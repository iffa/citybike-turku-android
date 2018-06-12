package xyz.santeri.citybike.ui

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class MainActivityModule {
    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity
}