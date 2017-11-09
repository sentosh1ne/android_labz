package com.labz.preferences

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.labz.R
import com.labz.main.MainActivity
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Stanislav Vylegzhanin on 19.10.17.
 */
class SettingsFragment : PreferenceFragmentCompat(), AnkoLogger {

    private lateinit var preferences: PreferencesHelper

    companion object {
        fun newInstance(): Fragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.app_preferences)
        preferences = PreferencesHelper.getInstance(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findPreference("themePreference").onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    preferences.saveThemeType(newValue.toString())
                    startActivity<MainActivity>()
                    return@OnPreferenceChangeListener true
                }

        findPreference("textPreference").onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    preferences.saveTexType(newValue.toString())
                    startActivity<MainActivity>()
                    return@OnPreferenceChangeListener true
                }
    }
}