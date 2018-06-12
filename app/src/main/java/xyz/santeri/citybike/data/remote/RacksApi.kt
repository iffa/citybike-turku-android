package xyz.santeri.citybike.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.reactivex.Single
import retrofit2.http.GET
import xyz.santeri.citybike.data.model.RackEntity

interface RacksApi {
    @GET("citybike/geojson")
    fun getBikeRacks(): Single<ResponseEntity>

    @JsonClass(generateAdapter = true)
    data class ResponseEntity(@Json(name = "features") val racks: List<RackEntity>)
}