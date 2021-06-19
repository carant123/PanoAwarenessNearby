package com.huawei.panoramawarenessnearby.nearbyservice.wifishare

import android.view.View
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import kotlinx.android.synthetic.main.activity_wifi_share.*

class WifiShareActivity : BaseActivity(), View.OnClickListener {

    private var mWiFiShare: WiFiShareHelper? = null
    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    override fun getLayout(): Int = R.layout.activity_wifi_share

    override fun inicializar() {
        initView()
        mWiFiShare =
            WiFiShareHelper(
                baseContext
            )
        mWiFiShare?.setViewToFill(listView, authCodeText)
        verificarPermisos()
    }

    private fun initView() {
        button_share_wifi.setOnClickListener(this)
        button_connect_wifi.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.button_share_wifi -> mWiFiShare?.shareWiFiConfig()
            R.id.button_connect_wifi -> mWiFiShare?.requestWiFiConfig()
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }

}