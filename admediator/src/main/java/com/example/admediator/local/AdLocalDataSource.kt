package com.example.admediator.local

import com.example.admediator.data.AdState
import com.example.admediator.data.NetworkConfigEntity
import com.example.admediator.data.ZoneConfigEntity
import io.reactivex.Completable
import io.reactivex.Single

interface AdLocalDataSource {

    fun saveZoneConfig(zoneId: String, zoneConfig: ZoneConfigEntity) : Completable

    fun getZoneConfig(zoneId: String) : Single<ZoneConfigEntity>

    fun removeZoneConfig(zoneId: String) : Completable

    fun saveAdState(adState: AdState) : Completable

    fun getAdState() : Single<AdState>

    fun removeAdState() : Completable
}