/*
 * Copyright (c) 2018 Ville-Matti Anttonen
 */

package fi.anttonen.villematti.apps.gymbuddy.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.*
import fi.anttonen.villematti.apps.gymbuddy.R
import fi.anttonen.villematti.apps.gymbuddy.misc.UnitManager
import android.R.attr.button
import android.content.Intent
import android.support.v4.app.ActivityCompat
import fi.anttonen.villematti.apps.gymbuddy.activity.ExerciseEditorList


class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val sharedPreferences = preferenceScreen.sharedPreferences
        val count = preferenceScreen.preferenceCount
        for (i in 0 until count) {
            val p = preferenceScreen.getPreference(i)
            handlePreference(p, sharedPreferences)
        }

        setupExerciseEditorButtons()
    }

    private fun setupExerciseEditorButtons() {
        val strengthButton = findPreference(getString(R.string.key_edit_strength_exercises))
        strengthButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            openStrengthExerciseEditor()
            true
        }
        val cardioButton = findPreference(getString(R.string.key_edit_cardio_exercises))
        cardioButton.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            openCardioExerciseEditor()
            true
        }
    }

    private fun openStrengthExerciseEditor() {
        if (context != null) {
            val intent = Intent(activity, ExerciseEditorList::class.java).apply {
                putExtra(ExerciseEditorList.STRENGTH_EXERCISES, true)
            }
            ActivityCompat.startActivity(context!!, intent, null)
        }
    }

    private fun openCardioExerciseEditor() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

            val weightSettingKey = getString(R.string.pref_weight_unit_key)
            val distanceSettingKey = getString(R.string.pref_distance_unit_key)
            if (key == weightSettingKey) {
                sharedPreference.getString(getString(R.string.pref_weight_unit_key), getString(R.string.weight_unit_kilograms_key)).apply {
                    when (this) {
                        getString(R.string.weight_unit_kilograms_key) -> UnitManager.Units.weightRatio = UnitManager.WeightRatio.KG
                        getString(R.string.weight_unit_pounds_key) -> UnitManager.Units.weightRatio = UnitManager.WeightRatio.LBS
                    }
                }
            }
            if (key == distanceSettingKey) {
                sharedPreference.getString(getString(R.string.pref_distance_unit_key), getString(R.string.distance_unit_kilometers_key)).apply {
                    when (this) {
                        getString(R.string.distance_unit_kilometers_key) -> UnitManager.Units.distanceRatio = UnitManager.DistanceRatio.KM
                        getString(R.string.distance_unit_miles_key) -> UnitManager.Units.distanceRatio = UnitManager.DistanceRatio.M
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        super.onStop()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}