package com.example.networkstats

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.os.Build
import android.telephony.CellInfoWcdma
import android.telephony.CellInfoLte
import android.telephony.CellInfoGsm
import android.telephony.CellInfoCdma
import android.telephony.CellInfo
import android.telephony.TelephonyManager
import com.example.networkstats.Models.GeneralNetworkModel
import android.location.LocationManager
import android.util.Log
import com.example.networkstats.Models.FourGNetworkModel
import com.example.networkstats.Models.ThreeGNetworkModel
import com.example.networkstats.Models.TwoGNetworkModel
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
        val imei = tele.subscriberId

        return imei
    }



    @SuppressLint("MissingPermission")
    private fun getCQI(): String {
        var cqi: String = String()
        var cqiMap: Map<String,Int> = mapOf()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                   val cqiString = cellInfo.cellSignalStrength.toString()
                if (cqiString.length > 0) {
                   val cqiArray = cqiString.split(" ").toMutableList()
                    cqiArray.removeAt(0)
                   cqiMap = cqiArray.associate {
                        val (l,r) = it.split("=")
                        l to r.toInt()
                    }
                    cqi = cqiMap["cqi"].toString()
                }
            }
        }
        return cqi
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
    private fun getRxLevel(): Int {
        var rxLevel: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                rxLevel = (2 * (cellInfo.cellSignalStrength.asuLevel) - 113)
            }
        }
        return rxLevel
    }

    @SuppressLint("MissingPermission")
    private fun getRxQual(): Int {
        var rxQual: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                // rxQual = cellInfo.cellSignalStrength.bitErrorRate
            }
        }
        return rxQual
    }

    @SuppressLint("MissingPermission")
    private fun getCellID(): Int {
        var cellID: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                cellID = cellInfo.cellIdentity.ci

            } else if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                cellID = cellInfo.cellIdentity.cid
            } else if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                cellID = cellInfo.cellIdentity.cid
            } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                cellID = cellInfo.cellIdentity.basestationId
                //cellID = ((tele.cellLocation) as GsmCellLocation).cid
            }
        }
        return cellID
    }

    @SuppressLint("MissingPermission")
    private fun getBcch(): String {
        var bcch = String()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
            }
        }
        return bcch
    }

    @SuppressLint("MissingPermission")
    private fun getArfcn(): Int {
        var arfcn: Int = -1
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    arfcn = cellInfo.cellIdentity.arfcn
                } else {
                    arfcn = -1
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
            null,
            getLAC(),
            getPSC(),
            getCQI(),
            getUArfcn(),
            null
        )
    }


    @SuppressLint("MissingPermission")
    private fun getRscp(): Int {
        var rscp: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                rscp = (cellInfo.cellSignalStrength.asuLevel - 116)
            } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                rscp = (cellInfo.cellSignalStrength.asuLevel - 116)
            }
        }
        return rscp
    }

    @SuppressLint("MissingPermission")
    private fun getLAC(): Int {
        var lac: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                lac = cellInfo.cellIdentity.lac
            }
            if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                lac = cellInfo.cellIdentity.lac
            }
        }
        return lac
    }


    @SuppressLint("MissingPermission")
    private fun getPSC(): String {
        var psc = String()
        val cellinfo = tele.allCellInfo
        for (cell in cellinfo) {
            if (cell.isRegistered && cell is CellInfoCdma) {
                (cell as CellInfoWcdma).cellIdentity.psc
            }
        }
        return psc
    }

    @SuppressLint("MissingPermission")
    private fun getUArfcn(): Int {
        var uArfcn: Int = -1
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    cellInfo.cellIdentity.earfcn
                } else {
                    uArfcn = -1
                }
            }
        }
        return uArfcn
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
            null,
            getEARFNC()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getRSRP(): Int {
        var rsrp: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    rsrp = cellInfo.cellSignalStrength.rsrp
                } else {
                    rsrp = cellInfo.cellSignalStrength.dbm
                }
            }
        }
        return rsrp
    }

    @SuppressLint("MissingPermission")
    private fun getRSRQ(): String {
        var rsrq = String()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    rsrq = cellInfo.cellSignalStrength.rsrq.toString()
                } else {

                }
            }
        }
        return rsrq
    }

    @SuppressLint("MissingPermission")
    private fun getTAC(): Int {
        var tac: Int = -1
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                tac = cellInfo.cellIdentity.tac
            }
        }
        return tac
    }

    @SuppressLint("MissingPermission")
    private fun getEARFNC(): String {
        var earfnc = String()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    earfnc = cellInfo.cellIdentity.earfcn.toString()
                } else {

                }
            }
        }
        return earfnc
    }

    @SuppressLint("MissingPermission")
    private fun getpci(): Int {
        var pci: Int = -1
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                pci = cellInfo.cellIdentity.pci
            }
        }
        return pci
    }

    @SuppressLint("MissingPermission")
    private fun getRssi(): String {
        var rssi = String()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                try {
                   // rssi = cellInfo.cellSignalStrength.rssi.toString()
                }catch (e: IOException){
                    Log.e("Exception", "No Rssi Found")
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
        val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            null,
            null,
            getNeighbors(),
            null,
            imei,
            getIMSI(),
            null,
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
    private fun getNeighbors(): List<CellInfo> {
        var neighboringCellList: MutableList<CellInfo> = mutableListOf()
        val cellinfo = tele.allCellInfo
        for (cell in cellinfo) {
            if (!cell.isRegistered) {
                neighboringCellList.add(cell)
            }
        }
        return neighboringCellList
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
        var networkType = String()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                networkType = "4g"
            } else if (cellInfo is CellInfoGsm && cellInfo.isRegistered) {
                networkType = "2g"
            } else if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                networkType = "3g"
            } else if (cellInfo is CellInfoCdma && cellInfo.isRegistered) {
                networkType = "3g"
            }
        }
        return networkType
    }


    @SuppressLint("MissingPermission")
    private fun getLat(): Double {
        val locationManager: LocationManager
        val context = context.getSystemService(LOCATION_SERVICE)
        locationManager = context as LocationManager
        val provider = LocationManager.NETWORK_PROVIDER
        val location = locationManager.getLastKnownLocation(provider)
        if (location != null) {
            return location!!.latitude
        }
        return 0.0
    }

    @SuppressLint("MissingPermission")
    private fun getLong(): Double {
        val locationManager: LocationManager
        val context = context.getSystemService(LOCATION_SERVICE)
        locationManager = context as LocationManager
        val provider = LocationManager.NETWORK_PROVIDER
        val location = locationManager.getLastKnownLocation(provider)
        if (location != null) {
            return location!!.longitude
        }
        return 0.0
    }


}

