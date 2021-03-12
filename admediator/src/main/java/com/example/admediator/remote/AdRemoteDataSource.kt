package com.example.admediator.remote

import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.data.ZoneConfigEntity
import io.reactivex.Single

interface AdRemoteDataSource {

    fun getAdNetworks(appId: String) : Single<List<AdNetworkEntity>>

    fun getZoneConfig(zoneId: String) : Single<ZoneConfigEntity>
}