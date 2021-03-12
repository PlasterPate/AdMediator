package com.example.admediator.local

import com.example.admediator.data.NetworkConfigEntity
import com.example.admediator.data.ZoneConfigEntity
import io.reactivex.Completable
import io.reactivex.Single

interface AdLocalDataSource {

    fun saveZoneConfig(zoneId: String, zoneConfig: ZoneConfigEntity) : Completable

    fun getZoneConfig(zoneId: String) : Single<ZoneConfigEntity>

    fun removeZoneConfig(zoneId: String) : Completable

    fun saveAdId(adId: String) : Completable

    fun getAdId() : Single<String>

    fun removeAdId() : Completable
}