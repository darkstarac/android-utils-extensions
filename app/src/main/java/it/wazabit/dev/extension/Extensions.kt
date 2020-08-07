package it.wazabit.dev.extension

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import it.wazabit.dev.extension.di.DaggerApplicationComponent
import timber.log.Timber

class Extensions : Application(){

    lateinit var sharedPreferences: SharedPreferences

    val  applicationComponent by lazy {
        DaggerApplicationComponent.factory().create(applicationContext)
    }

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