package com.example.admediator.data

import com.google.gson.annotations.SerializedName

internal data class AdNetworkDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val appId: String
)