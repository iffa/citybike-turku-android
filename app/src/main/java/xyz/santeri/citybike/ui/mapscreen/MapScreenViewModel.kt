package xyz.santeri.citybike.ui.mapscreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xyz.santeri.citybike.data.model.RackEntity
import xyz.santeri.citybike.data.remote.RacksApi
import xyz.santeri.citybike.ui.Data
import xyz.santeri.citybike.ui.DataState
import javax.inject.Inject

class MapScreenViewModel @Inject constructor(private val racksApi: RacksApi) : ViewModel() {
    val racks = MutableLiveData<Data<List<RackEntity>>>()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getRacks() =
            compositeDisposable.add(racksApi.getBikeRacks()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe({
                        Log.d("MapScreenViewModel", "Got racks")

                        racks.postValue(Data(DataState.SUCCESS, it.racks))
                    }, {
                        Log.d("MapScreenViewModel", "Error getting racks", it)

                        racks.postValue(Data(DataState.ERROR, null, it.message))
                    }))

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
