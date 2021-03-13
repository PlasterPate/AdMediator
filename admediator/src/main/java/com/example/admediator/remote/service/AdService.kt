package com.example.admediator.remote.service

import com.example.admediator.data.AdNetworkDto
import com.example.admediator.data.ZoneConfigDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

internal interface AdService {

    @GET("/{id}/networks/")
    fun getAdNetworks(@Path("id") appId: String): Single<List<AdNetworkDto>>

    @GET("/{id}/")
    fun getZoneConfig(@Path("id") zoneId: String): Single<ZoneConfigDto>
}
