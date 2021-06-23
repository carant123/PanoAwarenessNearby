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

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.huawei.hms.panorama.Panorama
import com.huawei.hms.panorama.PanoramaInterface
import com.huawei.panoramawarenessnearby.BaseActivity
import com.huawei.panoramawarenessnearby.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainPanoramaActivity : BaseActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    override fun getLayout(): Int = R.layout.activity_main

    override fun inicializar() {

    }

    private fun returnResource(drawable: Int): Uri {
        return Uri.parse("android.resource://$packageName/$drawable")
    }

}
