package xyz.santeri.citybike.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.ajalt.timberkt.Timber
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.*
import xyz.santeri.citybike.R
import xyz.santeri.citybike.data.model.RackEntity
import xyz.santeri.citybike.ui.base.BaseActivity
import xyz.santeri.citybike.ui.ext.stylize
import javax.inject.Inject

class MapActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var vm: MapViewModel

    companion object {
        const val MAP_BUNDLE = "map_bundle"
    }

    // Boundaries for map panning
    private val mapBounds = LatLngBounds(LatLng(60.425192, 22.220658), LatLng(60.46524, 22.31643))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)

        setSupportActionBar(toolbar)

        // Disable "pull to refresh", only care about the indicator
        refreshLayout.isEnabled = false

        vm = ViewModelProviders.of(this, viewModelFactory)[MapViewModel::class.java]

        vm.racks.observe(this, Observer {
            when (it?.dataState) {
                DataState.LOADING -> {
                    refreshLayout.isRefreshing = true
                }
                DataState.SUCCESS -> {
                    refreshLayout.isRefreshing = false

                    it.data?.let {
                        addRackMarkers(it)
                    }
                }
                DataState.ERROR -> {
                    refreshLayout.isRefreshing = false

                    if (it.errorCode != null) {
                        showSnackbar(getString(R.string.sb_racks_error_http, it.errorCode), Snackbar.LENGTH_LONG)
                    } else {
                        showSnackbar(R.string.sb_racks_error_generic, Snackbar.LENGTH_LONG)
                    }
                }
                DataState.EMPTY -> {
                    refreshLayout.isRefreshing = false

                    showSnackbar(R.string.sb_racks_error_generic, Snackbar.LENGTH_LONG)
                }
                null -> {
                    Timber.e { "DataState is null, this shouldn't happen" }
                }
            }
        })

        var mapBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapBundle = savedInstanceState.getBundle(MAP_BUNDLE)
        }

        mapView.onCreate(mapBundle)
        mapView.getMapAsync { map ->
            run {
                map?.let { configureMap(it) }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        var mapBundle = outState?.getBundle(MAP_BUNDLE)
        if (mapBundle == null) {
            mapBundle = Bundle()
            outState?.putBundle(MAP_BUNDLE, mapBundle)
        }

        mapView.onSaveInstanceState(mapBundle)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun configureMap(map: GoogleMap) {
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_dark2))
        map.isBuildingsEnabled = false
        map.isIndoorEnabled = false

        map.uiSettings.isMapToolbarEnabled = false

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(60.451813, 22.266630), 14.0f))

        // Map limits (panning, zooming etc.)
        map.setLatLngBoundsForCameraTarget(mapBounds)
        map.setMinZoomPreference(12.0f)
        map.setMaxZoomPreference(16.0f)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_map, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_refresh -> {
                vm.loadRacks()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addRackMarkers(racks: List<RackEntity>) {
        racks.forEach { rack ->
            mapView.getMapAsync { map ->
                run {
                    val marker = map.addMarker(MarkerOptions()
                            .position(LatLng(
                                    rack.location.coordinates[1],
                                    rack.location.coordinates[0]))
                            .title(rack.properties.name))

                    marker.tag = rack
                }
            }
        }
    }

    private fun showSnackbar(message: String, duration: Int) {
        val snack = Snackbar.make(refreshLayout, message, duration)
        snack.stylize(this)

        snack.show()
    }

    private fun showSnackbar(@StringRes message: Int, duration: Int) {
        showSnackbar(getString(message), duration)
    }
}
