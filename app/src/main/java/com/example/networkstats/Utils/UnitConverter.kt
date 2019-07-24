package com.example.networkstats.Utils

import android.telephony.NeighboringCellInfo


object UnitConverter {
    private val METERS_TO_FEET_MULTIPLIER = 3.280839895013123f
    private val METERS_PER_SECOND_TO_MILES_PER_HOUR_MULTIPLIER = 2.236980772f
    private val METERS_PER_SECOND_TO_KILOMETERS_PER_HOUR_MULTIPLIER = 3.5999712002f

    fun convertMetersToFeet(meters: Float): Float {
        return meters * METERS_TO_FEET_MULTIPLIER
    }

    fun convertMetersPerSecondToKilometersPerHour(metersPerSecond: Float): Float {
        return metersPerSecond * METERS_PER_SECOND_TO_KILOMETERS_PER_HOUR_MULTIPLIER
    }

    fun convertMetersPerSecondToMilesPerHour(metersPerSecond: Float): Float {
        return metersPerSecond * METERS_PER_SECOND_TO_MILES_PER_HOUR_MULTIPLIER
    }

    fun convertGsmAsuToDbm(asu: Int): Int {
        return if (asu == -1 || asu == NeighboringCellInfo.UNKNOWN_RSSI) -1 else 2 * asu - 113
    }

    // ranges taken from 5.0.0 android/telephony/CellSignalStrength.java#CellSignalStrength.getAsuLevel()
    fun convertGsmDbmToAsu(dbm: Int): Int {
        if (dbm == -1 || dbm == NeighboringCellInfo.UNKNOWN_RSSI)
            return -1
        if (dbm <= -113)
            return 0
        val asu = (dbm + 113) / 2
        return if (asu > 31) 31 else asu
    }

    fun convertLteAsuToDbm(asu: Int): Int {
        if (asu == -1|| asu == 99)
            return -1
        if (asu <= 0)
            return -140
        return if (asu >= 97) -43 else asu - 140
    }

    // converted from 5.0.0 android/telephony/CellSignalStrengthLte.java#CellSignalStrengthLte.getAsuLevel()
    fun convertLteDbmToAsu(dbm: Int): Int {
        if (dbm == -1 || dbm == 99)
            return 0
        if (dbm <= -140)
            return 0
        return if (dbm >= -43) 97 else dbm + 140
    }

    fun convertCdmaAsuToDbm(asu: Int): Int {
        if (asu == -1 || asu == 99)
            return -1
        if (asu == 16)
            return -75
        else if (asu == 8)
            return -82
        else if (asu == 4)
            return -90
        else if (asu == 2)
            return -95
        else if (asu == 1)
            return -100
        return -1
    }

    fun convertCdmaDbmToAsu(dbm: Int): Int {
        if (dbm >= -75)
            return 16
        else if (dbm >= -82)
            return 8
        else if (dbm >= -90)
            return 4
        else if (dbm >= -95)
            return 2
        else if (dbm >= -100)
            return 1
        return -1
    }
}