package br.com.marcottc.weatherpeek.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class NetworkUtil {

    companion object {
        fun hasConnectivity(connectivityManager: ConnectivityManager): Boolean {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }
    }
}