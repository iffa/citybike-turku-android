package xyz.santeri.citybike.ui.mapscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import xyz.santeri.citybike.R

class MapScreenFragment : Fragment() {

    companion object {
        fun newInstance() = MapScreenFragment()
    }

    private lateinit var viewModel: MapScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MapScreenViewModel::class.java)
    }
}
