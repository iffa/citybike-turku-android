package xyz.santeri.citybike.ui.mapscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import xyz.santeri.citybike.R
import xyz.santeri.citybike.ui.base.BaseFragment
import javax.inject.Inject

class MapScreenFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var vm: MapScreenViewModel

    companion object {
        fun newInstance() = MapScreenFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        vm = ViewModelProviders.of(this, viewModelFactory)[MapScreenViewModel::class.java]

        vm.racks.observe(this, Observer({
            Log.d("MapScreenFragment", "got " + it?.data?.size + " racks")
        }))
        vm.getRacks()
    }
}
