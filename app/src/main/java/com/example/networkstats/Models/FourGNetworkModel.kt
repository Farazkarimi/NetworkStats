package com.example.networkstats.Models

data class FourGNetworkModel(val rsrp: Int? = null,
                             val rsrq: Int? = null,
                             val tac: Int? = null,
                             val pci: Int? = null,
                             val rssi: Int? = null,
                             val cqi: Int? = null,
                             val eNodeB: Int? = null,
                             val earfnc: Int? = null)