package com.example.networkstats.Validators.Specific

import android.telephony.NeighboringCellInfo.UNKNOWN_CID
import android.telephony.gsm.GsmCellLocation


class GsmCellLocationValidator {

    fun isValid(cell: GsmCellLocation, mcc: Int, mnc: Int): Boolean {
        val valid = isValid(cell.cid, cell.lac, mnc, mcc, cell.psc)
        return valid
    }

    fun isValid(cid: Int, lac: Int, mnc: Int, mcc: Int, psc: Int): Boolean {
        return (isCidOrCiInRange(cid) && isLacOrTacInRange(lac)
                && isMncInRange(mnc) && isMccInRange(mcc))
    }

    private fun isCidOrCiInRange(cidOrCi: Int): Boolean {
        return cidOrCi >= 1 && cidOrCi <= 268435455
    }

    private fun isLacOrTacInRange(lacOrTac: Int): Boolean {
        return lacOrTac >= 1 && lacOrTac <= 65535
    }

    private fun isMncInRange(mnc: Int): Boolean {
        return mnc >= 0 && mnc <= 999
    }

    private fun isMccInRange(mcc: Int): Boolean {
        return mcc >= 100 && mcc <= 999
    }

    private fun isPscOrPciInRange(pscOrPci: Int): Boolean {
        return pscOrPci >= 0 && pscOrPci <= 511
    }

    companion object {

        private val TAG = GsmCellLocationValidator::class.java.simpleName
    }

}