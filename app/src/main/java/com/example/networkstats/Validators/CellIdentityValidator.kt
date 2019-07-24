package com.example.networkstats.Validators

import android.telephony.CellInfoCdma
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.os.Build
import android.telephony.CellInfoGsm
import android.telephony.CellInfo
import android.annotation.TargetApi
import com.example.networkstats.Validators.Specific.CdmaCellIdentityValidator
import com.example.networkstats.Validators.Specific.GsmCellIdentityValidator
import com.example.networkstats.Validators.Specific.LteCellIdentityValidator
import com.example.networkstats.Validators.Specific.WcdmaCellIdentityValidator


@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class CellIdentityValidator {

    private var gsmValidator: GsmCellIdentityValidator? = null
    private var wcdmaValidator: WcdmaCellIdentityValidator? = null
    private var lteValidator: LteCellIdentityValidator? = null
    private var cdmaValidator: CdmaCellIdentityValidator? = null

    fun isValid(cellInfo: CellInfo): Boolean {
        if (cellInfo is CellInfoGsm) {
// If is compatible with API 17 hack (PSC on GSM) return true
            val wcdmaApi17Valid = getWcdmaValidator().isValid(cellInfo.cellIdentity)
            return if (wcdmaApi17Valid) true else getGsmValidator().isValid(cellInfo.cellIdentity)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && cellInfo is CellInfoWcdma) {
            return getWcdmaValidator().isValid(cellInfo.cellIdentity)
        }
        if (cellInfo is CellInfoLte) {
            return getLteValidator().isValid(cellInfo.cellIdentity)
        }
        if (cellInfo is CellInfoCdma) {
            return getCdmaValidator().isValid(cellInfo.cellIdentity)
        }
        throw UnsupportedOperationException("Cell identity type not supported `" + cellInfo.javaClass.name + "`")
    }

    private fun getGsmValidator(): GsmCellIdentityValidator {
        if (gsmValidator == null) {
            gsmValidator = GsmCellIdentityValidator()
        }
        return gsmValidator as GsmCellIdentityValidator
    }

    private fun getWcdmaValidator(): WcdmaCellIdentityValidator {
        if (wcdmaValidator == null) {
            wcdmaValidator = WcdmaCellIdentityValidator()
        }
        return wcdmaValidator as WcdmaCellIdentityValidator
    }

    private fun getLteValidator(): LteCellIdentityValidator {
        if (lteValidator == null) {
            lteValidator = LteCellIdentityValidator()
        }
        return lteValidator as LteCellIdentityValidator
    }

    private fun getCdmaValidator(): CdmaCellIdentityValidator {
        if (cdmaValidator == null) {
            cdmaValidator = CdmaCellIdentityValidator()
        }
        return cdmaValidator as CdmaCellIdentityValidator
    }

}