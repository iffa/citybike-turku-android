package xyz.santeri.citybike.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import xyz.santeri.citybike.data.model.Rack
import xyz.santeri.citybike.data.remote.RacksApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MapViewModel @Inject constructor(private val racksApi: RacksApi) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val racks = MutableLiveData<Data<List<Rack>>>()

    init {
        loadRacks()
    }

    fun loadRacks() {
        Timber.d { "Loading rack data" }

        compositeDisposable.clear()

        racks.postValue(Data(DataState.LOADING))

        compositeDisposable.add(Observable.interval(0, 60,
                TimeUnit.SECONDS, Schedulers.computation())
                .flatMapSingle { racksApi.getBikeRacks() }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            Timber.d { "Successfully loaded ${it.racks.size} racks from API" }

                            if (it.racks.isNotEmpty()) {
                                racks.postValue(Data(DataState.SUCCESS, it.racks))
                            } else {
                                racks.postValue(Data(DataState.EMPTY))
                            }
                        },
                        {
                            Timber.e(it) { "Failed to load racks from API" }

                            if (it is HttpException) {
                                racks.postValue(Data(DataState.ERROR, null, it.code()))
                            } else {
                                racks.postValue(Data(DataState.ERROR, null, null))
                            }
                        }))
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}

enum class DataState { LOADING, SUCCESS, EMPTY, ERROR }

data class Data<out T> constructor(val dataState: DataState, val data: T? = null, val errorCode: Int? = null)