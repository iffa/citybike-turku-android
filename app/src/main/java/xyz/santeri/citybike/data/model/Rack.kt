package xyz.santeri.citybike.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RackEntity(
        @Json(name = "id") val id: String,
        @Json(name = "stop_code") val stopCode: String,
        @Json(name = "geometry") val location: RackLocation,
        @Json(name = "properties") val properties: RackProperties
)

@JsonClass(generateAdapter = true)
data class RackLocation(
        @Json(name = "coordinates") val coordinates: DoubleArray
)

@JsonClass(generateAdapter = true)
data class RackProperties(
        @Json(name = "operator") val operator: String,
        @Json(name = "name") val name: String,
        @Json(name = "last_seen") val lastSeen: Long,
        @Json(name = "bikes_avail") val bikesAvailable: Int,
        @Json(name = "slots_avail") val slotsAvailable: Int,
        @Json(name = "slots_total") val slotsTotal: Int
)