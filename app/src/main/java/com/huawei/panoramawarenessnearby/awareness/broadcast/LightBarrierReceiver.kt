package com.huawei.panoramawarenessnearby.awareness.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.huawei.hms.kit.awareness.barrier.BarrierStatus
import com.huawei.panoramawarenessnearby.awareness.Constantes
import com.huawei.panoramawarenessnearby.awareness.MainAwarenessActivity

class LightBarrierReceiver : BroadcastReceiver() {

    var TAG = "LightBarrierReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        var barrierStatus = BarrierStatus.extract(intent)
        var label = barrierStatus.barrierLabel

        if(label == Constantes.lightBarrierLabel) {
            var mensaje = ""
            when(barrierStatus.presentStatus){
                BarrierStatus.TRUE -> {
                    mensaje = "$label status:Cumple condicion"
                    Log.i(TAG, "$label status:true")
                }
                BarrierStatus.FALSE -> {
                    mensaje = "$label status:No cumple condicion"
                    Log.i(TAG, "$label status:false")
                }
                BarrierStatus.UNKNOWN -> {
                    mensaje = "$label status:unknown"
                    Log.i(TAG, "$label status:unknown")
                }
            }
            MainAwarenessActivity.activityIdent.mostrarMensajeBroadCast(mensaje)
        }
    }
}