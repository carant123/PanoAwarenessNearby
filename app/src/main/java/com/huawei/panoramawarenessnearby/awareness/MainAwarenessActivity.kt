package com.huawei.panoramawarenessnearby.awareness

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.AmbientLightBarrier
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import com.huawei.panoramawarenessnearby.awareness.broadcast.LightBarrierReceiver
import kotlinx.android.synthetic.main.activity_awareness.*

class MainAwarenessActivity : BaseActivity() {

    var barrierReceiver: LightBarrierReceiver? = null

    init {
        activityIdent = this
    }

    companion object {
        lateinit var activityIdent: MainAwarenessActivity
    }

    override fun getLayout(): Int = R.layout.activity_awareness

    override fun inicializar() {

    }

    private fun registrarBarreraBroadcast(): PendingIntent {
        var BARRIER_RECEIVER_ACTION = baseContext.packageName + Constantes.LIHT_BARRIER_RECEIVER_ACTION
        var intent = Intent(BARRIER_RECEIVER_ACTION)
        var pendingIntent = PendingIntent.getBroadcast(this,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        barrierReceiver = LightBarrierReceiver()
        registerReceiver(barrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))
        return pendingIntent
    }

}