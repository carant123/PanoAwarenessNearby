package com.huawei.panoramawarenessnearby.awareness.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.hms.kit.awareness.barrier.BarrierStatus

class BehaviorBarrierReceiver : BroadcastReceiver() {

    var TAG = "BehaviorBarrierReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val barrierStatus = BarrierStatus.extract(intent)
        val label = barrierStatus.barrierLabel
        when (barrierStatus.presentStatus) {
            BarrierStatus.TRUE -> Log.i(TAG, "$label status:true")
            BarrierStatus.FALSE -> Log.i(TAG, "$label status:false")
            BarrierStatus.UNKNOWN -> Log.i(TAG, "$label status:unknown")
        }
    }
}