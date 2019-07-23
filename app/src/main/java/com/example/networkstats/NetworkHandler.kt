package com.example.networkstats

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.*
import android.telephony.gsm.GsmCellLocation
import androidx.core.content.ContextCompat


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
            if (cellInfo is CellInfoLte) {
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
            if (cellInfo is CellInfoWcdma) {
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
            if (cellInfo is CellInfoGsm) {
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
            if (cellInfo is CellInfoLte) {

                val longCid = ((tele.cellLocation) as GsmCellLocation).cid
                val cellidHex = DecToHex(longCid)
                val eNBHex = cellidHex.substring(0, cellidHex.length - 2)
                val eNB = (HexToDec(eNBHex))

                cellID = eNB

            } else if (cellInfo is CellInfoGsm) {
                cellID = ((tele.cellLocation) as GsmCellLocation).cid
            } else if (cellInfo is CellInfoWcdma) {
                cellID = ((tele.cellLocation) as GsmCellLocation).cid
            } else if (cellInfo is CellInfoCdma) {
                cellID = ((tele.cellLocation) as GsmCellLocation).cid
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



    // Decimal -> hexadecimal
    private fun DecToHex(dec: Int): String {
        return String.format("%x", dec)
    }

    // hex -> decimal
    private fun HexToDec(hex: String): Int {
        return Integer.parseInt(hex, 16)
    }
}

