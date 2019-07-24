package com.example.networkstats.Validators.Specific

import android.telephony.cdma.CdmaCellLocation


class CdmaCellLocationValidator {

    fun isValid(cell: CdmaCellLocation): Boolean {
        val valid = (isBidInRange(cell.baseStationId) && isNidInRange(cell.networkId)
                && isSidInRange(cell.systemId))
        return valid
    }

    private fun isBidInRange(bid: Int): Boolean {
        return bid >= 1 && bid <= 65535
    }

    private fun isNidInRange(nid: Int): Boolean {
        return nid >= 1 && nid <= 65535
    }

    private fun isSidInRange(sid: Int): Boolean {
        return sid >= 0 && sid <= 32767
    }

    companion object {

        private val TAG = CdmaCellLocationValidator::class.java.simpleName
    }

}