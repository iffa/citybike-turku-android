package xyz.santeri.citybike.inject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import xyz.santeri.citybike.ui.mapscreen.MapScreenViewModel

@Module
abstract class ViewModelModule {
    @Binds
    internal abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MapScreenViewModel::class)
    internal abstract fun mapScreenViewModel(viewModel: MapScreenViewModel): ViewModel
}