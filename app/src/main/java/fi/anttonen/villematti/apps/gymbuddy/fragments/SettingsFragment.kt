package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.*
import fi.anttonen.villematti.apps.gymbuddy.R

class SettingsFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val sharedPreferences = preferenceScreen.sharedPreferences
        val count = preferenceScreen.preferenceCount
        for (i in 0 until count) {
            val p = preferenceScreen.getPreference(i)
            handlePreference(p, sharedPreferences)
        }
    }

    private fun handlePreference(preference: Preference, sharedPreferences: SharedPreferences) {
        if (preference is PreferenceCategory) {
            for (i in 0 until preference.preferenceCount) {
                handlePreference(preference.getPreference(i), sharedPreferences)
            }
        }
        if (preference !is CheckBoxPreference) {
            val value = sharedPreferences.getString(preference.key, "")
            setPreferenceSummary(preference, value)
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String) {
        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(value)
            if (prefIndex >= 0) {
                preference.summary = preference.entries[prefIndex]
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreference: SharedPreferences?, key: String?) {
        val preference = findPreference(key)
        if (preference != null && sharedPreference != null) {
            setPreferenceSummary(preference, sharedPreference.getString(preference.key, ""))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}