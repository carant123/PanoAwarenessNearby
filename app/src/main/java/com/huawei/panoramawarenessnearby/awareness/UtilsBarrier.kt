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

    fun addBarrier(context: Context?,
                   label: String?,
                   barrier: AwarenessBarrier?,
                   pendingIntent: PendingIntent?) {

        val builder = BarrierUpdateRequest.Builder()
        // When the status of the registered barrier changes, pendingIntent is triggered.
        // label is used to uniquely identify the barrier. You can query a barrier by label and delete it.
        val request =
            builder.addBarrier(label!!, barrier!!, pendingIntent!!).build()
        Awareness.getBarrierClient(context!!).updateBarriers(request)
            .addOnSuccessListener {showToast(context, "add barrier success") }
            .addOnFailureListener { e ->
                showToast(context, "add barrier failed")
                Log.e(TAG, "add barrier failed", e)
            }

    }

    fun addBatchBarrier(
        context: Context?,
        barrierList: List<BarrierEntity>) {

        val builder = BarrierUpdateRequest.Builder()
        for (entity in barrierList) {
            builder.addBarrier(
                entity.getBarrierLabel()!!,
                entity.getBarrier()!!,
                entity.getPendingIntent()!!
            )
        }
        Awareness.getBarrierClient(context!!).updateBarriers(builder.build())
            .addOnSuccessListener {
                showToast(context, "add barrier success")
            }
            .addOnFailureListener { e ->
                showToast(context, "add barrier failed")
                Log.e(TAG, "add barrier failed", e)
            }
    }

    fun deleteBarrier(
        context: Context?,
        vararg pendingIntents: PendingIntent?) {

        val builder = BarrierUpdateRequest.Builder()
        for (pendingIntent in pendingIntents) {
            builder.deleteBarrier(pendingIntent!!)
        }

        Awareness.getBarrierClient(context!!).updateBarriers(builder.build())
            .addOnSuccessListener {
                showToast(context, "delete Barrier success")
            }
            .addOnFailureListener { e ->
                showToast(context, "delete barrier failed")
                Log.e(TAG, "remove Barrier failed", e)
            }
    }

    fun deleteBarrier(
        context: Context?,
        vararg labels: String?
    ) {
        val builder = BarrierUpdateRequest.Builder()
        for (label in labels) {
            builder.deleteBarrier(label!!)
        }
        Awareness.getBarrierClient(context!!).updateBarriers(builder.build())
            .addOnSuccessListener {
                showToast(context, "delete Barrier success")
            }
            .addOnFailureListener { e ->
                showToast(context, "delete barrier failed")
                Log.e(TAG, "remove Barrier failed", e)
            }
    }

    fun queryBarrier(
        context: Context?,
        vararg labels: String?
    ) {
        val request = BarrierQueryRequest.forBarriers(*labels)
        Awareness.getBarrierClient(context!!).queryBarriers(request)
            .addOnSuccessListener { barrierQueryResponse ->
                val statusMap = barrierQueryResponse.barrierStatusMap
                val barrierLabel = "target barrier label"
                val barrierStatus = statusMap.getBarrierStatus(barrierLabel)
                if (barrierStatus != null) {
                    val str = "target barrier status is " + barrierStatus.presentStatus
                    Log.i(TAG, str)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "query barrier failed.", e)
            }
    }

    private fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}