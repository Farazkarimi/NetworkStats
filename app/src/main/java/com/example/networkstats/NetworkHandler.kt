package com.example.networkstats

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Build
import android.telephony.*
import android.text.TextUtils
import android.util.Log
import com.example.networkstats.Models.FourGNetworkModel
import com.example.networkstats.Models.GeneralNetworkModel
import com.example.networkstats.Models.ThreeGNetworkModel
import com.example.networkstats.Models.TwoGNetworkModel
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import java.io.IOException


class NetworkHandler(context: Context) {


    private var context: Context = context
    private val tele = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).createForSubscriptionId(0)
    } else {
        (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager)
    }


    @SuppressLint("MissingPermission")
    private fun getIMSI(): String {
        var imsi = String()
        if (tele.subscriberId != null) {
            imsi = tele.subscriberId
        }
        return imsi
    }


    // Decimal -> hexadecimal
    private fun DecToHex(dec: Int): String {
        return String.format("%x", dec)
    }

    // hex -> decimal
    private fun HexToDec(hex: String): Int {
        return Integer.parseInt(hex, 16)
    }

    @SuppressLint("MissingPermission")
    private fun getCellInfoString(): String? {
        var ret: String? = null
        val listCellInfo = tele.allCellInfo
        if (listCellInfo != null)
            for (a_Info in listCellInfo!!) {
                if (a_Info is CellInfoCdma)
                    ret = ret + "\n Cell info cdma: " + a_Info.toString()
                else if (a_Info is CellInfoGsm)
                    ret = ret + "\n Cell info gsm: " + a_Info.toString()
                else if (a_Info is CellInfoLte) {
                    ret = ret + "\n Cell info lte: " + a_Info.toString()
                    val cellInfoLte = a_Info as CellInfoLte
                    val cellIdentity = cellInfoLte
                        .cellIdentity

                } else if (a_Info is CellInfoWcdma)
                    ret = ret + "\n Cell info : wcdma" + a_Info
            }
        return ret
    }

    @SuppressLint("MissingPermission")
    private fun getCellInfo(): CellInfo {
        var cellInfo: CellInfo? = null
        val listCellInfo = tele.allCellInfo
        if (listCellInfo != null)
            for (a_Info in listCellInfo!!) {
                if (a_Info is CellInfoCdma)
                    cellInfo = a_Info
                else if (a_Info is CellInfoGsm)
                    cellInfo = a_Info
                else if (a_Info is CellInfoLte) {
                    cellInfo = a_Info
                    val cellInfoLte = a_Info as CellInfoLte
                    val cellIdentity = cellInfoLte
                        .cellIdentity

                } else if (a_Info is CellInfoWcdma)
                    cellInfo = a_Info
            }
        return cellInfo!!
    }

    private fun getSignalStrengthDbm(cellInfo: CellInfo): Int {
        return (cellInfo as? CellInfoCdma)?.cellSignalStrength?.dbm
            ?: ((cellInfo as? CellInfoGsm)?.cellSignalStrength?.dbm
                ?: ((cellInfo as? CellInfoLte)?.cellSignalStrength?.dbm
                    ?: ((cellInfo as? CellInfoWcdma)?.cellSignalStrength?.dbm
                        ?: 0)))
    }

    private fun getSignalStrengthLevel(cellInfo: CellInfo): Int {
        return (cellInfo as? CellInfoCdma)?.cellSignalStrength?.level
            ?: ((cellInfo as? CellInfoGsm)?.cellSignalStrength?.level
                ?: ((cellInfo as? CellInfoLte)?.cellSignalStrength?.level
                    ?: ((cellInfo as? CellInfoWcdma)?.cellSignalStrength?.level
                        ?: 0)))
    }

    private fun getSimOperatorByMnc(): String? {
        val operator = tele.simOperator ?: return null
        return when (operator) {
            "35" -> "Irancell"
            "11" -> "MCI"
            "20" -> "Rightel"
            "12" -> "Hiweb"
            "70" -> "wll"
            "41" -> "uso"
            "91" -> "kifzo"
            "32" -> "Taliya"
            else -> operator
        }
    }

    @SuppressLint("MissingPermission")
    fun get2gNetworkModel(): TwoGNetworkModel {
        return TwoGNetworkModel(
            getRxLevel(),
            getRxQual(),
            getLAC(),
            getCellID(),
            getBcch(),
            getArfcn()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getRxLevel(): String {
        var rxLevel = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    rxLevel = (2 * (cellInfo.cellSignalStrength.asuLevel) - 113).toString()
                }
            }
        }
        return rxLevel
    }

    @SuppressLint("MissingPermission")
    private fun getRxQual(): String {
        var rxQual = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    // rxQual = cellInfo.cellSignalStrength.bitErrorRate
                }
            }
        }
        return rxQual
    }

    @SuppressLint("MissingPermission")
    private fun getCellID(): String {
        var cellID = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    cellID = cellInfo.cellIdentity.ci.toString()

                } else if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    cellID = cellInfo.cellIdentity.cid.toString()
                } else if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                    cellID = cellInfo.cellIdentity.cid.toString()
                } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                    cellID = cellInfo.cellIdentity.basestationId.toString()
                    //cellID = ((tele.cellLocation) as GsmCellLocation).cid
                }
            }
        }
        return cellID
    }

    @SuppressLint("MissingPermission")
    private fun getBcch(): String {
        var bcch = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        bcch = cellInfo.cellIdentity.bsic.toString()
                    } else {
                        val cellIdentityString = cellInfo.cellIdentity.toString()
                        try {
                            val cellIdentityArray = cellIdentityString.split(" ").toMutableList()
                            cellIdentityArray.removeAt(0)
                            val cellSignalMap = cellIdentityArray.associate {
                                val (l, r) = it.split("=")
                                l to r
                            }
                            bcch = cellSignalMap["mBsic"]?.substring(2)?.let { HexToDec(it).toString() }
                                ?: String()
                        }catch (e: Exception){
                            Log.e("Error","getBcch Error")
                        }

                    }
                }
            }
        }
        return bcch
    }

    @SuppressLint("MissingPermission")
    private fun getArfcn(): String {
        var arfcn = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        arfcn = cellInfo.cellIdentity.arfcn.toString()
                    } else {
                        arfcn = String()
                    }
                }
            }
        }
        return arfcn
    }


    //GET 3g Network Model\\

    @SuppressLint("MissingPermission")
    fun get3gNetworkModel(): ThreeGNetworkModel {
        return ThreeGNetworkModel(
            getRscp(),
            getEcNO(),
            getLAC(),
            getPSC(),
            getCQI(),
            getUArfcn(),
            getNodeB()
        )
    }


    @SuppressLint("MissingPermission")
    private fun getRscp(): String {
        var rscp = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                    rscp = (cellInfo.cellSignalStrength.asuLevel - 116).toString()
                } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                    rscp = (cellInfo.cellSignalStrength.asuLevel - 116).toString()
                }
            }
        }
        return rscp
    }

    @SuppressLint("MissingPermission")
    private fun getEcNO(): String {
        var ecNo = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                    // ecNo = RSCP - RSSI
                } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                    // ecNo = RSCP - RSSI
                }
            }
        }
        return ecNo
    }

    @SuppressLint("MissingPermission")
    private fun getLAC(): String {
        var lac = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    lac = cellInfo.cellIdentity.lac.toString()
                }
                if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                    lac = cellInfo.cellIdentity.lac.toString()
                }
            }
        }
        return lac
    }


    @SuppressLint("MissingPermission")
    private fun getPSC(): String {
        var psc = String()
        val cellinfo = tele.allCellInfo
        if (cellinfo != null) {
            for (cell in cellinfo) {
                if (cell.isRegistered && cell is CellInfoWcdma) {
                    psc = cell.cellIdentity.psc.toString()
                }
            }
        }
        return psc
    }

    @SuppressLint("MissingPermission")
    private fun geteNodeB(): String {
        var eNodeB = String()
        val cellinfo = tele.allCellInfo
        if (cellinfo != null) {
            for (cellInfo in cellinfo) {
                if (cellInfo.isRegistered && cellInfo is CellInfoLte) {
                    val cellIdHex = DecToHex(cellInfo.cellIdentity.ci)
                    val eNBHex = cellIdHex
                        .substring(0, cellIdHex.length - 2)
                    eNodeB = (HexToDec(eNBHex)).toString()
                }
            }
        }
        return eNodeB
    }


    @SuppressLint("MissingPermission")
    private fun getCQI(): String {
        var cqi: String = String()
        var cqiMap: Map<String, Int> = mapOf()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    val cqiString = cellInfo.cellSignalStrength.toString()
                    if (cqiString.length > 0) {
                        try {
                            val cqiArray = cqiString.split(" ").toMutableList()
                            cqiArray.removeAt(0)
                            cqiMap = cqiArray.associate {
                                val (l, r) = it.split("=")
                                l to r.toInt()
                            }
                            cqi = cqiMap["cqi"].toString()
                        }catch (e: Exception){
                            Log.e("Error", "getCQI Error")
                        }

                    }
                }
            }
        }
        return cqi
    }

    @SuppressLint("MissingPermission")
    private fun getUArfcn(): String {
        var uArfcn = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        uArfcn = cellInfo.cellIdentity.uarfcn.toString()
                    } else {
                        val cellIdentityString = cellInfo.cellIdentity.toString()
                        try{
                            val cellIdentityArray = cellIdentityString.split(" ").toMutableList()
                            cellIdentityArray.removeAt(0)
                            val cellSignalMap = cellIdentityArray.associate {
                                val (l, r) = it.split("=")
                                l to r.toInt()
                            }
                            uArfcn = cellSignalMap["mUarfcn"].toString()
                        }catch (e: Exception){
                            Log.e("Error","getUArfcn Error")
                        }

                    }

                }
            }
        }
        return uArfcn
    }

    @SuppressLint("MissingPermission")
    private fun getNodeB(): String {
        var NodeB = String()
        val cellinfo = tele.allCellInfo
        if (cellinfo != null) {
            for (cellInfo in cellinfo) {
                if (cellInfo.isRegistered && cellInfo is CellInfoCdma) {
                    val cellIdHex = DecToHex(cellInfo.cellIdentity.basestationId)
                    val eNBHex = cellIdHex
                        .substring(0, cellIdHex.length - 2)
                    NodeB = (HexToDec(eNBHex)).toString()
                }
                if (cellInfo.isRegistered && cellInfo is CellInfoWcdma) {
                    val cellIdHex = DecToHex(cellInfo.cellIdentity.cid)
                    val eNBHex = cellIdHex
                        .substring(0, cellIdHex.length - 2)
                    NodeB = (HexToDec(eNBHex)).toString()
                }
            }
        }
        return NodeB
    }

    //GET 4g Network Model\\
    @SuppressLint("MissingPermission")
    fun get4gNetworkModel(): FourGNetworkModel {

        return FourGNetworkModel(
            getRSRP(),
            getRSRQ(),
            getTAC(),
            getpci(),
            getRssi(),
            getCQI(),
            geteNodeB(),
            getEArfcn()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getRSRP(): String {
        var rsrp = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        rsrp = cellInfo.cellSignalStrength.rsrp.toString()
                    } else {
                        rsrp = cellInfo.cellSignalStrength.dbm.toString()
                    }
                }
            }
        }
        return rsrp
    }

    @SuppressLint("MissingPermission")
    private fun getRSRQ(): String {
        var rsrq = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        rsrq = cellInfo.cellSignalStrength.rsrq.toString()
                    } else {
                        try {
                            val cellSignalString = cellInfo.cellSignalStrength.toString()
                            val cellSignalArray = cellSignalString.split(" ").toMutableList()
                            cellSignalArray.removeAt(0)
                            val cellSignalMap = cellSignalArray.associate {
                                val (l, r) = it.split("=")
                                l to r.toInt()
                            }
                            rsrq = cellSignalMap["rsrq"].toString()
                        } catch (e: Exception) {
                            Log.e("Error", "getRSRQ Error")
                        }
                    }
                }
            }
        }
        return rsrq
    }

    @SuppressLint("MissingPermission")
    private fun getTAC(): String {
        var tac = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    tac = cellInfo.cellIdentity.tac.toString()
                }
            }
        }
        return tac
    }

    @SuppressLint("MissingPermission")
    private fun getEArfcn(): String {
        var earfcn = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        earfcn = cellInfo.cellIdentity.earfcn.toString()
                    } else {
                        val cellIdentityString = cellInfo.cellIdentity.toString()
                        try {
                            val cellIdentityArray = cellIdentityString.split(" ").toMutableList()
                            cellIdentityArray.removeAt(0)
                            val cellSignalMap = cellIdentityArray.associate {
                                val (l, r) = it.split("=")
                                l to r.toInt()
                            }
                            earfcn = cellSignalMap["mEarfcn"].toString()
                        } catch (e: Exception) {
                            Log.e("Error", "getEarfcn Error")
                        }

                    }
                }
            }
        }
        return earfcn
    }


    @SuppressLint("MissingPermission")
    private fun getpci(): String {
        var pci = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    pci = cellInfo.cellIdentity.pci.toString()
                }
            }
        }
        return pci
    }

    @SuppressLint("MissingPermission")
    private fun getRssi(): String {
        var rssi = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    try {
                        // rssi = cellInfo.cellSignalStrength.rssi.toString()
                    } catch (e: IOException) {
                        Log.e("Exception", "No Rssi Found")
                    }
                }
            }
        }
        return rssi
    }


    //GET General Network Model\\
    @SuppressLint("MissingPermission")
    fun getGeneralNetworkModel(): GeneralNetworkModel {
        var generalNetworkModel: GeneralNetworkModel
        val mnc = getMNC()
        val mcc = getMCC()
        val operator = getOperatorName()
        val lat = getLat()
        val long = getLong()
        val networkType = getNewtworkType()
        val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && tele.imei != null) {
            tele.imei
        } else {
            tele.getDeviceId()
        }
        generalNetworkModel = GeneralNetworkModel(
            lat,
            long,
            networkType,
            operator,
            mcc,
            mnc,
            getUploadSpeed(),
            getDownloadSpeed(),
            getNeighbors()
            ,
            null,
            imei,
            getIMSI(),
            getDeviceName(),
            null,
            null
        )
        return generalNetworkModel
    }

    @SuppressLint("MissingPermission")
    private fun getMNC(): Int {
        var mnc: Int = 0
        if (tele.networkOperator.length > 0) {
            mnc = Integer.parseInt(tele.networkOperator.substring(3))
        }

        return mnc
    }

    @SuppressLint("MissingPermission")
    private fun getMCC(): Int {
        var mcc: Int = 0
        if (tele.networkOperator.length > 0) {
            mcc = Integer.parseInt(tele.networkOperator.substring(0, 3))
        }
        return mcc
    }

    @SuppressLint("MissingPermission")
    private fun getNeighbors(): String {
        var neighbors = String()
        var neighboringCellList: MutableList<CellInfo> = mutableListOf()
        val cellinfo = tele.allCellInfo
        if (cellinfo != null) {
            for (cell in cellinfo) {
                if (!cell.isRegistered) {
                    neighboringCellList.add(cell)
                }
            }
            neighbors = neighboringCellList.toString()
        }
        return neighbors
    }


    @SuppressLint("MissingPermission")
    private fun getOperatorName(): String {
        return tele.networkOperatorName
    }


    private fun getNetworkTypeString(Ntype: Int): String {
        var type = "unknown"
        when (Ntype) {
            TelephonyManager.NETWORK_TYPE_EDGE -> type = "2G"
            TelephonyManager.NETWORK_TYPE_GPRS -> type = "2G"
            TelephonyManager.NETWORK_TYPE_UMTS -> type = "3G"
            TelephonyManager.NETWORK_TYPE_1xRTT -> type = "2G"
            TelephonyManager.NETWORK_TYPE_HSDPA -> type = "3G"
            TelephonyManager.NETWORK_TYPE_CDMA -> type = "3G"
            TelephonyManager.NETWORK_TYPE_HSPA -> type = "3G"
            TelephonyManager.NETWORK_TYPE_LTE -> type = "LTE"
            else -> type = "unknown"
        }
        return type
    }

    @SuppressLint("MissingPermission")
    private fun getNewtworkType(): String {
        getNetworkTypeString(tele.networkType)
        var networkType = String()
        val cellInfoList = tele.allCellInfo
        if (cellInfoList != null) {
            for (cellInfo in cellInfoList) {
                if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                    networkType = "LTE"
                } else if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                    networkType = "2G"
                } else if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                    networkType = "3G"
                } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                    networkType = "3G"
                }
            }
            if (getNetworkTypeString(tele.networkType) != "unknown") {
                networkType = getNetworkTypeString(tele.networkType)
            }
        }
        return networkType
    }


    @SuppressLint("MissingPermission")
    private fun getLat(): Double {
        val locationManager: LocationManager
        val context = context.getSystemService(LOCATION_SERVICE)
        locationManager = context as LocationManager
        val provider = LocationManager.GPS_PROVIDER
        if (locationManager.getLastKnownLocation(provider) != null) {
            val location = locationManager.getLastKnownLocation(provider)
            return location!!.latitude
        }
        return 0.0
    }


    @SuppressLint("MissingPermission")
    private fun getLong(): Double {
        val locationManager: LocationManager
        val context = context.getSystemService(LOCATION_SERVICE)
        locationManager = context as LocationManager
        val provider = LocationManager.GPS_PROVIDER
        if (locationManager.getLastKnownLocation(provider) != null) {
            val location = locationManager.getLastKnownLocation(provider)
            return location!!.longitude
        }
        return 0.0
    }

    private fun getDownloadSpeed(): String {
        var downSpeed = String()
        val speedTestSocket = SpeedTestSocket()

        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                // called when download/upload is complete
                println("[COMPLETED] rate in octet/s : " + report.transferRateOctet)
                println("[COMPLETED] rate in bit/s   : " + report.transferRateBit)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                // called when a download/upload error occur
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                // called to notify download/upload progress
                println("[PROGRESS] progress : $percent%")
                println("[PROGRESS] rate in octet/s : " + report.transferRateOctet)
                println("[PROGRESS] rate in bit/s   : " + report.transferRateBit)
            }
        })
        return downSpeed
    }

    private fun getUploadSpeed(): String {
        var upSpeed = String()
        var cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm.getNetworkCapabilities(cm.activeNetwork) != null) {
                var nc = cm.getNetworkCapabilities(cm.activeNetwork)
                upSpeed = nc!!.linkUpstreamBandwidthKbps.toString()
                return upSpeed
            }
        }
        return upSpeed
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }

}

