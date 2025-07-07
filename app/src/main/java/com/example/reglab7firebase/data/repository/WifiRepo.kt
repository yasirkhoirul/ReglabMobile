package com.example.reglab7firebase.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.reglab7firebase.data.model.WifiRepo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class WifiRepoImpl(private val context: Context) : WifiRepo {
    override fun observeWifiSsid(): Flow<String?> = callbackFlow {
        Log.d("WIFIDEBUG", "callbackFlow dimulai (versi Plan B).")
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("WIFIDEBUG", "Izin lokasi tidak ada. Flow ditutup.")
            trySend(null)
            close(IllegalStateException("Izin ACCESS_FINE_LOCATION tidak diberikan."))
            return@callbackFlow
        }

        val updateSsid = {
            val connectionInfo = wifiManager.connectionInfo
            Log.d("WIFIDEBUG", "Plan B -> Objek SSID $connectionInfo?.ssid")
            val ssid = parseSsid(connectionInfo?.ssid)
            Log.d("WIFIDEBUG", "Plan B -> Mengambil SSID dari WifiManager: $ssid")
            trySend(ssid)
        }

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d("WIFIDEBUG", "Plan B -> onAvailable, memicu updateSsid()")
                updateSsid()
            }

            override fun onLost(network: Network) {
                Log.d("WIFIDEBUGS", "Plan B -> onLost, memicu updateSsid()")
                updateSsid()
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        Log.d("WIFIDEBUG", "Mencoba mendapatkan SSID awal dengan Plan B...")
        updateSsid()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        awaitClose {
            Log.d("WIFIDEBUG", "Flow ditutup, unregistering network callback.")
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    private fun parseSsid(ssid: String?): String? {
        Log.d("wifinyaadlaah","hasilnya $ssid")
        return ssid?.let {
            if (it.startsWith("\"") && it.endsWith("\"")) {
                it.substring(1, it.length - 1)
            } else if (it == "<unknown ssid>") {
                null
            } else {
                it
            }
        }
    }

}