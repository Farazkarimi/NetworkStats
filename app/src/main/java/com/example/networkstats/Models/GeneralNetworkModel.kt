package com.example.networkstats.Models

import android.telephony.CellInfo

data class GeneralNetworkModel(
    val lat: Double? = null,
    val long: Double? = null,
    val networkType: String? = null,
    val operator: String? = null,
    val mcc: Int? = null,
    val mnc: Int? = null,
    var upLink: String? = null,
    var downLink: String? = null,
    val neighbours: String? = null,
    val bandWith: String? = null,
    val imei: String? = null,
    val imsi: String? = null,
    val handsetModel: String? = null,
    val ping: Int? = null,
    var jitter: String? = null
                               )