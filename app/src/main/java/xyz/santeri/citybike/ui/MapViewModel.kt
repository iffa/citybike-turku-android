package xyz.santeri.citybike.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xyz.santeri.citybike.data.model.RackEntity
import xyz.santeri.citybike.data.remote.RacksApi
import javax.inject.Inject

class MapViewModel @Inject constructor(private val racksApi: RacksApi) : ViewModel() {
    val racks = MutableLiveData<Data<List<RackEntity>>>()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    init {
        loadRacks()
    }

    fun loadRacks() {
        Timber.d { "Loading rack data" }

        racks.postValue(Data(DataState.LOADING))

        compositeDisposable.add(racksApi.getBikeRacks()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    Timber.d { "Successfully loaded ${it.racks.size} racks from API" }

                    racks.postValue(Data(DataState.SUCCESS, it.racks))
                }, {
                    Timber.e(it, { "Failed to load racks from API" })

                    racks.postValue(Data(DataState.ERROR, null, it.message))
                }))
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
