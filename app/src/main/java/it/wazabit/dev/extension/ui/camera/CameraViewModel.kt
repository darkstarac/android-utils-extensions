package it.wazabit.dev.extension.ui.camera

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class CameraViewModel @Inject constructor() : ViewModel(){

    val preview = MutableLiveData<Bitmap>()

}