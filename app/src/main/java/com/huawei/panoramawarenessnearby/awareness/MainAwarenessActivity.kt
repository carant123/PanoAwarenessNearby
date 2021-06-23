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

    val luxvalue = 9.0f
    val lightBarrierLabel = Constantes.lightBarrierLabel
    var barrierReceiver: LightBarrierReceiver? = null

    init {
        activityIdent = this
    }

    companion object {
        lateinit var activityIdent: MainAwarenessActivity
    }

    override fun getLayout(): Int = R.layout.activity_awareness

    override fun inicializar() {
        verificarPermisos()
        inicializamosBotones()
    }

    private fun inicializamosBotones() {
        bt_weather.setOnClickListener {
            llamarAwarenessWeight()
        }

        bt_ambient_light.setOnClickListener {
            obtenerInfoLuzAmbiente()
        }

        bt_ambient_light_add_barrier.setOnClickListener {
            agregarBarreraLuzAmbiente()
        }

        bt_ambient_light_delete_barrier.setOnClickListener {
            eliminarBarreraLuzAmbiente()
        }
    }

    private fun llamarAwarenessWeight() {
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

    private fun obtenerInfoLuzAmbiente() {
        if(!tieneLocationPermisos()) {
            ActivityCompat.requestPermissions(this, LISTA_PERMISSION, 10)
        } else {
            llamarAwarenessLuzAmbiente()
        }
    }

    private fun llamarAwarenessLuzAmbiente() {
        Awareness.getCaptureClient(this).lightIntensity
            .addOnSuccessListener {
                var luzAmbienteStatus = it.ambientLightStatus
                mostrarMensaje("LuzAmbiente es " + luzAmbienteStatus.lightIntensity)
            }
            .addOnFailureListener {
                it.message?.let { it2 -> mostrarMensaje(it2) }
            }
    }

    private fun agregarBarreraLuzAmbiente() {
        var luxSobreBarreraCondicion : AwarenessBarrier = AmbientLightBarrier.above(luxvalue)
        var pendingIntent = registrarBarrierBroadcast()
        UtilsBarrier.addBarrier(this, lightBarrierLabel, luxSobreBarreraCondicion, pendingIntent)
    }

    private fun registrarBarrierBroadcast(): PendingIntent {
        val BARRIER_RECEIVER_ACTION = baseContext.packageName + Constantes.LIHT_BARRIER_RECEIVER_ACTION
        val intent = Intent(BARRIER_RECEIVER_ACTION)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        barrierReceiver = LightBarrierReceiver()
        registerReceiver(barrierReceiver, IntentFilter(BARRIER_RECEIVER_ACTION))
        return pendingIntent
    }

    private fun eliminarBarreraLuzAmbiente() {
        var pendingIntent = registrarBarrierBroadcast()
        UtilsBarrier.deleteBarrier(this, pendingIntent)
    }

    fun mostrarMensajeBroadCast(mensaje: String) {
        mostrarMensaje(mensaje)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (barrierReceiver != null) {
            unregisterReceiver(barrierReceiver)
        }
    }

}