package xyz.santeri.citybike.ui

import android.os.Bundle
import xyz.santeri.citybike.R
import xyz.santeri.citybike.ui.base.BaseActivity
import xyz.santeri.citybike.ui.mapscreen.MapScreenFragment

import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MapScreenFragment.newInstance())
                    .commitNow()
        }
    }

}
