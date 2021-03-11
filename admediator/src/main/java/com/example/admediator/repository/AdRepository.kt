package com.example.admediator.repository

import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.service.AdService
import com.example.admediator.service.ServiceBuilder
import com.example.admediator.toAdNetworkEntity
import io.reactivex.Single

class AdRepository {

    private val adService = ServiceBuilder.buildService(AdService::class.java)

    fun getAdNetworks(appId: String): Single<List<AdNetworkEntity>> {
        return adService.getAdNetworks(appId).map {networkList ->
            networkList.map {networkDto ->
                networkDto.toAdNetworkEntity()
            }
        }
    }
}