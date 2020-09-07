package it.wazabit.dev.extension.ds

import timber.log.Timber

interface Repository {
    fun amethod(){ Timber.d("A base method")}
}