package com.example.admediator

import com.example.admediator.data.*

fun AdNetworkDto.toAdNetworkEntity() : AdNetworkEntity{
    return AdNetworkEntity(
        name = name,
        appId = appId
    )
}

fun NetworkConfigDto.toNetworkConfigEntity() : NetworkConfigEntity{
    return NetworkConfigEntity(
        adNetwork = adNetwork,
        zoneId = zoneId,
        timeout = timeout
    )
}

fun ZoneConfigDto.toZoneConfigEntity() : ZoneConfigEntity{
    return ZoneConfigEntity(
        zoneType = zoneType,
        waterfall = waterfall.map { it.toNetworkConfigEntity() },
        timeout = timeout
    )
}