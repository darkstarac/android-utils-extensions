package it.wazabit.dev.extension.ds.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class HttpClient {

    companion object{
        fun getInstance(context: Context): OkHttpClient {

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val builder = OkHttpClient.Builder()

            builder.readTimeout(120, TimeUnit.SECONDS)
            builder.addInterceptor(loggingInterceptor)


            return builder.build()
        }



    }

    object Exceptions{
        class InvalidCredentialsException(message:String) : Exception(message)
        class InvalidResponseException(message:String? = null) : Exception(message)
        class InvalidJsonException(message:String? = null) : Exception(message)
    }

}