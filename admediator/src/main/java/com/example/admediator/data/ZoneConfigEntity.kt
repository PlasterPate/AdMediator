package com.example.admediator.data

data class ZoneConfigEntity(
    val zoneType: String,
    val waterfall: List<NetworkConfigEntity>,
    val timeout: Long
)