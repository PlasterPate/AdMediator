package com.example.admediator.data

import com.google.gson.annotations.SerializedName

data class ZoneConfigDto(
    @SerializedName("zoneType")
    val zoneType: String,
    @SerializedName("waterfall")
    val waterfall: List<NetworkConfigDto>,
    @SerializedName("timeout")
    val timeout: Long
)