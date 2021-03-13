package com.example.admediator.remote

import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.data.ZoneConfigEntity
import com.example.admediator.remote.service.AdService
import com.example.admediator.toAdNetworkEntity
import com.example.admediator.toZoneConfigEntity
import io.reactivex.Single

internal class AdRemoteDataSourceImpl(private val adService: AdService) : AdRemoteDataSource {
    override fun getAdNetworks(appId: String): Single<List<AdNetworkEntity>> {
        return adService.getAdNetworks(appId).map {networkList ->
            networkList.map {networkDto ->
                networkDto.toAdNetworkEntity()
            }
        }
    }

    override fun getZoneConfig(zoneId: String): Single<ZoneConfigEntity> {
        return adService.getZoneConfig(zoneId).map {
            it.toZoneConfigEntity()
        }
    }

}