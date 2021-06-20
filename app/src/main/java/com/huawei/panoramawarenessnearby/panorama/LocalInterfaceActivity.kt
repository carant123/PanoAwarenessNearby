/*
* Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

* Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package com.huawei.panoramawarenessnearby.panorama

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import com.huawei.hms.panorama.Panorama
import com.huawei.hms.panorama.PanoramaInterface
import com.huawei.hms.panorama.PanoramaInterface.PanoramaLocalInterface
import com.huawei.hms.panorama.PanoramaLocalApi
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import kotlinx.android.synthetic.main.activity_local_interface.*

class LocalInterfaceActivity : BaseActivity(), OnTouchListener, View.OnClickListener {

    private lateinit var mLocalInterface: PanoramaLocalInterface
    private var mChangeButtonCompass = false

    companion object {
        private const val TAG: String = "LocalInterfaceActivity"
    }

    override fun getLayout(): Int = R.layout.activity_local_interface

    override fun inicializar() {
        val intent = intent
        val uri = intent.data
        val type = intent.getIntExtra("PanoramaType", PanoramaInterface.IMAGE_TYPE_RING)
        callLocalApi(uri, type)
    }

    private fun callLocalApi(uri: Uri?, type: Int) {
        mLocalInterface = Panorama.getInstance().getLocalInstance(this)
        mLocalInterface.init()
        if(mLocalInterface.init() == 0 && mLocalInterface.setImage(uri,type) == 0){
            var view: View = mLocalInterface.view
            relativeLayout.addView(view)

            view.setOnTouchListener(this@LocalInterfaceActivity)
            changeButton.apply {
                bringToFront()
                setOnClickListener(this@LocalInterfaceActivity)
            }
        } else {
            Log.e(TAG, "local api error")
        }
    }

    private fun returnResource(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

    override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
        mLocalInterface.let {
            it.updateTouchEvent(event)
        }
        return true
    }

    override fun onClick(view: View?) {
        if(view?.id == R.id.changeButton) {
            if(mChangeButtonCompass) {
                mChangeButtonCompass = false
                mLocalInterface.setControlMode(PanoramaInterface.CONTROL_TYPE_TOUCH)
                mLocalInterface.setImage(
                    returnResource(R.raw.pano),
                    PanoramaLocalApi.IMAGE_TYPE_SPHERICAL
                )
            } else {
                mChangeButtonCompass = true
                mLocalInterface.setControlMode(PanoramaInterface.CONTROL_TYPE_TOUCH)
                mLocalInterface.setImage(
                    returnResource(R.raw.pano2),
                    PanoramaLocalApi.IMAGE_TYPE_RING
                )
            }
        }
    }

}