package com.example.admediator.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.admediator.data.AdNetworkEntity
import com.example.admediator.data.AdState
import com.example.admediator.data.ZoneConfigEntity
import com.example.admediator.local.AdLocalDataSourceImpl
import com.example.admediator.remote.AdRemoteDataSourceImpl
import com.example.admediator.remote.service.AdService
import com.example.admediator.remote.service.ServiceBuilder
import io.reactivex.Completable
import io.reactivex.Single

internal class AdRepository {

    private val adService = ServiceBuilder.buildService(AdService::class.java)
    private lateinit var sharedPreferences: SharedPreferences
    private val adRemoteDS = AdRemoteDataSourceImpl(adService)
    private val adLocalDS = AdLocalDataSourceImpl(sharedPreferences)

    fun getAdNetworks(appId: String): Single<List<AdNetworkEntity>> {
        return adRemoteDS.getAdNetworks(appId)
    }

    fun getZoneConfig(zoneId: String) : Single<ZoneConfigEntity>{
        return adLocalDS.getZoneConfig(zoneId).flatMap { localConfig ->
            // Request config from server and cache it, if it wasn't cached before
            if (localConfig.zoneType == ""){
                adRemoteDS.getZoneConfig(zoneId).map { remoteConfig ->
                    adLocalDS.saveZoneConfig(zoneId, remoteConfig)
                    remoteConfig
                }
            }else{
                Single.just(localConfig)
            }
        }
    }

    fun removeZoneConfig(zoneId: String) : Completable{
        return adLocalDS.removeZoneConfig(zoneId)
    }

    fun saveAdState(adState: AdState): Completable{
        return adLocalDS.saveAdState(adState)
    }

    fun getAdState() : Single<AdState>{
        return adLocalDS.getAdState().map { adId ->
            adLocalDS.removeAdState()
            adId
        }
    }

    fun initSharedPreferences(context: Context){
        if (!this::sharedPreferences.isInitialized){
            sharedPreferences = context.getSharedPreferences("ad_mediator_shared_pref", Context.MODE_PRIVATE)
        }
    }
}