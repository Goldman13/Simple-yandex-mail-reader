package com.dimnowgood.bestapp.ui

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.dimnowgood.bestapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val checkNumber: EditTextPreference? = findPreference("NumberOfCheckMessages")

        checkNumber?.setOnBindEditTextListener { editText ->
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        
        checkNumber?.setOnPreferenceChangeListener { preference, newValue ->
           newValue.toString()[0] != '0' && newValue.toString().toInt()<=100
        }
    }
}