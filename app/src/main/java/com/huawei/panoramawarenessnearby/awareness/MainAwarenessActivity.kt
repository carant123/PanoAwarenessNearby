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

    init {
        activityIdent = this
    }

    companion object {
        lateinit var activityIdent: MainAwarenessActivity
    }

    override fun getLayout(): Int = R.layout.activity_awareness

    override fun inicializar() {

    }

}