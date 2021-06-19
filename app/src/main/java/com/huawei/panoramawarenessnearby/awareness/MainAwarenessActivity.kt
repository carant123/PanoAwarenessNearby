package com.huawei.panoramawarenessnearby.awareness

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.huawei.hms.kit.awareness.Awareness
import com.huawei.hms.kit.awareness.barrier.*
import com.huawei.hms.kit.awareness.capture.AmbientLightResponse
import com.huawei.hms.kit.awareness.capture.BehaviorResponse
import com.huawei.hms.kit.awareness.capture.TimeCategoriesResponse

import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import com.huawei.panoramawarenessnearby.awareness.broadcast.BehaviorBarrierReceiver
import com.huawei.panoramawarenessnearby.awareness.broadcast.LightBarrierReceiver
import com.huawei.panoramawarenessnearby.awareness.broadcast.TimeBarrierReceiver
import kotlinx.android.synthetic.main.activity_awareness.*
import java.util.*

class MainAwarenessActivity : BaseActivity() {

    var TAG = "MainAwarenessActivity"
    val luxValue = 2.5f
    var luxSobreBarreraCondicion : AwarenessBarrier = AmbientLightBarrier.above(luxValue)
    val lightBarrierLabel = "light above barrier"
    var barrierManage = UtilsBarrier()

    override fun getLayout(): Int = R.layout.activity_awareness

    override fun inicializar() {
        verificarPermisos()
        inicializarBotones()
        inicializarBarreraSobreLux()
        inicializarBarreraTime()
        inicializarBarreraBehavior()
    }

    private fun inicializarBotones() {
        bt_weather.setOnClickListener {
            llamarAwarenesWeightPermiso()
        }

        bt_ambient_light.setOnClickListener {
            llamarAwarenesAmbientLightPermiso()
        }

        bt_time.setOnClickListener {
            llamarAwarenesTimePermiso()
        }

        bt_behavior.setOnClickListener {
            llamarAwarenesBehaviorPermiso()
        }
    }

    private fun llamarAwarenesWeightPermiso() {
        if (!tieneLocationPermisos()) {
            ActivityCompat.requestPermissions(this, LISTA_PERMISSION, 10)
        } else {
            llamarAwarenessWeight()
        }
    }

    private fun llamarAwarenesAmbientLightPermiso() {
        if (!tieneLocationPermisos()) {
            ActivityCompat.requestPermissions(this, LISTA_PERMISSION, 10)
        } else {
            llamarAwarenessAmbientLight()
        }
    }

    private fun llamarAwarenesTimePermiso() {
        if (!tieneLocationPermisos()) {
            ActivityCompat.requestPermissions(this, LISTA_PERMISSION, 10)
        } else {
            llamarAwarenessTime()
        }
    }

    private fun llamarAwarenesBehaviorPermiso() {
        if (!tieneLocationPermisos()) {
            ActivityCompat.requestPermissions(this, LISTA_PERMISSION, 10)
        } else {
            llamarAwarenessBehavior()
        }
    }

    private fun llamarAwarenessBehavior() {
        Awareness.getCaptureClient(this).behavior
            // Callback listener for execution success.
            .addOnSuccessListener { behaviorResponse: BehaviorResponse ->
                val behaviorStatus = behaviorResponse.behaviorStatus
                val mostLikelyBehavior = behaviorStatus.mostLikelyBehavior
                val str = "Most likely behavior type is " + mostLikelyBehavior.type +",the confidence is " + mostLikelyBehavior.confidence
                Log.i(TAG, str)
            }
            // Callback listener for execution success.
            .addOnFailureListener { e: Exception? -> Log.e(TAG, "get behavior failed", e)
            }
    }

    private fun llamarAwarenessTime() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Awareness.getCaptureClient(this).timeCategories
            // Callback listener for execution success.
            .addOnSuccessListener { timeCategoriesResponse: TimeCategoriesResponse ->
                val categories = timeCategoriesResponse.timeCategories
                val timeInfo = categories.timeCategories
                mostrarMensaje("Time is " + timeInfo + " time")
            }
            // Callback listener for execution failure.
            .addOnFailureListener { e: Exception? -> Log.e(TAG, "get Time Categories failed", e)
            }
    }

    private fun llamarAwarenessAmbientLight() {
        Awareness.getCaptureClient(this).lightIntensity
            // Callback listener for execution success.
            .addOnSuccessListener { ambientLightResponse: AmbientLightResponse ->
                val ambientLightStatus = ambientLightResponse.ambientLightStatus
                mostrarMensaje("Light intensity is " + ambientLightStatus.lightIntensity + " lux")
            }
            // Callback listener for execution failure.
            .addOnFailureListener { e: Exception? ->
                e?.message?.let { it1 -> mostrarMensaje(it1) }
            }
    }

    private fun llamarAwarenessWeight() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        Awareness.getCaptureClient(baseContext).weatherByDevice
            .addOnSuccessListener { weatherStatusResponse ->
                val weatherStatus = weatherStatusResponse.weatherStatus
                val weatherSituation = weatherStatus.weatherSituation
                val situation = weatherSituation.situation
                // For more weather information, please refer to the API Reference of Awareness Kit.
                val weatherInfoStr = """
                           City:${weatherSituation.city.name}
                           Weather id is ${situation.weatherId}
                           CN Weather id is ${situation.cnWeatherId}
                           Temperature is ${situation.temperatureC}℃,${situation.temperatureF}℉
                           Wind speed is ${situation.windSpeed}km/h
                           Wind direction is ${situation.windDir}
                           Humidity is ${situation.humidity}%
                           """.trimIndent()
                mostrarMensaje(weatherInfoStr)
            }
            // Callback listener for execution failure.
            .addOnFailureListener { e: Exception? ->
                e?.message?.let { it1 -> mostrarMensaje(it1) }
            }
    }

    private fun inicializarBarreraTime() {
        val oneHourMilliSecond = 60 * 60 * 1000L
        var periodOfDayBarrier : AwarenessBarrier = TimeBarrier.duringPeriodOfDay(TimeZone.getDefault(), 11 * oneHourMilliSecond, 12 * oneHourMilliSecond)
        val pendingIntent = registrarBroadcastBarreraTime()
        val timeBarrierLabel = periodOfDayBarrier.toString()

        barrierManage.addBarrier(this, timeBarrierLabel, periodOfDayBarrier, pendingIntent)

    }

    private fun registrarBroadcastBarreraBehavior(): PendingIntent {
        val BARRIER_RECEIVER_ACTION = application.packageName + "BEHAVIOR_BARRIER_RECEIVER_ACTION"
        val intent = Intent(BARRIER_RECEIVER_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val barrierReceiver = BehaviorBarrierReceiver()
        registerReceiver(barrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))
        return pendingIntent
    }

    private fun inicializarBarreraBehavior() {
        var keepStillBarrier : AwarenessBarrier = BehaviorBarrier.keeping(BehaviorBarrier.BEHAVIOR_STILL)
        val pendingIntent = registrarBroadcastBarreraSobreLux()
        val behaviorBarrierLabel = "2"
        barrierManage.addBarrier(this, behaviorBarrierLabel, keepStillBarrier, pendingIntent)
    }

    private fun registrarBroadcastBarreraTime(): PendingIntent {
        val BARRIER_RECEIVER_ACTION = application.packageName + "TIME_BARRIER_RECEIVER_ACTION"
        val intent = Intent(BARRIER_RECEIVER_ACTION)
        val pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val barrierReceiver = TimeBarrierReceiver()
        registerReceiver(barrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))
        return pendingIntent
    }

    private fun inicializarBarreraSobreLux() {
        val pendingIntent = registrarBroadcastBarreraSobreLux()
        barrierManage.addBarrier(this,lightBarrierLabel, luxSobreBarreraCondicion, pendingIntent)
    }

    private fun registrarBroadcastBarreraSobreLux(): PendingIntent {
        val BARRIER_RECEIVER_ACTION = baseContext.packageName + "LIGHT_BARRIER_RECEIVER_ACTION"
        val intent = Intent(BARRIER_RECEIVER_ACTION)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val barrierReceiver = LightBarrierReceiver()
        registerReceiver(barrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))
        return pendingIntent
    }

}