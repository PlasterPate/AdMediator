package com.example.admediator

import com.example.admediator.data.AdNetworkDto
import com.example.admediator.data.AdNetworkEntity

fun AdNetworkDto.toAdNetworkEntity() : AdNetworkEntity{
    return AdNetworkEntity(
        name = name,
        appId = appId
    )
}