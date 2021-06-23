package com.huawei.panoramawarenessnearby.nearbyservice.wifishare

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import com.huawei.hms.common.ApiException
import com.huawei.hms.nearby.Nearby
import com.huawei.hms.nearby.StatusCode
import com.huawei.hms.nearby.discovery.ScanEndpointInfo
import com.huawei.hms.nearby.wifishare.WifiShareCallback
import com.huawei.hms.nearby.wifishare.WifiShareEngine
import com.huawei.hms.nearby.wifishare.WifiSharePolicy
import com.huawei.panoramawarenessnearby.R
import java.util.*

class WiFiShareHelper : OnItemClickListener {

    private val TAG = "Wi-FiShareDemo*Helper"
    private var mContext: Context? = null
    private var mScanEndpointMap: HashMap<String, ScanEndpointInfo>? = null
    private var mNearbyDevicesListView: ListView? = null
    private var mAuthCodeTextView: TextView? = null
    private var mWiFiShareEngine: WifiShareEngine? = null

    var mSimpleAdapter: SimpleAdapter? = null
    var data: MutableList<HashMap<String, Any?>> = ArrayList()

    constructor(mContext: Context?) {
        this.mContext = mContext
        mWiFiShareEngine = Nearby.getWifiShareEngine(mContext)
        mScanEndpointMap = HashMap()
    }

    fun compartirConfigWifi(){
        mWiFiShareEngine!!.startWifiShare(mWifiShareCallback, WifiSharePolicy.POLICY_SHARE)
            .addOnFailureListener {
                if(it is ApiException) {
                    Toast.makeText(mContext, "" + StatusCode.getStatusCode(it.statusCode), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(mContext, "fallo", Toast.LENGTH_LONG).show()
                }
            }
        showLisView()
    }

    fun llenarLista(listView: ListView?, textView: TextView?) {
        mNearbyDevicesListView = listView
        mAuthCodeTextView = textView
    }

    private fun showLisView() {
        data.clear()
        for((key,value) in mScanEndpointMap!!){
            val item = HashMap<String, Any?>()
            item["id"] = key
            item["name"] = value.name
            data.add(item)
        }

        if (mSimpleAdapter == null) {
            mSimpleAdapter = SimpleAdapter(mContext, data, R.layout.item, arrayOf("id", "name"), intArrayOf(R.id.itemId, R.id.itemName))
            mNearbyDevicesListView!!.adapter = mSimpleAdapter
            mNearbyDevicesListView!!.onItemClickListener = this
        } else {
            mSimpleAdapter!!.notifyDataSetChanged()
        }
    }

    fun solicitarLaConfiguracionWifi() {
        mWiFiShareEngine!!.startWifiShare(mWifiShareCallback, WifiSharePolicy.POLICY_SET)
            .addOnFailureListener {
                if(it is ApiException) {
                    Toast.makeText(mContext, "" + StatusCode.getStatusCode(it.statusCode), Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(mContext, "conexion fallida", Toast.LENGTH_LONG).show()
                }
            }
    }

    private val mWifiShareCallback: WifiShareCallback = object : WifiShareCallback() {

        override fun onLost(endpointId: String) {
            mScanEndpointMap!!.remove(endpointId)
            showLisView()
        }

        override fun onFound(endpointId: String, scanEndpointInfo: ScanEndpointInfo) {
            mScanEndpointMap!![endpointId] = scanEndpointInfo
            showLisView()
        }

        override fun onFetchAuthCode(endpointId: String?, authCode: String?) {
            val toDisplay = "Auth Code " + authCode
            mAuthCodeTextView!!.text = toDisplay
        }

        override fun onWifiShareResult(p0: String?, p1: Int) {
            mWiFiShareEngine!!.stopWifiShare()
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = mNearbyDevicesListView!!.getItemAtPosition(position) as java.util.HashMap<String, Any>
        val endpointId = item["id"] as String?
        Log.e(TAG, "ListView on click listener Share WiFi to endpoint: $endpointId")
        mWiFiShareEngine!!.shareWifiConfig(endpointId)
    }

}