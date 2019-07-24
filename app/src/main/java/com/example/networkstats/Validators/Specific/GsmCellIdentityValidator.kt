package com.example.networkstats.Validators.Specific

import android.telephony.CellIdentityGsm
import android.os.Build
import android.annotation.TargetApi
import android.util.Log


class GsmCellIdentityValidator {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isValid(cell: CellIdentityGsm): Boolean {
        val valid = (isCidInRange(cell.cid) && isLacInRange(cell.lac)
                && isMncInRange(cell.mnc) && isMccInRange(cell.mcc))
        return valid
    }

    private fun isCidInRange(cid: Int): Boolean {
        return cid >= 1 && cid <= 65535
    }

    private fun isLacInRange(lac: Int): Boolean {
        return lac >= 1 && lac <= 65535
    }

    private fun isMncInRange(mnc: Int): Boolean {
        return mnc >= 0 && mnc <= 999
    }

    private fun isMccInRange(mcc: Int): Boolean {
        return mcc >= 100 && mcc <= 999
    }

    companion object {

        private val TAG = GsmCellIdentityValidator::class.java.simpleName
    }

}