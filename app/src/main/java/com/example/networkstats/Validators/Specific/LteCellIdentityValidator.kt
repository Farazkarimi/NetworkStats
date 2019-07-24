package com.example.networkstats.Validators.Specific

import android.telephony.CellIdentityLte
import android.os.Build
import android.annotation.TargetApi


class LteCellIdentityValidator {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun isValid(cell: CellIdentityLte): Boolean {
        val valid = (isCiInRange(cell.ci) && isTacInRange(cell.tac)
                && isMncInRange(cell.mnc) && isMccInRange(cell.mcc)
                && isPciInRange(cell.pci))
        return valid
    }

    private fun isCiInRange(ci: Int): Boolean {
        return ci >= 1 && ci <= 268435455
    }

    private fun isTacInRange(tac: Int): Boolean {
        return tac >= 1 && tac <= 65535
    }

    private fun isMncInRange(mnc: Int): Boolean {
        return mnc >= 0 && mnc <= 999
    }

    private fun isMccInRange(mcc: Int): Boolean {
        return mcc >= 100 && mcc <= 999
    }

    private fun isPciInRange(pci: Int): Boolean {
        return pci >= 0 && pci <= 503
    }

    companion object {

        private val TAG = LteCellIdentityValidator::class.java.simpleName
    }

}