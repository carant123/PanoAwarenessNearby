package com.huawei.panoramawarenessnearby.nearbyservice.nearby

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.os.RemoteException
import android.util.Log
import com.huawei.hms.nearby.Nearby
import com.huawei.hms.nearby.StatusCode
import com.huawei.hms.nearby.discovery.*
import com.huawei.hms.nearby.transfer.Data
import com.huawei.hms.nearby.transfer.DataCallback
import com.huawei.hms.nearby.transfer.TransferEngine
import com.huawei.hms.nearby.transfer.TransferStateUpdate
import java.nio.charset.Charset

class ChatService : ConnectCallback {
    private var myName: String? = null
    private var friendName: String? = null
    private var serviceId: String? = null
    private var endpointId: String? = null
    private var context: Context? = null
    private var mDiscoveryEngine: DiscoveryEngine? = null
    private var mTransferEngine: TransferEngine? = null
    private var connectTaskResult = 0
    private var listener: ChatServiceListener? = null
    private val TAG = "ChatService"
    private val TIMEOUT_MILLISECONDS = 10000

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            this.removeMessages(0)
            if (connectTaskResult != StatusCode.STATUS_SUCCESS) {
                if (listener != null) {
                    listener!!.onDisconnected()
                }
                displayToast("Connection timeout, make sure your friend is ready and try again.")
                if (myName!!.compareTo(friendName!!) > 0) {
                    mDiscoveryEngine!!.stopScan()
                } else {
                    mDiscoveryEngine!!.stopBroadcasting()
                }
            }
        }
    }

    constructor(myName: String?, friendName: String?) : super() {
        this.myName = myName
        this.friendName = friendName
        connectTaskResult = StatusCode.STATUS_ENDPOINT_UNKNOWN
        serviceId = if (friendName?.let { myName?.compareTo(it) }!! > 0) {
            myName + friendName
        } else {
            friendName + myName
        }
    }

    fun setListener(listener: ChatServiceListener?) {
        this.listener = listener
    }

    fun connect(context: Context?) {
        displayToast("Connecting to your friend.")
        this.context = context
        mDiscoveryEngine = Nearby.getDiscoveryEngine(context)
        try {
            if (myName!!.compareTo(friendName!!) > 0) {
                startScanning() //Client Mode
            } else {
                startBroadcasting() //Server Mode
            }
        } catch (e: RemoteException) {
            Log.e(TAG, e.toString())
        }
        Log.e(TAG, "sending message")
        handler.sendEmptyMessageDelayed(0, TIMEOUT_MILLISECONDS.toLong())
    }

    @Throws(RemoteException::class)
    fun startScanning() {
        Log.e(TAG, "startScanning()")
        val discBuilder = ScanOption.Builder()
        discBuilder.setPolicy(Policy.POLICY_STAR)
        mDiscoveryEngine!!.startScan(serviceId, scanEndpointCallback, discBuilder.build())
    }

    @Throws(RemoteException::class)
    fun startBroadcasting() {
        val advBuilder = BroadcastOption.Builder()
        advBuilder.setPolicy(Policy.POLICY_STAR)
        mDiscoveryEngine!!.startBroadcasting(myName, serviceId, this, advBuilder.build())
    }

    private fun displayToast(message: String) {
        if (listener != null) listener!!.showToast(message)
    }

    fun sendMessage(message: String) {
        val data =
            Data.fromBytes(message.toByteArray(Charset.defaultCharset()))
        Log.d(TAG, "myEndpointId $endpointId")
        mTransferEngine!!.sendData(endpointId, data)
            .addOnSuccessListener { result: Void? ->
                val item = MessageBean()
                item.setMyName(myName)
                item.setFriendName(friendName)
                item.setMsg(message)
                item.setSend(true)
                if (listener != null) listener!!.onMessageSent(item)
            }
    }


    private val scanEndpointCallback: ScanEndpointCallback = object : ScanEndpointCallback() {
        override fun onFound(
            endpointId: String,
            discoveryEndpointInfo: ScanEndpointInfo
        ) {
            this@ChatService.endpointId = endpointId
            mDiscoveryEngine!!.requestConnect(myName, endpointId, this@ChatService)
        }

        override fun onLost(endpointId: String) {
            Log.d(TAG, "Nearby Connection Demo app: Lost endpoint: $endpointId")
        }
    }

    private val dataCallback: DataCallback =
        object : DataCallback() {
            override fun onReceived(
                string: String,
                data: Data
            ) {
                val item = MessageBean()
                item.setMyName(myName)
                item.setFriendName(friendName)
                item.setMsg(String(data.asBytes()))
                item.setSend(false)
                if (listener != null) listener!!.onMessageReceived(item)
            }

            override fun onTransferUpdate(
                string: String,
                update: TransferStateUpdate
            ) {
            }
        }

    override fun onEstablish(
        endpointId: String?,
        connectionInfo: ConnectInfo?
    ) {
        mTransferEngine = Nearby.getTransferEngine(context)
        this.endpointId = endpointId
        mDiscoveryEngine!!.acceptConnect(endpointId, dataCallback)
        displayToast("Let's chat!")
        connectTaskResult = StatusCode.STATUS_SUCCESS
        if (listener != null) listener!!.onConnection()
        if (myName!!.compareTo(friendName!!) > 0) {
            mDiscoveryEngine!!.stopScan()
        } else {
            mDiscoveryEngine!!.stopBroadcasting()
        }
    }

    override fun onResult(endpointId: String?, resolution: ConnectResult?) {
        this.endpointId = endpointId
    }

    override fun onDisconnected(endpointId: String?) {
        displayToast("Disconnect.")
        connectTaskResult = StatusCode.STATUS_NOT_CONNECTED
        if (listener != null) listener!!.onDisconnected()
    }


    interface ChatServiceListener {
        fun showToast(message: String?)
        fun onConnection()
        fun onDisconnected()
        fun onMessageReceived(item: MessageBean?)
        fun onMessageSent(item: MessageBean?)
    }
}