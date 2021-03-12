package com.example.admediator.local

import android.content.SharedPreferences
import com.example.admediator.data.ZoneConfigEntity
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single

class AdLocalDataSourceImpl(private var sharedPreferences: SharedPreferences) : AdLocalDataSource {

    private val KEY_ZONECONFIG = "zoneconfig."
    private val KEY_AD_ID = "ad_id."

    override fun saveZoneConfig(zoneId: String, zoneConfig: ZoneConfigEntity): Completable {
        return Completable.fromAction {
            sharedPreferences.edit().apply {
                putString(KEY_ZONECONFIG.plus(zoneId), Gson().toJson(zoneConfig))
            }
        }
    }

    override fun getZoneConfig(zoneId: String): Single<ZoneConfigEntity> {
        val zoneConfig = sharedPreferences.getString(KEY_ZONECONFIG.plus(zoneId), null)
        return Single.just(
            zoneConfig?.let {
                    Gson().fromJson(it, ZoneConfigEntity::class.java)
            } ?: // Return an empty entity if no config found
            ZoneConfigEntity("", listOf(), 0)
        )
    }

    override fun removeZoneConfig(zoneId: String): Completable {
        return Completable.fromAction {
            sharedPreferences.edit().apply {
                remove(KEY_ZONECONFIG.plus(zoneId))
            }
        }
    }

    override fun saveAdId(adId: String): Completable {
        return Completable.fromAction {
            sharedPreferences.edit().apply{
                putString(KEY_AD_ID.plus(adId), adId)
            }
        }
    }

    override fun getAdId(): Single<String> {
        return Single.just(
            sharedPreferences.getString(KEY_AD_ID, "")
        )
    }

    override fun removeAdId(): Completable {
        return Completable.fromAction {
            sharedPreferences.edit().apply{
                remove(KEY_AD_ID)
            }
        }
    }

}