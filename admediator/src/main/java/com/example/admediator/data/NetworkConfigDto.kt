package com.example.admediator.data

import com.google.gson.annotations.SerializedName

internal data class NetworkConfigDto(
    @SerializedName("adNetwork")
    val adNetwork: String,
    @SerializedName("zoneId")
    val zoneId: String,
    @SerializedName("timeout")
    val timeout: Long
)