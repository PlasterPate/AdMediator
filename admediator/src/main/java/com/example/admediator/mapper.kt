package com.example.admediator

import com.example.admediator.data.*

internal fun AdNetworkDto.toAdNetworkEntity() : AdNetworkEntity{
    return AdNetworkEntity(
        name = name,
        appId = appId
    )
}

internal fun NetworkConfigDto.toNetworkConfigEntity() : NetworkConfigEntity{
    return NetworkConfigEntity(
        adNetwork = adNetwork,
        zoneId = zoneId,
        timeout = timeout
    )
}

internal fun ZoneConfigDto.toZoneConfigEntity() : ZoneConfigEntity{
    return ZoneConfigEntity(
        zoneType = zoneType,
        waterfall = waterfall.map { it.toNetworkConfigEntity() },
        timeout = timeout
    )
}