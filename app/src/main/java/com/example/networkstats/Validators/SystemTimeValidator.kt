package com.example.networkstats.Validators

import android.util.Log

class SystemTimeValidator {

    fun isValid(systemTime: Long, gpsTime: Long): Boolean {
        val timeDiff = systemTime - gpsTime
        return Math.abs(timeDiff) <= TwoDayTimeDiff
    }

    companion object {

        private val TAG = SystemTimeValidator::class.java.simpleName

        private val TwoDayTimeDiff = (2 * 24 * 60 * 60 * 1000).toLong()
    }

}