package com.huawei.panoramawarenessnearby.nearbyservice.nearby

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.huawei.panoramawarenessnearby.R

class NearbyActivity : AppCompatActivity() {

    private var fragment: NearbyFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)
        inicializarPrimerFragment()
    }

    private fun inicializarPrimerFragment() {
        fragment = NearbyFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, fragment!!).commit()
    }

}