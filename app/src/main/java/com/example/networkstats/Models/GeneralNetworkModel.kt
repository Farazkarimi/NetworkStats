package com.example.networkstats.Models

import android.telephony.CellInfo

data class GeneralNetworkModel(
    val lat: Double? = null,
    val long: Double? = null,
    val networkType: String? = null,
    val operator: String? = null,
    val mcc: Int? = null,
    val mnc: Int? = null,
    val upLink: Float? = null,
    val downLink: Float? = null,
    val neighbours: List<CellInfo>? = null,
    val bandWith: String? = null,
    val imei: String? = null,
    val imsi: String? = null,
    val handsetMode: String? = null,
    val ping: Int? = null,
    val jitter: String? = null
                               )