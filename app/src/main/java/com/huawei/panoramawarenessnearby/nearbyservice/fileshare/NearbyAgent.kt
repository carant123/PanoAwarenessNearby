package com.huawei.panoramawarenessnearby.nearbyservice.fileshare

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.nearby.Nearby
import com.huawei.hms.nearby.StatusCode
import com.huawei.hms.nearby.discovery.*
import com.huawei.hms.nearby.transfer.Data
import com.huawei.hms.nearby.transfer.DataCallback
import com.huawei.hms.nearby.transfer.TransferEngine
import com.huawei.hms.nearby.transfer.TransferStateUpdate
import com.huawei.panoramawarenessnearby.R
import java.io.*
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat

class NearbyAgent {
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )
    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1
    val REQUEST_CODE_SCAN_ONE = 0X01

    private var mContext: Context? = null
    private var mTransferEngine: TransferEngine? = null
    private var mDiscoveryEngine: DiscoveryEngine? = null
    private val TAG = "Nearby_Agent"
    private val mFileServiceId = "NearbyAgentFileService"
    private var mFiles: ArrayList<File> = ArrayList()
    private var mRemoteEndpointId: String? = null
    private var mRemoteEndpointName: String? = null
    private val mEndpointName = Build.DEVICE
    private var mScanInfo: String? = null
    private var mRcvedFilename: String? = null
    private var mResultImage: Bitmap? = null
    private var mBarcodeImage: ImageView? = null
    private var mFileName: String? = null
    private var mProgress: ProgressBar? = null
    private var mDescText: TextView? = null
    private var mStartTime: Long = 0
    private var mSpeed = 60f
    private var mSpeedStr = "60"
    private var isTransfer = false
    private var incomingFile: Data? = null

    constructor(mContext: Context?) {
        this.mContext = mContext
        mDiscoveryEngine = Nearby.getDiscoveryEngine(mContext)
        mTransferEngine = Nearby.getTransferEngine(mContext)
        if (mContext is Activity) {
            ActivityCompat.requestPermissions(
                mContext,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_REQUIRED_PERMISSIONS
            )
        }
        mProgress = (mContext as Activity).findViewById(R.id.pb_main_download)
        mProgress?.visibility = View.INVISIBLE
        mDescText = mContext.findViewById(R.id.tv_main_desc)
        mBarcodeImage = mContext.findViewById(R.id.barcode_image)
    }

    fun sendFile(file: File) {
        init()
        mFiles.add(file)
        sendFilesInner()
    }

    private fun sendFilesInner() {
        /* generate bitmap */try {
            //Generate the barcode.
            val options = HmsBuildBitmapOption.Creator().setBitmapMargin(1)
                .setBitmapColor(Color.BLACK)
                .setBitmapBackgroundColor(Color.WHITE).create()
            mResultImage =
                ScanUtil.buildBitmap(mEndpointName, HmsScan.QRCODE_SCAN_TYPE, 700, 700, options)
            mBarcodeImage!!.visibility = View.VISIBLE
            mBarcodeImage!!.setImageBitmap(mResultImage)
        } catch (e: WriterException) {
            Log.e(TAG, e.toString())
        }
        /* start broadcast */
        val advBuilder = BroadcastOption.Builder()
        advBuilder.setPolicy(Policy.POLICY_P2P)
        mDiscoveryEngine!!.startBroadcasting(mEndpointName, mFileServiceId, mConnCbSender, advBuilder.build())
        Log.d(TAG, "Start Broadcasting.")
    }

    fun receiveFile() {
        init()
        /* scan bitmap */init()
        val options = HmsScanAnalyzerOptions.Creator()
            .setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).create()
        ScanUtil.startScan(mContext as Activity?, REQUEST_CODE_SCAN_ONE, options)
    }


    fun onScanResult(data: Intent?) {
        if (data == null) {
            mDescText!!.text = "Scan Failed."
            return
        }
        /* save endpoint name */
        val obj: HmsScan = data.getParcelableExtra(ScanUtil.RESULT)!!
        mScanInfo = obj.getOriginalValue()
        /* start scan*/
        scanResult()
    }

    private fun scanResult() {
        val scanBuilder: ScanOption.Builder = ScanOption.Builder()
        scanBuilder.setPolicy(Policy.POLICY_P2P)
        mDiscoveryEngine!!.startScan(mFileServiceId, mDiscCb, scanBuilder.build())
        Log.d(TAG, "Start Scan.")
        mDescText!!.text = "Connecting to $mScanInfo..."
    }

    private fun sendOneFile() {
        var filenameMsg: Data? = null
        var filePayload: Data? = null
        isTransfer = true
        Log.d(TAG, "Left " + mFiles.size + " Files to send.")
        if (mFiles.isEmpty()) {
            Log.d(TAG, "All Files Done. Disconnect")
            mDescText!!.text = "All Files Sent Successfully."
            mProgress!!.visibility = View.INVISIBLE
            mDiscoveryEngine!!.disconnectAll()
            isTransfer = false
            return
        }
        try {
            mFileName = mFiles[0].name
            filePayload = Data.fromFile(mFiles[0])
            mFiles.removeAt(0)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "File not found", e)
            return
        }

        filenameMsg = Data.fromBytes(mFileName!!.toByteArray(StandardCharsets.UTF_8))
        Log.d(TAG, "Send filename: $mFileName")
        mTransferEngine!!.sendData(mRemoteEndpointId, filenameMsg)
        Log.d(TAG, "Send Payload.")
        mTransferEngine!!.sendData(mRemoteEndpointId, filePayload)
    }

    private val mDiscCb: ScanEndpointCallback = object : ScanEndpointCallback() {
        override fun onFound(
            endpointId: String,
            discoveryEndpointInfo: ScanEndpointInfo
        ) {
                Log.d(TAG, "Found endpoint:" + discoveryEndpointInfo.name + ". Connecting.")
                mDiscoveryEngine!!.requestConnect(mEndpointName, endpointId, mConnCbRcver)
        }

        override fun onLost(endpointId: String) {
            Log.d(TAG, "Lost endpoint.")
        }
    }

    private val mConnCbSender: ConnectCallback = object : ConnectCallback() {
        override fun onEstablish(
            endpointId: String,
            connectionInfo: ConnectInfo
        ) {
            Log.d(TAG, "Accept connection.")
            mDiscoveryEngine!!.acceptConnect(endpointId, mDataCbSender)
            mRemoteEndpointName = connectionInfo.endpointName
            mRemoteEndpointId = endpointId
        }

        override fun onResult(endpointId: String, result: ConnectResult) {
            if (result.status
                    .statusCode == StatusCode.STATUS_SUCCESS
            ) {
                Log.d(
                    TAG,
                    "Connection Established. Stop discovery. Start to send file."
                )
                mDiscoveryEngine!!.stopScan()
                mDiscoveryEngine!!.stopBroadcasting()
                sendOneFile()
                mBarcodeImage!!.visibility = View.INVISIBLE
                mDescText!!.text = "Sending file $mFileName to $mRemoteEndpointName."
                mProgress!!.visibility = View.VISIBLE
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected.")
            if (isTransfer == true) {
                mProgress!!.visibility = View.INVISIBLE
                mDescText!!.text = "Connection lost."
            }
        }
    }

    private val mDataCbSender: DataCallback =
        object : DataCallback() {
            override fun onReceived(
                endpointId: String,
                data: Data
            ) {
                if (data.type == Data.Type.BYTES) {
                    val msg =
                        String(data.asBytes(), StandardCharsets.UTF_8)
                    if (msg == "Receive Success") {
                        Log.d(TAG, "Received ACK. Send next.")
                        sendOneFile()
                    }
                }
            }

            override fun onTransferUpdate(
                string: String,
                update: TransferStateUpdate
            ) {
                if (update.status == TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS) {
                } else if (update.status == TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS) {
                    showProgressSpeed(update)
                } else if (update.status == TransferStateUpdate.Status.TRANSFER_STATE_FAILURE) {
                    Log.d(TAG, "Transfer failed.")
                } else {
                    Log.d(TAG, "Transfer cancelled.")
                }
            }
        }


    private val mConnCbRcver: ConnectCallback = object : ConnectCallback() {
        override fun onEstablish(
            endpointId: String,
            connectionInfo: ConnectInfo
        ) {
            Log.d(TAG, "Accept connection.")
            mRemoteEndpointName = connectionInfo.endpointName
            mRemoteEndpointId = endpointId
            mDiscoveryEngine!!.acceptConnect(endpointId, mDataCbRcver)
        }

        override fun onResult(endpointId: String, result: ConnectResult) {
            if (result.status
                    .statusCode == StatusCode.STATUS_SUCCESS
            ) {
                Log.d(TAG, "Connection Established. Stop Discovery.")
                mDiscoveryEngine!!.stopBroadcasting()
                mDiscoveryEngine!!.stopScan()
                mDescText!!.text = "Connected."
            }
        }

        override fun onDisconnected(endpointId: String) {
            Log.d(TAG, "Disconnected.")
            if (isTransfer == true) {
                mProgress!!.visibility = View.INVISIBLE
                mDescText!!.text = "Connection lost."
            }
        }
    }

    private val mDataCbRcver: DataCallback =
        object : DataCallback() {
            override fun onReceived(
                endpointId: String,
                data: Data
            ) {
                if (data.type == Data.Type.BYTES) {
                    val msg =
                        String(data.asBytes(), StandardCharsets.UTF_8)
                    mRcvedFilename = msg
                    Log.d(TAG, "received filename: $mRcvedFilename")
                    isTransfer = true
                    mDescText!!.text = "Receiving file $mRcvedFilename from $mRemoteEndpointName."
                    mProgress!!.visibility = View.VISIBLE
                } else if (data.type == Data.Type.FILE) {
                    incomingFile = data
                } else {
                    Log.d(TAG, "received stream. ")
                }
            }

            override fun onTransferUpdate(
                string: String,
                update: TransferStateUpdate
            ) {
                if (update.status == TransferStateUpdate.Status.TRANSFER_STATE_SUCCESS) {
                } else if (update.status == TransferStateUpdate.Status.TRANSFER_STATE_IN_PROGRESS) {
                    showProgressSpeed(update)
                    if (update.bytesTransferred == update.totalBytes) {
                        Log.d(TAG, "File transfer done. Rename File.")
                        renameFile()
                        Log.d(TAG, "Send Ack.")
                        mDescText!!.text = """
                            Transfer success. Speed: ${mSpeedStr}MB/s. 
                            View the File at /Sdcard/Download/Nearby
                            """.trimIndent()
                        mTransferEngine!!.sendData(
                            mRemoteEndpointId,
                            Data.fromBytes(
                                "Receive Success".toByteArray(StandardCharsets.UTF_8)
                            )
                        )
                        isTransfer = false
                    }
                } else if (update.status == TransferStateUpdate.Status.TRANSFER_STATE_FAILURE) {
                    Log.d(TAG, "Transfer failed.")
                } else {
                    Log.d(TAG, "Transfer cancelled.")
                }
            }
        }

    private fun renameFile() {
        if (incomingFile == null) {
            Log.d(TAG, "incomingFile is null")
            return
        }
        val rawFile = incomingFile!!.asFile().asJavaFile()
        Log.d(TAG, "raw file: " + rawFile.absolutePath)
        val targetFileName = File(rawFile.parentFile, mRcvedFilename)
        Log.d(TAG, "rename to : " + targetFileName.absolutePath)
        val uri = incomingFile!!.asFile().asUri()
        if (uri == null) {
            val result = rawFile.renameTo(targetFileName)
            if (!result) {
                Log.e(TAG, "rename failed")
            } else {
                Log.e(TAG, "rename Succeeded ")
            }
        } else {
            try {
                openStream(uri, targetFileName)
            } catch (e: IOException) {
                Log.e(TAG, e.toString())
            } finally {
                delFile(uri, rawFile)
            }
        }
    }

    @Throws(IOException::class)
    private fun openStream(uri: Uri, targetFileName: File) {
        val `in` = mContext!!.contentResolver.openInputStream(uri)
        Log.e(TAG, "open input stream successfuly")
        try {
            copyStream(`in`, FileOutputStream(targetFileName))
            Log.e(TAG, "copyStream successfuly")
        } catch (e: IOException) {
            Log.e(TAG, e.toString())
        } finally {
            `in`!!.close()
        }
    }

    @Throws(IOException::class)
    private fun copyStream(
        `in`: InputStream?,
        out: OutputStream
    ) {
        try {
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`!!.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            out.flush()
        } finally {
            out.close()
        }
    }

    private fun delFile(uri: Uri, payloadfile: File) {
        // Delete the original file.
        mContext!!.contentResolver.delete(uri, null, null)
        if (!payloadfile.exists()) {
            Log.e(TAG, "delete original file by uri successfully")
        } else {
            Log.e(
                TAG,
                "delete  original file by uri failed and try to delete it by File delete"
            )
            payloadfile.delete()
            if (payloadfile.exists()) {
                Log.e(TAG, "fail to delete original file")
            } else {
                Log.e(TAG, "delete original file successfully")
            }
        }
    }

    private fun showProgressSpeed(update: TransferStateUpdate) {
        val transferredBytes = update.bytesTransferred
        val totalBytes = update.totalBytes
        val curTime = System.currentTimeMillis()
        Log.d(
            TAG, "Transfer in progress. Transferred Bytes: "
                    + transferredBytes + " Total Bytes: " + totalBytes
        )
        mProgress!!.progress = (transferredBytes * 100 / totalBytes).toInt()
        if (mStartTime == 0L) {
            mStartTime = curTime
        }
        if (curTime != mStartTime) {
            mSpeed =
                transferredBytes.toFloat() / (curTime - mStartTime).toFloat() / 1000
            val myformat = DecimalFormat("0.00")
            mSpeedStr = myformat.format(mSpeed.toDouble())
            mDescText!!.text = "Transfer in Progress. Speed: " + mSpeedStr + "MB/s."
        }
        if (transferredBytes == totalBytes) {
            mStartTime = 0
        }
    }

    private fun init() {
        mProgress!!.progress = 0
        mProgress!!.visibility = View.INVISIBLE
        mDescText!!.text = ""
        mBarcodeImage!!.visibility = View.INVISIBLE
        mDiscoveryEngine!!.disconnectAll()
        mDiscoveryEngine!!.stopScan()
        mDiscoveryEngine!!.stopBroadcasting()
        mFiles.clear()
    }
}