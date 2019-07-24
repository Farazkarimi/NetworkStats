package com.example.networkstats.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager.TYPE_WIMAX
import android.provider.Settings
import androidx.core.content.ContextCompat.getSystemService


object NetworkUtils {
    @SuppressLint("MissingPermission")
    fun getNetworkType(context: Context): String {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                ?: return "NO CONNECTIVITY"
        val netInfo = manager.activeNetworkInfo ?: return "NO ACTIVE NETWORK"
        return netInfo.typeName.toLowerCase()
    }

    @SuppressLint("MissingPermission")
    fun isNetworkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = manager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    @SuppressLint("MissingPermission")
    fun isChargeFreeNetworkAvailable(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = manager.activeNetworkInfo
        return (activeNetworkInfo != null && activeNetworkInfo.isConnected
                && (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
                || activeNetworkInfo.type == TYPE_WIMAX
                || activeNetworkInfo.type == ConnectivityManager.TYPE_ETHERNET))
    }

    fun isInAirplaneMode(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) !== 0
        else
            Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) !== 0
    }
}