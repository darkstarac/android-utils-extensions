package it.wazabit.dev.extension

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Extensions : Application(){

    lateinit var sharedPreferences: SharedPreferences



    override fun onCreate() {
        super.onCreate()

        instance = this

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        sharedPreferences = applicationContext.getSharedPreferences("myawesomeprivatekey", Context.MODE_PRIVATE)
    }

    companion object {


        @get:Synchronized var instance: Extensions? = null
            private set
    }

}