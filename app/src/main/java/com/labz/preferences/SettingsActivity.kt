package com.labz.preferences

import android.os.Bundle
import com.labz.R
import com.labz.base.activity.BaseActivity

class SettingsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        replaceActivityFragment(SettingsFragment.newInstance(), false, true)
    }
}
