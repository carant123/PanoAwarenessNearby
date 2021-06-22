package com.huawei.panoramawarenessnearby.nearbyservice.wifishare

import android.view.View
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import kotlinx.android.synthetic.main.activity_wifi_share.*

class WifiShareActivity : BaseActivity() {

    private var mWiFiShare: WiFiShareHelper? = null
    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    override fun getLayout(): Int = R.layout.activity_wifi_share

    override fun inicializar() {

    }

}