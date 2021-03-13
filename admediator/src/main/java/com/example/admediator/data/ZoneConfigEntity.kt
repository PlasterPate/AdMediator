package com.example.admediator.data

internal data class ZoneConfigEntity(
    val zoneType: String,
    val waterfall: List<NetworkConfigEntity>,
    val timeout: Long
)