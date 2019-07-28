package com.example.networkstats

import android.Manifest.permission.*
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var networkHandler: NetworkHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        mainHandler.post(object : Runnable {
            override fun run() {
                calculate()
                mainHandler.postDelayed(this, 1000)
            }
        })

        networkHandler = NetworkHandler(this)

    }

    private fun calculate() {
        if (ContextCompat.checkSelfPermission(this,ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION, READ_PHONE_STATE, ACCESS_FINE_LOCATION), 100)

        }
        else {
            getData()
        }
    }

    fun getData() {
        val rsrp = networkHandler.getRSRP()
        rsrpTextView.text = rsrp.toString()
        val rscp = networkHandler.getRscp()
        rscpTextView.text = rscp.toString()
        val rxlevel = networkHandler.getRxLevel()
        rxLevelTextView.text = rxlevel.toString()
        val cellID = networkHandler.getCellID()
        cellIdTextView.text = cellID.toString()
        val mnc = networkHandler.getMNC()
        mncTextView.text = mnc.toString()
        val mcc = networkHandler.getMCC()
        mccTextView.text = mcc.toString()
        val lac = networkHandler.getLAC()
        lacTextView.text = lac.toString()
        val cqi = networkHandler.getCQI()
        cqi3gTextView.text = cqi.toString()
        val psc = networkHandler.getPSC()
        pscTextView.text = psc.toString()
        val operatorName = networkHandler.getOperatorName()
        operatorNameTextView.text = operatorName
        val networkType = networkHandler.getNewtworkType()
        networkTypeTextView.text = networkType
        val arfcn = networkHandler.getArfcn()
        arfcnTextView.text = arfcn.toString()
        val rxQual = networkHandler.getRxQual()
        rxQualTextView.text = rxQual.toString()

        val generalNetworkModel = networkHandler.GetGeneralNetworkModel()
        val lat = (generalNetworkModel.lat?.toString()?.substring(0, 7))
        latTextView.text = lat.toString()
        val long = (generalNetworkModel.long?.toString()?.substring(0, 7))
        longTextView.text = long.toString()
        val imei = generalNetworkModel.imei
        imeiTextView.text = imei.toString()


    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    getData()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
