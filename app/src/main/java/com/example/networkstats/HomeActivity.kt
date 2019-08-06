package com.example.networkstats

import android.Manifest.permission.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlinx.android.synthetic.main.activity_home.*
import java.math.BigDecimal
import fr.bmartel.speedtest.utils.SpeedTestUtils
import android.view.View.Z
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class HomeActivity : AppCompatActivity() {

    private var downLink = String()
    private var upLink = String()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val downloadTestSocket = SpeedTestSocket()
    private val uploadTestSocket = SpeedTestSocket()
    private lateinit var networkHandler: NetworkHandler
    private var jitter = String()
    private var reportTime = System.currentTimeMillis()

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
        val fileName = SpeedTestUtils.generateFileName() + ".txt"
        uploadTestSocket.startUpload("ftp://speedtest.tele2.net/upload/$fileName", 1000000)
        downloadTestSocket.startDownload("ftp://speedtest.tele2.net/1MB.zip")

        downloadTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                downloadTestSocket.forceStopTask()
                downloadTestSocket.startDownload("ftp://speedtest.tele2.net/1MB.zip")
                downLink = (report.transferRateBit / BigDecimal(1000)).toInt().toString() + "Kb"
                val time = System.currentTimeMillis()
                jitter = (time - reportTime).toString()
                reportTime = time
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                downLink = "-"
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                downLink = (report.transferRateBit / BigDecimal(1000)).toInt().toString() + "Kb"
                val time = System.currentTimeMillis()
                jitter = (time - reportTime).toString()
                reportTime = time
            }
        })

        uploadTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                upLink = (report.transferRateBit / BigDecimal(1000)).toInt().toString() + "Kb"
                val fileName = SpeedTestUtils.generateFileName() + ".txt"
                uploadTestSocket.startUpload("ftp://speedtest.tele2.net/upload/$fileName", 1000000)
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                upLink = "-"
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                upLink = (report.transferRateBit / BigDecimal(1000)).toInt().toString() + "Kb"

            }
        })


    }

    private fun pingCalculated(onPingCalculated: ((String) -> Unit)?) {
        val time = System.currentTimeMillis()
        try {
            val url = URL("http://google.com")

            val urlc = url.openConnection() as HttpURLConnection
            urlc.setRequestProperty("User-Agent", "Android Application:" + 1.0)
            urlc.setRequestProperty("Connection", "close")
            urlc.setConnectTimeout(1000 * 30) // mTimeout is in seconds
            urlc.connect()

            if (urlc.getResponseCode() === 200) {
                val currentTime = System.currentTimeMillis()
                val ping =  currentTime - time
                onPingCalculated?.invoke(ping.toString())
            }
        } catch (e1: MalformedURLException) {
            e1.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun calculate() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_COARSE_LOCATION, READ_PHONE_STATE, ACCESS_FINE_LOCATION),
                100
            )

        } else {
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
//        val neighbors = generalNetworkModel.neighbours
//        neighboursTextView.text = neighbors
        val imsi = generalNetworkModel.imsi
        imsiTextView.text = imsi
        val imei = generalNetworkModel.imei
        imeiTextView.text = imei.toString()
        val handsetModel = generalNetworkModel.handsetModel
        handsetModelTextView.text = handsetModel
        var ping = String()
        Thread {
            pingCalculated {
                ping = it
            }
            runOnUiThread {
                pingTextView.text = ping
            }
        }.start()

        //uploadTestSocket.forceStopTask()
        upLinlTextView.text = upLink
        generalNetworkModel.upLink = upLink

        downLinkTextView.text = downLink
        generalNetworkModel.downLink = downLink

        jitterTextView.text = jitter
        generalNetworkModel.jitter = jitter.toString()


        if (networkType == "2G") {
            twoGFrame.visibility = View.VISIBLE
            threeGFrame.visibility = View.GONE
            fourGFrame.visibility = View.GONE

            val twoGNetworkModel = networkHandler.get2gNetworkModel()

            val rxlevel = twoGNetworkModel.rxLevel
            rxLevelTextView.text = rxlevel.toString()
            val rxQual = twoGNetworkModel.rxQual
            rxQualTextView.text = rxQual.toString()
            val lac = twoGNetworkModel.lac
            lacTextView.text = lac.toString()
            val cellID = twoGNetworkModel.cellID
            cellIdTextView.text = cellID.toString()
            val bcch = twoGNetworkModel.bcch
            bcchTextView.text = bcch
            val arfcn = twoGNetworkModel.arfcn
            arfcnTextView.text = arfcn.toString()

        } else if (networkType == "3G") {
            twoGFrame.visibility = View.GONE
            threeGFrame.visibility = View.VISIBLE
            fourGFrame.visibility = View.GONE
            val threeGNetworkModel = networkHandler.get3gNetworkModel()

            val rscp = threeGNetworkModel.rscp
            rscpTextView.text = rscp.toString()
            val lacRnc = threeGNetworkModel.lac
            lacRncTextView.text = lacRnc
            val cqi = threeGNetworkModel.cqi
            cqi3gTextView.text = cqi.toString()
            val psc = threeGNetworkModel.psc
            pscTextView.text = psc.toString()
            val uarfcn = threeGNetworkModel.uarfcn
            uarfcnTextView.text = uarfcn
            val nodeB = threeGNetworkModel.nodeB
            nodeBTextView.text = nodeB

        } else if (networkType == "LTE") {
            twoGFrame.visibility = View.GONE
            threeGFrame.visibility = View.GONE
            fourGFrame.visibility = View.VISIBLE
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
            val eNodeB = fourGNetworkModel.eNodeB
            eNodeBTextView.text = eNodeB
            val earfcn = fourGNetworkModel.earfnc
            eArfcnTextView.text = earfcn
        }


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
                    (grantResults.isNotEmpty() && grantResults[2] == PackageManager.PERMISSION_GRANTED)
                ) {
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
