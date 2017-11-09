package com.labz.base.app

import android.app.Application
import com.labz.R
import uk.co.chrisjenx.calligraphy.CalligraphyConfig


/**
 * Created by Stanislav Vylegzhanin on 09.10.17.
 */
class LabsApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(
                CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}