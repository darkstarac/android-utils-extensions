package it.wazabit.dev.extension.ui.permissions

import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.preference.PreferenceFragmentCompat
import it.wazabit.dev.extension.R
import it.wazabit.dev.extensions.activity.checkPermission
import timber.log.Timber

class PermissionsSettingsFragment : PreferenceFragmentCompat() , SharedPreferences.OnSharedPreferenceChangeListener {


    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        preferenceManager.sharedPreferences.edit { putBoolean("camera",isGranted) }
    }

    private val requestExternalStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        preferenceManager.sharedPreferences.edit { putBoolean("external_storage",isGranted) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Timber.d("Preference changed $key $sharedPreferences")
        when(key){
            "camera" -> {
                sharedPreferences?.getBoolean("camera",false)?.let {
                    if (it){
                        requireActivity().checkPermission(requestCameraPermissionLauncher,"Why Not",Manifest.permission.CAMERA){
                            if (!it)preferenceManager.sharedPreferences.edit { putBoolean("camera",false) }
                        }
                    }
                }
            }
            "external_storage" -> {
                sharedPreferences?.getBoolean("external_storage",false)?.let {
                    if (it){
                        requireActivity().checkPermission(requestExternalStoragePermissionLauncher,"Why Not",Manifest.permission.WRITE_EXTERNAL_STORAGE){
                            preferenceManager.sharedPreferences.edit { putBoolean("external_storage",false) }
                        }
                    }
                }
            }
            else -> TODO("not implemented yet")
        }
    }


}


