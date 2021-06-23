package com.huawei.panoramawarenessnearby.nearbyservice.nearby

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.huawei.panoramawarenessnearby.databinding.NearbyBinding
import java.util.*

class NearbyFragment: Fragment(), ChatService.ChatServiceListener {

    private val PERMISSION_CODE = 1
    private var binding: NearbyBinding? = null
    private var messages: ArrayList<MessageBean?>? = null
    private var chatService: ChatService? = null
    private var adapter: ChatAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //nearbyViewModel = new ViewModelProvider(this).get(NearbyViewModel.class);
        binding = NearbyBinding.inflate(inflater, container, false)
        binding?.btnConnect?.setOnClickListener { view ->
            if (checkPermissions()) {
                setupConnection()
            } else requestPermissions()
        }
        binding?.btnSend?.setOnClickListener { view ->
            val message: String = binding?.etMsg?.text.toString()
            if (isMessageValid(message)) {
                binding?.etMsg?.setText("")
                chatService!!.sendMessage(message)
            }
        }
        return binding?.root
    }

    fun setupConnection() {
        val myName: String = binding?.etMyName?.text.toString()
        val friendName: String = binding?.etFriendName?.text.toString()
        if (validateNames(myName, friendName)) {
            if (chatService == null) chatService = ChatService(myName, friendName)
            chatService!!.setListener(this)
            chatService!!.connect(requireContext().applicationContext)
        }
    }


    private fun validateNames(
        myName: String,
        friendName: String
    ): Boolean {
        if (TextUtils.isEmpty(myName)) {
            showShortToastTop("Please input your name.")
            return false
        }
        if (TextUtils.isEmpty(friendName)) {
            showShortToastTop("Please input your friend's name.")
            return false
        }
        if (TextUtils.equals(myName, friendName)) {
            showShortToastTop("Please input two different names.")
            return false
        }
        return true
    }


    fun checkPermissions(): Boolean {
        val acl = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val afl = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        return acl == PackageManager.PERMISSION_GRANTED || afl == PackageManager.PERMISSION_GRANTED
    }

    fun getPermissions(): Array<String?>? {
        return arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun requestPermissions() {
        requestPermissions(getPermissions()!!, PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        run { if (checkPermissions()) setupConnection() }
    }

    fun showShortToastTop(msg: String?) {
        val toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    private fun isMessageValid(message: String): Boolean {
        if (TextUtils.isEmpty(message)) {
            showShortToastTop("Please input data you want to send.")
            return false
        }
        return true
    }


    override fun showToast(message: String?) {
        showShortToastTop(message)
    }

    override fun onConnection() {
        messages = ArrayList()
        adapter = ChatAdapter()
        adapter!!.setItems(messages)
        val manager = LinearLayoutManager(requireContext())
        manager.reverseLayout = true
        binding?.recycler?.layoutManager = manager
        binding?.recycler?.adapter = adapter
    }

    override fun onDisconnected() {
        Log.e("TAG", "onDisconnected")
    }

    override fun onMessageReceived(item: MessageBean?) {
        messages!!.add(0, item)
        adapter!!.notifyDataSetChanged()
    }

    override fun onMessageSent(item: MessageBean?) {
        messages!!.add(0, item)
        adapter!!.notifyDataSetChanged()
    }
}