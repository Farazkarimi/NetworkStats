package com.example.networkstats

import android.Manifest.permission.*
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

        val generalNetworkModel = networkHandler.getGeneralNetworkModel()

        val lat = generalNetworkModel.lat?.toString()
        latTextView.text = lat.toString()
        val long = generalNetworkModel.long?.toString()
        longTextView.text = long.toString()
        val networkType = generalNetworkModel.networkType
        networkTypeTextView.text = networkType
        val operatorName = generalNetworkModel.operator
        operatorNameTextView.text = operatorName
        val mcc = generalNetworkModel.mcc
        mccTextView.text = mcc.toString()
        val mnc = generalNetworkModel.mnc
        mncTextView.text = mnc.toString()
        val imsi = generalNetworkModel.imsi
        imsiTextView.text = imsi
        val imei = generalNetworkModel.imei
        imeiTextView.text = imei.toString()




        val twoGNetworkModel = networkHandler.get2gNetworkModel()

        val rxlevel = twoGNetworkModel.rxLevel
        rxLevelTextView.text = rxlevel.toString()
        val rxQual = twoGNetworkModel.rxQual
        rxQualTextView.text = rxQual.toString()
        val lac = twoGNetworkModel.lac
        lacTextView.text = lac.toString()
        val cellID = twoGNetworkModel.cellID
        cellIdTextView.text = cellID.toString()
        val arfcn = twoGNetworkModel.arfcn
        arfcnTextView.text = arfcn.toString()


        val threeGNetworkModel = networkHandler.get3gNetworkModel()

        val rscp = threeGNetworkModel.rscp
        rscpTextView.text = rscp.toString()
        val cqi = threeGNetworkModel.cqi
        cqi3gTextView.text = cqi.toString()
        val psc = threeGNetworkModel.psc
        pscTextView.text = psc.toString()



        val fourGNetworkModel = networkHandler.get4gNetworkModel()

        val rsrp = fourGNetworkModel.rsrp
        rsrpTextView.text = rsrp.toString()
        val rsrq = fourGNetworkModel.rsrq
        rsrqSnrTextView.text = rsrq
        val tac = fourGNetworkModel.tac
        tacTextView.text = tac.toString()
        val pci = fourGNetworkModel.pci
        pciTextView.text = pci.toString()
        val rssi = fourGNetworkModel.rssi
        rssiTextView.text = rssi
        val cqi4g = fourGNetworkModel.cqi
        cqi4gTextView.text = cqi4g

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
