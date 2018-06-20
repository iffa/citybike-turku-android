package xyz.santeri.citybike.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Rack(
        @Json(name = "id") val id: String,
        @Json(name = "stop_code") val stopCode: String,
        @Json(name = "geometry") val location: RackLocation,
        @Json(name = "properties") val properties: RackProperties
) {
    val availability: Availability
        get() {
            val x = properties.bikesAvailable

            return when {
                x > 3 -> Availability.GOOD
                x > 0 -> Availability.WEAK
                else -> Availability.EMPTY
            }
        }

    val latitude: Double get() = location.coordinates[1]
    val longitude: Double get() = location.coordinates[0]
}

@JsonClass(generateAdapter = true)
data class RackLocation(
        @Json(name = "coordinates") val coordinates: DoubleArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RackLocation

        if (!Arrays.equals(coordinates, other.coordinates)) return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(coordinates)
    }
}

@JsonClass(generateAdapter = true)
data class RackProperties(
        @Json(name = "operator") val operator: String,
        @Json(name = "name") val name: String,
        @Json(name = "last_seen") val lastSeen: Long,
        @Json(name = "bikes_avail") val bikesAvailable: Int,
        @Json(name = "slots_avail") val slotsAvailable: Int,
        @Json(name = "slots_total") val slotsTotal: Int
)

enum class Availability {
    GOOD,
    WEAK,
    EMPTY
}