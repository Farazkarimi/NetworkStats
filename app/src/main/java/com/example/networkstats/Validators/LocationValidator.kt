package com.example.networkstats.Validators

import android.location.Location
import android.util.Log

class LocationValidator(private val minAccuracy: Float) {

    fun isValid(location: Location): Boolean {
        return isLocationInRange(location) && hasRequiredAccuracy(location)
    }

    fun isUpToDate(gpsTimestamp: Long, systemTimestamp: Long): Boolean {
        val timeDiff = Math.abs(systemTimestamp - gpsTimestamp)
        // check conditions
        val valid = timeDiff <= NO_LOCATION_TIME_DIFF
        return valid
        // return true;
    }

    private fun isLocationInRange(location: Location): Boolean {
        val lat = location.getLatitude()
        val lon = location.getLongitude()
        return lat >= -90 && lat <= 90 && lat != 0.0 && lon >= -180 && lon <= 180 && lon != 0.0
    }

    fun hasRequiredAccuracy(location: Location): Boolean {
        return location.hasAccuracy() && location.getAccuracy() <= minAccuracy
    }

    companion object {

        private val TAG = LocationValidator::class.java.simpleName

        val NO_LOCATION_TIME_DIFF = (5 * 60 * 1000).toLong()// 5 minutes
    }

}