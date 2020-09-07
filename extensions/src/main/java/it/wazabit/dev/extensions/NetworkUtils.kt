package it.wazabit.dev.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun Context.isNetworkActive() :Boolean{
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        val nw      = activeNetwork ?: return false
        val actNw = getNetworkCapabilities(nw) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            //for other device how are able to connect with Ethernet
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            //for check internet over Bluetooth
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}

fun Context.getNetworkStatus() : LiveData<Boolean> {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val active = MutableLiveData<Boolean>()
    connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback(){

        override fun onAvailable(network: Network) {
            GlobalScope.launch(Dispatchers.Main){ active.value = true }
        }

        override fun onLost(network: Network) {
            GlobalScope.launch(Dispatchers.Main){ active.value = false }
        }
    })
    return active
}