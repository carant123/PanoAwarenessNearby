package com.huawei.panoramawarenessnearby

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

abstract class BaseActivity : AppCompatActivity(){

    // Lista de permisos que se solicitara dependiendo de la version es igual o menor a SDK 28

    val LISTA_PERMISSION = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACTIVITY_RECOGNITION)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        inicializar()
    }

    fun mostrarMensaje(mensaje: String) {
        Toast.makeText(application, mensaje, Toast.LENGTH_LONG).show()
    }

    abstract fun getLayout(): Int
    abstract fun inicializar()

    fun verificarPermisos() {
        if(!tieneLocationPermisos()) {
            ActivityCompat.requestPermissions(this, LISTA_PERMISSION, 10)
        }
    }

    fun tieneLocationPermisos() : Boolean {
        for (permisos in LISTA_PERMISSION) {
            if(baseContext?.let { ActivityCompat.checkSelfPermission(it, permisos) }
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

}