package com.example.admediator.networks

import com.example.admediator.constants.AdNetwork
import com.example.admediator.data.AdNetworkEntity

internal class NetworkFactory {

    fun createNetwork(adNetworkEntity: AdNetworkEntity): BaseNetwork {
        return when (adNetworkEntity.name) {
            AdNetwork.TAPSELL ->
                TapsellNetwork(adNetworkEntity.appId)
            AdNetwork.CHARTBOOST ->
                ChartboostNetwork(adNetworkEntity.appId)
            AdNetwork.UNITY_ADS ->
                UnityNetwork(adNetworkEntity.appId)
            else -> throw IllegalArgumentException(
                "There is no ${adNetworkEntity.name} network available"
            )
        }
    }
}