package com.huawei.panoramawarenessnearby.nearbyservice.nearby

class MessageBean {
    private var msg: String? = null
    private var isSend = false
    private var myName: String? = null
    private var friendName: String? = null

    fun MessageBean() {}

    fun getMsg(): String? {
        return msg
    }

    fun setMsg(msg: String?) {
        this.msg = msg
    }

    fun isSend(): Boolean {
        return isSend
    }

    fun setSend(send: Boolean) {
        isSend = send
    }

    fun getMyName(): String? {
        return myName
    }

    fun setMyName(myName: String?) {
        this.myName = myName
    }

    fun getFriendName(): String? {
        return friendName
    }

    fun setFriendName(friendName: String?) {
        this.friendName = friendName
    }
}