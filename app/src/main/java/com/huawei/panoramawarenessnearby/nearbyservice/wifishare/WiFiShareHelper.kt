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

class WiFiShareHelper {

    private val TAG = "Wi-FiShareDemo*Helper"
    private var mContext: Context? = null
    private var mScanEndpointMap: HashMap<String, ScanEndpointInfo>? = null
    private var mNearbyDevicesListView: ListView? = null
    private var mAuthCodeTextView: TextView? = null
    private var mWiFiShareEngine: WifiShareEngine? = null

    constructor(mContext: Context?) {
        this.mContext = mContext
        mWiFiShareEngine = Nearby.getWifiShareEngine(mContext)
        mScanEndpointMap = HashMap()
    }

    /**
     * The device to share WiFi
     */
    fun shareWiFiConfig() {
        Log.d(TAG, "Start to share WiFi")
        mWiFiShareEngine!!.startWifiShare(mWiFiShareCallback, WifiSharePolicy.POLICY_SHARE)
            .addOnFailureListener { ex: Exception ->
                if (ex is ApiException) {
                    val errorCode = ex.statusCode
                    val codeStr = StatusCode.getStatusCode(errorCode)
                    Toast.makeText(mContext, codeStr, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "shareWiFiConfig apiException.getStatusCode()====$errorCode,codeStr:$codeStr")
                } else {
                    Toast.makeText(mContext, "share failed", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, ex.toString())
                }
            }
        showListView()
    }

    /**
     * The device to connect WiFi
     */
    fun requestWiFiConfig() {
        Log.d(TAG, "requestWiFiConfig")
        mWiFiShareEngine!!.startWifiShare(mWiFiShareCallback, WifiSharePolicy.POLICY_SET)
            .addOnFailureListener { ex: Exception ->
                if (ex is ApiException) {
                    val errorCode = ex.statusCode
                    val codeStr = StatusCode.getStatusCode(errorCode)
                    Toast.makeText(mContext, codeStr, Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "shareWiFiConfig apiException.getStatusCode()====$errorCode,codeStr:$codeStr")
                } else {
                    Toast.makeText(mContext, "connect failed", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, ex.toString())
                }
            }
    }

    private val mWiFiShareCallback: WifiShareCallback = object : WifiShareCallback() {

        override fun onFound(endpointId: String, scanEndpointInfo: ScanEndpointInfo) {
            Log.i(TAG, "onFound,endpointId:" + endpointId + ",name:" + scanEndpointInfo.name)
            mScanEndpointMap!![endpointId] = scanEndpointInfo
            showListView()
        }

        override fun onLost(endpointId: String) {
            Log.i(TAG, "onLost,endpointId:$endpointId")
            mScanEndpointMap!!.remove(endpointId)
            showListView()
        }

        override fun onFetchAuthCode(endpointId: String, authCode: String) {
            Log.i(TAG, "onFetchAuthCode() authCode:$authCode,endpointId:$endpointId")
            // To display AuthCode
            val toDisplay = mContext!!.getString(R.string.auth_code) + authCode
            mAuthCodeTextView!!.text = toDisplay
        }

        override fun onWifiShareResult(endpointId: String, statusCode: Int) {
            Log.i(TAG, "-------onWifiShareResult: $endpointId, statusCode: $statusCode,statusStr:" + StatusCode.getStatusCode(statusCode))
            mWiFiShareEngine!!.stopWifiShare()
        }

    }

    fun setViewToFill(listView: ListView?, textView: TextView?) {
        mNearbyDevicesListView = listView
        mAuthCodeTextView = textView
    }

    var mSimpleAdapter: SimpleAdapter? = null
    var data: MutableList<HashMap<String, Any?>> =
        ArrayList()

    /**
     * To show onFound devices, and select a device to share WiFi
     */
    private fun showListView() {
        data.clear()
        for ((key, value) in mScanEndpointMap!!) {
            val item =
                HashMap<String, Any?>()
            item["id"] = key
            item["name"] = value.name
            data.add(item)
        }
        if (mSimpleAdapter == null) {
            mSimpleAdapter = SimpleAdapter(
                mContext,
                data,
                R.layout.item,
                arrayOf("id", "name"),
                intArrayOf(R.id.itemId, R.id.itemName)
            )
            mNearbyDevicesListView!!.adapter = mSimpleAdapter
            mNearbyDevicesListView!!.onItemClickListener =
                OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                    val item = mNearbyDevicesListView!!.getItemAtPosition(position) as HashMap<String, Any>
                    val endpointId = item["id"] as String?
                    Log.e(TAG, "ListView on click listener Share WiFi to endpoint: $endpointId")
                    mWiFiShareEngine!!.shareWifiConfig(endpointId)
                }
        } else {
            mSimpleAdapter!!.notifyDataSetChanged()
        }
    }
}