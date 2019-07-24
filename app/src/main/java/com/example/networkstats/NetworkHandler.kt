package com.example.networkstats

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import android.telephony.gsm.GsmCellLocation
import androidx.core.content.ContextCompat
import android.telephony.CellInfoWcdma
import android.telephony.CellIdentityLte
import android.telephony.CellInfoLte
import android.telephony.CellInfoGsm
import android.telephony.CellInfoCdma
import android.telephony.CellInfo
import android.annotation.TargetApi






class NetworkHandler(contextCompat: ContextCompat, context: Context) {

    private var lac = "LAC = 0"
    private var contextCompat: ContextCompat = contextCompat
    private var context: Context = context
    private val tele = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


    @SuppressLint("MissingPermission")
    fun getRSRP(): Int {
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
    fun getRscp(): Int {
        var rscp: Int = 0
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoWcdma && cellInfo.isRegistered) {
                rscp = (cellInfo.cellSignalStrength.asuLevel - 116)
            } else if (cellInfo is CellInfoCdma) {
                rscp = (cellInfo.cellSignalStrength.asuLevel - 116)
            }
        }
        return rscp
    }

    @SuppressLint("MissingPermission")
    fun getRxLevel(): Int {
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
    fun getCellID(): Int {
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
    fun getMNC(): Int {
        var mnc: Int = 0
        mnc = Integer.parseInt(tele.networkOperator.substring(0, 3))

        return mnc
    }

    @SuppressLint("MissingPermission")
    fun getMCC(): Int {
        var mcc: Int = 0
        mcc = Integer.parseInt(tele.networkOperator.substring(0, 3))

        return mcc
    }

    @SuppressLint("MissingPermission")
    fun getLAC(): Int {
        var lac: Int = 0
        lac = ((tele.cellLocation) as GsmCellLocation).lac

        return lac
    }

    @SuppressLint("MissingPermission")
    fun getCQI(): String {
        var cqi: String = String()
        val cellInfoList = tele.allCellInfo
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte && cellInfo.isRegistered) {
                val cqi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cellInfo.cellSignalStrength.cqi.toString()
                } else {
                    cqi = String()
                }
            }
        }
        return cqi
    }

    @SuppressLint("MissingPermission")
    fun getNeighbors(): List<CellInfo> {
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
    fun getPSC(): String {
        var psc = String()
        val cellinfo = tele.allCellInfo
        for (cell in cellinfo) {
            if (cell.isRegistered && cell is CellInfoCdma) {
                (cell as CellInfoWcdma).cellIdentity.psc
            }
        }
        return psc
    }

    fun getNewtworkType(): String {
        return getNetworkTypeString(tele.networkType)
    }


    @SuppressLint("MissingPermission")
    fun getOperatorName(): String {
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

    // Decimal -> hexadecimal
    private fun DecToHex(dec: Int): String {
        return String.format("%x", dec)
    }

    // hex -> decimal
    private fun HexToDec(hex: String): Int {
        return Integer.parseInt(hex, 16)
    }

    @SuppressLint("MissingPermission")
    fun getCellInfo(): String? {
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
                    ret = ret + "\n Cell info : wcdma" + a_Info.toString()
            }
        return ret
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

}

