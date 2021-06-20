package com.huawei.panoramawarenessnearby.nearbyservice.fileshare

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import com.obsez.android.lib.filechooser.ChooserDialog
import kotlinx.android.synthetic.main.activity_file_main.*
import java.io.File
import java.util.*

class FileMainActivity : BaseActivity() {

    private val FILE_SELECT_CODE = 0
    val REQUEST_CODE_SCAN_ONE = 0X01
    private var nearbyAgent: NearbyAgent? = null
    private var files: List<File> = ArrayList()

    override fun getLayout(): Int = R.layout.activity_file_main

    override fun inicializar() {
        nearbyAgent = NearbyAgent(this)
        sendBtn.setOnClickListener { showFileChooser() }
        recvBtn.setOnClickListener { nearbyAgent?.receiveFile() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            FILE_SELECT_CODE -> if (resultCode == Activity.RESULT_OK) {
                // Get the Uri of the selected file
                val uri = data?.data
                nearbyAgent?.sendFile(File(uri!!.path))
            }
            REQUEST_CODE_SCAN_ONE -> nearbyAgent?.onScanResult(data)
            else -> {
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showFileChooser() {
        //files?.clear()
        files = ArrayList()
        ChooserDialog(this@FileMainActivity)
            .enableMultiple(false)
            .withChosenListener(ChooserDialog.Result { path, pathFile -> //call nearby agent
                Toast.makeText(this, pathFile.path, Toast.LENGTH_LONG).show()
                nearbyAgent?.sendFile(pathFile)
                return@Result
            }) // to handle the back key pressed or clicked outside the dialog:
            .withOnCancelListener(DialogInterface.OnCancelListener { dialog ->
                dialog.cancel() // MUST have
            })
            .build()
            .show()
    }

}