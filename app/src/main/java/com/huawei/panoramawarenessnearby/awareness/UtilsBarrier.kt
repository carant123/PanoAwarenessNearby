package com.huawei.panoramawarenessnearby.awareness

import android.app.PendingIntent
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier
import com.huawei.hms.kit.awareness.barrier.BarrierQueryRequest
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest

class UtilsBarrier {

    private val TAG = "Utils"

    companion object {

        fun addBarrier(context:Context, label:String, barrier: AwarenessBarrier,
                       pendingIntent: PendingIntent){

            var builder = BarrierUpdateRequest.Builder()
            var request = builder.addBarrier(label, barrier, pendingIntent).build()
            Awareness.getBarrierClient(context).updateBarriers(request)
                .addOnSuccessListener { showToast(context,"Se agrego barrera") }
                .addOnFailureListener { showToast(context,"Fallo agrego barrera") }

        }

        fun deleteBarrier(context: Context?, pendingIntent: PendingIntent) {

            val builder = BarrierUpdateRequest.Builder()
            builder.deleteBarrier(pendingIntent)
            Awareness.getBarrierClient(context!!).updateBarriers(builder.build())
                .addOnSuccessListener {
                    showToast(context, "Elimino barrera")
                }
                .addOnFailureListener { e ->
                    showToast(context, "Fallo eliminacion de barrera")
                }
        }

        private fun showToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

}