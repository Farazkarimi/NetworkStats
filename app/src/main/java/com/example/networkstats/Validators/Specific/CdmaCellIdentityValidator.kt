package com.example.networkstats.Validators.Specific

import android.telephony.CellIdentityCdma
import android.os.Build
import android.annotation.TargetApi


class CdmaCellIdentityValidator {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isValid(cell: CellIdentityCdma): Boolean {
        val valid = (isBidInRange(cell.basestationId) && isNidInRange(cell.networkId)
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

        private val TAG = CdmaCellIdentityValidator::class.java.simpleName
    }

}