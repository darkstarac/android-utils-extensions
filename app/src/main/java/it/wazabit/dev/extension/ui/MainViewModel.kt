package it.wazabit.dev.extension.ui

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import it.wazabit.dev.extension.ds.MainRepository
import it.wazabit.dev.extension.ui.network.NetworkFragment
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(@Assisted private val savedStateHandle: SavedStateHandle, private val mainRepository: MainRepository)  : ViewModel(){

    init {
        Timber.d("MainViewModel init")
    }

    val loading = MutableLiveData<Boolean>()

    val users = mainRepository.users

    fun test(value: NetworkFragment.NetworkErrorSimulation?) = mainRepository.test(value)
}