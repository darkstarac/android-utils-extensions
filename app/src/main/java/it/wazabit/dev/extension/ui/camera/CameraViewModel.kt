package it.wazabit.dev.extension.ui.camera

import android.graphics.Bitmap
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import it.wazabit.dev.extension.ds.MainRepository
import timber.log.Timber

class CameraViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val mainRepository: MainRepository
)  : ViewModel(), LifecycleObserver {

    init {
        Timber.d("Camera view model init")
    }

    val preview = MutableLiveData<Bitmap>()

    val sharedValue = MutableLiveData<String>()

}