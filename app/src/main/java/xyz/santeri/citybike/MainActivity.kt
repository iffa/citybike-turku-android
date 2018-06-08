package xyz.santeri.citybike

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xyz.santeri.citybike.ui.mapscreen.MapScreenFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MapScreenFragment.newInstance())
                    .commitNow()
        }
    }

}
