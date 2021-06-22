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

    var mSimpleAdapter: SimpleAdapter? = null
    var data: MutableList<HashMap<String, Any?>> =
            ArrayList()

    constructor(mContext: Context?) {

    }

}