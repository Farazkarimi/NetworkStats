package com.example.networkstats.Validators.Specific

import android.telephony.CellIdentityGsm
import android.os.Build
import android.annotation.TargetApi
import android.telephony.CellIdentityWcdma


class WcdmaCellIdentityValidator {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun isValid(cell: CellIdentityWcdma): Boolean {
        val valid = (isCidInRange(cell.cid) && isLacInRange(cell.lac)
                && isMncInRange(cell.mnc) && isMccInRange(cell.mcc)
                && isPscInRange(cell.psc))
        return valid
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isValid(cell: CellIdentityGsm): Boolean {
        val valid = (isCidInRange(cell.cid) && isLacInRange(cell.lac)
                && isMncInRange(cell.mnc) && isMccInRange(cell.mcc)
                && isPscInRange(cell.psc))
        return valid
    }

    private fun isCidInRange(cid: Int): Boolean {
        return cid >= 1 && cid <= 268435455
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

    private fun isPscInRange(psc: Int): Boolean {
        return psc >= 0 && psc <= 511
    }

    companion object {

        private val TAG = WcdmaCellIdentityValidator::class.java.simpleName
    }

}