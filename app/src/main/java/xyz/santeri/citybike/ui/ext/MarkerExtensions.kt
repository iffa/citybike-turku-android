package xyz.santeri.citybike.ui.ext

import android.content.Context
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import xyz.santeri.citybike.R
import xyz.santeri.citybike.data.model.RackEntity

// TODO: Make subclass that has all these nice features built in ?
fun Marker.configure(
        context: Context,
        rack: RackEntity,
        listener: Marker.OnMarkerClickListener
): Marker {
    this.id = rack.id
    this.title = rack.properties.name
    this.position = GeoPoint(rack.location.coordinates[1], rack.location.coordinates[0])
    this.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    this.setIcon(ContextCompat.getDrawable(context, R.drawable.ic_map_marker))
    this.setInfoWindow(null)
    this.setOnMarkerClickListener(listener)
    return this
}