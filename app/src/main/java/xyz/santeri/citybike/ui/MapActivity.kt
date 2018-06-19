package xyz.santeri.citybike.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.ajalt.timberkt.Timber
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import xyz.santeri.citybike.R
import xyz.santeri.citybike.data.model.RackEntity
import xyz.santeri.citybike.inject.NetworkModule
import xyz.santeri.citybike.ui.base.BaseActivity
import xyz.santeri.citybike.ui.ext.stylize
import javax.inject.Inject

class MapActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var preferences: SharedPreferences

    private lateinit var vm: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, preferences)
        Configuration.getInstance().userAgentValue = NetworkModule.USER_AGENT

        setContentView(R.layout.activity_map)

        setSupportActionBar(toolbar)

        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(false)
        mapView.setMultiTouchControls(true)
        mapView.controller.setCenter(GeoPoint(60.451813, 22.266630))
        mapView.controller.setZoom(15.0)

        // Disable "pull to refresh", only care about the indicator
        refreshLayout.isEnabled = false

        vm = ViewModelProviders.of(this, viewModelFactory)[MapViewModel::class.java]

        vm.racks.observe(this, Observer({
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

                    showSnackbar(R.string.sb_racks_error, Snackbar.LENGTH_LONG)
                }
                null -> {
                    Timber.e { "DataState is null, this shouldn't happen" }
                }
            }
        }))
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()

        mapView.onPause()
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
        mapView.overlays.clear()

        racks.forEach { rack ->
            val marker = Marker(mapView)

            marker.id = rack.id
            marker.position = GeoPoint(rack.location.coordinates[1], rack.location.coordinates[0])
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_map_marker_variant))
            marker.setInfoWindow(null)
            mapView.overlays.add(marker)
        }

        mapView.invalidate()
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
