package com.huawei.panoramawarenessnearby.awareness

import android.app.PendingIntent
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier

class BarrierEntity {
    private var barrierLabel: String? = null
    private var barrier: AwarenessBarrier? = null
    private var pendingIntent: PendingIntent? = null

    fun setBarrierLabel(barrierLabel: String?) {
        this.barrierLabel = barrierLabel
    }

    fun setBarrier(barrier: AwarenessBarrier?) {
        this.barrier = barrier
    }

    fun setPendingIntent(pendingIntent: PendingIntent?) {
        this.pendingIntent = pendingIntent
    }

    fun getBarrierLabel(): String? {
        return barrierLabel
    }

    fun getBarrier(): AwarenessBarrier? {
        return barrier
    }

    fun getPendingIntent(): PendingIntent? {
        return pendingIntent
    }
}