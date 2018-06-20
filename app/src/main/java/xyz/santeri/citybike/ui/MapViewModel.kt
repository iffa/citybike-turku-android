package xyz.santeri.citybike.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.ajalt.timberkt.Timber
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.OffsetDateTime
import retrofit2.HttpException
import xyz.santeri.citybike.data.model.Rack
import xyz.santeri.citybike.data.remote.RacksApi
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MapViewModel @Inject constructor(private val racksApi: RacksApi) : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val racks = MutableLiveData<Data<List<Rack>>>()
    val details = MutableLiveData<Details>()

    init {
        loadRacks()
    }

    fun loadRacks() {
        Timber.d { "Loading rack data" }

        compositeDisposable.clear()

        racks.postValue(Data(DataState.LOADING))

        compositeDisposable.add(Observable.interval(0, 120,
                TimeUnit.SECONDS, Schedulers.computation())
                .flatMapSingle { racksApi.getBikeRacks() }
                .flatMapSingle { response ->
                    Single.fromCallable {
                        response.racks.sorted()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                        {
                            Timber.d { "Successfully loaded ${it.size} racks from API" }

                            if (it.isNotEmpty()) {
                                racks.postValue(Data(DataState.SUCCESS, it))

                                var totalBikesAvailable = 0
                                it.forEach {
                                    totalBikesAvailable += it.properties.bikesAvailable
                                }
                                details.postValue(Details(totalBikesAvailable, OffsetDateTime.now()))
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

data class Details(val totalBikesAvailable: Int, val updatedTime: OffsetDateTime)