package com.huawei.panoramawarenessnearby.nearbyservice.wifishare

import android.view.View
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import kotlinx.android.synthetic.main.activity_wifi_share.*

class WifiShareActivity : BaseActivity(), View.OnClickListener {

    private var mWifiShare: WiFiShareHelper? = null

    override fun getLayout(): Int = R.layout.activity_wifi_share

    override fun inicializar() {
        inicializarBotones()
        inicialzarWifiShareHelper()
        verificarPermisos()
    }

    private fun inicialzarWifiShareHelper() {
        mWifiShare = WiFiShareHelper(baseContext)
        mWifiShare?.llenarLista(listView, authCodeText)
    }

    private fun inicializarBotones() {
        button_share_wifi.setOnClickListener(this)
        button_connect_wifi.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.button_share_wifi -> mWifiShare?.compartirConfigWifi()
            R.id.button_connect_wifi -> mWifiShare?.solicitarLaConfiguracionWifi()
            else -> { // Note the block
                print("x is neither 1 nor 2")
            }
        }
    }

}